package com.laizhong.hotel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    
    /**
     * date2比date1多的天数
     * @param date1    
     * @param date2
     * @return    
     * @throws ParseException 
     */
    public static int differentDays(String startDate,String endDate) throws ParseException
    {     
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date date1 = format.parse(startDate);
    	Date date2 = format.parse(endDate);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
       int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            
            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {          
            return day2-day1;
        }
    }
    
 /*   public static void main(String[] args) throws ParseException {
    	String d1 = "2018-12-30";
    	String d2 = "2019-01-01";
    	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	
    	System.out.println(differentDays(format.parse(d1),format.parse(d2)));
    }*/
    
}
