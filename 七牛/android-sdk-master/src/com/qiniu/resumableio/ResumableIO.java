package com.qiniu.resumableio;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import com.qiniu.auth.Client;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.utils.ICancel;
import com.qiniu.utils.InputStreamAt;
import com.qiniu.utils.FileUri;
import com.qiniu.utils.QiniuException;

public class ResumableIO {
	ResumableClient mClient;
	private int BLOCK_SIZE = 4 * 1024 * 1024;
	private static int atomicId = 0;
	static HashMap<Integer, ICancel> idCancels = new HashMap<Integer, ICancel>();

	public ResumableIO(String uptoken) {
		mClient = new ResumableClient(Client.getMultithreadClient(), uptoken);
	}
	public ResumableIO(ResumableClient client) {
		mClient = client;
	}

	private synchronized Integer newTask(ICancel c) {
		idCancels.put(atomicId, c);
		return atomicId++;
	}

	private synchronized void removeTask(Integer id) {
		idCancels.remove(id);
	}

	private int putAndClose(final String key, final InputStreamAt input, final PutExtra extra, final JSONObjectRet ret) {
		return put(key, input, extra, new JSONObjectRet() {
			@Override
			public void onSuccess(JSONObject obj) {
				input.close();
				ret.onSuccess(obj);
			}

			@Override
			public void onProcess(long current, long total) {
				ret.onProcess(current, total);
			}

			@Override
			public void onPause(Object tag) {
				ret.onPause(tag);
			}

			@Override
			public void onFailure(QiniuException ex) {
				input.close();
				ret.onFailure(ex);
			}
		});
	}

	public int put(final String key, final InputStreamAt input, final PutExtra extra, final JSONObjectRet ret) {
		final int blkCount = (int) ((input.length() + BLOCK_SIZE - 1) / BLOCK_SIZE);
		if (extra.processes == null)  extra.processes = new PutRet[blkCount];
		extra.totalSize = input.length();
		final int[] success = new int[] {0};
		final long[] uploaded = new long[blkCount];
		final ICancel[][] cancelers = new ICancel[blkCount][1];
		final boolean[] failure = new boolean[] {false};
		final int taskId = newTask(new ICancel() {

			@Override
			public boolean cancel(boolean isIntercupt) {
				for (ICancel[] cancel: cancelers) {
					if (cancel == null || cancel[0] == null) continue;
					cancel[0].cancel(true);
				}
				failure[0] = true;
				ret.onPause(extra);
				return false;
			}
		});
		for (int i=0; i<blkCount; i++) {
			if (extra.processes[i] != null) {
				uploaded[i] = extra.processes[i].offset;
				if (uploaded[i] == BLOCK_SIZE) {
					success[0]++;
					continue;
				}
			}
			if (extra.processes[i] == null) extra.processes[i] = new PutRet();
			final long startPos = i * BLOCK_SIZE;
			cancelers[i] = mClient.putblock(input, extra, extra.processes[i], startPos, new JSONObjectRet(i) {
				int retryTime = 5;

				private void onAllSuccess() {
					String ctx = "";
					for (PutRet ret: extra.processes) ctx += "," + ret.ctx;
					if (ctx.length() > 0) ctx = ctx.substring(1);
					removeTask(taskId);
					mClient.mkfile(key, input.length(), extra.mimeType, extra.params, ctx, ret);
				}

				@Override
				public void onSuccess(JSONObject obj) {
					if (failure[0] || ++success[0] != blkCount) return;
					onAllSuccess();
				}

				@Override
				public void onProcess(long current, long total) {
					if (failure[0]) return;
					uploaded[mIdx] = current;
					current = 0;
					for (long c: uploaded) current += c;
					ret.onProcess(current, input.length());
				}

				@Override
				public void onFailure(QiniuException ex) {
					if (failure[0]) {
						ex.printStackTrace();
						return;
					}
					if (--retryTime <= 0 || (ex.getMessage() != null && ex.getMessage().contains("Unauthorized"))) {
						removeTask(taskId);
						failure[0] = true;
						ret.onFailure(ex);
						return;
					}
					if (ex.getMessage() != null && ex.getMessage().contains("invalid BlockCtx")) {
						uploaded[mIdx] = 0;
						extra.processes[mIdx] = new PutRet();
					}
					cancelers[mIdx] = mClient.putblock(input, extra, extra.processes[mIdx], startPos, this);
				}
			});
		}

		return taskId;
	}

	public int putFile(String key, File file, PutExtra extra, final JSONObjectRet ret) {
		return putAndClose(key, InputStreamAt.fromFile(file), extra, ret);
	}

	public int putFile(Context mContext, String key, Uri uri, PutExtra extra, final JSONObjectRet ret) {
		File file = FileUri.getFile(mContext, uri);
		if (!file.exists()) {
			ret.onFailure(QiniuException.fileNotFound(uri.toString()));
			return -1;
		}

		return putAndClose(key, InputStreamAt.fromFile(file), extra, ret);
	}

	public synchronized static void stop(int id) {
		ICancel c = idCancels.get(id);
		if (c == null) return;
		c.cancel(true);
		idCancels.remove(id);
	}

	public static ResumableIO defaultInstance(String uptoken) {
		return new ResumableIO(new ResumableClient(Client.getMultithreadClient(), uptoken));
	}

	public static int putAndClose(String uptoken, String key, InputStreamAt isa, PutExtra extra, JSONObjectRet ret) {
		return ResumableIO.defaultInstance(uptoken).putAndClose(uptoken, key, isa, extra, ret);
	}

	public static int put(String uptoken, String key, InputStreamAt isa, PutExtra extra, JSONObjectRet ret) {
		return ResumableIO.defaultInstance(uptoken).put(key, isa, extra, ret);
	}

	public static int putFile(Context mContext, String uptoken, String key, Uri uri, PutExtra extra, JSONObjectRet ret) {
		return ResumableIO.defaultInstance(uptoken).putFile(mContext, key, uri, extra, ret);
	}

}
