package com.laizhong.hotel.controller;

/**
 * 路由
 */
public interface Urls {
    
	/*************安卓端接口*******************************/
	
    String APP_BASEURL = "/app/api";
  
    
    /**
     * 获取酒店信息
     */
    String APP_GetHotelInfo = APP_BASEURL + "/getHotelInfo";
    /**
     * 获取酒店所有房型信息
     */
    String APP_GetRoomType = APP_BASEURL + "/getRoomType";
    /**
     * 获取楼层楼栋
     */
    String APP_GetBuildingInfo = APP_BASEURL + "/getBuildingInfo";
    /**
     * 获取楼栋楼层下指定房型的空房信息
     */
    String APP_GetStateByRoom = APP_BASEURL + "/getStateByRoom";
    /**
     * 获取日租房房价
     */
    String APP_GetRoomPriceByLadder = APP_BASEURL + "/getRoomPriceByLadder";
    /**
     * 获取钟点房房价
     */
    String APP_GetRoomPriceByHour = APP_BASEURL + "/getRoomPriceByHour";
    /**
     * 获取预授权
     */
    String APP_GetAuth = APP_BASEURL + "/getAuth";
    /**
     * 支付
     */
    String APP_Pay = APP_BASEURL + "/pay";
    /**
     * 入住
     */
    String APP_CheckInRoom = APP_BASEURL + "/checkInRoom";
    /**
     * 获取互联网订单信息
     */
    String APP_GetInternetOrderInfo = APP_BASEURL + "/getInternetOrderInfo";
    /**
     * 获取在住订单
     */
    String APP_GetNowOrder = APP_BASEURL + "/getNowOrder";    
    /**
     * 续住
     */
    String APP_AgainCheckInRoom = APP_BASEURL + "/againCheckInRoom";     
    /**
     * 获取酒店发票二维码
     */
    String APP_GetQRCode = APP_BASEURL + "/getQRCode"; 
    /**
     * 获取已经支付的押金
     */
    String APP_GetHotelDeposit = APP_BASEURL + "/getHotelDeposit"; 
    
    /**
     *退房
     */
    String APP_CheckOut = APP_BASEURL + "/checkout"; 
    
    String APP_YS_PAY_RECEIVE = APP_BASEURL+"/yspay/receive/pay";
    
    
    /*************酒店接口*******************************/
    
    String BASEURL = "/api";
    /**
     * 获取房型
     */
    String Hotel_GetRoomType = BASEURL +"/getRoomType";
    
    /**
     * 获取楼栋楼层
     */
    String Hotel_GetBuildingInfo = BASEURL +"/getBuildingInfo";
    
    /**
     * 获取楼栋楼层下的空房间
     */
    String Hotel_GetStateByRoom = BASEURL +"/getStateByRoom";
    
    /**
     * 获取日租房房价
     */
    String Hotel_GetRoomPriceByLadder = BASEURL +"/getRoomPriceByLadder";
    
    /**
     * 获取钟点房房价
     */
    String Hotel_GetRoomPriceByHour = BASEURL +"/getRoomPriceByHour";
    
    /**
     * 入住
     */
    String Hotel_CheckInRoom = BASEURL +"/checkInRoom";
    
    /**
     * 获取互联网订单信息
     */
    String Hotel_GetInternetOrderInfo = BASEURL +"/getInternetOrderInfo";
    
    /**
     * 获取在住订单
     */
    String Hotel_GetNowOrder = BASEURL +"/getNowOrder";
    
    /**
     * 续住
     */
    String Hotel_AgainCheckInRoom = BASEURL +"/againCheckInRoom";
    
    
    /*************支付接口*******************************/
	//支付接口
    String YS_Pay = "https://qrcode.ysepay.com/gateway.do";
    
    //String YS_Guarantee = "https://openapi.ysepay.com/gateway.do";
    //子商户注册接口
    String YS_Register = "https://register.ysepay.com:2443/register_gateway/gateway.do";
    //子商户上传图片接口
    String YS_Upload = "https://uploadApi.ysepay.com:2443/yspay-upload-service?method=upload";
    //分账接口
    String YS_Pay_Division = "https://commonapi.ysepay.com/gateway.do";
    
}
