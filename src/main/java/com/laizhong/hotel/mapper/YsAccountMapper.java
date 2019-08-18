package com.laizhong.hotel.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.YsAccount;

@Mapper
public interface YsAccountMapper {

	int insert(YsAccount account);
	
	YsAccount getYsAccount(YsAccount account);
	
	int updateByPrimaryKeySelective(YsAccount account);
	
}