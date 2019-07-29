package com.laizhong.hotel.dto;

import lombok.Data;

/**
 * 酒店房型信息
 * @author zhousiting
 *
 */
@Data
public class RoomTypeInfoDTO {
	//酒店参数
	private String hotelCode;
	private String roomTypeCode;
	private String roomTypeTitle;
	private String individualPrice;
	private int roomNum;
	private int checkinNum;
	private String roomDesc;
	
	//前端参数
	private String roomImage;
	private int breakfast;
}
