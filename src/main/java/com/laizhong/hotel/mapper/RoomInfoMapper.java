package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.RoomInfo;
 
@Mapper
public interface RoomInfoMapper{
    
    List<RoomInfo> getRoomInfoByModelSelective(RoomInfo params);
}