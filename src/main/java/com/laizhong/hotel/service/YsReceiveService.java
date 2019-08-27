package com.laizhong.hotel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.controller.Urls;
import com.laizhong.hotel.dto.CustomerInfoDTO;
import com.laizhong.hotel.mapper.AgainCheckinInfoMapper;
import com.laizhong.hotel.mapper.CheckinInfoMapper;
import com.laizhong.hotel.mapper.CheckinInfoPayMapper;
import com.laizhong.hotel.mapper.CheckinInfoTenantMapper;
import com.laizhong.hotel.mapper.HotelInfoMapper;
import com.laizhong.hotel.mapper.YsAccountMapper;
import com.laizhong.hotel.model.AgainCheckinInfo;
import com.laizhong.hotel.model.CheckinInfo;
import com.laizhong.hotel.model.HotelInfo;
import com.laizhong.hotel.model.PayInfo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.model.YsAccount;
import com.laizhong.hotel.pay.ys.utils.DateUtil;
import com.laizhong.hotel.pay.ys.utils.Https;
import com.laizhong.hotel.pay.ys.utils.MyStringUtils;
import com.laizhong.hotel.pay.ys.utils.SignUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class YsReceiveService {
	
	  @Autowired
	    private CheckinInfoPayMapper checkinInfoPayMapper = null;
	    @Autowired
		private YsAccountMapper ysAccountMapper = null;
		@Value("${hotel.prd.url}")
		private String prdUrl; //正式环境地址
		@Value("${hotel.hotelCode}")
		private String hotelCode;
		@Value("${hotel.insurance.ys.account}") //保险公司子商户号
		private String insuranceUserCode;
		@Value("${hotel.yspay.pri.cert.path}")
		private String ysPriCertPath; //银盛证书私钥路径
		@Value("${hotel.yspay.pub.cert.path}")
		private String ysPubCertPath; //银盛证书公钥路径
		@Value("${hotel.pay.model}")
		private String payModel; //支付环境
		@Autowired
		private AppDataService appDataService = null;		  
		@Autowired
		private HotelInfoMapper hotelInfoMapper = null;
		@Autowired
		private CheckinInfoMapper checkinInfoMapper = null;
	    @Autowired
	    private CheckinInfoTenantMapper checkinInfoTenantMapper = null;
	    @Autowired
	    private AgainCheckinInfoMapper againCheckinInfoMapper = null;
		/**
		 * 支付回调处理
		 * @param payTradeNo 支付流水号
		 * @param myTradeNo 我方订单号
		 * @param tradeStatus 交易状态
		 */
		public void payReceive(String payTradeNo,String myTradeNo,String tradeStatus,boolean isFirstPay) {
			PayInfo info =checkinInfoPayMapper.getPayInfoByKey(payTradeNo);
			if(null!=info) {
				//避免重复分账
				if(tradeStatus.equals("TRADE_SUCCESS") && !info.getPayTradeStatus().equals("TRADE_SUCCESS")) {					
					payDivision(myTradeNo,info.getDeposit()+info.getRoomPrice(),info.getInsurePrice());
					HotelInfo hotelInfo = hotelInfoMapper.getHotelInfoByCode(hotelCode); 					
					try {
						if(isFirstPay) {
							CheckinInfo checkin = checkinInfoMapper.getOrderInfoByTradeNo(myTradeNo);
							List<TenantInfo> tenantList  = checkinInfoTenantMapper.getTenantInfoByKey(myTradeNo);
							List<CustomerInfoDTO> customerList = new ArrayList<CustomerInfoDTO>();
							if(null!=tenantList && tenantList.size()>0) {
								customerList = JSONObject.parseArray(JSONObject.toJSONString(tenantList), CustomerInfoDTO.class);
							}
							//办理入住
							appDataService.checkInAfterPay(myTradeNo, hotelInfo, checkin.getRoomNo(), checkin.getCheckinTime(), checkin.getOutTime(),checkin.getCheckinNum(),checkin.getRoomPrice(),checkin.getCardNum(),checkin.getDeposit(),customerList );
							
						}else {
							AgainCheckinInfo again =againCheckinInfoMapper.getOrderInfoByChildTradeNo(myTradeNo);
							CheckinInfo checkin = checkinInfoMapper.getOrderInfoByTradeNo(again.getTradeNo());
							//续住
							appDataService.agaginCheckinAfterPay(checkin.getOrderNo(),again.getOutTime(),hotelInfo);
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				info.setPayTradeStatus(tradeStatus);
				checkinInfoPayMapper.updateByPrimaryKeySelective(info);
				
			}
		}
		
	 /**
     * 分账
     * @param tradeNo 我方订单号
     * @param hotelPrice 酒店金额
     * @param insurePrice 保险金额
     * @return
     * @throws Exception
     */
    public void payDivision(String tradeNo,int hotelPrice,int insurePrice ) {
    	try {
	    	YsAccount params = new YsAccount();
			params.setHotelCode(hotelCode);		 		 
			YsAccount exist =ysAccountMapper.getYsAccount(params); 
	    	Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_04,prdUrl+Urls.APP_YS_PAY_RECEIVE_DIVISION);
	        Map<String,Object> bizContent = new HashMap<>();
	        bizContent.put("out_trade_no", tradeNo);
	        bizContent.put("payee_usercode", HotelConstant.YSPAY_PARTNER_ID);
	       
	        bizContent.put("is_divistion", "01");
	        bizContent.put("is_again_division", "Y");
	        bizContent.put("division_mode", "02");
	        List<Map<String,String>> divList = new ArrayList<Map<String,String>>();
	        Map<String,String> hotelMap = new HashMap<String,String>();
	        hotelMap.put("division_mer_usercode", exist.getUserCode());
	        if(payModel.equals("PRD")) {
	        	 bizContent.put("total_amount",String.valueOf(hotelPrice+insurePrice));
	        	 hotelMap.put("div_amount", String.valueOf(hotelPrice));
	        }else {
	        	if(insurePrice>0){
	        		bizContent.put("total_amount",String.valueOf("0.3"));
	        	}else {
	        		bizContent.put("total_amount",String.valueOf("0.2"));
	        	}	        	 
	        	hotelMap.put("div_amount", String.valueOf("0.2"));
	        }
	       
	        hotelMap.put("is_chargeFee", "01");
	        divList.add(hotelMap);
	        if(insurePrice>0){
	        	//保险账
	        	 Map<String,String> insureMap = new HashMap<String,String>();
	        	 insureMap.put("division_mer_usercode", insuranceUserCode);
	        	 if(payModel.equals("PRD")) {
	        		 insureMap.put("div_amount", String.valueOf(insurePrice));
	        	 }else {
	        		 insureMap.put("div_amount", String.valueOf("0.1"));
	        	 }	        	
	        	insureMap.put("is_chargeFee", "01");
	 	        divList.add(insureMap);
	        }
	        bizContent.put("div_list", divList);
	        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
	        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		
				 String response = Https.httpsSend(Urls.YS_Pay_Division,paramsMap);
				 log.info("[分账结果返回消息===={}]",response);
				 JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	 JSONObject payResponse = ysResponse.getJSONObject("ysepay_single_division_online_accept_response");		    	
		    	 String returnCode = payResponse.getString("returnCode");
		    	 String retrunInfo = payResponse.getString("retrunInfo");
		    	 PayInfo info = new PayInfo();
		    	 info.setTradeNo(tradeNo);
		    	 info.setReturnCode(returnCode);
		    	 info.setReturnInfo(retrunInfo);
		    	 checkinInfoPayMapper.updateByPrimaryKeySelective(info);
			} catch (Exception e) {
				e.printStackTrace();
							 
			}
	}
    /**
     * 分账回调
     */
    public void divisionReceive(String tradeNo,String divisionStatus,String divisionCode) {
    	PayInfo info = checkinInfoPayMapper.getPayInfoByTradeNo(tradeNo);
    	if(null!=info) {
    		info.setReturnCode(divisionStatus);
    		//分账成功押金担保交易开始
    		if(divisionCode.equals("02") && !info.getReturnCode().equals("预分账成功")) {
    			if(info.getDeposit()>0) {
					guarantee(info.getPayTradeNo(),tradeNo,HotelConstant.YSPAY_METHOD_02);
				}
    		}
    		checkinInfoPayMapper.updateByPrimaryKeySelective(info);
    	}
    }
    
    /**
     * 押金，担保交易
     * @param tradeNo 我方订单号
     * @param payTradeNo 交易流水号
     * @param method 接口方法名
     */
    public void guarantee(String tradeNo,String payTradeNo,String method ) {
    	try {    	 
	    		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(method,null);	        
		        String shopdate = tradeNo.substring(0,8);
		        Map<String,Object> bizContent = new HashMap<>();
		        bizContent.put("out_trade_no", tradeNo);
		        bizContent.put("shopdate", shopdate);
		        bizContent.put("trade_no",payTradeNo);	  
		        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
		        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		
				 String response = Https.httpsSend(Urls.YS_Guarantee,paramsMap);
				 log.info("[担保交易结果返回消息===={}]",response);
				 JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	 JSONObject payResponse = ysResponse.getJSONObject("ysepay_online_trade_delivered_response");		    	
		    	 String code = payResponse.getString("code");
		    	 if(code.equals("10000")) {
		    		 
		    	 }else {
		    		 
		    	 }
			} catch (Exception e) {
				e.printStackTrace();
							 
			}
	}
    
    public void refund(String tradeNo,String payTradeNo ) {
    	try {    	 
	    		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_05,null);	        
		        String shopdate = tradeNo.substring(0,8);
		        Map<String,Object> bizContent = new HashMap<>();
		        bizContent.put("out_trade_no", tradeNo);
		        bizContent.put("shopdate", shopdate);
		        bizContent.put("trade_no",payTradeNo);	  
		        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
		        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		
				 String response = Https.httpsSend(Urls.YS_Guarantee,paramsMap);
				 log.info("[担保交易结果返回消息===={}]",response);
				 JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	 JSONObject payResponse = ysResponse.getJSONObject("ysepay_single_division_online_accept_response");		    	
		    	 String code = payResponse.getString("code");
		    	 if(code.equals("10000")) {
		    		 
		    	 }else {
		    		 
		    	 }
			} catch (Exception e) {
				e.printStackTrace();
							 
			}
	}
}
