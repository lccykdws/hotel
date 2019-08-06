package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.Authorize;
 
@Mapper
public interface AuthorizeMapper{
    List<Authorize> getAuthorizeInfoByModelSelective(Authorize auth);
    
    int updateStatusById(int id);
    
	void insertAuthorize(Authorize authorize);
	
}