<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.tenpay.client.TenpayHttpClient" %>
<%@ page import="com.tenpay.DownloadBillRequestHandler"%>
<%
		//�̻��� 
		String bargainor_id = "1900000107";
		//��Կ 
		String key = "82d2f8b9fd7695aec51415ab2900a755";
		
		//���������������
		DownloadBillRequestHandler reqHandler = new DownloadBillRequestHandler(null, null);
		//ͨ�Ŷ���
		TenpayHttpClient httpClient = new TenpayHttpClient();
		
		//-----------------------------
		//�����������
		//-----------------------------
		reqHandler.init();
		reqHandler.setKey(key);
		reqHandler.setGateUrl("http://mch.tenpay.com/cgi-bin/mchdown_real_new.cgi");
		//-----------------------------
		//���ýӿڲ���
		//-----------------------------
		String timestamp = Long.toString(System.currentTimeMillis()/1000);
		reqHandler.setParameter("spid", bargainor_id);	
		reqHandler.setParameter("trans_time", "2011-10-12");
		reqHandler.setParameter("stamp", timestamp);
		reqHandler.setParameter("cft_signtype", "0");
		reqHandler.setParameter("mchtype", "0");
		
		//������������
		httpClient.setReqContent(reqHandler.getRequestURL());
		System.out.println(reqHandler.getRequestURL());
		out.println(reqHandler.getRequestURL());
		//���÷������� GET
		httpClient.setMethod("GET");
		//��̨����
		if(httpClient.call()) {
			String resContent = httpClient.getResContent();
			System.out.println("--------------------");
			System.out.println("responseContent:");
			System.out.println(resContent);			
			System.out.println("--------------------");	
			out.println("�������سɹ����ο���̨��־");
		} else {
			System.out.println("��̨����ͨ��ʧ��");
			
			System.out.println(httpClient.getResponseCode());
			System.out.println(httpClient.getErrInfo());
			//�п�����Ϊ����ԭ�������Ѿ�������δ�յ�Ӧ��
		}

%>
