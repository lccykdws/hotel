package com.laizhong.hotel.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class CheckinInfo {
	private int id;
	private String hotelCode;
	private String name;
	private String sex;
	private String mobile;
	private String credtype;
	private String credno;
	private String roomNo;
	private String roomPrice;
	
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore   
	private Date checkin_time;
	
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore   
	private Date out_time;
}
