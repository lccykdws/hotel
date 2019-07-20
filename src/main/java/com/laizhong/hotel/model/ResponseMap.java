package com.laizhong.hotel.model;

import java.util.HashMap;
import java.util.Map;

public class ResponseMap {

	public static final String CODE_SUCCESS = "success";
	public static final String CODE_ERROR = "fail";

	public static Map<String, Object> success(Object data, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", CODE_SUCCESS);
		result.put("message", msg);
		result.put("data", data);
		return result;
	}

	public static Map<String, Object> success(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", CODE_SUCCESS);
		result.put("message", msg);
		return result;
	}

	public static Map<String, Object> error(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", CODE_ERROR);
		result.put("message", msg);
		return result;
	}
}
