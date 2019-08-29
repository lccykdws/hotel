package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AccountRole {
	/**
	 * 编号
	 */
	private int id;
	/**
	 * 账户
	 */
	private String accountId;
	/**
	 * 角色编号
	 */
	private String roleId;
	/**
	 * 角色名称
	 */
	private String roleName;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	private Date createdDate;
}
