package com.laizhong.hotel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.laizhong.hotel.model.YsAccountImage;
 
@Mapper
public interface YsAccountImageMapper{
    
    List<YsAccountImage> getImageByModelSelective(YsAccountImage params);
    
    int insert(YsAccountImage params);
    
    int delete(int id);
    
    int updateUrlById(@Param("id")int id,@Param("imgUrl")String imgUrl);
    
    int deleteByMerchantNo(@Param("merchantNo")String merchantNo);
}