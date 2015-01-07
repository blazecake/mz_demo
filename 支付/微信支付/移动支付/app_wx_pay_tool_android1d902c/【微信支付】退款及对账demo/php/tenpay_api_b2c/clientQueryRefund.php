<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<title>������ѯ��̨����ʾ��</title>
</head>
<body>

<?php
//---------------------------------------------------------
//�Ƹ�ͨ�������̨����ʾ�����̻����մ��ĵ����п�������
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

$reqHandler->setGateUrl("https://gw.tenpay.com/gateway/normalrefundquery.xml");
$reqHandler->setParameter("partner", $partner);
//out_trade_no��transaction_id��out_refund_no��refund_id����һ�����
//ͬʱ����ʱ�����ȼ���Ϊ׼�����ȼ�Ϊ��refund_id>out_refund_no>transaction_id>out_trade_no
$reqHandler->setParameter("out_trade_no", "201101121111462844");
//$reqHandler->setParameter("transaction_id", "1900000109201101120023707085");			



//-----------------------------
//����ͨ�Ų���
//-----------------------------
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
		
		//���,�Է�Ϊ��λ
		$refund_count = $resHandler->getParameter("refund_count");
		
		echo "�˿����:" . $refund_count;
		
		//ÿ���˿�����
		for($i=0; $i<$refund_count; $i++) {
			echo "��" . ($i+1) . "�ʣ�" . "refund_state_" . $i . "=" . $resHandler->getParameter("refund_state_".$i) . ",out_refund_no_" . $i . "=" . $resHandler->getParameter("out_refund_no_".$i) . ",refund_fee_" . $i . "=" . $resHandler->getParameter("refund_fee_".$i) . "<br>";;
			
		}
		
		
		
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
