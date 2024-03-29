package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors( chain = true ) 
public class CheckinInfo {
	private String tradeNo; //流水号
	
	private String orderNo; //酒店返回的订单号
	private String hotelCode;
	private String payTradeNo; //支付流水号
	private String roomNo;
	private int roomPrice;
	private int cardNum;
	private int deposit;
	private int isBuyInsure;
	private String roomTypeCode;
	private String roomTypeTitle;
	private int checkinNum;
	private int isCheckOut;
	private int tradeType;
	private String interOrderNo;
	private String checkinTime;
	private String outTime;
	private String checkinType;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	@JsonIgnore   
	private Date insureDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	@JsonIgnore   
	private Date createdDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	@JsonIgnore   
	private Date updatedDate;
}
