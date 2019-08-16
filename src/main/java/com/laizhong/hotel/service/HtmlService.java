package com.laizhong.hotel.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	public String upload(MultipartFile file) {
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
}
