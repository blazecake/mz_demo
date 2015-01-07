package net.sourceforge.simcpux;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.sourceforge.simcpux.util.WXPayUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class PayActivity extends Activity {

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	
	private IWXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		Button payBtn = (Button) findViewById(R.id.pay_btn);
		payBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				new GetAccessTokenTask().execute();
				new WXPayUtil(PayActivity.this, "86157a96-cff6-4795-8b7e-339e870e2484", "dbed00729bad4ebbb3141ba11be509f5", 
						"���߹���߼�ģʽ(������������)", "http://weixin.qq.com", "251905").pay(APP_SECRET, PARTNER_KEY, APP_KEY);
				
				//"20fb0f6b-5bc1-4824-a5e8-1996616c884c"
			}
		});
		
		Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
		checkPayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				Toast.makeText(PayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * ΢�Ź���ƽ̨�̻�ģ����̻�Լ������Կ
	 * 
	 * ע�⣺����hardcode�ڿͻ��ˣ�����genPackage��������ɷ����������
	 */
	private static final String PARTNER_KEY = "8934e7d15453e97507ef794cf7b0519d";
//	private static final String PARTNER_KEY = "256b544f0a1b68e267849c6c631a377c";
	
	private String genPackage(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(PARTNER_KEY); // ע�⣺����hardcode�ڿͻ��ˣ�����genPackage������̶��ɷ����������
		
		// ����md5ժҪǰ��params����Ϊԭʼ���ݣ�δ����url encode����
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		
		return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
	}
	
	 /**
     * ΢�ſ���ƽ̨���̻�Լ������Կ
     * 
     * ע�⣺����hardcode�ڿͻ��ˣ�����genSign��������ɷ����������
     */
	private static final String APP_SECRET = "db426a9829e4b49a0dcac7b4162da6b6"; // wxd930ea5d5a258f4f ��Ӧ����Կ
//	private static final String APP_SECRET = "a50a014aa9187443f2f2a7a1074a6af9"; // wxd930ea5d5a258f4f ��Ӧ����Կ
	
	/**
     * ΢�ſ���ƽ̨���̻�Լ����֧����Կ
     * 
     * ע�⣺����hardcode�ڿͻ��ˣ�����genSign��������ɷ����������
     */
	private static final String APP_KEY = "L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K"; // wxd930ea5d5a258f4f ��Ӧ��֧����Կ
//	private static final String APP_KEY = "vVCx9gdC5nEr82j67x9zmubw8fKqxdmyISPBTsICS0xZd5pvrgYhgBnN7yVhEn1bkHuFsY3vbtsmTwUBujIIuPmmt0C8WCIayUbLw6iGSr6RXSaQFuI6zQAWOspKybUk"; // wxd930ea5d5a258f4f ��Ӧ��֧����Կ
	
	private class GetAccessTokenTask extends AsyncTask<Void, Void, GetAccessTokenResult> {

		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(PayActivity.this, getString(R.string.app_tip), getString(R.string.getting_access_token));
		}

		@Override
		protected void onPostExecute(GetAccessTokenResult result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (result.localRetCode == LocalRetCode.ERR_OK) {
				Toast.makeText(PayActivity.this, R.string.get_access_token_succ, Toast.LENGTH_LONG).show();
				Log.d(TAG, "onPostExecute, accessToken = " + result.accessToken);
				
				GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
				getPrepayId.execute();
			} else {
				Toast.makeText(PayActivity.this, getString(R.string.get_access_token_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected GetAccessTokenResult doInBackground(Void... params) {
			GetAccessTokenResult result = new GetAccessTokenResult();

			String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
					Constants.APP_ID, APP_SECRET);
			Log.d(TAG, "get access token, url = " + url);
			
			byte[] buf = Util.httpGet(url);
			if (buf == null || buf.length == 0) {
				result.localRetCode = LocalRetCode.ERR_HTTP;
				return result;
			}
			
			String content = new String(buf);
			result.parseFrom(content);
			return result;
		}
	}
	
	private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult> {

		private ProgressDialog dialog;
		private String accessToken;
		
		public GetPrepayIdTask(String accessToken) {
			this.accessToken = accessToken;
		}
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(PayActivity.this, getString(R.string.app_tip), getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(GetPrepayIdResult result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (result.localRetCode == LocalRetCode.ERR_OK) {
				Toast.makeText(PayActivity.this, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
				sendPayReq(result);
			} else {
				Toast.makeText(PayActivity.this, getString(R.string.get_prepayid_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected GetPrepayIdResult doInBackground(Void... params) {

			String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
			String entity = genProductArgs();
			
			Log.d(TAG, "doInBackground, url = " + url);
			Log.d(TAG, "doInBackground, entity = " + entity);
			
			GetPrepayIdResult result = new GetPrepayIdResult();
			
			byte[] buf = Util.httpPost(url, entity);
			if (buf == null || buf.length == 0) {
				result.localRetCode = LocalRetCode.ERR_HTTP;
				return result;
			}
			
			String content = new String(buf);
			Log.d(TAG, "doInBackground, content = " + content);
			result.parseFrom(content);
			return result;
		}
	}

	private static enum LocalRetCode {
		ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
	}
	
	private static class GetAccessTokenResult {
		
		private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetAccessTokenResult";
		
		public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
		public String accessToken;
		public int expiresIn;
		public int errCode;
		public String errMsg;
		
		public void parseFrom(String content) {

			if (content == null || content.length() <= 0) {
				Log.e(TAG, "parseFrom fail, content is null");
				localRetCode = LocalRetCode.ERR_JSON;
				return;
			}
			
			try {
				JSONObject json = new JSONObject(content);
				if (json.has("access_token")) { // success case
					accessToken = json.getString("access_token");
					expiresIn = json.getInt("expires_in");
					localRetCode = LocalRetCode.ERR_OK;
				} else {
					errCode = json.getInt("errcode");
					errMsg = json.getString("errmsg");
					localRetCode = LocalRetCode.ERR_JSON;
				}
				
			} catch (Exception e) {
				localRetCode = LocalRetCode.ERR_JSON;
			}
		}
	}
	
	private static class GetPrepayIdResult {
		
		private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetPrepayIdResult";
		
		public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
		public String prepayId;
		public int errCode;
		public String errMsg;
		
		public void parseFrom(String content) {
			
			if (content == null || content.length() <= 0) {
				Log.e(TAG, "parseFrom fail, content is null");
				localRetCode = LocalRetCode.ERR_JSON;
				return;
			}
			
			try {
				JSONObject json = new JSONObject(content);
				if (json.has("prepayid")) { // success case
					prepayId = json.getString("prepayid");
					localRetCode = LocalRetCode.ERR_OK;
				} else {
					localRetCode = LocalRetCode.ERR_JSON;
				}
				
				errCode = json.getInt("errcode");
				errMsg = json.getString("errmsg");
				
			} catch (Exception e) {
				localRetCode = LocalRetCode.ERR_JSON;
			}
		}
	}
	
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * ���� traceid �ֶΰ����û���Ϣ��������Ϣ����������Զ���״̬�Ĳ�ѯ�͸���
	 */
	private String getTraceId() {
		return "crestxu_" + genTimeStamp(); 
	}
	
	/**
	 * ע�⣺�̻�ϵͳ�ڲ��Ķ�����,32���ַ��ڡ��ɰ�����ĸ,ȷ�����̻�ϵͳΨһ
	 */
	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	
	private long timeStamp;
	private String nonceStr, packageValue; 
	
	private String genSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for (; i < params.size() - 1; i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append(params.get(i).getName());
		sb.append('=');
		sb.append(params.get(i).getValue());
		
		String sha1 = Util.sha1(sb.toString());
		Log.d(TAG, "genSign, sha1 = " + sha1);
		return sha1;
	}
	
	private String genProductArgs() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("appid", Constants.APP_ID);
			String traceId = getTraceId();  // traceId �ɿ������Զ��壬�����ڶ����Ĳ�ѯ����٣��������֧���û���Ϣ���ɴ�id
			json.put("traceid", traceId);
			nonceStr = genNonceStr();
			json.put("noncestr", nonceStr);
			
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("bank_type", "WX"));
			packageParams.add(new BasicNameValuePair("body", "���߹���߼�ģʽ()"));  //�޸�
			packageParams.add(new BasicNameValuePair("fee_type", "1"));  //����  1:������
			packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
			packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));  //΢��֪ͨ����������url �޸�
			packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));  //�̻�������  �޸�
			packageParams.add(new BasicNameValuePair("partner", "1900000109"));
//			packageParams.add(new BasicNameValuePair("partner", Constants.PARTNER_ID));  //�̻���
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));  //�û�IP,����IP �޸�
			packageParams.add(new BasicNameValuePair("total_fee", "1"));  //�ܼ۸� �޸�
			packageValue = genPackage(packageParams);
			
			json.put("package", packageValue);
			timeStamp = genTimeStamp();
			json.put("timestamp", timeStamp);
			
			List<NameValuePair> signParams = new LinkedList<NameValuePair>();
			signParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			signParams.add(new BasicNameValuePair("appkey", APP_KEY));
			signParams.add(new BasicNameValuePair("noncestr", nonceStr));
			signParams.add(new BasicNameValuePair("package", packageValue));
			signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
			signParams.add(new BasicNameValuePair("traceid", traceId));
			json.put("app_signature", genSign(signParams));
			
			json.put("sign_method", "sha1");
		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
		
		return json.toString();
	}
	
	/**
	 * �Խ�Activityʵ��IWXAPIEventHandler�ӿ��е�onResp(BaseResp resp)����  
	 *  @Override
	 *	public void onResp(BaseResp resp) {
	 *		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
	 *
	 *		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
	 *			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 *			builder.setTitle(R.string.app_tip);
	 *			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
	 *			builder.show();
	 *		}
	 *	}
	 * @param result
	 */
	private void sendPayReq(GetPrepayIdResult result) {
		
		PayReq req = new PayReq();
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.PARTNER_ID;
		req.prepayId = result.prepayId;
		req.nonceStr = nonceStr;
		req.timeStamp = String.valueOf(timeStamp);
		req.packageValue = "Sign=" + packageValue;  //�Ҽ� ע�����1.����΢��֧�� SDK ʱ����������� package ����дΪ��Sign=WXPay��
		
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("appkey", APP_KEY));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genSign(signParams);
		
		// ��֧��֮ǰ�����Ӧ��û��ע�ᵽ΢�ţ�Ӧ���ȵ���IWXMsg.registerApp��Ӧ��ע�ᵽ΢��
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

		// ����appע�ᵽ΢��
		msgApi.registerApp(Constants.APP_ID);
		api.sendReq(req);
	}
}
