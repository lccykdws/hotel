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
import com.laizhong.hotel.constant.ErrorCodeEnum;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.dto.BuildingInfoDTO;
import com.laizhong.hotel.dto.CustomerInfoDTO;
import com.laizhong.hotel.dto.InternetOrderDTO;
import com.laizhong.hotel.dto.RoomInfoDTO;
import com.laizhong.hotel.dto.RoomTypeInfoDTO;
import com.laizhong.hotel.mapper.AgainCheckinInfoMapper;
import com.laizhong.hotel.mapper.AuthorizeMapper;
import com.laizhong.hotel.mapper.CheckinInfoMapper;
import com.laizhong.hotel.mapper.CheckinInfoPayMapper;
import com.laizhong.hotel.mapper.CheckinInfoTenantMapper;
import com.laizhong.hotel.mapper.HotelImageMapper;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.mapper.RoomImageMapper;
import com.laizhong.hotel.mapper.RoomInfoMapper;
import com.laizhong.hotel.mapper.YsAccountMapper;
import com.laizhong.hotel.model.AgainCheckinInfo;
import com.laizhong.hotel.model.Authorize;
import com.laizhong.hotel.model.CheckinInfo;
import com.laizhong.hotel.model.HotelImage;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.PayInfo;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.pay.ys.utils.DateUtil;
import com.laizhong.hotel.pay.ys.utils.Https;
import com.laizhong.hotel.pay.ys.utils.MyStringUtils;
import com.laizhong.hotel.pay.ys.utils.SignUtils;
import com.laizhong.hotel.utils.GenerateCodeUtil;
import com.laizhong.hotel.utils.HotelDataUtils;

import lombok.extern.slf4j.Slf4j;
 



@Service
@Slf4j
public class AppDataService {

	@Value("${hotel.insurance.price}")
	private int insurePrice;//保险价格
	
	@Value("${hotel.pay.type}")
	private String payType; //酒店支付方式
	
	@Value("${hotel.pay.model}")
	private String payModel; //支付环境
 
	
	@Value("${hotel.yspay.pri.cert.path}")
	private String ysPriCertPath; //银盛证书私钥路径
	@Value("${hotel.yspay.pub.cert.path}")
	private String ysPubCertPath; //银盛证书公钥路径
	@Value("${hotel.prd.url}")
	private String prdUrl; //正式环境地址
	@Value("${hotel.hotelCode}")
	private String hotelCode;
 
	
	
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
    @Autowired
    private CheckinInfoPayMapper checkinInfoPayMapper = null;
   
    
    @Autowired
    private YsReceiveService ysReceiveService = null;
    @Autowired
    private AgainCheckinInfoMapper againCheckinInfoMapper = null;
    @Autowired
	private HotelImageMapper hotelImageMapper = null;
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
     * 获取酒店屏保图片
     * @param params
     * @return
     */
    public ResponseVo<List<HotelImage>> getHotelImageByCode(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      
		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		List<HotelImage> list = hotelImageMapper.selectByHotelCode(hotelCode);
		return ResponseVo.success(list);
    	 
    	
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
   
	public ResponseVo<Object> checkInRoom(Map<String, Object> params) throws Exception {
		String hotelCode = params.get("hotelCode").toString();
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		List<CustomerInfoDTO> customerList  = JSONObject.parseArray(JSONObject.toJSONString(params.get("customerList")), CustomerInfoDTO.class) ;
		if(null==customerList || customerList.size()==0){
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_016);
		}
		for(CustomerInfoDTO dto : customerList ){
			List<CheckinInfo> list = checkinInfoMapper.getNowOrderInfoByTenant(hotelCode,dto.getCredno(),dto.getCredtype()+"");
			if(list.size()>0){
				return ResponseVo.fail(HotelConstant.HOTEL_ERROR_017); 
			}
		}
		
		String roomNo= params.get("roomNo").toString();
		String checkinDate= params.get("checkinDate").toString(); 
		String checkoutDate= params.get("checkoutDate").toString();
		int checkinNum= Integer.parseInt(params.get("checkinNum").toString());
		int cardnum= Integer.parseInt(params.get("cardnum").toString());
		String roomTypeCode= params.get("roomTypeCode").toString(); 
		String roomTypeTitle= params.get("roomTypeTitle").toString();
		//支付方式
		String payWay= params.get("payWay").toString();
		//支付码
		String qrcode = params.get("qrcode").toString();
		String checkinType = params.get("checkinType")==null?"Daily":params.get("checkinType").toString();
		
		
		int testMoney = 0;
		
		//押金
		int deposit= 0 ;
		if(null!=params.get("deposit")) {
			deposit= Integer.parseInt(params.get("deposit").toString());	
			testMoney= testMoney+1;
			
		}
		//单晚房价
		int roomPrice = 0;
		if(null!=params.get("roomPrice")) {
			roomPrice= Integer.parseInt(params.get("roomPrice").toString());
			testMoney= testMoney+1;
		}		
		//总房价
		int roomAllPrice = 0;
		
		if(checkinType.equals(HotelConstant.CHECKIN_TYPE_DAILY)){//日租房
			//算入住多少晚 
			int diffday =  HotelDataUtils.differentDays(checkinDate, checkoutDate);
			roomAllPrice = roomPrice*diffday;	
		}else if(checkinType.equals(HotelConstant.CHECKIN_TYPE_HOUR)){//钟点房
			roomAllPrice = roomPrice;
		}
		
		//是否购买保险
		int isInsure = Integer.parseInt(params.get("isInsure").toString());
		int payinsurePrice = 0;
		if(isInsure==1) {
			testMoney= testMoney+1;		 
			payinsurePrice = insurePrice*customerList.size();
		}
		String tradeNo = DateUtil.getCurrentDate("yyyyMMddHHmmss"+GenerateCodeUtil.generateShortUuid());	
		CheckinInfo checkinfo  = new CheckinInfo();
		checkinfo.setTradeNo(tradeNo);
		checkinfo.setCardNum(cardnum);	  
		checkinfo.setCheckinTime(checkinDate);
		checkinfo.setCreatedDate(new Date());
		checkinfo.setDeposit(deposit);
		checkinfo.setHotelCode(hotelCode);
		checkinfo.setCheckinType(checkinType);
		if(isInsure==1) {
			checkinfo.setInsureDate(new Date());
		}
		checkinfo.setIsBuyInsure(isInsure);   		 
		checkinfo.setOutTime(checkoutDate);
		checkinfo.setRoomNo(roomNo);
		checkinfo.setRoomPrice(roomPrice);
		checkinfo.setRoomTypeCode(roomTypeCode);
		checkinfo.setRoomTypeTitle(roomTypeTitle);
		checkinfo.setCheckinNum(checkinNum);
		checkinfo.setIsCheckOut(0);
		
		//插入入住订单信息
		checkinInfoMapper.insert(checkinfo);
		List<TenantInfo> tenantList = new ArrayList<TenantInfo>();
		for(CustomerInfoDTO dto : customerList) {			
			TenantInfo t = new TenantInfo();
			t.setTradeNo(tradeNo);
			t.setCredno(dto.getCredno());
			t.setCredtype(dto.getCredtype());
			t.setName(dto.getName());
			t.setSex(dto.getSex());
			tenantList.add(t);
		}
		//插入入住人信息
		checkinInfoTenantMapper.batchInsert(tenantList);
		
		if(payType.equals(HotelConstant.HOTEL_PAY_TYPE_YS)){
			//支付代码
			Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_03,prdUrl+Urls.APP_YS_PAY_RECEIVE_PAY);
			if(info.getHotelDeposit()>0){
				paramsMap.put("tran_type", "2");//入住时有押金，一定是担保交易
			}			
			String shopdate = DateUtil.getCurrentDate("yyyyMMdd");
				
			Map<String,String> bizContent = new HashMap<>();
			bizContent.put("out_trade_no", tradeNo);
			bizContent.put("shopdate", shopdate);
			bizContent.put("scene", "bar_code");
			String bankType = "1902000";//默认微信支付
			if(payWay.equals(HotelConstant.CUSTOMER_PAY_WAY_ALIPAY)) {
				bankType = "1903000";
			}
			bizContent.put("bank_type", bankType);
			bizContent.put("auth_code", qrcode);
			if(payModel.equals("PRD")) {
				bizContent.put("total_amount",String.valueOf(roomAllPrice+deposit+payinsurePrice));
			}else {
				bizContent.put("total_amount", (testMoney*1.0/10)+"");
			}			
			bizContent.put("subject", info.getHotelName()+roomTypeTitle+roomNo+"入住");
			bizContent.put("seller_id", HotelConstant.YSPAY_PARTNER_ID);
			bizContent.put("seller_name", HotelConstant.YSPAY_PARTNER_NAME);
			bizContent.put("timeout_express", "5m");//订单超时设置5分钟			
			bizContent.put("business_code", "3010002");
			paramsMap.put("biz_content", MyStringUtils.toJson(bizContent));
		    paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));
		    try{
		    	String response = Https.httpsSend(Urls.YS_Pay, paramsMap);		    	
		    	JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	JSONObject payResponse = ysResponse.getJSONObject("ysepay_online_barcodepay_response");		    	
		    	String code = payResponse.getString("code");
		    	if(code.equals("10000")) {
		    		String payTradeNo = payResponse.getString("trade_no");
		    		String payTradeStatus = payResponse.getString("trade_status");
		    		PayInfo payInfo = new PayInfo();
		    		payInfo.setPayTradeNo(payTradeNo);
		    		payInfo.setPayTradeType(payWay);
		    		payInfo.setPayTradeStatus(payTradeStatus);
		    		payInfo.setTradeNo(tradeNo);
		    		payInfo.setCreatedDate(new Date());
		    		payInfo.setDeposit(deposit);
		    		payInfo.setRoomPrice(roomAllPrice);
		    		payInfo.setInsurePrice(payinsurePrice);
		    		payInfo.setTradeType(HotelConstant.PAY_INFO_TRADE_TYPE_01);
		    		payInfo.setPayType(HotelConstant.PAY_INFO_PAY_TYPE_01);
		    		checkinInfoPayMapper.insert(payInfo);	 
		    		
		    		if(info.getHotelDeposit()>0){
		    			//担保交易
		    			if(!payTradeStatus.equals("WAIT_SELLER_SEND_GOODS")) {				    			 
		    				String subMsg = payResponse.getString("sub_msg");
			    			if(subMsg.contains("USERPAYING")) {
			    				//JSONObject result = new JSONObject();
			    				//result.put("tradeNo", tradeNo);
			    				return ResponseVo.fail(ErrorCodeEnum.E0002,tradeNo);
			    			}else {
			    				return ResponseVo.fail(subMsg);
			    			}
			    		}
		    			JSONObject guaranteeResponse = ysReceiveService.guarantee(tradeNo,payTradeNo,HotelConstant.YSPAY_METHOD_02);
		    			String guaranteeCode = guaranteeResponse.getString("code");
				    	if(!guaranteeCode.equals("10000")) {
				    		return ResponseVo.fail("担保交易发货失败，错误信息："+guaranteeResponse.getString("sub_msg"));
				    	} 
				    	//更新交易状态
				    	payInfo.setPayTradeStatus(guaranteeResponse.getString("trade_status"));
				    	checkinInfoPayMapper.updateByPrimaryKeySelective(payInfo);
		    		}else {
		    			if(!payTradeStatus.equals("TRADE_SUCCESS")) {				    			 
		    				String subMsg = payResponse.getString("sub_msg");
			    			if(subMsg.contains("USERPAYING")) {
			    				//JSONObject result = new JSONObject();
			    				//result.put("tradeNo", tradeNo);
			    				return ResponseVo.fail(ErrorCodeEnum.E0002,tradeNo);
			    			}else {
			    				return ResponseVo.fail(subMsg);
			    			}
			    		}
		    			
		    		}
		    	}else {
		    		String msg = payResponse.getString("sub_msg");
		    		return ResponseVo.fail("银盛支付失败，错误信息:"+msg);
		    	}
		    }catch(Exception e) {
		    	return ResponseVo.fail("银盛支付失败，错误信息:"+e.getMessage());
		    }
		    

		}else if(payType.equals(HotelConstant.HOTEL_PAY_TYPE_UNIONPAY)){
			
		}
		 try {
			 return ResponseVo.success(checkInAfterPay(tradeNo,info,roomNo,checkinDate,checkoutDate,checkinNum,roomPrice,cardnum,deposit,customerList,payWay));
		 }catch(Exception ex) {
			 ex.printStackTrace();
			 return ResponseVo.fail("通知酒店入住异常，错误信息:"+ex.getMessage());
		 }		 		
	}
    /**
     * 通知酒店入住
     * @param tradeNo 我方订单号
     * @param info 酒店信息
     * @param roomNo 房间号
     * @param checkinDate 入住时间
     * @param checkoutDate 离店时间
     * @param checkinNum 入住人数
     * @param roomPrice 单晚房费
     * @param cardnum 入住
     * @param deposit
     * @param customerList
     * @return
     * @throws Exception
     */
	public JSONObject checkInAfterPay(String tradeNo,HotelInfo info,String roomNo,String checkinDate,String checkoutDate,int checkinNum,int roomPrice,int cardnum, int deposit, List<CustomerInfoDTO> customerList,String payWay) throws Exception {
		log.info("[流水号"+tradeNo+"通知酒店办理入住开始]");
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
		jsonParams.put("payWay",payWay);
		String url = info.getHotelSysUrl()+Urls.Hotel_CheckInRoom;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {   
    		JSONObject obj = JSONObject.parseObject(result.getData().toString());
    		String orderNo  = obj.getString("orderNo");
    		checkinInfoMapper.updateOrderNoById(tradeNo,orderNo);
    		
    		log.info("[流水号"+tradeNo+"入住成功，通知酒店制卡]");
    		//发起制卡请求
    		JSONObject createParams = new JSONObject();
    		createParams.put("hotelCode", hotelCode);
    		createParams.put("orderNo",orderNo);
    		String createUrl = info.getHotelSysUrl()+Urls.Hotel_CreateCard;
        	ResponseVo<Object> result2 = HotelDataUtils.getHotelData(createUrl,info.getSecretKey(), createParams);
        	if(result2.getCode().equals(HotelConstant.SUCCESS_CODE)) {
        		return obj;
        	}else {
        		throw new Exception("酒店制卡失败，错误信息:"+result2.getMessage()); 
        	}
    		
    	}else {
    		throw new Exception("酒店数据请求失败，错误信息:"+result.getMessage());
   		 
    	}
	}
	
	/**
	 * 获取互联网订单信息
	 * @param hotelCode 酒店代码
	 * @param credtype 证件类型
	 * @param credno 证件号
	 * @return
	 */
    public ResponseVo<InternetOrderDTO> getInternetOrderInfo(Map<String, String> params) {
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
		
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("credno",credno);
		jsonParams.put("credtype", credtype);

		String url = info.getHotelSysUrl()+Urls.Hotel_GetInternetOrderInfo;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {  
    		InternetOrderDTO dto = JSONObject.parseObject(result.getData().toString(), InternetOrderDTO.class);
    		//获取房型首图图片
    		 RoomImage search = new  RoomImage();
    		 search.setHotelCode(hotelCode);
    		 search.setRoomTypeCode(dto.getRoomTypeCode());
    		 search.setImageType(HotelConstant.ROOM_IMAGE_TYPE_FIRST);
    		 List<RoomImage> roomList = roomImageMapper.getRoomTypeInfoByModelSelective(search);
    		 if(null == roomList || roomList.size()==0) {
    			 return ResponseVo.fail(HotelConstant.HOTEL_ERROR_012);
    		 }
    		 RoomImage first = roomImageMapper.getRoomTypeInfoByModelSelective(search).get(0);
    		 dto.setRoomImage(first.getRoomTypeImage());
    		 return ResponseVo.success(dto);
    	}else {
    		return ResponseVo.fail("酒店数据请求失败，错误信息:"+result.getMessage());
    	}
    	 
	 
    }
    
    /**
     * 获取在住订单
     * @param hotelCode 酒店代码
     * @param credtype 证件类型
     * @return
     * @throws ParseException 
     */
    public ResponseVo<Map<String, Object>> getNowOrder(Map<String, String> params) throws ParseException {
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
		List<CheckinInfo> list = checkinInfoMapper.getNowOrderInfoByTenant(hotelCode,credno,credtype);
		if(null == list || list.size()==0){
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_006); 
		}
		CheckinInfo dto = list.get(0);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("checkinDate", dto.getCheckinTime());
		map.put("checkoutDate", dto.getOutTime());
		map.put("roomNo", dto.getRoomNo());
		map.put("orderNo", dto.getOrderNo());
		map.put("roomPrice", dto.getRoomPrice());
		map.put("customerList", checkinInfoTenantMapper.getTenantInfoByKey(dto.getTradeNo()));
		 
		map.put("roomTypeCode", dto.getRoomTypeCode());
		map.put("roomTypeTitle", dto.getRoomTypeTitle());
		 
		int sumPrice = 0;
		if(dto.getCheckinType().equals(HotelConstant.CHECKIN_TYPE_DAILY)){
			//算入住多少晚 
			int diffday =  HotelDataUtils.differentDays(dto.getCheckinTime(), dto.getOutTime()); 
			sumPrice= dto.getRoomPrice()*diffday;
			 
		}else if(dto.getCheckinType().equals(HotelConstant.CHECKIN_TYPE_HOUR)){
			sumPrice = dto.getRoomPrice();
		}		  		 		
		//是否购买保险
		int isInsure = dto.getIsBuyInsure();
		if(isInsure==1) {
			sumPrice=sumPrice+insurePrice*dto.getCheckinNum();
		}
		map.put("totalPrice", sumPrice);
		map.put("deposit", info.getHotelDeposit());
		return ResponseVo.success(map);
    	 
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
    public ResponseVo<String> againCheckInRoom(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      
    	String orderNo = params.get("orderNo");
    
		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		CheckinInfo checkin = checkinInfoMapper.getOrderInfoByKey(hotelCode, orderNo);
		if(checkin.getCheckinType().equals(HotelConstant.CHECKIN_TYPE_HOUR)) {
			return ResponseVo.fail(ErrorCodeEnum.E0001);
		}
		String checkoutDate = params.get("checkoutDate");
		//支付码
		String qrcode = params.get("qrcode").toString();
		double testMoney = 0;
		//单晚房价
		int roomPrice = 0;
		if(null!=params.get("roomPrice")) {
			roomPrice= Integer.parseInt(params.get("roomPrice").toString());
			testMoney= testMoney+0.1;
		}		
	 
		//算入住多少晚 
		int diffday=0;
		try {
			diffday = HotelDataUtils.differentDays(checkin.getOutTime(), checkoutDate);
		} catch (ParseException e) {			 
			e.printStackTrace();
		}
		//支付方式
		String payWay= params.get("payWay").toString();
		int roomAllPrice = roomPrice*diffday;
		//是否购买保险
		int isInsure = Integer.parseInt(params.get("isInsure").toString());
		int payinsurePrice = 0;
		if(isInsure==1) {
			testMoney= testMoney+0.1;
			payinsurePrice = insurePrice*checkin.getCheckinNum();
		}
		String tradeNo = DateUtil.getCurrentDate("yyyyMMddHHmmss"+GenerateCodeUtil.generateShortUuid());
		//TODO
		//支付代码
		if(payType.equals(HotelConstant.HOTEL_PAY_TYPE_YS)){
			//支付代码
			Map<String, String> paramsMap =SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_03,prdUrl+Urls.APP_YS_PAY_RECEIVE_AGAINPAY);

			String shopdate = DateUtil.getCurrentDate("yyyyMMdd");
				
			Map<String,String> bizContent = new HashMap<>();
			bizContent.put("out_trade_no", tradeNo);
			bizContent.put("shopdate", shopdate);
			bizContent.put("scene", "bar_code");
			String bankType = "1902000";//默认微信支付
			if(payWay.equals(HotelConstant.CUSTOMER_PAY_WAY_ALIPAY)) {
				bankType = "1903000";
			}
			bizContent.put("bank_type", bankType);
			bizContent.put("auth_code", qrcode);
			if(payModel.equals("PRD")) {
				bizContent.put("total_amount",String.valueOf(roomAllPrice+payinsurePrice));
			}else {
				bizContent.put("total_amount", testMoney+"");
			}			
			bizContent.put("subject", info.getHotelName()+checkin.getRoomTypeTitle()+checkin.getRoomNo()+"续住"+diffday+"晚");
			bizContent.put("seller_id", HotelConstant.YSPAY_PARTNER_ID);
			bizContent.put("seller_name", HotelConstant.YSPAY_PARTNER_NAME);
			bizContent.put("timeout_express", "5m");//订单超时设置5分钟			
			bizContent.put("business_code", "3010002");
			paramsMap.put("biz_content", MyStringUtils.toJson(bizContent));
		    paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));
		    try{
		    	String response = Https.httpsSend(Urls.YS_Pay, paramsMap);		    	
		    	JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	JSONObject payResponse = ysResponse.getJSONObject("ysepay_online_barcodepay_response");		    	
		    	String code = payResponse.getString("code");
		    	if(code.equals("10000")) {
		    		String payTradeNo = payResponse.getString("trade_no");
		    		String payTradeStatus = payResponse.getString("trade_status");
		    		PayInfo payInfo = new PayInfo();
		    		payInfo.setPayTradeNo(payTradeNo);
		    		payInfo.setPayTradeType(payWay);
		    		payInfo.setPayTradeStatus(payTradeStatus);
		    		payInfo.setTradeNo(tradeNo);
		    		payInfo.setCreatedDate(new Date());
		    		payInfo.setDeposit(0);
		    		payInfo.setRoomPrice(roomAllPrice);
		    		payInfo.setInsurePrice(payinsurePrice);
		    		payInfo.setTradeType(HotelConstant.PAY_INFO_TRADE_TYPE_02);
		    		payInfo.setPayType(HotelConstant.PAY_INFO_PAY_TYPE_02);
		    		checkinInfoPayMapper.insert(payInfo);		
		    		
		    		AgainCheckinInfo cki = new AgainCheckinInfo();
		    		cki.setChildTradeNo(tradeNo);
		    		cki.setTradeNo(checkin.getTradeNo());
		    		cki.setCreatedDate(new Date());
		    		cki.setOutTime(checkoutDate);
		    		againCheckinInfoMapper.insert(cki);
		    		if(!payTradeStatus.equals("TRADE_SUCCESS")) {
		    			String subMsg = payResponse.getString("sub_msg");
		    			if(subMsg.contains("USERPAYING")) {
		    				return ResponseVo.fail(ErrorCodeEnum.E0002,tradeNo);
		    			}else {
		    				return ResponseVo.fail(subMsg);
		    			}
		    			
		    		}
		    		
		    		 
		    	}else {
		    		String msg = payResponse.getString("sub_msg");
		    		return ResponseVo.fail("银盛支付失败，错误信息:"+msg);
		    	}
		    }catch(Exception e) {
		    	return ResponseVo.fail("银盛支付异常，错误信息:"+e.getMessage());
		    }
		    

		}
		 return agaginCheckinAfterPay(orderNo,checkoutDate,info,payWay);
    	 
    }
    /**
     *支付后通知酒店 续住
     * @param orderNo 酒店订单号
     * @param checkoutDate 离店时间
     * @param info
     * @return
     */
    public ResponseVo<String> agaginCheckinAfterPay(String orderNo,String checkoutDate,HotelInfo info,String payWay) {
    	log.info("[酒店订单号"+orderNo+"通知酒店办理续住开始]");
    	//通知酒店续住		
		JSONObject jsonParams = new JSONObject();
		jsonParams.put("hotelCode", hotelCode);
		jsonParams.put("orderNo",orderNo);
		jsonParams.put("extendDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		jsonParams.put("checkoutDate",checkoutDate);
		jsonParams.put("payWay",payWay);
		 	 
		String url = info.getHotelSysUrl()+Urls.Hotel_AgainCheckInRoom;
    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) { 
    		JSONObject obj = JSONObject.parseObject(result.getData().toString());
    		String isSuccess   = obj.getString("isSuccess");
    		if(isSuccess.equals("true")) {
    			CheckinInfo update = checkinInfoMapper.getOrderInfoByKey(hotelCode, orderNo);   		 
				update.setOutTime(checkoutDate);				  			   			 
    			checkinInfoMapper.updateByPrimaryKeySelective(update);
    			log.info("[酒店订单号"+orderNo+"续住成功，通知酒店再次制卡]");
    			//发起制卡请求
        		JSONObject createParams = new JSONObject();
        		createParams.put("hotelCode", hotelCode);
        		createParams.put("orderNo",orderNo);
        		String createUrl = info.getHotelSysUrl()+Urls.Hotel_CreateCard;
            	ResponseVo<Object> resultCreate = HotelDataUtils.getHotelData(createUrl,info.getSecretKey(), createParams);
            	if(!resultCreate.getCode().equals(HotelConstant.SUCCESS_CODE)) {
            		return ResponseVo.fail("酒店制卡失败，错误信息:"+resultCreate.getMessage());
            	}           		             	
    			return ResponseVo.success();
    		}else {
    			return ResponseVo.fail("酒店续住失败，错误信息:"+result.getMessage());
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
     * 退房
     * @return
     */
    @Transactional
    public ResponseVo<String> checkout(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String orderNo = params.get("orderNo");
		int deposit = Integer.parseInt(params.get("deposit"));
		if(StringUtils.isBlank(orderNo)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_009);
		}	
		 
		CheckinInfo orderInfo = checkinInfoMapper.getOrderInfoByKey(hotelCode,orderNo);
		if(null == orderInfo) {
			return  ResponseVo.fail(HotelConstant.HOTEL_ERROR_011);
		}

		if(deposit>0) {
		    log.info("[流水号"+orderInfo.getTradeNo()+"担保交易退房开始]");
			PayInfo payInfo = checkinInfoPayMapper.getFirstPayInfoByKey(orderInfo.getTradeNo());
			if(deposit>payInfo.getDeposit()) {
				return ResponseVo.fail("退押金额不能大于已支付押金金额");
			}
			 PayInfo update = new PayInfo();
			//1.担保交易确认收货			 
		 	try {
		 		if(payInfo.getPayTradeStatus().equals("WAIT_BUYER_CONFIRM_GOODS")) {
		 			 log.info("[流水号"+payInfo.getTradeNo()+"担保交易确认收货开始]");
		 			JSONObject guaranteeResponse = ysReceiveService.guarantee(payInfo.getTradeNo(),payInfo.getPayTradeNo(),HotelConstant.YSPAY_METHOD_01);
					String guaranteeCode = guaranteeResponse.getString("code");
			    	if(!guaranteeCode.equals("10000")) {
			    		return ResponseVo.fail("流水号["+payInfo.getTradeNo()+"]担保交易确认收货失败，错误原因："+ guaranteeResponse.getString("sub_msg")); 
			    	} 
			    	String tradeStatus = guaranteeResponse.getString("trade_status");
			    	update.setPayTradeStatus(tradeStatus);
			    	 log.info("[流水号"+payInfo.getTradeNo()+"担保交易确认收货结束]");
		 		}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseVo.fail("流水号["+payInfo.getTradeNo()+"]担保交易确认收货异常，错误原因："+e.getMessage());
			}
			
			//2.分账
			try {
				if(StringUtils.isBlank(payInfo.getReturnCode()) || !payInfo.getReturnCode().equals("预分账成功")) {
					log.info("[流水号"+payInfo.getTradeNo()+"分账开始]");
					JSONObject divisionResponse = ysReceiveService.payDivision(payInfo);
					String returnCode = divisionResponse.getString("returnCode");
			    	 String retrunInfo = divisionResponse.getString("retrunInfo");
			    	
			    	 update.setTradeNo(payInfo.getTradeNo());
			    	 update.setReturnCode(returnCode);
			    	 update.setReturnInfo(retrunInfo);
			    	
			    	 if(!returnCode.equals("0000")){
			    		 return ResponseVo.fail("流水号["+payInfo.getTradeNo()+"]分账失败，错误原因："+ retrunInfo); 
			    	 }
			    	 log.info("[流水号"+payInfo.getTradeNo()+"分账结束]");
				}
				
		    	 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseVo.fail("流水号["+payInfo.getTradeNo()+"]分账异常，错误原因："+e.getMessage());
			}
			
			//3.退款需在预分账成功后开始，等分账回调成功后发起退款
			String outRequestNo = DateUtil.getCurrentDate("yyyyMMddHHmmss"+GenerateCodeUtil.generateShortUuid());
			update.setOutRequestNo(outRequestNo);
			update.setRefundDeposit(deposit);		
			
			checkinInfoPayMapper.updateByPrimaryKeySelective(update);
			log.info("[流水号"+payInfo.getTradeNo()+"生成退款订单]");
		}
		
		//4.续房的都分账
		 List<AgainCheckinInfo> list = againCheckinInfoMapper.getOrderInfoByTradeNo(orderInfo.getTradeNo());
		 for(AgainCheckinInfo ck:list){
			 PayInfo agInfo = checkinInfoPayMapper.getPayInfoByTradeNo(ck.getChildTradeNo());
			 try {
				  if(agInfo.getPayTradeStatus().equals("TRADE_SUCCESS")){
					  log.info("[主流水号"+orderInfo.getTradeNo()+"下的子流水号"+agInfo.getTradeNo()+"续房订单分账开始]");
					  JSONObject payResponse = ysReceiveService.payDivision(agInfo);
					  String code = payResponse.getString("code");
					  if(code.equals("10000")) {
						  String returnCode = payResponse.getString("returnCode");
					    	 String retrunInfo = payResponse.getString("retrunInfo");
					    	 PayInfo pinfo = new PayInfo();
					    	 pinfo.setTradeNo(agInfo.getTradeNo());
					    	 pinfo.setReturnCode(returnCode);
					    	 pinfo.setReturnInfo(retrunInfo);
					    	 checkinInfoPayMapper.updateByPrimaryKeySelective(pinfo);
					    	 if(!returnCode.equals("0000")){
					    		 return ResponseVo.fail("流水号["+agInfo.getTradeNo()+"]分账失败，错误原因："+ retrunInfo); 
					    	 }
					  }else {
						  return ResponseVo.fail("流水号["+agInfo.getTradeNo()+"]分账失败，错误原因："+ payResponse.getString("sub_msg"));  
					  }
					  log.info("[主流水号"+orderInfo.getTradeNo()+"下的子流水号"+agInfo.getTradeNo()+"续房订单分账结束]");
				  }							    	 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ResponseVo.fail("流水号["+agInfo.getTradeNo()+"]分账异常，错误原因："+e.getMessage());
				}
		 }
		
				
		//发起销卡请求
		log.info("[流水号"+orderInfo.getTradeNo()+"发起销卡请求开始]");
		JSONObject createParams = new JSONObject();
		createParams.put("hotelCode", hotelCode);
		createParams.put("orderNo",orderNo);
		String createUrl = info.getHotelSysUrl()+Urls.Hotel_DestroyCard;
    	ResponseVo<Object> resultCreate = HotelDataUtils.getHotelData(createUrl,info.getSecretKey(), createParams);
    	if(!resultCreate.getCode().equals(HotelConstant.SUCCESS_CODE)) {
    		return ResponseVo.fail("酒店销卡失败，错误信息:"+resultCreate.getMessage());
    	} 
    	log.info("[流水号"+orderInfo.getTradeNo()+"发起销卡请求结束]");
    	//更新退房状态
    	checkinInfoMapper.checkoutByKey(hotelCode,orderNo);
		return ResponseVo.success("退房成功");
		 
    }
    
    /**
     * 支付异常时检查支付状态
     * @param params
     * @return
     */
    public ResponseVo<JSONObject> checkPay(Map<String, String> params) {
    	String hotelCode = params.get("hotelCode");
		if (StringUtils.isBlank(hotelCode)) {
			return ResponseVo.fail(HotelConstant.HOTEL_ERROR_001);
		}
    	HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      

		if(null==info) {
			return ResponseVo.fail(HotelConstant.CONFIG_ERROR_MESSAGE);
		}
		String tradeNo = params.get("tradeNo");
		PayInfo payInfo =checkinInfoPayMapper.getPayInfoByTradeNo(tradeNo);
		if(null!=payInfo) {
		
			HotelInfo hotelInfo = hotelInfoMapper.getHotelInfoByCode(hotelCode);
			if(payInfo.getTradeType()==0) {		
				log.info("[流水号"+payInfo.getTradeNo()+"担保交易发货开始]");
				if(payInfo.getPayTradeStatus().equals("WAIT_SELLER_SEND_GOODS")) {				
					//担保交易发货
					try {							
						JSONObject guaranteeResponse = ysReceiveService.guarantee(tradeNo,payInfo.getPayTradeNo(),HotelConstant.YSPAY_METHOD_02);
		    			String guaranteeCode = guaranteeResponse.getString("code");
				    	if(!guaranteeCode.equals("10000")) {							
				    		return ResponseVo.fail("担保交易发货失败，错误原因："+guaranteeResponse.getString("msg"));						 						    	  								
				    	}  
				    	//更新担保交易状态
				    	payInfo.setPayTradeStatus(guaranteeResponse.getString("trade_status"));
				    	checkinInfoPayMapper.updateByPrimaryKeySelective(payInfo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return ResponseVo.fail("担保交易发货异常，错误原因："+e.getMessage());
					}
				}else {
					return ResponseVo.fail("支付未成功，交易可能存在延时请稍等一会再点确认");
				}	 
				log.info("[流水号"+payInfo.getTradeNo()+"担保交易发货结束]");
			}else {
				//直接交易
				if(!payInfo.getPayTradeStatus().equals("TRADE_SUCCESS")) {
					return ResponseVo.fail("支付未成功，交易可能存在延时请稍等一会再点确认");
				} 
				 
			}
			List<TenantInfo> tenantList  = checkinInfoTenantMapper.getTenantInfoByKey(tradeNo);
			List<CustomerInfoDTO> customerList = new ArrayList<CustomerInfoDTO>();
			if(null!=tenantList && tenantList.size()>0) {
				customerList = JSONObject.parseArray(JSONObject.toJSONString(tenantList), CustomerInfoDTO.class);
			}		
			if(payInfo.getPayType()==0) {
				//入住交易办理入住
				try {
					log.info("[流水号"+payInfo.getTradeNo()+"入住开始]");
					CheckinInfo checkin = checkinInfoMapper.getOrderInfoByTradeNo(tradeNo);
					return ResponseVo.success(checkInAfterPay(tradeNo, hotelInfo, checkin.getRoomNo(), checkin.getCheckinTime(), checkin.getOutTime(),checkin.getCheckinNum(),checkin.getRoomPrice(),checkin.getCardNum(),checkin.getDeposit(),customerList,payInfo.getPayTradeType()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ResponseVo.fail("入住异常，错误原因："+e.getMessage());
				}
			}else {
				//续住交易办理续住
				log.info("[流水号"+payInfo.getTradeNo()+"续住开始]");
				AgainCheckinInfo again =againCheckinInfoMapper.getOrderInfoByChildTradeNo(tradeNo);		
				CheckinInfo checkin = checkinInfoMapper.getOrderInfoByTradeNo(again.getTradeNo());
				//续住
				agaginCheckinAfterPay(checkin.getOrderNo(),again.getOutTime(),hotelInfo,payInfo.getPayTradeType());
				return ResponseVo.success();
			}
		}else {
			return ResponseVo.fail("流水号["+tradeNo+"]没有找到该笔交易");
		}
		 
    }
   
}
