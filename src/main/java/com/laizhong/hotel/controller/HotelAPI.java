package com.laizhong.hotel.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.dto.BuildingInfoDTO;
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
		return appDataService.getHotelInfoByCode(params);
	}

	 

	/**
	 * 获取酒店所有房型信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomType)
	public ResponseVo<List<RoomTypeInfoDTO>> getRoomType(@RequestBody Map<String, String> params) {
		return appDataService.getRoomType(params);
	}

	/**
	 * 获取楼层楼栋
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetBuildingInfo)
	public ResponseVo<List<BuildingInfoDTO>> getBuildingInfo(@RequestBody Map<String, String> params) {
		return appDataService.getBuildingInfo(params);
	}

	/**
	 * 获取楼栋楼层下指定房型的空房信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetStateByRoom)
	public ResponseVo<List<RoomInfoDTO>> getStateByRoom(@RequestBody Map<String, String> params) {
		// TODO
		return appDataService.getStateByRoom(params);
	}

	/**
	 * 获取日租房房价
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomPriceByLadder)
	public ResponseVo<Map<String, Object>> getRoomPriceByLadder(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取钟点房房价
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomPriceByHour)
	public ResponseVo<Map<String, Object>> getRoomPriceByHour(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取预授权
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetAuth)
	public ResponseVo<Map<String, Object>> getAuth(@RequestBody Map<String, String> params) {
		// TODO
		return null;
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
	 */
	@PostMapping(Urls.APP_CheckInRoom)
	public ResponseVo<Map<String, Object>> checkInRoom(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取互联网订单信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetInternetOrderInfo)
	public ResponseVo<Map<String, Object>> getInternetOrderInfo(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取在住订单
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetNowOrder)
	public ResponseVo<Map<String, Object>> getNowOrder(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 续住
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_AgainCheckInRoom)
	public ResponseVo<Map<String, Object>> againCheckInRoom(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取酒店发票二维码
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetQCCode)
	public ResponseVo<Map<String, Object>> getQCCode(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}
}
