<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.tenpay.client.TenpayHttpClient" %>
<%@ page import="com.tenpay.DownloadBillRequestHandler"%>
<%
		//商户号 
		String bargainor_id = "1900000107";
		//密钥 
		String key = "82d2f8b9fd7695aec51415ab2900a755";
		
		//创建分账请求对象
		DownloadBillRequestHandler reqHandler = new DownloadBillRequestHandler(null, null);
		//通信对象
		TenpayHttpClient httpClient = new TenpayHttpClient();
		
		//-----------------------------
		//设置请求参数
		//-----------------------------
		reqHandler.init();
		reqHandler.setKey(key);
		reqHandler.setGateUrl("http://mch.tenpay.com/cgi-bin/mchdown_real_new.cgi");
		//-----------------------------
		//设置接口参数
		//-----------------------------
		String timestamp = Long.toString(System.currentTimeMillis()/1000);
		reqHandler.setParameter("spid", bargainor_id);	
		reqHandler.setParameter("trans_time", "2011-10-12");
		reqHandler.setParameter("stamp", timestamp);
		reqHandler.setParameter("cft_signtype", "0");
		reqHandler.setParameter("mchtype", "0");
		
		//设置请求内容
		httpClient.setReqContent(reqHandler.getRequestURL());
		System.out.println(reqHandler.getRequestURL());
		out.println(reqHandler.getRequestURL());
		//设置发送类型 GET
		httpClient.setMethod("GET");
		//后台调用
		if(httpClient.call()) {
			String resContent = httpClient.getResContent();
			System.out.println("--------------------");
			System.out.println("responseContent:");
			System.out.println(resContent);			
			System.out.println("--------------------");	
			out.println("订单下载成功，参考后台日志");
		} else {
			System.out.println("后台调用通信失败");
			
			System.out.println(httpClient.getResponseCode());
			System.out.println(httpClient.getErrInfo());
			//有可能因为网络原因，请求已经处理，但未收到应答。
		}

%>
