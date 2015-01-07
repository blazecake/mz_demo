/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.alipay.android.msp.demo;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088111056827904";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "payment@mingdao.com";

	//商户私钥，自助生成
//	public static final String PRIVATE = "loyrr0m1ph9w2gf5to2v6n7pc5yv8sxr";
	public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKwS9Z7jIIJVygCi"+
	"2Xm8FEShMPJ5HW/DCz3Ot8qOYeJS4ZFJVbXTcN1bevu1vN2+IglzLVQS1bUzBarU"+
	"wL/4IfQkD7eg31j4YJKrbngq6aHxloRNxbw6+H1u0o/KZ3EkP4RlaXXCdLYTl9WH"+
	"uRURvCeTlqBXB61m5mLnOeTpH9O/AgMBAAECgYA4Zd5rPTYigcu7eaSd6a8a71oD"+
	"nv6u6tZ8Y4yJrzEV5ORZohediLetVZZxeZd1diPhxQZOT/4LbbWl0CV8xe8adCHz"+
	"CDxI9KnIG3mkUnnqb2FKdgiVXHYTNOJO6CAV4VjHO7GNDrPxWh45C6vhy12+S3ss"+
	"aTDX0hL/Sf4Y8RsKAQJBANsSyCKCKDZY4kEKIPlLserQzQXnXdEsoAFgPHTNXceF"+
	"GJXt1IhzCchJNccfHXmIDLHBhGikfgMEWa2i0eEECJ8CQQDJFB07oYnMY9SVd4Hb"+
	"7Fj8PXJazk96ugob3H5/Frb00ZU7Z0/9c2saBOKQ9z6KMYLLBxInb6nHGl5O4hNT"+
	"w8DhAkEAvwEdJZk93kSY4AQow7LPqN/sId2b2qiByTFTBLOZtD/DM9VsnJtQSCQs"+
	"kzP5yuBn0QPcoi/o/lBosA9p1jpiGQJARObgkYNsDZ6TxNfbprmZUG3Z1qXldD91"+
	"oVBwBCqLbN64TU+8iDPZmmPcIyKlyoLmt51nqh3IRpKRiGRNjSGB4QJALNleUkoL"+
	"8TPfsz+9kITJIM7XmudNaGw8G0fZp01mTAZCKqfLqgrGTPsMtUhCtPJ/5KK8pOQ1"+
	"dVqRs0FEWOsTOA==";
	
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
