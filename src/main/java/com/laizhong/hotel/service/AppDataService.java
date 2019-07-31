package com.laizhong.hotel.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.dto.BuildingInfoDTO;
import com.laizhong.hotel.dto.CustomerInfoDTO;
import com.laizhong.hotel.dto.OrderDTO;
import com.laizhong.hotel.dto.RoomInfoDTO;
import com.laizhong.hotel.dto.RoomTypeInfoDTO;
import com.laizhong.hotel.mapper.AuthorizeMapper;
import com.laizhong.hotel.mapper.CheckinInfoMapper;
import com.laizhong.hotel.mapper.CheckinInfoTenantMapper;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.mapper.RoomImageMapper;
import com.laizhong.hotel.mapper.RoomInfoMapper;
import com.laizhong.hotel.model.Authorize;
import com.laizhong.hotel.model.CheckinInfo;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.utils.HotelDataUtils;

import lombok.extern.slf4j.Slf4j;
 



@Service
@Slf4j
public class AppDataService {

	@Value("${hotel.insure.price}")
	private int insurePrice;
    @Autowired
    private HotelInfoMapper hotelInfoMapper = null;
    
    @Autowired
    private RoomImageMapper roomImageMapper = null;
    @Autowired
    private RoomInfoMapper roomInfoMapper = null;
    @Autowired
    private AuthorizeMapper authorizeMapper = null;
    @Autowired
    private CheckinInfoMapper checkinInfoMapper = null;
    @Autowired
    private CheckinInfoTenantMapper checkinInfoTenantMapper = null;
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
	public ResponseVo<JSONObject> getRoomPriceByLadder(Map<String, String> params) {
		String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String checkinDate = params.get("checkinDate");
		String checkoutDate = params.get("checkoutDate");
		String roomTypeCode = params.get("roomTypeCode");
		String credno = params.get("credno");
		String credtype = params.get("credtype");
		
		if(StringUtils.isBlank(checkinDate)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_002);
		}
		if(StringUtils.isBlank(checkoutDate)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_003);
		}
		if(StringUtils.isBlank(roomTypeCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_004);
		}
		
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("checkinDate",checkinDate);
		jsonParams.put("checkoutDate", checkoutDate);
		jsonParams.put("roomTypeCode", roomTypeCode);
	
		if(StringUtils.isNotBlank(credno)) {
			jsonParams.put("credno", credno);
		}
		if(StringUtils.isNotBlank(credtype)) {
			jsonParams.put("credtype", credtype);
		}
		String url = info.getHotelSysUrl()+Urls.Hotel_GetRoomPriceByLadder;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {   
    		JSONObject obj = JSONObject.parseObject(result.getData().toString());
    		return ResponseVo.success(obj);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
		 
	}    
    
    /**
     * 获取钟点房房价
     * @param hotelCode 酒店代码
     * @param roomNo 房间号
     * @return
     */
    public ResponseVo<JSONObject> getRoomPriceByHour(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String roomNo = params.get("roomNo");
 
		
		if(StringUtils.isBlank(roomNo)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_005);
		}		
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("roomNo",roomNo);
 
		String url = info.getHotelSysUrl()+Urls.Hotel_GetRoomPriceByHour;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {   
    		JSONObject obj = JSONObject.parseObject(result.getData().toString());
    		return ResponseVo.success(obj);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
		 
    }
    
    
    /**
     * 获取预授权
     * @param hotelCode 酒店代码
     * @param authorizationCode 授权码
     * @return
     */
    @Transactional
    public ResponseVo<String> getAuth(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
    	  String authorizationCode = params.get("authorizationCode");
    	  String authorizationType = params.get("authorizationType");
    	  
    	  Authorize auth = new Authorize();
    	  auth.setHotelCode(hotelCode);
    	  auth.setAuthType(Integer.parseInt(authorizationType));
    	  auth.setStatus(HotelConstant.AUTHORIZE_STATUS_UNUSED);  
    	  
    	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	  String date = format.format(new Date());
    	  try {
			auth.setCreatedDate(format.parse(date));
		} catch (ParseException e) {
		 
		}
    	  auth.setAuthCode(authorizationCode);
    	  List<Authorize> list = authorizeMapper.getAuthorizeInfoByModelSelective(auth);
    	  if(null == list || list.size()==0) {
    		  return  ResponseVo.fail("没有预授权信息");
    	  }else {
    		  //置为已使用
    		  authorizeMapper.updateStatusById(list.get(0).getId());
    		  return  ResponseVo.success();
    	  }
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
     * @throws Exception 
     */
    @Transactional
	public ResponseVo<JSONObject> checkInRoom(Map<String, Object> params) throws Exception {
		String hotelCode = params.get("hotelCode").toString();
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		
		String roomNo= params.get("roomNo").toString();
		String checkinDate= params.get("checkinDate").toString(); 
		String checkoutDate= params.get("checkoutDate").toString();
		int checkinNum= Integer.parseInt(params.get("checkinNum").toString());
		int cardnum= Integer.parseInt(params.get("cardnum").toString());
		String roomTypeCode= params.get("roomTypeCode").toString(); 
		String roomTypeTitle= params.get("roomTypeTitle").toString();
		//支付码
		String qrcode = params.get("qrcode").toString();
		 
		List<CustomerInfoDTO> customerList  = JSONObject.parseArray(JSONObject.toJSONString(params.get("customerList")), CustomerInfoDTO.class) ;
		 
		int deposit= 0 ;
		if(null!=params.get("deposit")) {
			deposit= Integer.parseInt(params.get("deposit").toString());
		}
		//单晚房价
		int roomPrice = 0;
		if(null!=params.get("roomPrice")) {
			roomPrice= Integer.parseInt(params.get("roomPrice").toString());
		}		
		
		//算入住多少晚 
		int diffday =  HotelDataUtils.differentDays(checkinDate, checkoutDate);
		 
		int sumPrice = roomPrice*diffday;
		//是否购买保险
		int isInsure = Integer.parseInt(params.get("isInsure").toString());
		if(isInsure==1) {
			sumPrice = sumPrice+insurePrice;
		}
		//支付代码
		
		//入住
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("roomNo",roomNo);
		jsonParams.put("checkinDate", checkinDate);
		jsonParams.put("checkoutDate",checkoutDate);
		jsonParams.put("checkinNum",checkinNum);
		jsonParams.put("roomPrice",roomPrice);
		jsonParams.put("cardnum",cardnum);
		jsonParams.put("deposit",deposit);
		jsonParams.put("customerList",customerList);
		String url = info.getHotelSysUrl()+Urls.Hotel_CheckInRoom;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {   
    		JSONObject obj = JSONObject.parseObject(result.getData().toString());
    		String orderNo  = obj.getString("orderNo");
    		CheckinInfo checkinfo  = new CheckinInfo();
    		checkinfo.setOrderNo(orderNo);
    		checkinfo.setCardNum(cardnum);
    	  
    		checkinfo.setCheckinTime(checkinDate);
    		checkinfo.setCreatedDate(new Date());
    		checkinfo.setDeposit(String.valueOf(deposit));
    		checkinfo.setHotelCode(hotelCode);
    		if(isInsure==1) {
    			checkinfo.setInsureDate(new Date());
    		}
    		checkinfo.setIsBuyInsure(isInsure);   		 
    		checkinfo.setOutTime(checkoutDate);
    		checkinfo.setRoomNo(roomNo);
    		checkinfo.setRoomPrice(String.valueOf(roomPrice));
    		checkinfo.setRoomTypeCode(roomTypeCode);
    		checkinfo.setRoomTypeTitle(roomTypeTitle);
    		checkinfo.setCheckinNum(checkinNum);
    		checkinfo.setIsCheckOut(0);
    		
    		//插入入住订单信息
    		checkinInfoMapper.insert(checkinfo);
    		List<TenantInfo> tenantList = new ArrayList<TenantInfo>();
    		for(CustomerInfoDTO dto : customerList) {
    			TenantInfo t = new TenantInfo();
    			t.setOrderNo(orderNo);
    			t.setHotelCode(hotelCode);
    			t.setCredno(dto.getCredno());
    			t.setCredtype(dto.getCredtype());
    			t.setName(dto.getName());
    			t.setSex(dto.getSex());
    			tenantList.add(t);
    		}
    		//插入入住人信息
    		checkinInfoTenantMapper.batchInsert(tenantList);
    		
    		
    		return ResponseVo.success(obj);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
	}
    
	/**
	 * 获取互联网订单信息
	 * @param hotelCode 酒店代码
	 * @param credtype 证件类型
	 * @param credno 证件号
	 * @return
	 */
    public ResponseVo<Map<String, Object>> getInternetOrderInfo(Map<String, String> params) {
    	return null;
	 
    }
    
    /**
     * 获取在住订单
     * @param hotelCode 酒店代码
     * @param credtype 证件类型
     * @return
     */
    public ResponseVo<Map<String, Object>> getNowOrder(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String credno = params.get("credno");
		String credtype = params.get("credtype");
		if(StringUtils.isBlank(credno)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_007);
		}	
		if(StringUtils.isBlank(credtype)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_008);
		}	
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("credno",credno);
		jsonParams.put("credtype",credtype);
		String url = info.getHotelSysUrl()+Urls.Hotel_GetNowOrder;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {   
    		OrderDTO dto = JSONObject.parseObject(result.getData().toString(), OrderDTO.class);
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("checkinDate", dto.getCheckinDate());
    		map.put("checkoutDate", dto.getCheckoutDate());
    		map.put("roomNo", dto.getRoomNo());
    		map.put("orderNo", dto.getOrderNo());
    		map.put("roomPrice", dto.getRoomPrice());
    		map.put("customerList", checkinInfoTenantMapper.getTenantInfoByOrder(dto.getOrderNo(), hotelCode));
    		
    		return ResponseVo.success(map);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
    }
    
    /**
     * 续住
     * @param hotelCode 酒店代码
     * @param orderNo 订单号
     * @param extendDate 办理续住时间
     * @param checkoutDate 离店时间
     * @return
     */
    @Transactional
    public ResponseVo<Map<String, String>> againCheckInRoom(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String orderNo = params.get("orderNo");
		String checkoutDate = params.get("checkoutDate");
		//支付码
		String qrcode = params.get("qrcode").toString();
		//单晚房价
		int roomPrice = 0;
		if(null!=params.get("roomPrice")) {
			roomPrice= Integer.parseInt(params.get("roomPrice").toString());
		}		
		String nowDate=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		//算入住多少晚 
		int diffday=0;
		try {
			diffday = HotelDataUtils.differentDays(nowDate, checkoutDate);
		} catch (ParseException e) {			 
			e.printStackTrace();
		}
		 
		int sumPrice = roomPrice*diffday;
		//是否购买保险
		int isInsure = Integer.parseInt(params.get("isInsure").toString());
		if(isInsure==1) {
			sumPrice = sumPrice+insurePrice;
		}
		
		//TODO
		//支付代码
		
		//通知酒店续住		
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("orderNo",orderNo);
		jsonParams.put("extendDate ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		jsonParams.put("checkoutDate",checkoutDate);
		 	 
		String url = info.getHotelSysUrl()+Urls.Hotel_AgainCheckInRoom;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) { 
    		JSONObject obj = JSONObject.parseObject(result.getData().toString());
    		String isSuccess   = obj.getString("isSuccess");
    		if(isSuccess.equals("true")) {
    			CheckinInfo update = new CheckinInfo();
    		 
				update.setOutTime(checkoutDate);
				  			
    			update.setHotelCode(hotelCode);
    			update.setOrderNo(orderNo);
    			checkinInfoMapper.updateByPrimaryKeySelective(update);
    			
    			//TODO
    			//再次制卡
    			
    			
    			return ResponseVo.success();
    		}else {
    			return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    		}
    		
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
    	 
    }
    
    /**
     * 获取酒店发票二维码
     * @param hotelCode 酒店代码
     * @return
     */
    public ResponseVo<String> getQRCode(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		return ResponseVo.success(info.getHotelQrcode());
    }
    
    /**
     * 获取已经支付的押金
     * @return
     */
    public ResponseVo<Map<String,String>> getHotelDeposit(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String credno = params.get("credno");
		String credtype = params.get("credtype");
		if(StringUtils.isBlank(credno)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_007);
		}	
		if(StringUtils.isBlank(credtype)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_008);
		}	
		List<CheckinInfo> list = checkinInfoMapper.getNowOrderInfoByTenant(hotelCode, credno, credtype);
		if(null == list || list.size()==0) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_006);
		}
		String orderNo = list.get(0).getOrderNo();
		String deposit = list.get(0).getDeposit();
		Map<String,String> map = new HashMap<String,String>();
		map.put("orderNo", orderNo);
		map.put("deposit", deposit);
		return ResponseVo.success(map);
		 
    }
    /**
     * 退房
     * @return
     */
    public ResponseVo<Map<String,String>> checkout(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String orderNo = params.get("orderNo");
		String deposit = params.get("deposit");
		if(StringUtils.isBlank(orderNo)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_009);
		}	
		if(StringUtils.isBlank(deposit)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_010);
		}	
		CheckinInfo orderInfo = checkinInfoMapper.getOrderInfoByKey(hotelCode,orderNo);
		if(null == orderInfo) {
			return  ResponseVo.fail(HotelConstant.HOTEL_ERROR_011);
		}
		//更新退房状态
		checkinInfoMapper.checkoutByKey(hotelCode,orderNo);
		
		//TODO
		//解冻押金代码
		return ResponseVo.success();
		 
    }
}