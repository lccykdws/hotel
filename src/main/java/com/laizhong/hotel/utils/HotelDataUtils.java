package com.laizhong.hotel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.dto.InsuranceDTO;
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
    
    public static String createInsurenXML(InsuranceDTO dto) {
    	String xml = "<INSURENCEINFO>" + 
    			"  <USERNAME>44dbe7ea98aab26b3d8b748c481e85</USERNAME>" + 
    			"  <PASSWORD>31abbf083d261b88f6b4b6f1262f</PASSWORD>" + 
    			"  <ORDER>" + 
    			"    <ORDERID>订单号</ORDERID>" + 
    			"    <SOURCEORDERID></SOURCEORDERID>" + 
    			"    <PAYORDERNO/>  " + 
    			"    <PAYWAY>ALIPAY</PAYWAY>  " + 
    			"    <PAYAMOUNT>3.00</PAYAMOUNT>  " + 
    			"    <PAYACCOUNT></PAYACCOUNT>  " + 
    			"    <APPID>2017060707441456</APPID>  " + 
    			"    <PAYTIME>2016-11-29 01:19:27</PAYTIME>  " + 
    			"    <POLICYINFO> " + 
    			"      <PRODUCTCODE>QP050414</PRODUCTCODE>  " + 
    			"      <INSURDATE>2016-11-29 01:19:27</INSURDATE>  " + 
    			"      <INSURSTARTDATE>2016-11-29 12:00:00</INSURSTARTDATE>  " + 
    			"      <INSURENDDATE>2016-11-30 12:00:00</INSURENDDATE>  " + 
    			"      <INSURPERIOD>1</INSURPERIOD>" + 
    			"      <PERIODFLAG>D</PERIODFLAG>" + 
    			"      <MULT>1</MULT>" + 
    			"      <PREMIUM>3.00</PREMIUM>" + 
    			"      <AMOUNT>243000</AMOUNT>  " + 
    			"      <BENEFMODE>0</BENEFMODE>  " + 
    			"      <APPNTNAME>韩露健</APPNTNAME>  " + 
    			"      <APPNTSEX>1</APPNTSEX>  " + 
    			"      <APPNTBIRTHDAY>1990-10-25</APPNTBIRTHDAY>  " + 
    			"      <APPNTIDTYPE>10</APPNTIDTYPE>  " + 
    			"      <APPNTIDNO>32132319901025001X</APPNTIDNO>  " + 
    			"      <APPNTPHONE>18607192771</APPNTPHONE>  " + 
    			"      <APPNTMOBILE>18607192771</APPNTMOBILE>  " + 
    			"      <INSUREDLIST> " + 
    			"        <INSURED> " + 
    			"          <INSUREDNAME>韩露健</INSUREDNAME>  " + 
    			"          <INSUREDSEX>1</INSUREDSEX>  " + 
    			"          <RELATIONSHIP>10</RELATIONSHIP>  " + 
    			"          <INSUREDBIRTHDAY>1990-10-25</INSUREDBIRTHDAY>  " + 
    			"          <INSUREDIDNO>32132319901025001X</INSUREDIDNO>  " + 
    			"          <INSUREDIDTYPE>10</INSUREDIDTYPE> " + 
    			"        </INSURED> " + 
    			"      </INSUREDLIST> " + 
    			"    </POLICYINFO> " + 
    			"  </ORDER> " + 
    			"</INSURENCEINFO>"; 
    	
    	return xml.trim();
    }
    
}
