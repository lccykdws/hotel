package com.laizhong.hotel.model;

import lombok.Data;

@Data
public class HotelInfo {
	private String hotelCode;
	private String hotelName;
	private String hotelDesc;
	private String hotelImage;
	private String hotelSysUrl;
	private String secretKey;
	private String hotelVideoUrl;
	private int hotelDeposit;
	private int hotelBreakfast;
	private String hotelQrcode;
}
