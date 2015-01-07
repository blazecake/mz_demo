本demo是财付通即时到账API接口应用的java示例
|-jsp
|  |_tenpay_api_b2c
|      |___index.jsp            示例入口 
|      |___DownloadBill.jsp     对账单下载接口示例 
|      |___refundQuery.jsp 	退款明细查询接口示例
|      |___clientRefund.jsp     退款请求接口示例
|      |___WEB-INF
|            |_lib              编译时需要的jar包目录
|
|－java
     |_src
        |__com   
            |_tenpay 
                 |_client                后台交互模式的类
                 |_util                  工具类
                 |_RequestHandler.java   所有请求类的基类
                 |_ResponseHandler.java  页面交互模式的应答基类
