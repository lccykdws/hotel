package com.laizhong.hotel.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.Charsets;

import com.alibaba.fastjson.JSONObject;
import com.laizhong.hotel.model.ResponseVo;

public class ResponseUtils {
	
	public static <T> void writeResponse(HttpServletResponse response, ResponseVo<T> resp) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(JSONObject.toJSONString(resp).getBytes(Charsets.UTF_8));
    }
	
	public static void writeResponse(HttpServletResponse response, String s) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(s.getBytes(Charsets.UTF_8));
    }

}
