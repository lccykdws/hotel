package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.laizhong.hotel.model.TenantInfo;
 
@Mapper
public interface CheckinInfoTenantMapper{
     
    
    int batchInsert(List<TenantInfo> info);
    
}