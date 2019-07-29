package com.laizhong.hotel.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class RoomInfo {
	 
	//酒店编码
	private String hotelCode;
	//房型代码
	private String roomTypeCode;

	private String roomTypeName;
	
	//床数
	private int bedNum;
	//创建时间
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	private Date createdDate;
}
