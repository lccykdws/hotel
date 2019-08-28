package com.laizhong.hotel.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.service.YsReceiveService;

import lombok.extern.slf4j.Slf4j;

/**
 * 接收异步请求
 *
 */
@Slf4j
@RestController
public class ReceiveController {
	
	@Autowired
	private YsReceiveService ysReceiveService = null;
 
	/**
	 * 公用回调
	 * @param request
	 * @param response
	 */
	@PostMapping(Urls.APP_YS_PAY_RECEIVE_COMMON)
	public void receiveCommon(HttpServletRequest request, HttpServletResponse response) {	
		log.info("[银盛公共回调开始：参数={}]",JSONObject.toJSONString(request.getParameterMap()));		 
	}
	
	/**
	 * 入住支付回调
	 * @param request
	 * @param response
	 */
	@PostMapping(Urls.APP_YS_PAY_RECEIVE_PAY)
	public void receivePay(HttpServletRequest request, HttpServletResponse response) {	
		log.info("[银盛入住支付回调开始：参数={}]",JSONObject.toJSONString(request.getParameterMap()));
		//支付流水号
		String payTradeNo = request.getParameter("trade_no");
		//订单号
		String myTradeNo = request.getParameter("out_trade_no");
		//交易状态
		String tradeStatus = request.getParameter("trade_status");
		ysReceiveService.payReceive1(payTradeNo, myTradeNo, tradeStatus);
	}
	/**
	 * 续住支付回调
	 * @param request
	 * @param response
	 */
	@PostMapping(Urls.APP_YS_PAY_RECEIVE_AGAINPAY)
	public void receiveAgainPay(HttpServletRequest request, HttpServletResponse response) {	
		log.info("[银盛续住支付回调开始：参数={}]",JSONObject.toJSONString(request.getParameterMap()));
		//支付流水号
		String payTradeNo = request.getParameter("trade_no");
		//订单号
		String myTradeNo = request.getParameter("out_trade_no");
		//交易状态
		String tradeStatus = request.getParameter("trade_status");
		ysReceiveService.payReceive2(payTradeNo, myTradeNo, tradeStatus);
	}
	
	/**
	 * 分账回调
	 * @param request
	 * @param response
	 * @throws  
	 */
	@PostMapping(Urls.APP_YS_PAY_RECEIVE_DIVISION)
	public void receivePayDivision(HttpServletRequest request, HttpServletResponse response) {	
		log.info("[银盛分账回调开始：参数={}]",JSONObject.toJSONString(request.getParameterMap()));
		/*//订单号
		String myTradeNo = request.getParameter("out_trade_no");
		//分账状态
		String tradeStatus = request.getParameter("division_status");
		String tradeStatusCode = request.getParameter("division_status_code");
		ysReceiveService.divisionReceive(myTradeNo,tradeStatus,tradeStatusCode);*/
		try {
			ysReceiveService.guarantee(request.getParameter("tradeNo"),request.getParameter("payTradeNo"),HotelConstant.YSPAY_METHOD_02);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
