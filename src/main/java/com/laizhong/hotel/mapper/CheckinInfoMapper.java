package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.dto.OrderParamDTO;
import com.laizhong.hotel.model.CheckinInfo;
 
@Mapper
public interface CheckinInfoMapper{
     
    
    int insert(CheckinInfo info);
    
    List<CheckinInfo> getNowOrderInfoByTenant(@Param("hotelCode")String hotelCode,@Param("credno")String credno,@Param("credtype")String credtype);
    
    CheckinInfo getOrderInfoByKey(@Param("hotelCode")String hotelCode,@Param("orderNo")String orderNo);
    
    int checkoutByKey(@Param("hotelCode")String hotelCode,@Param("orderNo")String orderNo);
    
    int updateByPrimaryKeySelective(CheckinInfo info);
    
	List<CheckinInfo> getCheckinInfoList(@Param("order") OrderParamDTO order);
}