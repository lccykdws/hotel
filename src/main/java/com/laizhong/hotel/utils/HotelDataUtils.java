package com.laizhong.hotel.utils;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.model.ResponseVo;

/**
 * 获取酒店数据专用类
 * @author zhousiting
 *
 */

public class HotelDataUtils {
    
	/**
	 * 获取酒店数据
	 * @param url 请求地址
	 * @param secretKey 密钥
	 * @param params 参数
	 * @return
	 */
    public static ResponseVo<Object> getHotelData(String url,String secretKey,JSONObject params) {  	
		try {
			String signature = MD5Utils.md5(JSONObject.toJSONString(params, SerializerFeature.SortField), secretKey);
			params.put("key", signature);			
			String result = HttpClientUtil.httpPost(url, params);
			if(StringUtils.isBlank(result)) {
				 return ResponseVo.fail("酒店方未返回数据");
			}else {
				JSONObject obj = JSONObject.parseObject(result);
				 if(obj.getString("code").equals(HotelConstant.SUCCESS_CODE)) {					
					 return ResponseVo.success(obj.get("data"));
				 }else {
					 return ResponseVo.fail("酒店方出错，错误原因"+obj.getString("message"));
				 }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
    }
}
