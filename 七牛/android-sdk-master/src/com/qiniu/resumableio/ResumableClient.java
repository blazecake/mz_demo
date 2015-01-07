package com.qiniu.resumableio;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.qiniu.auth.CallRet;
import com.qiniu.auth.Client;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.conf.Conf;
import com.qiniu.utils.ICancel;
import com.qiniu.utils.InputStreamAt;
import com.qiniu.utils.Base64;
import com.qiniu.utils.QiniuException;
import com.qiniu.utils.RetryRet;

public class ResumableClient extends Client {
	String mUpToken;
	int CHUNK_SIZE = 256 * 1024;
	int BLOCK_SIZE = 4 * 1024 * 1024;
	public ResumableClient(HttpClient client, String uptoken) {
		super(client);
		mUpToken = uptoken;
	}

	@Override
	protected HttpResponse roundtrip(HttpRequestBase httpRequest) throws IOException {
		if (mUpToken != null) {
			httpRequest.setHeader("Authorization", "UpToken " + mUpToken);
		}
		return super.roundtrip(httpRequest);
	}

	public ICancel[] putblock(final InputStreamAt input, final PutExtra extra, final PutRet putRet, final long offset, final JSONObjectRet callback) {
		final int writeNeed = (int) Math.min(input.length()-offset, BLOCK_SIZE);
		final ICancel[] canceler = new ICancel[] {null};
		JSONObjectRet ret = new JSONObjectRet() {
			long crc32, wrote, writing = 0;
			public void onInit(int flag) {
				flag = putRet.isInvalid() ? 0 : 1;
				if (flag == 0) putInit();
				if (flag == 1) putNext();
			}

			public void putInit() {
				int chunkSize = Math.min(writeNeed, CHUNK_SIZE);
				try {
					crc32 = input.partCrc32(offset, chunkSize);
				} catch (IOException e) {
					onFailure(new QiniuException(QiniuException.IO, "crc IOException", e));
					return;
				}
				canceler[0] = mkblk(input, offset, writeNeed, chunkSize, this);
			}

			public void putNext() {
				wrote = putRet.offset;
				int remainLength = Math.min((int) (input.length() - offset - putRet.offset), CHUNK_SIZE);
				try {
					crc32 = input.partCrc32(offset+putRet.offset, remainLength);
				} catch (IOException e) {
					onFailure(new QiniuException(QiniuException.IO, "next crc IOException", e));
					return;
				}
				canceler[0] = bput(putRet.host, input, putRet.ctx, offset, putRet.offset, remainLength, this);
			}

			@Override
			public void onSuccess(JSONObject obj) {
				if (crc32 != new PutRet(obj).crc32) {
					onInit(-1);
					return;
				}
				putRet.parse(obj);
				if (extra.notify != null) extra.notify.onSuccessUpload(extra);
				wrote += writing;
				if (putRet.offset == writeNeed) {
					callback.onSuccess(obj);
					return;
				}
				putNext();
			}

			@Override
			public void onProcess(long current, long total) {
				writing = current;
				callback.onProcess(wrote+writing, writeNeed);
			}

			@Override
			public void onFailure(QiniuException ex) {
				callback.onFailure(ex);
			}
		};
		ret.onInit(-1);
		return canceler;
	}

	public ICancel mkblk(final InputStreamAt input, final long offset, final int blockSize, final int writeSize, final CallRet ret) {
		String url = Conf.UP_HOST + "/mkblk/" + blockSize;
		ClientExecutor executor = makeClientExecutor();
		CallRet retryRet = new RetryRet(ret){
			@Override
			public void onFailure(QiniuException ex) {
				if (RetryRet.noRetry(ex)){
					ret.onFailure(ex);
					return;
				}
				ClientExecutor executor2 = makeClientExecutor();
				String url2 = Conf.UP_HOST2 + "/mkblk/" + blockSize;
				call(executor2, url2, input.toHttpEntity(offset, writeSize, executor2), ret);
			}
		};

		call(executor, url, input.toHttpEntity(offset, writeSize, executor), retryRet);
		return executor;
	}

	public ICancel bput(String host, InputStreamAt input, String ctx, long blockOffset, long offset, int writeLength, CallRet ret) {
		String url = host + "/bput/" + ctx + "/" + offset;
		ClientExecutor client = makeClientExecutor();

		call(client, url, input.toHttpEntity(blockOffset+offset, writeLength, client), ret);
		return client;
	}

	public ICancel mkfile(final String key, final long fsize, final String mimeType, final Map<String, String> params, final String ctxs, final CallRet ret) {
		String url = Conf.UP_HOST + mkfilePath(key, fsize, mimeType, params);
		StringEntity entity = null;
		try {
			entity = new StringEntity(ctxs);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ret.onFailure(new QiniuException(QiniuException.InvalidEncode, "mkfile", e));
			return null;
		}

		CallRet retryRet = new RetryRet(ret){
			@Override
			public void onFailure(QiniuException ex) {
				if (RetryRet.noRetry(ex)){
					ret.onFailure(ex);
					return;
				}
				String url2 = Conf.UP_HOST2 + mkfilePath(key, fsize, mimeType, params);
				StringEntity entity2 = null;
				try {
					entity2 = new StringEntity(ctxs);
				} catch (UnsupportedEncodingException e) {
				}
				call(makeClientExecutor(), url2, entity2, ret);
			}
		};
		return call(makeClientExecutor(), url, entity, retryRet);
	}

	private static String mkfilePath(String key, long fsize, String mimeType, Map<String, String> params){
		String path = "/mkfile/" + fsize;
		if (mimeType != null && mimeType.length() > 0) {
			path += "/mimeType/" + Base64.encode(mimeType);
		}
		if (key != null && key.length() > 0) {
			path += "/key/" + Base64.encode(key);
		}
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> a: params.entrySet()) {
				path += "/" + a.getKey() + "/" + Base64.encode(a.getValue());
			}
		}
		return path;
	}
}
