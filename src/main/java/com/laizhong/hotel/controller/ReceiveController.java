package com.laizhong.hotel.controller;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 接收异步请求
 *
 */
@Slf4j
@RestController
public class ReceiveController {
	@PostMapping(Urls.APP_YS_PAY_RECEIVE)
	public void receivePayReturn(HttpServletRequest request, HttpServletResponse response) {	
		 
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null){
			sb.append(line);
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		log.info(sb.toString());
		JSONObject jsonObj = JSONObject.parseObject(sb.toString());
		String content = jsonObj.getString("ysepay_online_trade_query_response");
		String sign = jsonObj.getString("sign");
	}
}
