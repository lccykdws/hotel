package com.laizhong.hotel.model;

import lombok.Data;

@Data
public class RoomImage {
	private int id;
	//酒店编码
	private String hotelCode;
	//房型代码
	private String roomTypeCode;
	//房型图片地址
	private String roomTypeImage;
	
	//图片类型（0 : 首图 1：房态图）
	private int imageType;
}
