package com.laizhong.hotel.constant;


public class HotelConstant {
	public static final String CONFIG_ERROR_MESSAGE = "找不到酒店配置信息，请登录PMS后台检查是否配置";
	public static final String ERROR_CODE = "fail";
	public static final String SUCCESS_CODE = "success";
	public static final int AUTHORIZE_STATUS_UNUSED = 1;
	public static final int AUTHORIZE_STATUS_USED = 0;
	
	public static final int ROOM_IMAGE_TYPE_FIRST = 0;
	public static final int ROOM_IMAGE_TYPE_OTHER = 1;
	public static final String HOTEL_ERROR_001 = "酒店编号不能为空，请检查配置";
	public static final String HOTEL_ERROR_002 = "请选择入住时间";
	public static final String HOTEL_ERROR_003 = "请选择离店时间";
	public static final String HOTEL_ERROR_004 = "请选择房型";
	public static final String HOTEL_ERROR_005 = "请选择入住房间";
	public static final String HOTEL_ERROR_006 = "没有查到该用户的入住信息";
	public static final String HOTEL_ERROR_007 = "证件号码不能为空";
	public static final String HOTEL_ERROR_008 = "证件类型不能为空";
	public static final String HOTEL_ERROR_009 = "订单号不能为空";
	public static final String HOTEL_ERROR_010 = "退押金额不能为空";
	public static final String HOTEL_ERROR_011 = "订单信息不存在";
	public static final String HOTEL_ERROR_012 = "找不到该房型图片信息，请登录PMS后台检查是否配置";
	public static final String HOTEL_ERROR_013 = "请输入账号";
	public static final String HOTEL_ERROR_014 = "请输入密码";
	public static final String HOTEL_ERROR_015 = "账号或密码错误";
	public static final String HOTEL_ERROR_016 = "入住人不能为空";
	public static final String HOTEL_ERROR_017 = "您当前有入住中的订单，不可以再入住";
	public static final String HOTEL_IMAGE = "IMAGE";
	public static final String HOTEL_VIDEO = "VIDEO";
	public static final String HOTEL_QRCODE = "QRCODE";
	
	//证书私钥密码
	public static final String PASSWORD_PARTNER_PKCS12 = "laizhong";
	//主商户
	public static final String YSPAY_PARTNER_ID = "lzxx";
	public static final String YSPAY_PARTNER_NAME = "深圳市徕众信息科技有限公司";
	
	/**
	 *担保交易确认收货
	 */
	public static final String YSPAY_METHOD_01 = "ysepay.online.trade.confirm";
	
	/**
	 * 担保交易发货
	 */
	public static final String YSPAY_METHOD_02 = "ysepay.online.trade.delivered";
	
	/**
	 * 支付
	 */
	public static final String YSPAY_METHOD_03 = "ysepay.online.barcodepay";
	
	/**
	 * 分账
	 */
	public static final String YSPAY_METHOD_04 = "ysepay.single.division.online.accept";
	
	/**
	 * 分账退款
	 */
	public static final String YSPAY_METHOD_05 = "ysepay.online.trade.refund.split";
	
	/**
	 * 子商户注册
	 */
	public static final String YSPAY_METHOD_06 = "ysepay.merchant.register.accept";
	
	/**
	 * 获取token
	 */
	public static final String YSPAY_METHOD_07 = "ysepay.merchant.register.token.get";
	/**
	 * 子商户注册状态查询
	 */
	public static final String YSPAY_METHOD_08 = "ysepay.merchant.register.query";
	
	
	//酒店支付方式
	public static final String HOTEL_PAY_TYPE_YS = "YS";
	public static final String HOTEL_PAY_TYPE_UNIONPAY = "UnionPay";
	
	
	public static final String CUSTOMER_PAY_WAY_ALIPAY = "Alipay";
	public static final String CUSTOMER_PAY_WAY_WECHAT = "Wechat";
	
	public static final String CHECKIN_TYPE_DAILY = "Daily";
	public static final String CHECKIN_TYPE_HOUR = "Hour";
	
 
	
}
