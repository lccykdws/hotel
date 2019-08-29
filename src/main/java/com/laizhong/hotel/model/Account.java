package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Account {
	/**
	 * 账户
	 */
	private String accountId;
	/**
	 * 姓名
	 */
	private String accountName;
	/**
	 * 密码
	 */
	private String accountPwd;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	private Date createdDate;
}
