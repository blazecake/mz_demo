<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<title>���˵����غ�̨����ʾ��</title>
</head>
<body>

<?php


require ("classes/CheckRequestHandler.class.php");
require ("classes/client/TenpayHttpClient.class.php");

/* �̻��� */
$spid = "1900000109";

/* ��Կ */
$key = "8934e7d15453e97507ef794cf7b0519d";



/* ����֧��������� */
$reqHandler = new CheckRequestHandler();
//ͨ�Ŷ���
$httpClient = new TenpayHttpClient();


//-----------------------------
//�����������
//-----------------------------
$reqHandler->init();
$reqHandler->setKey($key);

//-----------------------------
//����֧������
//-----------------------------
$reqHandler->setParameter("spid", $spid);
$reqHandler->setParameter("trans_time", "2011-10-12");
$reqHandler->setParameter("stamp", time());		
$reqHandler->setParameter("cft_signtype", "0");				
$reqHandler->setParameter("mchtype", "0");	


//-----------------------------
//����ͨ�Ų���
//-----------------------------

//������������
$httpClient->setReqContent($reqHandler->getRequestURL());

//��̨����
if($httpClient->call()) {
	
	echo "OK<br>";

	
} else {
	//��̨����ͨ��ʧ��
	echo "call err:" . $httpClient->getErrInfo() . "<br>";
	//�п�����Ϊ����ԭ�������Ѿ�������δ�յ�Ӧ��
}

echo "����:<br>\r\n" . $httpClient->getResContent() . "<br>";
//������Ϣ,���������Ӧ�����ݡ�debug��Ϣ��ͨ�ŷ�����д����־�����㶨λ����

echo "<br>------------------------------------------------------<br>";
echo "http res:" . $httpClient->getResponseCode() . "," . $httpClient->getErrInfo() . "<br>";
echo "req:" . htmlentities($reqHandler->getRequestURL(), ENT_NOQUOTES, "GB2312") . "<br><br>";
echo "reqdebug:" . $reqHandler->getDebugInfo() . "<br><br>" ;



?>


</body>
</html>
