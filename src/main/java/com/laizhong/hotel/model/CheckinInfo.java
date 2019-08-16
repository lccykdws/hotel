package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class CheckinInfo {
	private String tradeNo; //流水号
	
	private String orderNo; //酒店返回的订单号
	private String hotelCode;
	private String payTradeNo; //支付流水号
	private String roomNo;
	private String roomPrice;
	private int cardNum;
	private String deposit;
	private int isBuyInsure;
	private String roomTypeCode;
	private String roomTypeTitle;
	private int checkinNum;
	private int isCheckOut;
	private int tradeType;
  
	private String checkinTime;
	private String outTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 
	@JsonIgnore   
	private Date insureDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 
	@JsonIgnore   
	private Date createdDate;
}
