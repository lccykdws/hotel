package com.laizhong.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetOrderDTO {
	private String checkinDate;
	private String checkoutDate;
	private String roomTypeCode;
	private String roomTypeTitle;
	private String roomPrice;
	private String interOrderNo;
	private String totalPrice;
	
	//前端参数
	private String roomImage;
}
