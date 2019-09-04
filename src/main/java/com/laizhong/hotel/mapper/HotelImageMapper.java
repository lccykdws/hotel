package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.HotelImage;

@Mapper
public interface HotelImageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HotelImage record);

    int insertSelective(HotelImage record);

    HotelImage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HotelImage record);

    int updateByPrimaryKey(HotelImage record);
    
    List<HotelImage> selectByHotelCode(String hotelCode);
}