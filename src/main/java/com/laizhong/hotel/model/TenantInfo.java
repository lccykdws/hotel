package com.laizhong.hotel.model;

import lombok.Data;

@Data
public class TenantInfo {
	private int id;
	private String tradeNo;
 
	/**
	 * 证件号码
	 */
	private String credno;
	
	/**
	 * 证件类型  0:身份证 1：驾驶证 2：军官证 3：护照
	 */
	private int credtype;
	
	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 性别 1:男 0:女
	 */
	private int sex;
	
	private String mobile;
}
