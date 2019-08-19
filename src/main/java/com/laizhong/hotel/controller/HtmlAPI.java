package com.laizhong.hotel.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.dto.Auth;
import com.laizhong.hotel.dto.LoginInfoDTO;
import com.laizhong.hotel.dto.OrderParamDTO;
import com.laizhong.hotel.dto.RoomTypeInfoDTO;
import com.laizhong.hotel.dto.UserInfoDTO;
import com.laizhong.hotel.filter.LoginFilter;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.HotelRole;
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.model.YsAccount;
import com.laizhong.hotel.service.AuthService;
import com.laizhong.hotel.service.HtmlService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HtmlAPI {
	
	@Value("${frame.auth.cookie.minutes:2880}")
	private int cookieMinutes;
	@Autowired
	private HtmlService htmlService = null;
	@Autowired
	private AuthService authService = null;
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public ResponseVo<LoginInfoDTO> auth(@RequestParam("account") String account,
                                @RequestParam("pwd") String pwd,
                                HttpServletResponse response) {
		ResponseVo<LoginInfoDTO> result = authService.login(account, pwd);
		if(result.isSuccess()) {			
			Cookie cookie = new Cookie(LoginFilter.LZ_TOKEN, result.getData().getToken());
	        cookie.setPath("/");
	        cookie.setMaxAge(cookieMinutes * 60);
	        response.addCookie(cookie);
			return result;
		}else {
			return ResponseVo.fail(result.getMessage());
		}	   
    }
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
		try {
			return ResponseVo.success(htmlService.saveRoomImg(htmlService.upload(file), roomType, imgType));
		} catch (Exception e) {
			 return ResponseVo.fail(e.getMessage());
		}
	}

	/**
	 * 获取预授权码
	 * @param authCode 预授权码
	 * @return
	 */
	@PostMapping("/api/getAuthCode")
	public ResponseVo<String> getAuthCode(@RequestParam(name="authType") int authType, HttpServletRequest request) {
		log.info("[RequestParam]authType:{}", authType);
		Auth auth = authService.getAuthFormRequest(request);
		return ResponseVo.success(htmlService.getAuthCode(authType, auth.getAccountId()));
	}
	
	/**
	 * 获取该笔订单下的所有入住人信息
	 * @param orderNo 订单号
	 * @return
	 */
	@PostMapping("/api/getCheckinInfoTenant")
	public ResponseVo<List<TenantInfo>> getCheckinInfoTenant(@RequestParam(name = "tradeNo") String tradeNo) {
		log.info("[RequestParam]tradeNo:{}", tradeNo);
		return ResponseVo.success(htmlService.getCheckinInfoTenant(tradeNo));
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
	public ResponseVo<List<RoomTypeInfoDTO>> getRoom() {		
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
		try {
			return ResponseVo.success(htmlService.saveImgAndVideo(htmlService.upload(file), type));
		} catch (Exception e) {
			return ResponseVo.fail(e.getMessage());
		}
	}
	
	@PostMapping("/api/getUrl")
	public ResponseVo<?> getUrl(HttpServletRequest request) {
		Auth auth = authService.getAuthFormRequest(request);
		return ResponseVo.success(htmlService.getUrl(auth.getAccountId()));
	}
	
	@PostMapping("/api/logout")
	public ResponseVo<?> logout(HttpServletRequest request) {
		Auth auth = authService.getAuthFormRequest(request);
		authService.logout(auth.getToken());
		return ResponseVo.success(null);
	}
	
	@RequestMapping(value = "/api/uploadYsImg", method = RequestMethod.POST, consumes = "multipart/form-data")
	public  ResponseVo<String> uploadYsImg(@RequestPart MultipartFile file, HttpServletRequest request) {
		String type = request.getParameter("type");
		String merchantNo = request.getParameter("merchantNo");
		if(StringUtils.isBlank(merchantNo)){
			return ResponseVo.fail("账户编号不能为为空");
		}
		try {
			String filePath =htmlService.upload(file);
			return htmlService.saveYsAccouImg(filePath, type, merchantNo);
		} catch (Exception e) {
			e.printStackTrace();
			 return ResponseVo.fail(e.getMessage());
		} 
		 
	}
	@RequestMapping(value = "/api/saveYsApplyInfo", method = RequestMethod.POST)
	public  ResponseVo<String> saveYsApplyInfo(@RequestBody YsAccount info) { 
		try {			 
			return htmlService.saveYsApplyInfo(info);
		} catch (Exception e) {
			e.printStackTrace();
			 return ResponseVo.fail(e.getMessage());
		} 		 
	}
	@RequestMapping(value = "/api/getYsApplyInfo", method = RequestMethod.POST)
	public  ResponseVo<JSONObject> getYsApplyInfo() { 
		try {			 
			return htmlService.getYsApplyInfo();
		} catch (Exception e) {
			e.printStackTrace();
			 return ResponseVo.fail(e.getMessage());
		} 		 
	}
	@RequestMapping(value = "/api/applyYsMerchant", method = RequestMethod.POST)
	public  ResponseVo<String> applyYsMerchant(@RequestParam(name = "merchantNo") String merchantNo) { 
		try {			 
			return htmlService.applyYsMerchant(merchantNo);
		} catch (Exception e) {
			e.printStackTrace();
			 return ResponseVo.fail(e.getMessage());
		} 		 
	}
	 
    public static void main(String[] args) throws Exception {
        
       File file = new File("E:\\other\\涞众\\支付\\银盛\\行号行名列表20190719更新.xlsx");
        InputStream inputStream  = new FileInputStream(file);
        List<List<Object>> list = getBankListByExcel(inputStream, "行号行名列表20190719更新.xlsx");
        inputStream.close();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            sb.append("insert into bank_type(bank_type,bank_short_name,bank_name) values('"+lo.get(0)+"','"+lo.get(1)+"','"+lo.get(2)+"');");
        }
         System.out.println(sb.toString());
    }
	 public static List getBankListByExcel(InputStream in, String fileName) throws Exception {
	        List list = new ArrayList<>();
	        //创建Excel工作薄
	        Workbook work = getWorkbook(in, fileName);
	        if (null == work) {
	            throw new Exception("创建Excel工作薄为空！");
	        }
	        Sheet sheet = null;
	        Row row = null;
	        Cell cell = null;

	        for (int i = 0; i < work.getNumberOfSheets(); i++) {
	            sheet = work.getSheetAt(i);
	            if (sheet == null) {
	                continue;
	            }

	            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
	                row = sheet.getRow(j);
	                if (row == null || row.getFirstCellNum() == j) {
	                    continue;
	                }

	                List<Object> li = new ArrayList<>();
	                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
	                    cell = row.getCell(y);
	                    li.add(cell);
	                }
	                list.add(li);
	            }
	        }
	        work.close();
	        return list;
	    }

	    /**
	     * 判断文件格式
	     *
	     * @param inStr
	     * @param fileName
	     * @return
	     * @throws Exception
	     */
	    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
	        Workbook workbook = null;
	        String fileType = fileName.substring(fileName.lastIndexOf("."));
	        if (".xls".equals(fileType)) {
	            workbook = new HSSFWorkbook(inStr);
	        } else if (".xlsx".equals(fileType)) {
	            workbook = new XSSFWorkbook(inStr);
	        } else {
	            throw new Exception("请上传excel文件！");
	        }
	        return workbook;
	    }

}
