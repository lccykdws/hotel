package com.laizhong.hotel.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.dto.BuildingInfoDTO;
import com.laizhong.hotel.dto.RoomInfoDTO;
import com.laizhong.hotel.dto.RoomTypeInfoDTO;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.mapper.RoomImageMapper;
import com.laizhong.hotel.mapper.RoomInfoMapper;
import com.laizhong.hotel.model.CustomerInfo;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;
import com.laizhong.hotel.utils.HotelDataUtils;

import lombok.extern.slf4j.Slf4j;
 



@Service
@Slf4j
public class AppDataService {

    @Autowired
    private HotelInfoMapper hotelInfoMapper = null;
    
    @Autowired
    private RoomImageMapper roomImageMapper = null;
    @Autowired
    private RoomInfoMapper roomInfoMapper = null;
    
    
    /**
     * 获取酒店基本信息
     * @param params
     * @return
     */
    public ResponseVo<HotelInfo> getHotelInfoByCode(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      
		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}	 
		return ResponseVo.success(info);
    	 
    	
    }
    
    /**
     * 获取酒店的所有房型信息
     * @param hotelCode 酒店代码
     * @return
     */
    public ResponseVo<List<RoomTypeInfoDTO>> getRoomType(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		String url = info.getHotelSysUrl()+Urls.Hotel_GetRoomType;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {
    		List<RoomTypeInfoDTO> list = JSONObject.parseArray(result.getData().toString(), RoomTypeInfoDTO.class);
    		for(RoomTypeInfoDTO dto : list) {
    			dto.setBreakfast(info.getHotelBreakfast());
    			RoomImage search = new RoomImage();
    			search.setHotelCode(hotelCode);
    			search.setImageType(0);
    			search.setRoomTypeCode(dto.getRoomTypeCode());
    			try{
    				String imageUrl = roomImageMapper.getRoomTypeInfoByModelSelective(search).get(0).getRoomTypeImage();
    				dto.setRoomImage(imageUrl);
    			}catch(Exception ex) {
    				log.error("[酒店编号={},房型编号={}的首图未上传]",info.getHotelCode(),dto.getRoomTypeCode());
    				dto.setRoomImage("");
    			}
    		}
    		return ResponseVo.success(list);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
    	
    }


    /**
     * 获取酒店楼层楼栋信息
     * @param hotelCode 酒店代码
     * @return
     */
    public ResponseVo<List<BuildingInfoDTO>> getBuildingInfo(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		String url = info.getHotelSysUrl()+Urls.Hotel_GetBuildingInfo;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {
    		List<BuildingInfoDTO> list = JSONObject.parseArray(result.getData().toString(), BuildingInfoDTO.class);
    		return ResponseVo.success(list);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
    }
    
	/**
	 * 获取楼栋楼层下指定房型的空房信息
	 */
	public ResponseVo<List<RoomInfoDTO>> getStateByRoom(Map<String, String> params) {
		String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String floorCode = params.get("floorCode");
		String buildingCode = params.get("buildingCode");
		String roomNo = params.get("roomNo");
		 
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		if(StringUtils.isNotBlank(floorCode)) {
			jsonParams.put("floorCode", floorCode);
		}
		if(StringUtils.isNotBlank(buildingCode)) {
			jsonParams.put("buildingCode", buildingCode);
		}
		if(StringUtils.isNotBlank(roomNo)) {
			jsonParams.put("roomNo", roomNo);
		}
		String url = info.getHotelSysUrl()+Urls.Hotel_GetStateByRoom;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {
    		List<RoomInfoDTO> list = JSONObject.parseArray(result.getData().toString(), RoomInfoDTO.class);
    		String roomTypeCode = params.get("roomTypeCode");
    		Iterator<RoomInfoDTO> iterator = list.iterator();
    		while (iterator.hasNext()) {
    			RoomInfoDTO dto = iterator.next();
    			//根据房型筛选房间
	    		if (roomTypeCode.equals(dto.getRoomTypeCode())) {
	    			RoomInfo search = new RoomInfo();
	    			search.setHotelCode(hotelCode);   		 
	    			search.setRoomTypeCode(dto.getRoomTypeCode());
	    			try{
	    				int bedNum = roomInfoMapper.getRoomInfoByModelSelective(search).get(0).getBedNum();
	    				dto.setBedNum(bedNum);
	    			}catch(Exception ex) {
	    				log.error("[酒店编号={},房型编号={}的床数未设置]",info.getHotelCode(),dto.getRoomTypeCode());
	    				dto.setBedNum(0);
	    			}
	    		}else {
	    			iterator.remove();
	    		}
    		}
    	 
    		return ResponseVo.success(list);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
    
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
	public ResponseVo<Map<String, Object>> getRoomPriceByLadder(String hotelCode, String checkinDate, String checkoutDate,
			String roomTypeCode, String credno, String credtype) {
		// TODO
		return null;
	}    
    
    /**
     * 获取钟点房房价
     * @param hotelCode 酒店代码
     * @param roomNo 房间号
     * @return
     */
    public ResponseVo<Map<String, Object>> getRoomPriceByHour(String hotelCode, String roomNo) {
    	//TODO
    	return null;
    }
    
    
    /**
     * 获取预授权
     * @param hotelCode 酒店代码
     * @param authorizationCode 授权码
     * @return
     */
    public ResponseVo<Map<String, Object>> getAuth(String hotelCode, String authorizationCode) {
    	//TODO
    	return null;
    }    
    
    /**
     * 支付
     * @param hotelCode 酒店代码
     * @param roomPrice 房价
     * @param isInsure 是否买保险(1 是 0 否)
     * @param isDeposit 是否有押金(1 是 0 否)
     * @return
     */
    public ResponseVo<Map<String, Object>> pay(String hotelCode, String roomPrice, String isInsure, String isDeposit) {
    	//TODO
    	return null;
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
    	return null;
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
    	return null;
    }
    
    /**
     * 获取在住订单
     * @param hotelCode 酒店代码
     * @param credtype 证件类型
     * @return
     */
    public Map<String, Object> getNowOrder(String hotelCode, String credtype) {
    	//TODO
    	return null;
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
    	return null;
    }
    
    /**
     * 获取酒店发票二维码
     * @param hotelCode 酒店代码
     * @return
     */
    public Map<String, Object> getQCCode(String hotelCode) {
    	//TODO
    	return null;
    }
    
    
}
