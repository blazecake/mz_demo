<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>

<%@ page import="com.tenpay.RequestHandler" %>
<%@ page import="com.tenpay.client.ClientResponseHandler" %>    
<%@ page import="com.tenpay.client.TenpayHttpClient" %>
<%@ page import="java.io.File" %>
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
    reqHandler.setGateUrl("https://gw.tenpay.com/gateway/normalrefundquery.xml");
    
    //-----------------------------
    //���ýӿڲ���
    //-----------------------------
    reqHandler.setParameter("partner", partner);	
    //reqHandler.setParameter("out_trade_no", "0934416328");	
    reqHandler.setParameter("transaction_id", "1900000109201102240030005078");
    //reqHandler.setParameter("out_refund_no", "1033537282");	
    //reqHandler.setParameter("refund_id", "1111900000109201102240360176");

    //-----------------------------
    //����ͨ�Ų���
    //-----------------------------
    //�������󷵻صĵȴ�ʱ��
    httpClient.setTimeOut(5);	
    
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
    	//ֻ��ǩ����ȷ����retcodeΪ0��������ɹ�
    	if(resHandler.isTenpaySign()&& "0".equals(retcode)) {
    		//ȡ���������ҵ����
		//�˿����
		String refund_count = resHandler.getParameter("refund_count");
		
		out.println("�˿����:" + refund_count);
		int count = Integer.parseInt(refund_count);
		//ÿ���˿�����
		/*�˿�״̬	refund_status	
			4��10���˿�ɹ���
			3��5��6���˿�ʧ�ܡ�
			8��9��11:�˿���С�
			1��2: δȷ������Ҫ�̻�ԭ�˿�����·���
			7��ת��������˿���з����û��Ŀ����ϻ��߶����ˣ�����ԭ·�˿����п�ʧ�ܣ��ʽ�������̻����ֽ��ʺţ���Ҫ�̻��˹���Ԥ��ͨ�����»��߲Ƹ�ͨת�˵ķ�ʽ�����˿
			*/
		for(int i=0; i<count; i++){ 
		    String refund_state_n = "refund_state_" + Integer.toString(i);
		    String out_refund_no_n = "out_refund_no_" + Integer.toString(i);
		    String refund_fee_n = "refund_fee_" + Integer.toString(i);
		    
		    out.println("��" + Integer.toString(i) + "�ʣ�" + refund_state_n + "=" + resHandler.getParameter(refund_state_n) 
		        + "," + out_refund_no_n + "=" + resHandler.getParameter(out_refund_no_n) 
		        + "," + refund_fee_n + "=" + resHandler.getParameter(refund_fee_n));
		}	
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
	<title>�˿���ϸ��ѯ����ʾ��</title>
</head>
<body>
</body>
</html>
