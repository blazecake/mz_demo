<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ page import="com.tenpay.RequestHandler" %>
<%@ page import="com.tenpay.ResponseHandler" %>   
<%@ page import="com.tenpay.client.ClientResponseHandler" %>    
<%@ page import="com.tenpay.client.TenpayHttpClient" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
//---------------------------------------------------------
//�Ƹ�֧ͨ��֪ͨ����̨֪ͨ��ʾ�����̻����մ��ĵ����п�������
//---------------------------------------------------------
//�̻���
String partner = "1900000109";

//��Կ
String key = "8934e7d15453e97507ef794cf7b0519d";

//����֧��Ӧ�����
ResponseHandler resHandler = new ResponseHandler(request, response);
resHandler.setKey(key);

//�ж�ǩ��
if(resHandler.isTenpaySign()) {
	
	//֪ͨid
	String notify_id = resHandler.getParameter("notify_id");
	
	//�����������
	RequestHandler queryReq = new RequestHandler(null, null);
	//ͨ�Ŷ���
	TenpayHttpClient httpClient = new TenpayHttpClient();
	//Ӧ�����
	ClientResponseHandler queryRes = new ClientResponseHandler();
	
	//ͨ��֪ͨID��ѯ��ȷ��֪ͨ�����Ƹ�ͨ
	queryReq.init();
	queryReq.setKey(key);
	queryReq.setGateUrl("https://gw.tenpay.com/gateway/verifynotifyid.xml");
	queryReq.setParameter("partner", partner);
	queryReq.setParameter("notify_id", notify_id);
	
	//ͨ�Ŷ���
	httpClient.setTimeOut(5);
	//������������
	httpClient.setReqContent(queryReq.getRequestURL());
	System.out.println("queryReq:" + queryReq.getRequestURL());
	//��̨����
	if(httpClient.call()) {
		//���ý������
		queryRes.setContent(httpClient.getResContent());
		System.out.println("queryRes:" + httpClient.getResContent());
		queryRes.setKey(key);
			
			
		//��ȡ���ز���
		String retcode = queryRes.getParameter("retcode");
		String trade_state = queryRes.getParameter("trade_state");
	
		String trade_mode = queryRes.getParameter("trade_mode");
			
		//�ж�ǩ�������
		if(queryRes.isTenpaySign()&& "0".equals(retcode) && "0".equals(trade_state) && "1".equals(trade_mode)) {
			System.out.println("������ѯ�ɹ�");
			//ȡ���������ҵ����				
			System.out.println("out_trade_no:" + queryRes.getParameter("out_trade_no")+
					" transaction_id:" + queryRes.getParameter("transaction_id"));
			System.out.println("trade_state:" + queryRes.getParameter("trade_state")+
					" total_fee:" + queryRes.getParameter("total_fee"));
		        //�����ʹ���ۿ�ȯ��discount��ֵ��total_fee+discount=ԭ�����total_fee
			System.out.println("discount:" + queryRes.getParameter("discount")+
					" time_end:" + queryRes.getParameter("time_end"));
			//------------------------------
			//����ҵ��ʼ
			//------------------------------
			
			//�������ݿ��߼�
			//ע�⽻�׵���Ҫ�ظ�����
			//ע���жϷ��ؽ��
			
			//------------------------------
			//����ҵ�����
			//------------------------------
			resHandler.sendToCFT("Success");
		}
		else{
				//����ʱ�����ؽ��δǩ������¼retcode��retmsg��ʧ�����顣
				System.out.println("��ѯ��֤ǩ��ʧ�ܻ�ҵ�����");
				System.out.println("retcode:" + queryRes.getParameter("retcode")+
						" retmsg:" + queryRes.getParameter("retmsg"));
		}
	
	} else {

		System.out.println("��̨����ͨ��ʧ��");
			
		System.out.println(httpClient.getResponseCode());
		System.out.println(httpClient.getErrInfo());
		//�п�����Ϊ����ԭ�������Ѿ�������δ�յ�Ӧ��
	}
}
else{
	System.out.println("֪ͨǩ����֤ʧ��");
}

%>

