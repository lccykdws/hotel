package com.laizhong.hotel.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.laizhong.hotel.model.ResponseMap;
import com.laizhong.hotel.service.HotelInfoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HotelAPI {

	@Autowired
	private HotelInfoService hotelInfoService = null;

	@PostMapping(Urls.APP_GetHotelCode)
	public Map<String, Object> getHotelInfoByName(@RequestBody Map<String, String> params) {
		String name = params.get("hotelname");
		if (StringUtils.isBlank(name)) {
			return ResponseMap.error("酒店名称不能为空，请检查配置");
		}
		return hotelInfoService.getHotelInfoByName(name);
	}

	/**
	 * 获取酒店信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetHotelInfo)
	public Map<String, Object> getHotelInfo(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取酒店所有房型信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomType)
	public Map<String, Object> getRoomType(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取楼层楼栋
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetBuildingInfo)
	public Map<String, Object> getBuildingInfo(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取楼栋楼层下指定房型的空房信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetStateByRoom)
	public Map<String, Object> getStateByRoom(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取日租房房价
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomPriceByLadder)
	public Map<String, Object> getRoomPriceByLadder(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取钟点房房价
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetRoomPriceByHour)
	public Map<String, Object> getRoomPriceByHour(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取预授权
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetAuth)
	public Map<String, Object> getAuth(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 支付
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_Pay)
	public Map<String, Object> pay(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 入住
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_CheckInRoom)
	public Map<String, Object> checkInRoom(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取互联网订单信息
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetInternetOrderInfo)
	public Map<String, Object> getInternetOrderInfo(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取在住订单
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetNowOrder)
	public Map<String, Object> getNowOrder(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 续住
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_AgainCheckInRoom)
	public Map<String, Object> againCheckInRoom(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}

	/**
	 * 获取酒店发票二维码
	 * @param params
	 * @return
	 */
	@PostMapping(Urls.APP_GetQCCode)
	public Map<String, Object> getQCCode(@RequestBody Map<String, String> params) {
		// TODO
		return null;
	}
}
