package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.model.AccountRole;

@Mapper
public interface AccountRoleMapper {

	void insertAccountRole(AccountRole accountRole);
	
	List<AccountRole> getAccountRoles(@Param("accountId") String accountId); 
	
	List<String> getAccountRoleIds(@Param("accountId") String accountId);
	
}