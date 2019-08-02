package com.laizhong.hotel.dto;

import lombok.Data;

@Data
public class InsuranceDTO {
	
    private String userName;

   
    private String password;

    //订单号
    private String orderId;
    //支付时间yyyy-MM-dd HH:mm:ss
    private String payTime;
    
  //产品代码，保险公司 提供产品唯一代码
    private String productCode;
  //投保时间yyyy-MM-dd HH:mm:ss
    private String insureDate;
    
	//被保人名称
    private String name;
   //被保人性别
    private String sex;
   //投保人与被保人关系，固定值10
    private String relationship="10";
    //出生日期
    private String birthday;
    //证件号码
    private String idNo;
    //证件类型
    private String idType = "10";
}
