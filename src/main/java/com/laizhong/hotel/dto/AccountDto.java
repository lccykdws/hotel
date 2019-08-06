package com.laizhong.hotel.dto;

import java.util.List;

import com.laizhong.hotel.model.Account;
import com.laizhong.hotel.model.AccountRole;

import lombok.Getter;
import lombok.Setter;

public class AccountDto extends Account {

	/**
	 * 角色
	 */
	@Getter
	@Setter
	private List<AccountRole> accountRoles;
	
	public AccountDto(Account account) {
		this.setAccountId(account.getAccountId());
		this.setAccountName(account.getAccountName());
		this.setAccountPwd(account.getAccountPwd());
		this.setCreatedDate(account.getCreatedDate());
	}

}
