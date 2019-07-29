package com.laizhong.hotel.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.utils.HttpClientUtil;
import com.laizhong.hotel.utils.MD5Utils;

/**
 * 获取酒店数据
 * @author zhousiting
 *
 */
@Service
public class HotelDataService {
	
 
    
    /**
     * 获取酒店的所有房型信息
     * @param hotelCode 酒店代码
     * @return
     */
    public ResponseVo<Map<String,Object>> getRoomType(HotelInfo info,JSONObject params) {
    	
		try {
			String signature = MD5Utils.md5(JSONObject.toJSONString(params, SerializerFeature.SortField), info.getSecretKey());
			params.put("key", signature);
			String url = info.getHotelSysUrl()+Urls.Hotel_GetRoomType;
			String result = HttpClientUtil.httpPost(url, params);
			return ResponseVo.success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
    }
}
