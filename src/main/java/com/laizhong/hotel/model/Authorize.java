package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Authorize {
	private int id;
	private String hotelCode;
	private String authCode;
		
	private String createdBy;
	//0 已使用 1 未使用
	private int status;
		
	//类型（0 会员预授权 1 退押预授权）
	private int authType;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonIgnore   
	private Date createdDate;
}
