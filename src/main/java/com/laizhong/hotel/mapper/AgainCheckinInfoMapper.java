package com.laizhong.hotel.mapper;

import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.model.AgainCheckinInfo;

public interface AgainCheckinInfoMapper {
    int insert(AgainCheckinInfo record);

    int insertSelective(AgainCheckinInfo record);
    
    AgainCheckinInfo getOrderInfoByChildTradeNo(@Param("childTradeNo") String childTradeNo);
}