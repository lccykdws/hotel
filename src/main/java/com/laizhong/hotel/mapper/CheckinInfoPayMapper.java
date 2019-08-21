package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.model.PayInfo;
 
@Mapper
public interface CheckinInfoPayMapper{
     
    
    int batchInsert(List<PayInfo> info);
    
    int insert(PayInfo info);
    
    int updateByPrimaryKeySelective(PayInfo info);
    
    PayInfo getPayInfoByKey(@Param("payTradeNo")String payTradeNo);
}