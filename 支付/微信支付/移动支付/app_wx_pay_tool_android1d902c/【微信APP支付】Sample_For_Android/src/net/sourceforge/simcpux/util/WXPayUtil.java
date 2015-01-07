package net.sourceforge.simcpux.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.R;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付
 * @toTODO
 * @author maozhou
 * @2014-8-12上午11:55:22
 */
public class WXPayUtil {
	
	private String partner_key;
	private String app_key;
	
	Context context;
	private IWXAPI api;
	
	private String nonceStr;  //32位内的随机串，防重发
	private String packageValue; //订单详情
	private long timeStamp;
	
	private String userId;  //用户Id
	private String outTradNo;  //商户订单号  注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
	private String body;  //商品描述
	private String notify_url;  //微信通知己方服务器url
	private String total_fee; //总价格  单位为分
	
	/**
	 * 
	 * @param context
	 * @param userId
	 * @param outTradNo
	 * @param body
	 * @param notify_url
	 * @param total_fee  总价格 单位为分
	 */
	public WXPayUtil(Context context, String userId, String outTradNo, String body, String notify_url, String total_fee){
		api = WXAPIFactory.createWXAPI(context, Constants.APP_ID);
		this.context=context;
		this.userId=userId;
		this.outTradNo=outTradNo;
		this.body=body;
		this.notify_url=notify_url;
		this.total_fee=total_fee;
	}

	public void pay(String app_secret, String partner_key, String app_key){
		//是否支持微信支付
		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
		if(!isPaySupported){
			Toast.makeText(context, "不支持微信支付", Toast.LENGTH_SHORT).show();
			return;
		}
		
		this.partner_key=partner_key;
		this.app_key=app_key;
		
		new GetAccessTokenTask().execute(app_secret);
	}
	
	private class GetAccessTokenTask extends AsyncTask<String, Void, GetAccessTokenResult> {

		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(context, context.getString(R.string.app_tip), context.getString(R.string.getting_access_token));
		}

		@Override
		protected void onPostExecute(GetAccessTokenResult result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (result.localRetCode == LocalRetCode.ERR_OK) {
				Toast.makeText(context, R.string.get_access_token_succ, Toast.LENGTH_LONG).show();
				Log.d("TAG", "onPostExecute, accessToken = " + result.accessToken);
				
				GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
				getPrepayId.execute();
			} else {
				Toast.makeText(context, context.getString(R.string.get_access_token_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected GetAccessTokenResult doInBackground(String... params) {
			GetAccessTokenResult result = new GetAccessTokenResult();

			String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
					Constants.APP_ID, params[0]);
			Log.d("TAG", "get access token, url = " + url);
			
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

	private static enum LocalRetCode {
		ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
	}
	
	private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult> {

		private ProgressDialog dialog;
		private String accessToken;
		
		public GetPrepayIdTask(String accessToken) {
			this.accessToken = accessToken;
		}
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(context, context.getString(R.string.app_tip), context.getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(GetPrepayIdResult result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (result.localRetCode == LocalRetCode.ERR_OK) {
				Toast.makeText(context, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
				sendPayReq(result);
			} else {
				Toast.makeText(context, context.getString(R.string.get_prepayid_fail, result.localRetCode.name()+" errCode:"+result.errCode+" errMsg"+result.errMsg), Toast.LENGTH_LONG).show();
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
			
			Log.d("TAG", "doInBackground, url = " + url);
			Log.d("TAG", "doInBackground, entity = " + entity);
			
			GetPrepayIdResult result = new GetPrepayIdResult();
			
			byte[] buf = Util.httpPost(url, entity);
			if (buf == null || buf.length == 0) {
				result.localRetCode = LocalRetCode.ERR_HTTP;
				return result;
			}
			
			String content = new String(buf);
			Log.d("TAG", "doInBackground, content = " + content);
			result.parseFrom(content);
			return result;
		}
	}
	
	private String genProductArgs() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("appid", Constants.APP_ID);
			String traceId = userId + "_" + outTradNo;  // traceId 由开发者自定义，可用于订单的查询与跟踪，建议根据支付用户信息生成此id。  建议 traceid 字段包含用户信息及订单信息，方便后续对订单状态的查询和跟踪
			json.put("traceid", traceId);
			nonceStr = genNonceStr();
			json.put("noncestr", nonceStr);
			
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("bank_type", "WX"));
			packageParams.add(new BasicNameValuePair("body", body));  //商品描述  修改
			packageParams.add(new BasicNameValuePair("fee_type", "1"));  //币种  1:人命币
			packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
			packageParams.add(new BasicNameValuePair("notify_url", notify_url));  //微信通知己方服务器url 修改
			packageParams.add(new BasicNameValuePair("out_trade_no", outTradNo));  //商户订单号  修改
//			packageParams.add(new BasicNameValuePair("partner", "1900000109"));  //商户号
			packageParams.add(new BasicNameValuePair("partner", Constants.PARTNER_ID));  //商户号
			packageParams.add(new BasicNameValuePair("spbill_create_ip", NetWrokUtil.getLocalIpAddress()));  //用户IP,本机IP 修改
			packageParams.add(new BasicNameValuePair("total_fee", total_fee));  //总价格 修改
			packageValue = genPackage(packageParams);
			
			json.put("package", packageValue);
			timeStamp = System.currentTimeMillis() / 1000;
			json.put("timestamp", timeStamp);
			
			List<NameValuePair> signParams = new LinkedList<NameValuePair>();
			signParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			signParams.add(new BasicNameValuePair("appkey", app_key));
			signParams.add(new BasicNameValuePair("noncestr", nonceStr));
			signParams.add(new BasicNameValuePair("package", packageValue));
			signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
			signParams.add(new BasicNameValuePair("traceid", traceId));
			json.put("app_signature", genSign(signParams));
			
			json.put("sign_method", "sha1");
		} catch (Exception e) {
			Log.e("TAG", "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
		
		return json.toString();
	}
	
	private String genPackage(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(partner_key); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成
		
		// 进行md5摘要前，params内容为原始内容，未经过url encode处理
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		
		return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
	}
	
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
		Log.d("TAG", "genSign, sha1 = " + sha1);
		return sha1;
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
	
	private void sendPayReq(GetPrepayIdResult result) {
		PayReq req = new PayReq();
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.PARTNER_ID;
		req.prepayId = result.prepayId;
		req.nonceStr = nonceStr;
		req.timeStamp = String.valueOf(timeStamp);
		req.packageValue = "Sign=" + packageValue;  //我加 注意事项：1.调起微信支付 SDK 时，请求参数中 package 需填写为：Sign=WXPay。
		
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("appkey", app_key));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genSign(signParams);
		
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		api.sendReq(req);
	}

}
