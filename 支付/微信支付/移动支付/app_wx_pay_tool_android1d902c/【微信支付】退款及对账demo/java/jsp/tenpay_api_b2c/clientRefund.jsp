<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>

<%@ page import="com.tenpay.RequestHandler" %>
<%@ page import="com.tenpay.client.ClientResponseHandler" %>    
<%@ page import="com.tenpay.client.TenpayHttpClient" %>
<%@ page import="java.io.File" %>
<%@ page import="com.tenpay.util.MD5Util" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    //�̻��� 
    String partner = "1900000109";

    //��Կ 
    String key = "8934e7d15453e97507ef794cf7b0519d";
    
    //������ѯ�������
    RequestHandler reqHandler = new RequestHandler(null, null);
    //ͨ�Ŷ���
    TenpayHttpClient httpClient = new TenpayHttpClient();
    //Ӧ�����
    ClientResponseHandler resHandler = new ClientResponseHandler();
    
    //-----------------------------
    //�����������
    //-----------------------------
    reqHandler.init();
    reqHandler.setKey(key);
    reqHandler.setGateUrl("https://mch.tenpay.com/refundapi/gateway/refund.xml");
    
    //-----------------------------
    //���ýӿڲ���
    //-----------------------------
     reqHandler.setParameter("service_version", "1.1");
    reqHandler.setParameter("partner", "1900000109");	
    reqHandler.setParameter("out_trade_no", "1033537274");	
    reqHandler.setParameter("transaction_id", "1900000109201101270026218385");
    reqHandler.setParameter("out_refund_no", "1033537274");	
    reqHandler.setParameter("total_fee", "1");	
    reqHandler.setParameter("refund_fee", "1");
    reqHandler.setParameter("op_user_id", "1900000109");	
    //����Ա����,MD5����
    reqHandler.setParameter("op_user_passwd", MD5Util.MD5Encode("111111","GBK"));	
    	
    reqHandler.setParameter("recv_user_id", "");	
    reqHandler.setParameter("reccv_user_name", "");
    //-----------------------------
    //����ͨ�Ų���
    //-----------------------------
    //�������󷵻صĵȴ�ʱ��
    httpClient.setTimeOut(5);	
    //����ca֤��
    httpClient.setCaInfo(new File("e:/cacert.pem"));
		
    //���ø���(�̻�)֤��
    httpClient.setCertInfo(new File("e:/1900000109.pfx"), "1900000109");
    
    //���÷�������POST
    httpClient.setMethod("POST");     
    
    //������������
    String requestUrl = reqHandler.getRequestURL();
    httpClient.setReqContent(requestUrl);
    String rescontent = "null";

    //��̨����
    if(httpClient.call()) {
    	//���ý������
    	rescontent = httpClient.getResContent();
    	resHandler.setContent(rescontent);
    	resHandler.setKey(key);
    	   	
    	//��ȡ���ز���
    	String retcode = resHandler.getParameter("retcode");
    	
    	//�ж�ǩ�������
    	if(resHandler.isTenpaySign()&& "0".equals(retcode)) {
    	/*�˿�״̬	refund_status	
			4��10���˿�ɹ���
			3��5��6���˿�ʧ�ܡ�
			8��9��11:�˿���С�
			1��2: δȷ������Ҫ�̻�ԭ�˿�����·���
			7��ת��������˿���з����û��Ŀ����ϻ��߶����ˣ�����ԭ·�˿����п�ʧ�ܣ��ʽ�������̻����ֽ��ʺţ���Ҫ�̻��˹���Ԥ��ͨ�����»��߲Ƹ�ͨת�˵ķ�ʽ�����˿
			*/
    	String refund_status=resHandler.getParameter("refund_status");
    	String out_refund_no=resHandler.getParameter("out_refund_no");
    	
    	out.println("�̻��˿��"+out_refund_no+"���˿�״̬�ǣ�"+refund_status);
    		

    	} else {
    		//����ʱ�����ؽ��δǩ������¼retcode��retmsg��ʧ�����顣
    		System.out.println("��֤ǩ��ʧ�ܻ�ҵ�����");
    		System.out.println("retcode:" + resHandler.getParameter("retcode")+
    	    	                    " retmsg:" + resHandler.getParameter("retmsg"));
    	    	out.println("retcode:" + resHandler.getParameter("retcode")+
    	    	                    " retmsg:" + resHandler.getParameter("retmsg"));
    	}	
    } else {
    	System.out.println("��̨����ͨ��ʧ��");   	
    	System.out.println(httpClient.getResponseCode());
    	System.out.println(httpClient.getErrInfo());
    	//�п�����Ϊ����ԭ�������Ѿ�������δ�յ�Ӧ��
    }
    
    //��ȡdebug��Ϣ,���������Ӧ�����ݡ�debug��Ϣ��ͨ�ŷ�����д����־�����㶨λ����
    System.out.println("http res:" + httpClient.getResponseCode() + "," + httpClient.getErrInfo());
    System.out.println("req url:" + requestUrl);
    System.out.println("req debug:" + reqHandler.getDebugInfo());
    System.out.println("res content:" + rescontent);
    System.out.println("res debug:" + resHandler.getDebugInfo());
    
%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<title>�˿��̨����ʾ��</title>
</head>
<body>
</body>
</html>
