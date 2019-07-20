package com.laizhong.hotel.controller;

/**
 * 路由
 */
public interface Urls {
    
	/*************前端接口*******************************/
	
    String BASEURL = "/api";
  
    String APP_GetHotelCode =BASEURL+"/getHotelcode";
    /**
     * 获取酒店信息
     */
    String APP_GetHotelInfo = BASEURL + "/getHotelInfo";
    /**
     * 获取酒店所有房型信息
     */
    String APP_GetRoomType = BASEURL + "/getRoomType";
    /**
     * 获取楼层楼栋
     */
    String APP_GetBuildingInfo = BASEURL + "/getBuildingInfo";
    /**
     * 获取楼栋楼层下指定房型的空房信息
     */
    String APP_GetStateByRoom = BASEURL + "/getStateByRoom";
    /**
     * 获取日租房房价
     */
    String APP_GetRoomPriceByLadder = BASEURL + "/getRoomPriceByLadder";
    /**
     * 获取钟点房房价
     */
    String APP_GetRoomPriceByHour = BASEURL + "/getRoomPriceByHour";
    /**
     * 获取预授权
     */
    String APP_GetAuth = BASEURL + "/getAuth";
    /**
     * 支付
     */
    String APP_Pay = BASEURL + "/pay";
    /**
     * 入住
     */
    String APP_CheckInRoom = BASEURL + "/checkInRoom";
    /**
     * 获取互联网订单信息
     */
    String APP_GetInternetOrderInfo = BASEURL + "/getInternetOrderInfo";
    /**
     * 获取在住订单
     */
    String APP_GetNowOrder = BASEURL + "/getNowOrder";    
    /**
     * 续住
     */
    String APP_AgainCheckInRoom = BASEURL + "/againCheckInRoom";     
    /**
     * 获取酒店发票二维码
     */
    String APP_GetQCCode = BASEURL + "/getQCCode"; 
    
    
    /*************接口*******************************/
    
    String Hotel_GetHotelCode = "getHotelcode";
    
}
