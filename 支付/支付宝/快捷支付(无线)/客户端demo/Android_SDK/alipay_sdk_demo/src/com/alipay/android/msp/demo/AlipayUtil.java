package com.alipay.android.msp.demo;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.android.app.sdk.AliPay;

/**
 * 支付宝支付
 * @toTODO
 * @author maozhou
 * @2014-8-14下午5:02:44
 */
public class AlipayUtil {
	
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	
	Activity activity;

	private String outTradeNo;  //商户订单号
	private String subject;  //商品名称
	private String body; //商品详情
	private double total_fee;  //该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01，100000000.00]，精确到小数点后两位。
	private String notify_url; //支付宝服务器主动通知商户网站里指定的页面http路径。需要URL encode
	
	public AlipayUtil(Activity activity, String outTradeNo, String subject, String body, double total_fee, String notify_url){
		this.activity=activity;
		this.outTradeNo=outTradeNo;
		this.subject=subject;
		this.body=body;
		this.total_fee=total_fee;
		this.notify_url=notify_url;
	}
	
	public void pay(){
		try {
			Log.i("ExternalPartner", "onItemClick");
			String info = getNewOrderInfo();
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("ExternalPartner", "start pay");
			// start the pay.
			Log.i("TAG", "info = " + info);

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(activity, mHandler);
					
					//设置为沙箱模式，不设置默认为线上环境
					//alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Log.i("TAG", "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(activity, R.string.remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}
	}
	
	//处理支付结果
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);

			switch (msg.what) {
			case RQF_PAY:
			case RQF_LOGIN: {
				
				Toast.makeText(activity, result.getResult(), Toast.LENGTH_SHORT).show();
			}
				break;
			default:
				break;
			}
		};
	};
	
	private String getNewOrderInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(outTradeNo);
		sb.append("\"&subject=\"");
		sb.append(subject);
		sb.append("\"&body=\"");
		sb.append(body);
		sb.append("\"&total_fee=\"");
		sb.append(total_fee);
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		sb.append(URLEncoder.encode(notify_url));
		
		sb.append("\"&service=\"mobile.securitypay.pay");  //接口名称。固定值。
		sb.append("\"&_input_charset=\"UTF-8");
//		sb.append("\"&return_url=\"");
//		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");  //商品展示的超链接。预留参数。
		sb.append("\"&it_b_pay=\"2h");   //未付款交易的超时时间
		sb.append("\"");

		return new String(sb);
	}
	
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
	

}
