package com.laizhong.hotel.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.CheckinInfo;
 
@Mapper
public interface CheckinInfoMapper{
     
    
    int insert(CheckinInfo info);
    
}