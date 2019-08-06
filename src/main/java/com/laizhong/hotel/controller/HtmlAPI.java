package com.laizhong.hotel.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.laizhong.hotel.dto.OrderParamDTO;
import com.laizhong.hotel.dto.UserInfoDTO;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.HotelRole;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.service.HtmlService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HtmlAPI {
	
	
	@Autowired
	private HtmlService htmlService = null;
	
	
	/**
	 * 获取角色列表
	 * @return
	 */
	@GetMapping("/api/getRoleList")
	public ResponseVo<List<HotelRole>> getRoleList() {
		return ResponseVo.success(htmlService.getRoleList());
	}
	
	/**
	 * 上传图片 本地
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/api/uploadImageLocal", method = RequestMethod.POST, consumes = "multipart/form-data")
	public ResponseVo<String> uploadImageLocal(@RequestPart MultipartFile file, HttpServletRequest request) {
		int imgType = Integer.parseInt(request.getParameter("imgType"));
		String roomType = request.getParameter("roomType");
		log.info("\n--------------------->[imgType]{},[roomType]{}", imgType, roomType);
		return ResponseVo.success(htmlService.saveRoomImg(htmlService.upload(file), roomType, imgType));
	}

	/**
	 * 获取预授权码
	 * @param authCode 预授权码
	 * @return
	 */
	@PostMapping("/api/getAuthCode")
	public ResponseVo<String> getAuthCode(@RequestParam(name="authType") int authType) {
		log.info("[RequestParam]authType:{}", authType);
		return ResponseVo.success(htmlService.getAuthCode(authType));
	}
	
	/**
	 * 获取该笔订单下的所有入住人信息
	 * @param orderNo 订单号
	 * @return
	 */
	@PostMapping("/api/getCheckinInfoTenant")
	public ResponseVo<List<TenantInfo>> getCheckinInfoTenant(@RequestParam(name = "orderNo") String orderNo,
			@RequestParam(name = "hotelCode") String hotelCode) {
		log.info("[RequestParam]orderNo:{},hotelCode:{}", orderNo, hotelCode);
		return ResponseVo.success(htmlService.getCheckinInfoTenant(orderNo, hotelCode));
	}
	
	/**
	 * 获取订单列表
	 * @param order
	 * @return
	 */
	@PostMapping("/api/getOrderList")
	public ResponseVo<?> getOrderList(@RequestBody OrderParamDTO order) {
		log.info("[RequestParam]order:{}", order);
		return ResponseVo.success(htmlService.getOrderList(order));
	}
	
	/**
	 * 创建用户 
	 * @param userInfo
	 * @return
	 */
	@PostMapping("/api/createUser")
	public ResponseVo<String> createUser(@RequestBody UserInfoDTO userInfo) {
		log.info("[RequestParam]userInfo:{}", userInfo);
		return ResponseVo.success(htmlService.createUser(userInfo));
	}
	
	/**
	 * 获取房型（下拉属性）
	 * @param userInfo
	 * @return
	 */
	@PostMapping("/api/getRoom")
	public ResponseVo<List<RoomInfo>> getRoom() {
		return ResponseVo.success(htmlService.getRoom());
	}
	
	/**
	 * 获取用户列表
	 * @param userInfo
	 * @return
	 */
	@PostMapping("/api/getAccounts")
	public ResponseVo<?> getAccounts(@RequestParam(name = "accountName") String accountName,
			@RequestParam(name = "pageNum") int pageNum, @RequestParam(name = "pageSize") int pageSize) {
		return ResponseVo.success(htmlService.getAccount(accountName, pageNum, pageSize));
	}
	
	/**
	 * 获取详细房型信息
	 * @param userInfo
	 * @return
	 */
	@PostMapping("/api/getRoomInfo")
	public ResponseVo<RoomInfo> getRoomInfo(@RequestParam(name = "roomTypeCode") String roomTypeCode) {
		return ResponseVo.success(htmlService.getRoomInfo(roomTypeCode));
	}
	
	@PostMapping("/api/getRoomImage")
	public ResponseVo<List<RoomImage>> getRoomImage(@RequestParam(name = "roomTypeCode") String roomTypeCode) {
		return ResponseVo.success(htmlService.getRoomImage(roomTypeCode));
	}
	
	@PostMapping("/api/saveRoomInfo")
	public ResponseVo<String> saveRoomInfo(@RequestBody RoomInfo roomInfo) {
		return ResponseVo.success(htmlService.saveRoomInfo(roomInfo));
	}
	
	@PostMapping("/api/deleteImg")
	public ResponseVo<String> deleteImg(@RequestParam(name = "id") int id) {
		return ResponseVo.success(htmlService.deleteImg(id));
	}
	
	@PostMapping("/api/getBaseHotelInfo")
	public ResponseVo<HotelInfo> getHotelInfo() {
		return ResponseVo.success(htmlService.getHotelInfo());
	}
	
	@PostMapping("/api/saveHotelInfo")
	public ResponseVo<String> updateHotelInfo(@RequestBody HotelInfo hotelInfo) {
		return ResponseVo.success(htmlService.saveHotelInfo(hotelInfo));
	}
	
	@RequestMapping(value = "/api/uploadImgAndVideo", method = RequestMethod.POST, consumes = "multipart/form-data")
	public ResponseVo<String> uploadImgAndVideo(@RequestPart MultipartFile file, HttpServletRequest request) {
		String type = request.getParameter("type");
		log.info("\n--------------------->[type]{}", type);
		return ResponseVo.success(htmlService.saveImgAndVideo(htmlService.upload(file), type));
	}
}