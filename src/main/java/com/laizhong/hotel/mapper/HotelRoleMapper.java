package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.HotelRole;

@Mapper
public interface HotelRoleMapper {

	List<HotelRole> getHotelRoleList();
	
}