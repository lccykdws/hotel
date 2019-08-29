package com.laizhong.hotel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
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
import com.laizhong.hotel.model.ResponseVo;
import com.laizhong.hotel.model.TenantInfo;
import com.laizhong.hotel.model.YsAccount;
import com.laizhong.hotel.pay.ys.utils.DateUtil;
import com.laizhong.hotel.pay.ys.utils.Https;
import com.laizhong.hotel.pay.ys.utils.MyStringUtils;
import com.laizhong.hotel.pay.ys.utils.SignUtils;
import com.laizhong.hotel.utils.GenerateCodeUtil;

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
		 * 入住支付回调处理
		 * @param payTradeNo 支付流水号
		 * @param myTradeNo 我方订单号
		 * @param tradeStatus 交易状态
		 */
		public String payReceive1(String payTradeNo,String myTradeNo,String tradeStatus) {
			PayInfo info =checkinInfoPayMapper.getPayInfoByKey(payTradeNo);
			if(null!=info) {
				//担保交易	
				if(tradeStatus.equals("WAIT_SELLER_SEND_GOODS") && !info.getPayTradeStatus().equals("WAIT_SELLER_SEND_GOODS")) {
					if(info.getDeposit()>0) {
						//担保交易发货
						try {							
							JSONObject guaranteeResponse = guarantee(myTradeNo,payTradeNo,HotelConstant.YSPAY_METHOD_02);
			    			String guaranteeCode = guaranteeResponse.getString("code");
					    	if(guaranteeCode.equals("10000")) {
					    		HotelInfo hotelInfo = hotelInfoMapper.getHotelInfoByCode(hotelCode); 								 
								CheckinInfo checkin = checkinInfoMapper.getOrderInfoByTradeNo(myTradeNo);
								List<TenantInfo> tenantList  = checkinInfoTenantMapper.getTenantInfoByKey(myTradeNo);
								List<CustomerInfoDTO> customerList = new ArrayList<CustomerInfoDTO>();
								if(null!=tenantList && tenantList.size()>0) {
									customerList = JSONObject.parseArray(JSONObject.toJSONString(tenantList), CustomerInfoDTO.class);
								}							    								 
						    	//办理入住
								appDataService.checkInAfterPay(myTradeNo, hotelInfo, checkin.getRoomNo(), checkin.getCheckinTime(), checkin.getOutTime(),checkin.getCheckinNum(),checkin.getRoomPrice(),checkin.getCardNum(),checkin.getDeposit(),customerList,info.getPayTradeType() );								 						    	  								
					    	}  
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();							
						}
					}	 
				}
				info.setPayTradeStatus(tradeStatus);
				checkinInfoPayMapper.updateByPrimaryKeySelective(info);				
			}
			return "success";
		}
		
		
		/**
		 * 续住支付回调处理
		 * @param payTradeNo 支付流水号
		 * @param myTradeNo 我方订单号
		 * @param tradeStatus 交易状态
		 */
		public String payReceive2(String payTradeNo,String myTradeNo,String tradeStatus) {
			PayInfo info =checkinInfoPayMapper.getPayInfoByKey(payTradeNo);
			if(null!=info) {			
				if(tradeStatus.equals("TRADE_SUCCESS") && !info.getPayTradeStatus().equals("TRADE_SUCCESS")) {	
					HotelInfo hotelInfo = hotelInfoMapper.getHotelInfoByCode(hotelCode); 					
					try {						
						AgainCheckinInfo again =againCheckinInfoMapper.getOrderInfoByChildTradeNo(myTradeNo);
						CheckinInfo checkin = checkinInfoMapper.getOrderInfoByTradeNo(again.getTradeNo());
						//续住
						appDataService.agaginCheckinAfterPay(checkin.getOrderNo(),again.getOutTime(),hotelInfo,info.getPayTradeType());												
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				info.setPayTradeStatus(tradeStatus);
				checkinInfoPayMapper.updateByPrimaryKeySelective(info);				
			}
			return "success";
		}
		
	 /**
     * 分账
     * @param tradeNo 我方订单号
     * @param hotelPrice 酒店金额
     * @param insurePrice 保险金额
     * @return
     * @throws Exception
     */
    public JSONObject payDivision(PayInfo info) throws Exception {
    	try {
	    	YsAccount params = new YsAccount();
			params.setHotelCode(hotelCode);		 		 
			YsAccount exist =ysAccountMapper.getYsAccount(params); 
	    	Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_04,prdUrl+Urls.APP_YS_PAY_RECEIVE_DIVISION);
	        Map<String,Object> bizContent = new HashMap<>();
	        bizContent.put("out_trade_no", info.getTradeNo());
	        bizContent.put("payee_usercode", HotelConstant.YSPAY_PARTNER_ID);
	       
	        bizContent.put("is_divistion", "01");
	        bizContent.put("is_again_division", "Y");
	        bizContent.put("division_mode", "02");
	        List<Map<String,String>> divList = new ArrayList<Map<String,String>>();
	        
	        //手续费从主商户扣
	        BigDecimal charge = new BigDecimal(0);
	        Map<String,String> chargeMap = new HashMap<String,String>();
	        chargeMap.put("is_chargeFee", "01");
	        chargeMap.put("division_mer_usercode", HotelConstant.YSPAY_PARTNER_ID);
	        
	        Map<String,String> hotelMap = new HashMap<String,String>();
	        hotelMap.put("division_mer_usercode", exist.getUserCode());
	        if(payModel.equals("PRD")) {
	        	 //订单总金额	        	 
	        	 bizContent.put("total_amount",String.valueOf(info.getRoomPrice()+info.getDeposit()+info.getInsurePrice()));
	        	 //酒店的手续费
	        	 BigDecimal hbg = new BigDecimal((info.getRoomPrice()+info.getDeposit())*0.004).setScale(2, RoundingMode.UP);
	        	 charge = charge.add(hbg);
	        	 //划给酒店扣掉手续费后的钱
	        	 hotelMap.put("div_amount", String.valueOf(info.getRoomPrice()+info.getDeposit()-hbg.doubleValue()));
	        }else {
	        	int testMoney = 0;
	        	
	        	if(info.getDeposit()>0){
	        		testMoney = testMoney+1;
	        	} 
	        	if(info.getRoomPrice()>0){
	        		testMoney = testMoney+1;
	        	} 	        	
	        	hotelMap.put("div_amount", String.valueOf((testMoney*1.0/10)-0.01));	        	
	        	if(info.getInsurePrice()>0){
	        		testMoney = testMoney+1;
	        	} 
	        	bizContent.put("total_amount",String.valueOf(testMoney*1.0/10));	        	
	        }
	       
	        hotelMap.put("is_chargeFee", "02");
	        divList.add(hotelMap);
	        if(info.getInsurePrice()>0){
	        	//保险账
	        	 Map<String,String> insureMap = new HashMap<String,String>();
	        	 insureMap.put("division_mer_usercode", insuranceUserCode);
	        	 if(payModel.equals("PRD")) {
	        		 //保险的手续费
	        		 BigDecimal ibg = new BigDecimal((info.getInsurePrice())*0.003).setScale(2, RoundingMode.UP);
	        		 charge = charge.add(ibg);
	        		 //划给保险扣掉手续费后的钱
	        		 insureMap.put("div_amount", String.valueOf(info.getInsurePrice()-ibg.doubleValue()));
	        	 }else {
	        		 insureMap.put("div_amount", String.valueOf("0.1"));
	        	 }	        	
	        	insureMap.put("is_chargeFee", "02");
	 	        divList.add(insureMap);
	        }
	        if(payModel.equals("PRD")) {
	        	 chargeMap.put("div_amount", String.valueOf(charge));
	        }else {
	        	 chargeMap.put("div_amount", String.valueOf("0.01"));
	        }
	        divList.add(chargeMap);
	        bizContent.put("div_list", divList);
	        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
	        log.info("[biz_content={}]",MyStringUtils.toJson(bizContent));
	        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		
				 String response = Https.httpsSend(Urls.YS_Pay_Division,paramsMap);
				 log.info("[分账结果返回消息===={}]",response);
				 JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	 JSONObject payResponse = ysResponse.getJSONObject("ysepay_single_division_online_accept_response");	
		    	 return payResponse;
		    	 
			} catch (Exception e) {
				e.printStackTrace();
				throw e;		 
			}
	}
    /**
     * 分账回调
     */
    public String divisionReceive(String tradeNo,String divisionStatus,String divisionCode) {
    	PayInfo info = checkinInfoPayMapper.getPayInfoByTradeNo(tradeNo);
    	if(null!=info) {
    		info.setReturnCode(divisionStatus); 
    		 
    		if(divisionCode.equals("02")) {
    			if(StringUtils.isNotBlank(info.getOutRequestNo()) && StringUtils.isBlank(info.getAccountDate())) {
    				try {
    				
    				JSONObject refundResponse = refund(info);				
    				String refundCode = refundResponse.getString("code");
    		    	if(!refundCode.equals("10000")) {
    		    		//return ResponseVo.fail("订单号["+info.getTradeNo()+"]退押金失败，错误原因："+refundResponse.getString("sub_msg"));
    		    	}
    		    	
    		    	String accountDate = refundResponse.getString("account_date");
    		    	info.setAccountDate(accountDate);
    		    	
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			 
    			}
    			}
    		}
    		checkinInfoPayMapper.updateByPrimaryKeySelective(info);
    	}
    	return "success";
    }
    
    /**
     * 押金，担保交易
     * @param tradeNo 我方订单号
     * @param payTradeNo 交易流水号
     * @param method 接口方法名
     * @throws Exception 
     */
    public JSONObject guarantee(String tradeNo,String payTradeNo,String method ) throws Exception {
    	try {    	 
	    		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(method,null);	        
		        String shopdate = tradeNo.substring(0,8);
		        Map<String,Object> bizContent = new HashMap<>();
		        bizContent.put("out_trade_no", tradeNo);
		        bizContent.put("shopdate", shopdate);
		        bizContent.put("trade_no",payTradeNo);	  
		        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
		        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       
		
				 String response = Https.httpsSend(Urls.YS_Open,paramsMap);
				 log.info("[担保交易结果返回消息===={}]",response);
				 JSONObject ysResponse = JSONObject.parseObject(response);
				 String body = "";
				 if(method.equals(HotelConstant.YSPAY_METHOD_02)){
					 body = "ysepay_online_trade_delivered_response";
				 }
				 if(method.equals(HotelConstant.YSPAY_METHOD_01)){
					 body = "ysepay_online_trade_confirm_response";
				 }
		    	 JSONObject payResponse = ysResponse.getJSONObject(body);
		    	 return payResponse;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;			 
			}
    	 
	}
    
    /**
     * 押金退款
     * @param payInfo 带押金的那笔支付信息
     * @param realDeposit 实际退款金额
     * @param outRequestNo 退款单号
     * @return
     * @throws Exception
     */
    public JSONObject refund(PayInfo payInfo) throws Exception {
    	try {    	 
	    		Map<String, String> paramsMap = SignUtils.getYsHeaderMap(HotelConstant.YSPAY_METHOD_05,prdUrl+Urls.APP_YS_PAY_RECEIVE_REFUND);	        
		       
		        paramsMap.put("tran_type", "2");
		        
	    		String shopdate = payInfo.getTradeNo().substring(0,8);
		        Map<String,Object> bizContent = new HashMap<>();
		        bizContent.put("out_trade_no", payInfo.getTradeNo());
		        bizContent.put("shopdate", shopdate);
		        bizContent.put("trade_no",payInfo.getPayTradeNo());	
		        
		        bizContent.put("refund_reason","退押金");
		        
		        bizContent.put("out_request_no", payInfo.getOutRequestNo());		        
		        bizContent.put("is_division", "01");
		        
		        YsAccount params = new YsAccount();
				params.setHotelCode(hotelCode);		 
		    	YsAccount exist =ysAccountMapper.getYsAccount(params); 
		    	
		        JSONArray refundSplitInfo = new JSONArray();
		        JSONObject obj = new JSONObject();
		        obj.put("refund_mer_id", exist.getUserCode());
		        if(payModel.equals("PRD")) {
		        	bizContent.put("refund_amount",payInfo.getRefundDeposit());
		        	obj.put("refund_amount", payInfo.getRefundDeposit());
		        }else{
		        	bizContent.put("refund_amount","0.1");
		        	obj.put("refund_amount", "0.1");
		        }
		       
		        refundSplitInfo.add(obj);		        
		        bizContent.put("refund_split_info", refundSplitInfo);
		        bizContent.put("ori_division_mode", "02");

		        JSONArray orderDivList = new JSONArray();
		        BigDecimal hbg = new BigDecimal((payInfo.getDeposit()+payInfo.getRoomPrice())*0.003).setScale(2, RoundingMode.UP);
		        JSONObject obj2 = new JSONObject();
		        obj2.put("division_mer_id", exist.getUserCode());
		        if(payModel.equals("PRD")) {
		        	 obj2.put("division_amount",hbg.doubleValue());
		        }else{
		        	 obj2.put("division_amount","0.19");
		        }
		       
		        obj2.put("is_charge_fee", "01");
		        orderDivList.add(obj2);		        
		        bizContent.put("order_div_list", orderDivList);
		        
		        paramsMap.put("biz_content",MyStringUtils.toJson(bizContent));
		        paramsMap.put("sign", SignUtils.rsaSign(paramsMap,"UTF-8",ysPriCertPath));	       		
				String response = Https.httpsSend(Urls.YS_Open,paramsMap);
				 log.info("[分账退款结果返回消息===={}]",response);
				 JSONObject ysResponse = JSONObject.parseObject(response);		    	 
		    	 JSONObject payResponse = ysResponse.getJSONObject("ysepay_online_trade_refund_split_response");		    	
		    	 return payResponse;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}
}
