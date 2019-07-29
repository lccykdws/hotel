package com.laizhong.hotel.dto;

import lombok.Data;

@Data
public class RoomInfoDTO {
	private String hotelCode;
	private String roomNo;
	private String buildingCode;
	private String buildingName;
	private String floorCode;
	private String floorName;
	private String roomTypeCode;
	private String roomTypeTitle;
	private String roomStateCode;
	private String road;
	private String toward;
	private String window;
	private int guestNum;
	private int cardNum;
	
	//前端参数
	private int bedNum;
}
