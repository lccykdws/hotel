package com.laizhong.hotel.dto;

import lombok.Data;

@Data
public class OrderParamDTO {
	private String orderNo;
	private String credno;
	private String roomNo;
	private String roomTypeCode;
	private String checkinTime;
	private String outTime;
	private int pageNum;
	private int pageSize;
}
