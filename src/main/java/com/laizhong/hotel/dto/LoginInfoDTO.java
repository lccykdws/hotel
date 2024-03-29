package com.laizhong.hotel.dto;

import java.util.List;

import com.laizhong.hotel.model.AccountRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginInfoDTO {
	/**
	 * 账户
	 */
	private String accountId;
	/**
	 * 姓名
	 */
	private String accountName;
	
	private String token;
	
	private List<AccountRole> roles;
}
