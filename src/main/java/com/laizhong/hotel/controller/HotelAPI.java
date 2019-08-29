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
			return appDataService.getHotelInfoByCode(params);
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
			return appDataService.getRoomType(params);
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
			return appDataService.getStateByRoom(params);
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
	 * 支付
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_Pay)
	public ResponseVo<Map<String, Object>> pay(@RequestBody Map<String, String> params) {
		// TODO
		return null;
		
	}

	/**
	 * 入住
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(Urls.APP_CheckInRoom)
	public ResponseVo<JSONObject> checkInRoom(@RequestBody Map<String, Object> params) {
		log.info("[开始办理入住，请求参数={}]",params);
		try {
			return appDataService.checkInRoom(params);
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
			return appDataService.getInternetOrderInfo(params);
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
			return appDataService.getNowOrder(params);
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
			return appDataService.againCheckInRoom(params);
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
	 * 获取已经支付的押金
	 * @param params
	 * @return
	 */
	/*@PostMapping(Urls.APP_GetHotelDeposit)
	public ResponseVo<Map<String, String>> getHotelDeposit(@RequestBody Map<String, String> params) {
		log.info("[开始获取已经支付的押金，请求参数={}]",params);
		try {
			return appDataService.getHotelDeposit(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}*/
	/**
	 * 办理退房
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_CheckOut)
	public ResponseVo<String> checkout(@RequestBody Map<String, String> params) {
		log.info("[开始办理退房，请求参数={}]",params);
		try {
			return appDataService.checkout(params);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}
}
