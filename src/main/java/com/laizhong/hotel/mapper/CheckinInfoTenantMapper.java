package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.model.TenantInfo;
 
@Mapper
public interface CheckinInfoTenantMapper{
     
    
    int batchInsert(List<TenantInfo> info);
    
    List<TenantInfo> getTenantInfoByKey(@Param("orderNo")String orderNo);
    
    List<TenantInfo> getTenantInfoByCredNo(@Param("credno")String credno);
}