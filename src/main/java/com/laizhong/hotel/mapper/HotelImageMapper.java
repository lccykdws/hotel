package com.laizhong.hotel.mapper;

import com.laizhong.hotel.model.HotelImage;

public interface HotelImageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HotelImage record);

    int insertSelective(HotelImage record);

    HotelImage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HotelImage record);

    int updateByPrimaryKey(HotelImage record);
}