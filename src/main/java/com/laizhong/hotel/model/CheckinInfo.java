package com.laizhong.hotel.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class CheckinInfo {
	private String orderNo;
	private String hotelCode;
	private String roomNo;
	private String roomPrice;
	private int cardNum;
	private String deposit;
	private int isBuyInsure;
	
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore   
	private Date checkinTime;
	
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore   
	private Date outTime;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore   
	private Date insureDate;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore   
	private Date createdDate;
}
