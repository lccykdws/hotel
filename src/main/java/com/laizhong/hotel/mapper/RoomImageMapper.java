package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.RoomImage;
 
@Mapper
public interface RoomImageMapper{
    
    List<RoomImage> getRoomTypeInfoByModelSelective(RoomImage params);
}