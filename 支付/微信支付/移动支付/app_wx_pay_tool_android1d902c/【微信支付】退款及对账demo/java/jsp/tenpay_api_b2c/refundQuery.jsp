<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>

<%@ page import="com.tenpay.RequestHandler" %>
<%@ page import="com.tenpay.client.ClientResponseHandler" %>    
<%@ page import="com.tenpay.client.TenpayHttpClient" %>
<%@ page import="java.io.File" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    //商户号 
    String partner = "1900000109";
    //密钥 
    String key = "8934e7d15453e97507ef794cf7b0519d";
    //创建查询请求对象
    RequestHandler reqHandler = new RequestHandler(null, null);
    //通信对象
    TenpayHttpClient httpClient = new TenpayHttpClient();
    //应答对象
    ClientResponseHandler resHandler = new ClientResponseHandler();
    
    //-----------------------------
    //设置请求参数
    //-----------------------------
    reqHandler.init();
    reqHandler.setKey(key);
    reqHandler.setGateUrl("https://gw.tenpay.com/gateway/normalrefundquery.xml");
    
    //-----------------------------
    //设置接口参数
    //-----------------------------
    reqHandler.setParameter("partner", partner);	
    //reqHandler.setParameter("out_trade_no", "0934416328");	
    reqHandler.setParameter("transaction_id", "1900000109201102240030005078");
    //reqHandler.setParameter("out_refund_no", "1033537282");	
    //reqHandler.setParameter("refund_id", "1111900000109201102240360176");

    //-----------------------------
    //设置通信参数
    //-----------------------------
    //设置请求返回的等待时间
    httpClient.setTimeOut(5);	
    
    //设置发送类型POST
    httpClient.setMethod("POST");     
    
    //设置请求内容
    String requestUrl = reqHandler.getRequestURL();
    httpClient.setReqContent(requestUrl);
    String rescontent = "null";

    //后台调用
    if(httpClient.call()) {
    	//设置结果参数
    	rescontent = httpClient.getResContent();
    	resHandler.setContent(rescontent);
    	resHandler.setKey(key);
    	   	
    	//获取返回参数
    	String retcode = resHandler.getParameter("retcode");
    	
    	//判断签名及结果
    	//只有签名正确并且retcode为0才是请求成功
    	if(resHandler.isTenpaySign()&& "0".equals(retcode)) {
    		//取结果参数做业务处理
		//退款笔数
		String refund_count = resHandler.getParameter("refund_count");
		
		out.println("退款笔数:" + refund_count);
		int count = Integer.parseInt(refund_count);
		//每笔退款详情
		/*退款状态	refund_status	
			4，10：退款成功。
			3，5，6：退款失败。
			8，9，11:退款处理中。
			1，2: 未确定，需要商户原退款单号重新发起。
			7：转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
			*/
		for(int i=0; i<count; i++){ 
		    String refund_state_n = "refund_state_" + Integer.toString(i);
		    String out_refund_no_n = "out_refund_no_" + Integer.toString(i);
		    String refund_fee_n = "refund_fee_" + Integer.toString(i);
		    
		    out.println("第" + Integer.toString(i) + "笔：" + refund_state_n + "=" + resHandler.getParameter(refund_state_n) 
		        + "," + out_refund_no_n + "=" + resHandler.getParameter(out_refund_no_n) 
		        + "," + refund_fee_n + "=" + resHandler.getParameter(refund_fee_n));
		}	
    	} else {
    		//错误时，返回结果未签名，记录retcode、retmsg看失败详情。
    		System.out.println("验证签名失败或业务错误");
    		System.out.println("retcode:" + resHandler.getParameter("retcode")+
    	    	                    " retmsg:" + resHandler.getParameter("retmsg"));
    	    	out.println("retcode:" + resHandler.getParameter("retcode")+
    	    	                    " retmsg:" + resHandler.getParameter("retmsg"));
    	}	
    } else {
    	System.out.println("后台调用通信失败");   	
    	System.out.println(httpClient.getResponseCode());
    	System.out.println(httpClient.getErrInfo());
    	//有可能因为网络原因，请求已经处理，但未收到应答。
    }
    
    //获取debug信息,建议把请求、应答内容、debug信息，通信返回码写入日志，方便定位问题
    System.out.println("http res:" + httpClient.getResponseCode() + "," + httpClient.getErrInfo());
    System.out.println("req url:" + requestUrl);
    System.out.println("req debug:" + reqHandler.getDebugInfo());
    System.out.println("res content:" + rescontent);
    System.out.println("res debug:" + resHandler.getDebugInfo());
    
%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<title>退款明细查询调用示例</title>
</head>
<body>
</body>
</html>
