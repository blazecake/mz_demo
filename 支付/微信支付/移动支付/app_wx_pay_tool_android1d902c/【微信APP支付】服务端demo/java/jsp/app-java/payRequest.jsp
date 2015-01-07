<%@page import="java.util.Date"%>
<%@page import="com.tenpay.AccessTokenRequestHandler"%>
<%@ page language="java" contentType="text/html; charset=GBK"
	pageEncoding="GBK"%>
<%@ page import="com.tenpay.ClientRequestHandler"%>
<%@ page import="com.tenpay.PackageRequestHandler"%>
<%@ page import="com.tenpay.PrepayIdRequestHandler"%>
<%@ page import="com.tenpay.util.TenpayUtil"%>
<%@ page import="com.tenpay.util.MD5Util"%>
<%@ page import="com.tenpay.util.WXUtil"%>
<%@ page import="com.tenpay.util.ConstantUtil"%>
<%@ page import="com.tenpay.RequestHandler"%>
<%@ page import="com.tenpay.ResponseHandler"%>
<%@ page import="com.tenpay.client.TenpayHttpClient"%>
<%@ page import="java.io.BufferedWriter"%>
<%@ page import="java.io.BufferedOutputStream"%>
<%@ page import="java.io.OutputStream"%>
<%@ page import="com.tenpay.util.Sha1Util"%>
<%@ page import="java.util.SortedMap"%>
<%@ page import="java.util.TreeMap"%>

<%
	response.resetBuffer();
	response.setHeader("ContentType", "text/xml");
	out.println("<?xml version=\"1.0\" encoding=\"GBK\"?>");
	out.println("<root>");
	//---------------------------------------------------------
	//΢��֧������ʾ�����̻����մ��ĵ����п������� 
	//---------------------------------------------------------

	//���ղƸ�֪ͨͨ��URL
	String notify_url = "http://127.0.0.1:8180/tenpay_api_b2c/payNotifyUrl.jsp";

	//---------------���ɶ����� ��ʼ------------------------
	//��ǰʱ�� yyyyMMddHHmmss
	String currTime = TenpayUtil.getCurrTime();
	//8λ����
	String strTime = currTime.substring(8, currTime.length());
	//��λ�����
	String strRandom = TenpayUtil.buildRandom(4) + "";
	//10λ���к�,�������е�����
	String strReq = strTime + strRandom;
	//�����ţ��˴���ʱ�����������ɣ��̻������Լ����������ֻҪ����ȫ��Ψһ����
	String out_trade_no = strReq;
	//---------------���ɶ����� ����------------------------

	PackageRequestHandler packageReqHandler = new PackageRequestHandler(request, response);//����package�������� 
	PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler(request, response);//��ȡprepayid��������
	ClientRequestHandler clientHandler = new ClientRequestHandler(request, response);//���ؿͻ���֧��������������
	packageReqHandler.setKey(ConstantUtil.PARTNER_KEY);

	int retcode;
	String retmsg = "";
	String xml_body = "";
	//��ȡtokenֵ 
	String token = AccessTokenRequestHandler.getAccessToken();
	if (!"".equals(token)) {
		//����package��������
		packageReqHandler.setParameter("bank_type", "WX");//��������
		packageReqHandler.setParameter("body", "����"); //��Ʒ����   
		packageReqHandler.setParameter("notify_url", notify_url); //���ղƸ�֪ͨͨ��URL  
		packageReqHandler.setParameter("partner", ConstantUtil.PARTNER); //�̻���    
		packageReqHandler.setParameter("out_trade_no", out_trade_no); //�̼Ҷ�����   
		packageReqHandler.setParameter("total_fee", "1"); //��Ʒ���,�Է�Ϊ��λ  
		packageReqHandler.setParameter("spbill_create_ip",request.getRemoteAddr()); //�������ɵĻ���IP��ָ�û��������IP  
		packageReqHandler.setParameter("fee_type", "1"); //���֣�1�����   66
		packageReqHandler.setParameter("input_charset", "GBK"); //�ַ�����

		//��ȡpackage��
		String packageValue = packageReqHandler.getRequestURL();

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		String traceid = "";
		////���û�ȡprepayid֧������
		prepayReqHandler.setParameter("appid", ConstantUtil.APP_ID);
		prepayReqHandler.setParameter("appkey", ConstantUtil.APP_KEY);
		prepayReqHandler.setParameter("noncestr", noncestr);
		prepayReqHandler.setParameter("package", packageValue);
		prepayReqHandler.setParameter("timestamp", timestamp);
		prepayReqHandler.setParameter("traceid", traceid);

		//���ɻ�ȡԤ֧��ǩ��
		String sign = prepayReqHandler.createSHA1Sign();
		//���ӷǲ���ǩ���Ķ������
		prepayReqHandler.setParameter("app_signature", sign);
		prepayReqHandler.setParameter("sign_method",
				ConstantUtil.SIGN_METHOD);
		String gateUrl = ConstantUtil.GATEURL + token;
		prepayReqHandler.setGateUrl(gateUrl);

		//��ȡprepayId
		String prepayid = prepayReqHandler.sendPrepay();
		//�»ظ��ͻ��˵Ĳ���
		if (null != prepayid && !"".equals(prepayid)) {
			//��������б�
			clientHandler.setParameter("appid", ConstantUtil.APP_ID);
			clientHandler.setParameter("appkey", ConstantUtil.APP_KEY);
			clientHandler.setParameter("noncestr", noncestr);
			//clientHandler.setParameter("package", "Sign=" + packageValue);
			clientHandler.setParameter("package", "Sign=WXPay");
			clientHandler.setParameter("partnerid", ConstantUtil.PARTNER);
			clientHandler.setParameter("prepayid", prepayid);
			clientHandler.setParameter("timestamp", timestamp);
			//����ǩ��
			sign = clientHandler.createSHA1Sign();
			clientHandler.setParameter("sign", sign);

			xml_body = clientHandler.getXmlBody();
			retcode = 0;
			retmsg = "OK";
		} else {
			retcode = -2;
			retmsg = "���󣺻�ȡprepayIdʧ��";
		}
	} else {
		retcode = -1;
		retmsg = "���󣺻�ȡ����Token";
	}
	/**
		��ӡdebug��Ϣ
	 */
	System.out.println("\r\ndebuginfo:\r\n" + new Date());
	System.out.println(packageReqHandler.getDebugInfo());
	System.out.println(prepayReqHandler.getDebugInfo());
	System.out.println(clientHandler.getDebugInfo());
	out.println("<retcode>" + retcode + "</retcode");
	out.println("<retmsg>" + retmsg + "<retmsg>");
	if (!"".equals(xml_body)) {
		out.println(xml_body);
	}
	out.println("</root>");
%>
