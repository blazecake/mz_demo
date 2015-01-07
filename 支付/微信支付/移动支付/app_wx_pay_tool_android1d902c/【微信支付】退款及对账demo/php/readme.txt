一、classes下文件：实现基础接口逻辑，一般无需修改
1、RequestHandler.class.php所有请求类的基类
2、ResponseHandler.class.php页面交互模式的应答基类
3、client\ClientResponseHandler.class.php后台系统调用模式的应答基类，支持XML格式
4、client\TenpayHttpClient.class.php通讯类，支持http、https、双向https

二、根目录下的php的文件：调用的例子，需要根据业务情况调整，client开头的为后台系统调用模式接口
1、clientRefund.phh 退款(带证书后台https）接口示例
2、clientQueryRefund.php 退款查询接口示例
3、clientCheck.php 对账单下载接口示例

