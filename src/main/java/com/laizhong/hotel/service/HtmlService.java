package com.laizhong.hotel.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.dto.AccountDto;
import com.laizhong.hotel.dto.OrderParamDTO;
import com.laizhong.hotel.dto.RoomTypeInfoDTO;
import com.laizhong.hotel.dto.UserInfoDTO;
import com.laizhong.hotel.mapper.AccountMapper;
import com.laizhong.hotel.mapper.AccountRoleMapper;
import com.laizhong.hotel.mapper.AuthorizeMapper;
import com.laizhong.hotel.mapper.CheckinInfoMapper;
import com.laizhong.hotel.mapper.CheckinInfoTenantMapper;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.mapper.HotelRoleMapper;
import com.laizhong.hotel.mapper.RoomImageMapper;
import com.laizhong.hotel.mapper.RoomInfoMapper;
import com.laizhong.hotel.mapper.YsAccountImageMapper;
import com.laizhong.hotel.mapper.YsAccountMapper;
import com.laizhong.hotel.model.Account;
import com.laizhong.hotel.model.AccountRole;
import com.laizhong.hotel.model.Authorize;
import com.laizhong.hotel.model.CheckinInfo;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.HotelRole;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.model.YsAccount;
import com.laizhong.hotel.model.YsAccountImage;
import com.laizhong.hotel.pay.ys.utils.DateUtil;
import com.laizhong.hotel.pay.ys.utils.Https;
import com.laizhong.hotel.pay.ys.utils.MyStringUtils;
import com.laizhong.hotel.pay.ys.utils.SignUtils;
import com.laizhong.hotel.pay.ys.utils.SrcDesUtil;
import com.laizhong.hotel.utils.FileUtil;
import com.laizhong.hotel.utils.GenerateCodeUtil;
import com.laizhong.hotel.utils.HotelDataUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HtmlService {
	
	@Value("${hotel.img.classPath}")
	private String classPath;
	@Value("${hotel.img.imagePath}")
	private String imagePath;
	@Value("${hotel.hotelCode}")
	private String hotelCode;
	@Value("${hotel.yspay.pri.cert.path}")
	private String ysPriCertPath; //银盛证书私钥路径
	@Value("${hotel.yspay.pub.cert.path}")
	private String ysPubCertPath; //银盛证书公钥路径
	@Value("${hotel.prd.url}")
	private String prdUrl; //正式环境地址
	
	@Autowired
	private HotelRoleMapper hotelRoleMapper = null;
	@Autowired
	private AuthorizeMapper authorizeMapper = null;
	@Autowired
	private CheckinInfoTenantMapper checkinInfoTenantMapper = null;
	@Autowired
	private CheckinInfoMapper checkinInfoMapper = null;
	@Autowired
	private AccountMapper accountMapper = null;
	@Autowired
	private AccountRoleMapper accountRoleMapper = null;
	@Autowired
	private RoomInfoMapper roomInfoMapper = null;
	@Autowired
	private RoomImageMapper roomImageMapper = null;
	@Autowired
	private HotelInfoMapper hotelInfoMapper = null;
	@Autowired
	private YsAccountImageMapper ysAccountImageMapper = null;
 
	@Autowired
	private YsAccountMapper ysAccountMapper = null;
	public List<HotelRole> getRoleList() {
		log.info("roleList:{}" , hotelRoleMapper.getHotelRoleList());
		return hotelRoleMapper.getHotelRoleList();
	}
	
	/**
	 * 预授权码
	 * @param authType
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	public String getAuthCode(int authType, String auth) {
		Authorize authorize = new Authorize();
		authorize.setAuthCode(GenerateCodeUtil.generateShortUuid());
		authorize.setCreatedDate(new Date());
		authorize.setAuthType(authType);
		authorize.setStatus(HotelConstant.AUTHORIZE_STATUS_UNUSED);
		authorize.setHotelCode(hotelCode);
		authorize.setCreatedBy(auth);
		log.info("authorize:" + authorize);
		authorizeMapper.insertAuthorize(authorize);
		return authorize.getAuthCode();
	}
	
	/**
	 * 获取该笔订单下的所有入住人信息
	 * @param orderNo 订单号
	 * @param hotelCode 酒店编号
	 * @return
	 */
	public List<TenantInfo> getCheckinInfoTenant(String tradeNo) {
		return checkinInfoTenantMapper.getTenantInfoByKey(tradeNo);
	}
	
	/**
	 * 订单列表
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageInfo<CheckinInfo> getOrderList(OrderParamDTO order) {
		PageHelper.startPage(order.getPageNum(), order.getPageSize());
		List<CheckinInfo> checkinInfo = checkinInfoMapper.getCheckinInfoList(order);
		PageInfo<CheckinInfo> pageInfo = new PageInfo<>(checkinInfo);
		log.info("\n checkinInfo:{} \n pageInfo:{}", checkinInfo, pageInfo);
		return pageInfo;
	}
	
	/**
	 * 创建用户
	 * @param userInfo
	 */
	@Transactional(rollbackFor = {Exception.class})
	public String createUser(UserInfoDTO userInfo) {
		AccountRole accountRole = new AccountRole();
		Account account = new Account();
		Account existaccount = accountMapper.getAccountById(userInfo.getAccount());
		if (null != existaccount) {
			return "账号已存在！";
		}
		account.setAccountId(userInfo.getAccount());
		account.setAccountName(userInfo.getUsername());
		account.setAccountPwd(userInfo.getPassword());
		account.setCreatedDate(new Date());
		log.info("account:{}", account);
		accountMapper.insertAccount(account);
		Arrays.stream(userInfo.getRole()).forEach((roleId) -> {
			accountRole.setAccountId(account.getAccountId());
			accountRole.setRoleId(roleId);
			accountRole.setCreatedDate(account.getCreatedDate());
			log.info("accountRole:{}", accountRole);
			accountRoleMapper.insertAccountRole(accountRole);
		});
		return "创建用户成功！";
	}
	
	/**
	 * 用户列表
	 * @param accountName
	 * @return
	 */
	public PageInfo<AccountDto> getAccount(String accountName, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<AccountDto> AccountDtos = new ArrayList<>();
		List<Account> accounts = accountMapper.getAccountsByName(accountName);
		PageInfo<Account> pageInfo = new PageInfo<>(accounts);
		log.info("\n accounts:{} \n", accounts);
		accounts.stream().forEach((account) -> {
			AccountDto accountDto = new AccountDto(account);
			List<AccountRole> accountRoles = accountRoleMapper.getAccountRoles(account.getAccountId());
			accountDto.setAccountRoles(accountRoles);
			AccountDtos.add(accountDto);
		});
		PageInfo<AccountDto> AccountDtoPageInfo = new PageInfo<>(AccountDtos);
		AccountDtoPageInfo.setPages(pageInfo.getPages());
		log.info("\n AccountDtos:{} \n pageInfo:{}", AccountDtos, pageInfo);
		return AccountDtoPageInfo;
	}
	
	/**
	 * 获取房间类型
	 * @return
	 */
	public List<RoomTypeInfoDTO> getRoom() {
		List<RoomTypeInfoDTO> list = new ArrayList<RoomTypeInfoDTO>();
		HotelInfo info = hotelInfoMapper.getHotelInfoByCode(hotelCode);      
		if(null!=info) {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put("hotelCode", hotelCode);
			String url = info.getHotelSysUrl()+Urls.Hotel_GetRoomType;
	    	ResponseVo<Object> result = HotelDataUtils.getHotelData(url,info.getSecretKey(), jsonParams);
	    	if(result.getCode().equals(HotelConstant.SUCCESS_CODE)) {
	    		 list = JSONObject.parseArray(result.getData().toString(), RoomTypeInfoDTO.class);	    		
	    	}
		}		
    	return list;
	}
	
	/**
	 * 系统管理-上传房型图片
	 * @param roomTypeCode
	 * @return
	 */
	public RoomInfo getRoomInfo(String roomTypeCode) {
		//RoomDTO roomDto = null;
		RoomInfo info = new RoomInfo();
		info.setHotelCode(hotelCode);
		info.setRoomTypeCode(roomTypeCode);
		List<RoomInfo> roomInfos = roomInfoMapper.getRoomInfoByModelSelective(info);
//		if (!roomInfos.isEmpty() && null != roomInfos.get(0)) {
//			RoomImage roomImage = new RoomImage();
//			roomImage.setHotelCode(hotelCode);
//			roomImage.setRoomTypeCode(roomTypeCode);
//			List<RoomImage> roomImages = roomImageMapper.getRoomTypeInfoByModelSelective(roomImage);
//			roomDto = new RoomDTO(roomInfos.get(0));
//			roomDto.setRoomImg(roomImages);
//		}
		return roomInfos.get(0);
	}
	
	
	public List<RoomImage> getRoomImage(String roomTypeCode) {
		RoomImage roomImage = new RoomImage();
		roomImage.setRoomTypeCode(roomTypeCode);
		roomImage.setHotelCode(hotelCode);
		List<RoomImage> roomImages = roomImageMapper.getRoomTypeInfoByModelSelective(roomImage);
		return roomImages;
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public String saveRoomImg(String img, String roomType, int imageType) {
		RoomImage params = new RoomImage();
		params.setHotelCode(hotelCode);
		params.setRoomTypeCode(roomType);
		params.setImageType(imageType);
		params.setRoomTypeImage(img);
		if (img.isEmpty()) {
			return "上传失败！";
		}
		//首图只传一张
		if (HotelConstant.ROOM_IMAGE_TYPE_FIRST == imageType) {
			List<RoomImage> RoomImages = roomImageMapper.getRoomTypeInfoByModelSelective(params);
			if (!RoomImages.isEmpty()) {
				return "首图只能上传一张!";
			}
		}
		roomImageMapper.insertRoomImage(params);
		return "上传成功！";
	}
	
	public String upload(MultipartFile file) throws Exception {
		try {
			if(!file.isEmpty()){
				String fileName = file.getOriginalFilename();
				String filetype = fileName.substring(fileName.lastIndexOf("."));
				String tempName = UUID.randomUUID() + filetype;
				FileUtil.createDirIfNotExists(FileUtil.getCurrentPath() + classPath + imagePath);
				File des = new File(FileUtil.getCurrentPath() + classPath + imagePath + tempName);
				log.info("\n--------------------->[path]"+ des.getPath());
				byte[] bytes = file.getBytes();
				FileUtil.saveByteArrayToFile(des, bytes);
				return imagePath + tempName;
			}
		} catch (IOException e) {
			log.error("[上传失败,错误原因->{}",e);
			throw e;
		}
		return "";
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public String saveRoomInfo(RoomInfo roomInfo) {
		roomInfo.setHotelCode(hotelCode);
		roomInfo.setCreatedDate(new Date());
		List<RoomInfo> RoomInfos = roomInfoMapper.getRoomInfoByModelSelective(roomInfo);
		if (RoomInfos.isEmpty()) {
			roomInfoMapper.insertRoomInfo(roomInfo);
		} else {
			roomInfoMapper.updateRoomInfo(roomInfo);
		}
		return "保存成功！";
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public String deleteImg(int id) {
		roomImageMapper.deleteRoomImage(id);
		return "删除成功！";
	}
	
	public HotelInfo getHotelInfo() {
		return hotelInfoMapper.getHotelInfoByCode(hotelCode);
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public String saveHotelInfo(HotelInfo hotelInfo) {
		HotelInfo hi = hotelInfoMapper.getHotelInfoByCode(hotelCode);
		if (null == hi) {
			hotelInfo.setHotelCode(hotelCode);
			hotelInfoMapper.insertHotelInfo(hotelInfo);
		} else {
			hotelInfoMapper.updateHotelInfo(hotelInfo);
		}
		return "保存成功！";
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public String saveImgAndVideo(String path, String type) {
		if (path.isEmpty()) {
			return "上传失败！";
		}
		HotelInfo hotelInfo = hotelInfoMapper.getHotelInfoByCode(hotelCode);
		if (HotelConstant.HOTEL_IMAGE.equals(type)) {
			hotelInfo.setHotelImage(path);
		} else if (HotelConstant.HOTEL_VIDEO.equals(type)) {
			hotelInfo.setHotelVideoUrl(path);
		} else if (HotelConstant.HOTEL_QRCODE.equals(type)) {
			hotelInfo.setHotelQrcode(path);
		}
		hotelInfoMapper.updateHotelInfo(hotelInfo);
		return "上传成功！";
	}
	
	public UserInfoDTO getUrl(String accountId) {
		UserInfoDTO info = new UserInfoDTO();
		info.setAccount(accountId);
		info.setRole(new String[] {"R03"});
		List<String> roleids = accountRoleMapper.getAccountRoleIds(accountId);
		for (String roleid : roleids) {
			if ("R01".equals(roleid)) {
				info.setRole(new String[] {"R01"});
				break;
			} else if ("R02".equals(roleid)) {
				info.setRole(new String[] {"R02"});
				break;
			}
		}
		return info;
	}
	
	@Transactional
	public ResponseVo<String> saveYsAccouImg(String path, String type,String merchantNo) {
		YsAccountImage params = new YsAccountImage();		
		params.setImgType(type);		
		params.setMerchantNo(merchantNo);		
		List<YsAccountImage> list =ysAccountImageMapper.getImageByModelSelective(params);		
		params.setImgUrl(path);
		if(null!=list && list.size()>0){			
			ysAccountImageMapper.updateUrlById(list.get(0).getId(), path);
		}else{
			ysAccountImageMapper.insert(params);
		}
		return ResponseVo.success(path);
		 
	}
	@Transactional
	public ResponseVo<String> saveYsApplyInfo(YsAccount info) {
		YsAccount params = new YsAccount();
		params.setHotelCode(hotelCode);
		params.setMerchantNo(info.getMerchantNo());
		YsAccount exist =ysAccountMapper.getYsAccount(params);		
		 
		if(null!=exist){
			ysAccountMapper.updateByPrimaryKeySelective(info);
		}else{
			info.setHotelCode(hotelCode);
			info.setMerFlag("11");//默认为普通商户
			info.setCustType("B");//只支持企业注册	
			info.setCreatedDate(new Date());
			ysAccountMapper.insert(info);
			
		}
		return ResponseVo.success();
		 
	}
	public ResponseVo<JSONObject> getYsApplyInfo() {
		YsAccount params = new YsAccount();
		params.setHotelCode(hotelCode);		 
		JSONObject obj = new JSONObject();
		YsAccount exist =ysAccountMapper.getYsAccount(params);
		if(null!= exist){
			YsAccountImage image = new YsAccountImage();
			image.setMerchantNo(exist.getMerchantNo());
			List<YsAccountImage> imgs = ysAccountImageMapper.getImageByModelSelective(image);
			obj.put("imgs", imgs);
			if(StringUtils.isNotBlank(exist.getUserStatus()) || StringUtils.isNotBlank(exist.getCustStatue())){
				if(!exist.getUserStatus().equals("生效") || !exist.getCustStatue().equals("生效")) {
					try {
						String queryResult = getApplyResult(exist.getUserCode());
						JSONObject ysResponse = JSONObject.parseObject(queryResult);
						JSONObject regResponse = ysResponse.getJSONObject("ysepay_merchant_register_query_response");
						String code = regResponse.getString("code");
				    	if(!code.equals("10000")) {
				    		 String msg = regResponse.getString("sub_msg");
				    		 log.error("[查询注册信息失败，错误原因=={}]",msg);
				    	}else {
				    		YsAccount update = new YsAccount();
					    	update.setHotelCode(hotelCode);
					    	update.setMerchantNo(exist.getMerchantNo());				    	 
					    	update.setUserStatus(regResponse.getString("user_status"));
					    	update.setCustStatue(regResponse.getString("cust_status"));
					    	ysAccountMapper.updateByPrimaryKeySelective(update);
					    	
					    	exist.setUserStatus(regResponse.getString("user_status"));
					    	exist.setCustStatue(regResponse.getString("cust_status"));
				    	}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}		
		obj.put("info", exist);		
		return ResponseVo.success(obj);
		 
	}
	public ResponseVo<String> applyYsMerchant(String merchantNo) {				
		YsAccount params = new YsAccount();
		params.setHotelCode(hotelCode);		
		params.setMerchantNo(merchantNo);		
		YsAccount exist =ysAccountMapper.getYsAccount(params);
		if(null!= exist){
			try {
				String token = getToken();
				YsAccountImage image = new YsAccountImage();
				image.setMerchantNo(exist.getMerchantNo());
				List<YsAccountImage> imgs = ysAccountImageMapper.getImageByModelSelective(image);
				
				for(YsAccountImage img:imgs) {
					Map<String,String> parmar = new HashMap<String,String>();
					parmar.put("picType", img.getImgType());
					parmar.put("token", token);
					parmar.put("superUsercode", HotelConstant.YSPAY_PARTNER_ID);
					String url = img.getImgUrl().substring(1);
					ClassPathResource cpr = new ClassPathResource(url);					 
					File file = cpr.getFile();
					String response = Https.sendHttpMessage(Urls.YS_Upload, parmar, file);
					log.info("[上传"+merchantNo+"的图片结果=={}]",response);					 
				}	
				String registerResult = register(token,exist);
				JSONObject ysResponse = JSONObject.parseObject(registerResult);
				JSONObject regResponse = ysResponse.getJSONObject("ysepay_merchant_register_accept_response");
				String code = regResponse.getString("code");
		    	if(!code.equals("10000")) {
		    		 String msg = regResponse.getString("sub_msg");
		    		 return ResponseVo.fail("银盛错误信息："+msg);
		    	} 
		    	YsAccount update = new YsAccount();
		    	update.setHotelCode(hotelCode);
		    	update.setMerchantNo(merchantNo);
		    	update.setUserCode(regResponse.getString("usercode"));
		    	update.setCustId(regResponse.getString("custid"));
		    	update.setUserStatus(regResponse.getString("user_status"));
		    	ysAccountMapper.updateByPrimaryKeySelective(update);
		    	
											
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseVo.fail("上传文件失败");
			}			
		}else {
			return ResponseVo.fail("找不到"+merchantNo+"的注册信息");
		}					
		return ResponseVo.success();
		 
	}
	
	
	public String getToken() throws Exception {
		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_07,prdUrl+Urls.APP_YS_PAY_RECEIVE_COMMON);
        Map<String,String> bizContent = new HashMap<>();
        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		try {
			 String response = Https.httpsSend(Urls.YS_Register,paramsMap);
			JSONObject jsonObj = JSONObject.parseObject(response);		        
	        JSONObject ysepay_merchant_register_token_get_response =  jsonObj.getJSONObject("ysepay_merchant_register_token_get_response");
	        return (String) ysepay_merchant_register_token_get_response.get("token");
		} catch (Exception e) {
			e.printStackTrace();
			 throw e;				 
		}
	}
	public String getApplyResult(String usercode) throws Exception {
		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_08,prdUrl+Urls.APP_YS_PAY_RECEIVE_COMMON);	   
        Map<String,String> bizContent = new HashMap<>();
        bizContent.put("usercode", usercode);
        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		try {
			 String response = Https.httpsSend(Urls.YS_Register,paramsMap);
			 log.info("[查询银盛注册结果返回消息===={}]",response);
			 return response;
		} catch (Exception e) {
			e.printStackTrace();
			 throw e;				 
		}
	}
	/**
	 * 注册银盛子商户
	 * @param token
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public String register(String token,YsAccount info) throws Exception{
		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_06,prdUrl+Urls.APP_YS_PAY_RECEIVE_COMMON);       
        Map<String,String> bizContent = new HashMap<>();
        bizContent.put("merchant_no",info.getMerchantNo());
        bizContent.put("cust_type","B");      //企业
        bizContent.put("token",token);
        bizContent.put("another_name",info.getAnotherName());
        bizContent.put("cust_name",info.getCustName());
        bizContent.put("industry",info.getIndustry());
        bizContent.put("province",info.getProvince());
        bizContent.put("city",info.getCity());
        bizContent.put("company_addr",info.getCompanyAddr());
        bizContent.put("legal_name",info.getLegalName());      //企业法人名字,小微商户可空
        bizContent.put("legal_tel",info.getLegalTel());      //企业法人手机号
        bizContent.put("legal_cert_type",info.getLegalCertType());
        bizContent.put("legal_cert_no",SrcDesUtil.encryptData(HotelConstant.YSPAY_PARTNER_ID,info.getLegalCertNo()));
        bizContent.put("bus_license",info.getBusLicense());        //营业执照,个体商户、企业户时为必填
        if(StringUtils.isNotBlank(info.getBusLicenseExpire())) {
        	  bizContent.put("bus_license_expire",info.getBusLicenseExpire());      //营业执照有效期，客户类型为个体商户、企业商户时为必填
        }
       
        bizContent.put("settle_type","1");      //银行卡账户
        bizContent.put("bank_account_no",info.getBankAccountNo());
        bizContent.put("bank_account_name",info.getBankAccountName());
        bizContent.put("bank_account_type",info.getBankAccountType()); 
        bizContent.put("bank_card_type",info.getBankCardType()); 

        bizContent.put("bank_name",info.getBankName());
        bizContent.put("bank_type",info.getBankType());
        bizContent.put("bank_province",info.getBankProvince());
        bizContent.put("bank_city",info.getBankCity());
        bizContent.put("cert_type","00");       //目前只支持00，00是身份证
        bizContent.put("cert_no",SrcDesUtil.encryptData(HotelConstant.YSPAY_PARTNER_ID,info.getCertNo()));
        bizContent.put("bank_telephone_no",info.getBankTelephoneNo());
        bizContent.put("sub_account_flag", "Y"); //参与分账
        bizContent.put("org_no", "6240000010");
        
        paramsMap.put("biz_content", MyStringUtils.toJson(bizContent));
        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));

        String response = Https.httpsSend(Urls.YS_Register,paramsMap);  
        log.info("[注册子商户返回信息==={}]",response);
        return response;
	}
}
