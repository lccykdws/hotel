package com.laizhong.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
	private String orderNo;
	private String roomNo;
	private String roomPrice;
	private String checkinDate;
	private String checkoutDate;
	private String roomTypeCode;
	private String roomTypeTitle;
	private String totalPrice;
}
