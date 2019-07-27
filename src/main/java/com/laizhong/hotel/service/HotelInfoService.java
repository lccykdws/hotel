package com.laizhong.hotel.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.model.CustomerInfo;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.ResponseMap;
import com.laizhong.hotel.utils.HttpClientUtil;
import com.laizhong.hotel.utils.MD5Utils;
 



@Service
public class HotelInfoService {

    @Autowired
    private HotelInfoMapper hotelInfoMapper = null;
  
    public Map<String, Object> getHotelInfoByCode(String hotelCode) {
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      
         
    	 
		if(null==info) {
			return ResponseMap.error("找不到酒店，请检查是否配置酒店信息");
		}
		String url = info+Urls.Hotel_GetHotelCode;
		 
		JSONObject params = new JSONObject();
		params.put("hotelCode", hotelCode);
		
		
		try{
			String signature = MD5Utils.md5(JSONObject.toJSONString(params, SerializerFeature.SortField), info.getSecretKey());
			params.put("key", signature);
			String result = HttpClientUtil.httpPost(url, params);
			
			JSONObject obj = JSONObject.parseObject(result);
			String code = obj.getString("code");
			if(ResponseMap.CODE_SUCCESS.equals(code)) {
				JSONObject data = JSONObject.parseObject(obj.getString("data"));
				String tel = data.getString("tel");
				String address = data.getString("address");
				
				return ResponseMap.success(info,"查询成功");
			}else {
				return ResponseMap.error("酒店方出错，错误原因："+obj.getString("message"));
			}
		}catch(Exception ex) {
			return ResponseMap.error("酒店方出错，错误原因："+ex.getMessage());
		}
    }
    
    /**
     * 获取酒店的所有房型信息
     * @param hotelCode 酒店代码
     * @return
     */
    public Map<String, Object> getRoomType(String hotelCode) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }


    /**
     * 获取酒店楼层楼栋信息
     * @param hotelCode 酒店代码
     * @return
     */
    public Map<String, Object> getBuildingInfo(String hotelCode) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
	/**
	 * 获取楼栋楼层下指定房型的空房信息
	 * @param hotelCode 酒店代码
	 * @param getStateByRoom 楼层编码
	 * @param buildingCode 楼栋编码
	 * @param roomNo 房间号
	 * @param roomTypeCode 房型编码
	 * @return
	 */
	public Map<String, Object> getStateByRoom(String hotelCode, String getStateByRoom, String buildingCode,
			String roomNo, String roomTypeCode) {
		// TODO
		return ResponseMap.success(null, "查询成功");
	}
    

    /**
     * 获取日租房房价
     * @param hotelCode 酒店代码
     * @param checkinDate 入住开始时间
     * @param checkoutDate 离店时间
     * @param roomTypeCode 房型代码
     * @param credno 证件号码
     * @param credtype 证件类型
     * @return
     */
	public Map<String, Object> getRoomPriceByLadder(String hotelCode, String checkinDate, String checkoutDate,
			String roomTypeCode, String credno, String credtype) {
		// TODO
		return ResponseMap.success(null, "查询成功");
	}    
    
    /**
     * 获取钟点房房价
     * @param hotelCode 酒店代码
     * @param roomNo 房间号
     * @return
     */
    public Map<String, Object> getRoomPriceByHour(String hotelCode, String roomNo) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
    
    /**
     * 获取预授权
     * @param hotelCode 酒店代码
     * @param authorizationCode 授权码
     * @return
     */
    public Map<String, Object> getAuth(String hotelCode, String authorizationCode) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }    
    
    /**
     * 支付
     * @param hotelCode 酒店代码
     * @param roomPrice 房价
     * @param isInsure 是否买保险(1 是 0 否)
     * @param isDeposit 是否有押金(1 是 0 否)
     * @return
     */
    public Map<String, Object> pay(String hotelCode, String roomPrice, String isInsure, String isDeposit) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
    
    /**
     * 入住
     * @param hotelCode 酒店代码
     * @param roomNo 房间号
     * @param checkinDate 入住时间
     * @param checkoutDate 离店时间
     * @param checkinNum 入住人数
     * @param cardnum 制卡数
     * @param roomPrice 房价
     * @param customerList 入住人信息
     * @return
     */
	public Map<String, Object> checkInRoom(String hotelCode, String roomNo, String checkinDate, String checkoutDate,
			int checkinNum, int cardnum, String roomPrice, List<CustomerInfo> customerList) {
		//TODO
    	return ResponseMap.success(null,"查询成功");
	}
    
	/**
	 * 获取互联网订单信息
	 * @param hotelCode 酒店代码
	 * @param credtype 证件类型
	 * @param credno 证件号
	 * @return
	 */
    public Map<String, Object> getInternetOrderInfo(String hotelCode, String credtype, String credno) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
    /**
     * 获取在住订单
     * @param hotelCode 酒店代码
     * @param credtype 证件类型
     * @return
     */
    public Map<String, Object> getNowOrder(String hotelCode, String credtype) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
    /**
     * 续住
     * @param hotelCode 酒店代码
     * @param orderNo 订单号
     * @param extendDate 办理续住时间
     * @param checkoutDate 离店时间
     * @return
     */
    public Map<String, Object> againCheckInRoom(String hotelCode, String orderNo, String extendDate, String checkoutDate) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
    /**
     * 获取酒店发票二维码
     * @param hotelCode 酒店代码
     * @return
     */
    public Map<String, Object> getQCCode(String hotelCode) {
    	//TODO
    	return ResponseMap.success(null,"查询成功");
    }
    
    
}
