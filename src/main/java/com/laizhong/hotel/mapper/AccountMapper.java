package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.model.Account;

@Mapper
public interface AccountMapper {

	void insertAccount(Account account);
	
	List<Account> getAccountsByName(@Param("accountName") String accountName); 
	
	Account getAccountById(@Param("accountId") String accountId);
	
	Account getAccountByIdAndPwd(@Param("accountId") String accountId,@Param("pwd") String pwd);
	
}