package com.laizhong.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.dto.BuildingInfoDTO;
import com.laizhong.hotel.dto.InternetOrderDTO;
import com.laizhong.hotel.dto.RoomInfoDTO;
import com.laizhong.hotel.dto.RoomTypeInfoDTO;
import com.laizhong.hotel.model.HotelImage;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.service.AppDataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HotelAPI {

	@Autowired
	private AppDataService appDataService = null;

	
 

	@PostMapping(Urls.APP_GetHotelInfo)
	public ResponseVo<HotelInfo> getHotelInfoByCode(@RequestBody Map<String, String> params) {	
		log.info("[开始获取酒店信息，请求参数={}]",params);		
		try {
			 ResponseVo<HotelInfo> result = appDataService.getHotelInfoByCode(params);
			 log.info("[获取酒店信息结束，返回结果={}]",JSONObject.toJSONString(result));
			 return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}
	@PostMapping(Urls.APP_GetHotelImage)
	public ResponseVo<List<HotelImage>> getHotelImageByCode(@RequestBody Map<String, String> params) {	
		log.info("[开始获取酒店酒店屏保图，请求参数={}]",params);		
		try {
			return appDataService.getHotelImageByCode(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}
	
	 

	/**
	 * 获取酒店所有房型信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomType)
	public ResponseVo<List<RoomTypeInfoDTO>> getRoomType(@RequestBody Map<String, String> params) {
		log.info("[开始获取酒店所有房型信息，请求参数={}]",params);
		try {
			ResponseVo<List<RoomTypeInfoDTO>> result = appDataService.getRoomType(params);
			log.info("[获取酒店所有房型信息结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
		
	}

	/**
	 * 获取楼层楼栋
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetBuildingInfo)
	public ResponseVo<List<BuildingInfoDTO>> getBuildingInfo(@RequestBody Map<String, String> params) {
		log.info("[开始获取酒店楼层楼栋信息，请求参数={}]",params);
		try {
			return appDataService.getBuildingInfo(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
		
	}

	/**
	 * 获取楼栋楼层下指定房型的空房信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetStateByRoom)
	public ResponseVo<List<RoomInfoDTO>> getStateByRoom(@RequestBody Map<String, String> params) {
		log.info("[开始获取酒店楼栋楼层下指定房型的空房信息，请求参数={}]",params);
		
		try {
			 ResponseVo<List<RoomInfoDTO>> result = appDataService.getStateByRoom(params);
			 log.info("[获取酒店楼栋楼层下指定房型的空房信息结束，返回结果={}]",JSONObject.toJSONString(result));
			 return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 获取日租房房价
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomPriceByLadder)
	public ResponseVo<JSONObject> getRoomPriceByLadder(@RequestBody Map<String, String> params) {
		log.info("[开始获取日租房房价，请求参数={}]",params);
		try {
			 return appDataService.getRoomPriceByLadder(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	  
		 
	}

	/**
	 * 获取钟点房房价
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomPriceByHour)
	public ResponseVo<JSONObject> getRoomPriceByHour(@RequestBody Map<String, String> params) {
		log.info("[开始获取钟点房房价，请求参数={}]",params);
		
		try {
			return appDataService.getRoomPriceByHour(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 获取预授权
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetAuth)
	public ResponseVo<String> getAuth(@RequestBody Map<String, String> params) {
		log.info("[开始获取预授权，请求参数={}]",params);
		
		try {
			return appDataService.getAuth(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 检查支付状态并办理入住或续房
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_CheckPay)
	public ResponseVo<JSONObject> pay(@RequestBody Map<String, String> params) {
		log.info("[开始检查支付状态，请求参数={}]",params);
		
		try {			  
			ResponseVo<JSONObject> result =appDataService.checkPay(params);
			log.info("[检查支付状态结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 入住
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(Urls.APP_CheckInRoom)
	public ResponseVo<Object> checkInRoom(@RequestBody Map<String, Object> params) {
		log.info("[开始办理入住，请求参数={}]",params);
		try {
			ResponseVo<Object> result = appDataService.checkInRoom(params);
			log.info("[办理入住结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 获取互联网订单信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetInternetOrderInfo)
	public ResponseVo<InternetOrderDTO> getInternetOrderInfo(@RequestBody Map<String, String> params) {
		log.info("[开始获取互联网订单信息，请求参数={}]",params);
		try {			 
			ResponseVo<InternetOrderDTO>  result = appDataService.getInternetOrderInfo(params);
			log.info("[获取互联网订单信息结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}				 
	}

	/**
	 * 获取在住订单
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetNowOrder)
	public ResponseVo<Map<String, Object>> getNowOrder(@RequestBody Map<String, String> params) {
		log.info("[开始获取在住订单，请求参数={}]",params);
		try {
			ResponseVo<Map<String, Object>> result =  appDataService.getNowOrder(params);
			log.info("[获取在住订单结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 续住
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_AgainCheckInRoom)
	public ResponseVo<String> againCheckInRoom(@RequestBody Map<String, String> params) {
		log.info("[办理续住，请求参数={}]",params);
		try {
			ResponseVo<String>  result = appDataService.againCheckInRoom(params);
			log.info("[办理续住结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
		
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 获取酒店发票二维码
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetQRCode)
	public ResponseVo<String> getQrCode(@RequestBody Map<String, String> params) {
		log.info("[开始获取酒店发票二维码，请求参数={}]",params);
		try {
			return appDataService.getQRCode(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}
	
 
	/**
	 * 办理退房
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_CheckOut)
	public ResponseVo<String> checkout(@RequestBody Map<String, String> params) {
		log.info("[开始办理退房，请求参数={}]",params);
		try {
			ResponseVo<String> result = appDataService.checkout(params);
			log.info("[办理退房结束，返回结果={}]",JSONObject.toJSONString(result));
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}
}
