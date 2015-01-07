package net.sourceforge.simcpux;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OldPayActivity extends Activity {

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	
	private IWXAPI api;
	private ProgressDialog dialog;
	private String packageValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		Button payBtn = (Button) findViewById(R.id.pay_btn);
		payBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<NameValuePair> params = new LinkedList<NameValuePair>();
				params.add(new BasicNameValuePair("OutPutType", "JSON"));
				params.add(new BasicNameValuePair("body", "千足金箍棒"));
				params.add(new BasicNameValuePair("fee_type", "1"));
				params.add(new BasicNameValuePair("input_charset", "UTF-8"));
				params.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
				params.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
				params.add(new BasicNameValuePair("partner", "1900000109"));
				params.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
				params.add(new BasicNameValuePair("total_fee", "1"));
				
				packageValue = genPackage(params);
				Log.d(TAG, "packageValue = " + packageValue);
				
				params.add(new BasicNameValuePair("sign", packageValue));
				
				String url = "https://www.tenpay.com/app/v1.0/wx_app_api.cgi?" + URLEncodedUtils.format(params, "utf-8");
				Log.d(TAG, "url = " + url);
				new GetPrepayIdTask().execute(url);
			}
		});
		
		Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
		checkPayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				Toast.makeText(OldPayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void sendPayReq(GetPrepayIdResult result) {
		if (result.retCode != 0) {
			Log.e(TAG, "sendPayReq fail, retCode = " + result.retCode + ", retmsg = " + result.retMsg);
			return;
		}
		
		String tradeToken = result.tradeToken;
		Log.d(TAG, "sendPayReq, tradeToken = " + tradeToken + ", tenpaySign = " + result.tenpaySign);
		
		if (tradeToken == null || tradeToken.length() == 0) {
			Log.e(TAG, "sendPayReq fail, tradeToken is empty");
			return;
		}
		
		PayReq req = new PayReq();
		//req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.PARTNER_ID;
		req.prepayId = tradeToken;
		req.nonceStr = genNonceStr();
		req.timeStamp = genTimeStamp();
		//req.packageValue = "Sign=" + packageValue;
		req.packageValue = "Sign=" + result.tenpaySign;
		req.sign = genSign(req);
		req.extData = "app data"; // optional，微信不处理该字段，会在PayResp结构体中回传该字段
		
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		api.sendReq(req);
	}
	
	/**
	 * 财付通和商户约定的商户密钥
	 * 
	 * 注意：不能hardcode在客户端，建议genPackage这个过程由服务器端完成
	 */
	private static final String PARTNER_KEY = "8934e7d15453e97507ef794cf7b0519d";
	
	private String genPackage(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成
		
		return MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
	}
	
	private static final char SPLIT = '&';
	 /**
     * 微信开放平台和商户约定的密钥
     * 
     * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
     */
    //private static final String APP_KEY = "4333d426b8d01a3fe64d53f36892dff4"; // wxf8b4f85f3a794e77 对应的appkey
	private static final String APP_KEY = "L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K"; // wxd930ea5d5a258f4f 对应的appkey
	
	private String genSign(PayReq req) {
		StringBuilder sb = new StringBuilder();
		sb.append("appid=");
		sb.append(req.appId);
		sb.append(SPLIT);
		
		sb.append("appkey=");
		sb.append(APP_KEY); // 注意：不能hardcode在客户端，建议genSign这个过程都由服务器端完成
		sb.append(SPLIT);
		
		sb.append("noncestr=");
		sb.append(req.nonceStr);
		sb.append(SPLIT);
		
		sb.append("package=");
		sb.append(req.packageValue);
		sb.append(SPLIT);
		
		sb.append("partnerid=");
		sb.append(req.partnerId);
		sb.append(SPLIT);
		
		sb.append("prepayid=");
		sb.append(req.prepayId);
		sb.append(SPLIT);
		
		sb.append("timestamp=");
		sb.append(req.timeStamp);
	
		String sha1 = Util.sha1(sb.toString());
		Log.d(TAG, "genSign, sha1 = " + sha1);
		return sha1;
	}
	
	private class GetPrepayIdTask extends AsyncTask<String, Void, GetPrepayIdResult> {

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(OldPayActivity.this, getString(R.string.app_tip), getString(R.string.paying));
			/*dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					GetPrepayIdTask.this.cancel(true);
				}
			});*/
		}

		@Override
		protected void onPostExecute(GetPrepayIdResult result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (result.localRetCode == LocalRetCode.ERR_OK) {
				Toast.makeText(OldPayActivity.this, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
				sendPayReq(result);
			} else {
				Toast.makeText(OldPayActivity.this, getString(R.string.get_prepayid_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected GetPrepayIdResult doInBackground(String... params) {
			GetPrepayIdResult result = new GetPrepayIdResult();
			
			if (params == null || params.length <= 0) {
				result.localRetCode = LocalRetCode.ERR_ARGU;
				return result;
			}
			
			byte[] buf = Util.httpGet(params[0]);
			if (buf == null || buf.length == 0) {
				result.localRetCode = LocalRetCode.ERR_HTTP;
				return result;
			}
			
			String content = new String(buf);
			result.parseFrom(content);
			return result;
		}
	}

	private static enum LocalRetCode {
		ERR_OK, ERR_ARGU, ERR_HTTP, ERR_JSON, ERR_OTHER
	}
	
	private static class GetPrepayIdResult {
		
		public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
		public int retCode;
		public String retMsg;
		public String tradeToken;
		public String tenpaySign;
		
		public void parseFrom(String content) {
			
			try {
				JSONObject json = new JSONObject(content);
				retCode = Integer.valueOf(json.getString("retcode"));
				retMsg = json.getString("retmsg");
				tradeToken = json.getString("trade_token");
				localRetCode = LocalRetCode.ERR_OK;
				tenpaySign = json.getString("tenpay_sign");
			} catch (Exception e) {
				localRetCode = LocalRetCode.ERR_JSON;
			}
		}
	}
	
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	
	private String genTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * 注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
	 */
	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
}
