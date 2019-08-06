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
	
	public static final String HOTEL_IMAGE = "IMAGE";
	public static final String HOTEL_VIDEO = "VIDEO";
	public static final String HOTEL_QRCODE = "QRCODE";
}
