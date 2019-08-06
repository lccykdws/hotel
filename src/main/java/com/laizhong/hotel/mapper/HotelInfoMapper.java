package com.laizhong.hotel.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.HotelInfo;
 
@Mapper
public interface HotelInfoMapper{
    HotelInfo getHotelInfoByCode(String code);
    
    void insertHotelInfo(HotelInfo hotelInfo);
    
    void updateHotelInfo(HotelInfo hotelInfo);
    
}