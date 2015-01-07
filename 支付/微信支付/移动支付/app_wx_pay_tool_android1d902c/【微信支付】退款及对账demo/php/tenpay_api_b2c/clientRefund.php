<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<title>�˿��̨����ʾ��</title>
</head>
<body>

<?php
//---------------------------------------------------------
//�Ƹ�ͨ�˿��̨����ʾ�����̻����մ��ĵ����п�������
//---------------------------------------------------------

require ("classes/RequestHandler.class.php");
require ("classes/client/ClientResponseHandler.class.php");
require ("classes/client/TenpayHttpClient.class.php");

/* �̻��� */
$partner = "1900000109";


/* ��Կ */
$key = "8934e7d15453e97507ef794cf7b0519d";




/* ����֧��������� */
$reqHandler = new RequestHandler();

//ͨ�Ŷ���
$httpClient = new TenpayHttpClient();

//Ӧ�����
$resHandler = new ClientResponseHandler();

//-----------------------------
//�����������
//-----------------------------
$reqHandler->init();
$reqHandler->setKey($key);

$reqHandler->setGateUrl("https://mch.tenpay.com/refundapi/gateway/refund.xml");
$reqHandler->setParameter("partner", $partner);

//out_trade_no��transaction_id����һ�����ͬʱ����ʱtransaction_id����
//$reqHandler->setParameter("out_trade_no", "201101121111462844");
$reqHandler->setParameter("transaction_id", "1900000109201101120023707085");
//���뱣֤ȫ��Ψһ��ͬ���˿�ŲƸ�ͨ��Ϊ��ͬ������
$reqHandler->setParameter("out_refund_no", "2011032400002");
$reqHandler->setParameter("total_fee", "2");
$reqHandler->setParameter("refund_fee", "1");
$reqHandler->setParameter("op_user_id", "1900000109");
//����Ա����,MD5����
$reqHandler->setParameter("op_user_passwd", md5("111111"));		
//�ӿڰ汾��,ȡֵ1.1
$reqHandler->setParameter("service_version", "1.1");


//-----------------------------
//����ͨ�Ų���
//-----------------------------
//����PEM֤�飬pfx֤��תpem������openssl pkcs12 -in 2000000501.pfx  -out 2000000501.pem
//֤���������û����ز�����Ŀ¼������֤�鱻��ȡ
$httpClient->setCertInfo("C:\\key\\1900000109.pem", "1900000109");
//����CA
$httpClient->setCaInfo("C:\\key\\cacert.pem");
$httpClient->setTimeOut(5);
//������������
$httpClient->setReqContent($reqHandler->getRequestURL());

//��̨����
if($httpClient->call()) {
	//���ý������
	$resHandler->setContent($httpClient->getResContent());
	$resHandler->setKey($key);

	//�ж�ǩ�������
	//ֻ��ǩ����ȷ����retcodeΪ0��������ɹ�
	if($resHandler->isTenpaySign() && $resHandler->getParameter("retcode") == "0" ) {
		//ȡ���������ҵ����
		//�̻�������
		$out_trade_no = $resHandler->getParameter("out_trade_no");	
		//�Ƹ�ͨ������
		$transaction_id = $resHandler->getParameter("transaction_id");	
		//�̻��˿��
		$out_refund_no = $resHandler->getParameter("out_refund_no");	
		//�Ƹ�ͨ�˿��
		$refund_id = $resHandler->getParameter("refund_id");	
		//�˿���,�Է�Ϊ��λ
		$refund_fee = $resHandler->getParameter("refund_fee");		
		//�˿�״̬
		$refund_status = $resHandler->getParameter("refund_status");
		
		
		
		echo "OK,refund_status=" . $refund_status . ",out_refund_no=" . $resHandler->getParameter("out_refund_no") . ",refund_fee=" . $resHandler->getParameter("refund_fee") . "<br>";
		
		
	} else {
		//����ʱ�����ؽ������û��ǩ������¼retcode��retmsg��ʧ�����顣
		echo "��֤ǩ��ʧ�� �� ҵ�������Ϣ:retcode=" . $resHandler->getParameter("retcode"). ",retmsg=" . $resHandler->getParameter("retmsg") . "<br>";
	}
	
} else {
	//��̨����ͨ��ʧ��
	echo "call err:" . $httpClient->getResponseCode() ."," . $httpClient->getErrInfo() . "<br>";
	//�п�����Ϊ����ԭ�������Ѿ�������δ�յ�Ӧ��
}


//������Ϣ,���������Ӧ�����ݡ�debug��Ϣ��ͨ�ŷ�����д����־�����㶨λ����
/*
echo "<br>------------------------------------------------------<br>";
echo "http res:" . $httpClient->getResponseCode() . "," . $httpClient->getErrInfo() . "<br>";
echo "req:" . htmlentities($reqHandler->getRequestURL(), ENT_NOQUOTES, "GB2312") . "<br><br>";
echo "res:" . htmlentities($resHandler->getContent(), ENT_NOQUOTES, "GB2312") . "<br><br>";
echo "reqdebug:" . $reqHandler->getDebugInfo() . "<br><br>" ;
echo "resdebug:" . $resHandler->getDebugInfo() . "<br><br>";
*/

?>


</body>
</html>
