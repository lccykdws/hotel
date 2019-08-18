package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class YsAccount {
	 
	private String merchantNo;
 
	private String hotelCode;
	 
	private String remark;
	
	private String custType;
	
	private String anotherName;
	
	private String custName;
	 
	private String merFlag;
	 
	private String industry;
	
	private String province;
	
	private String city;
	
	private String companyAddr;
	 
	private String legalName;
	 
	private String legalTel;
	
	private String legalCertType;
	
	private String legalCertNo;
	
	private String legalCertExpire;
	 
	private String busLicense;
	 
	private String busLicenseExpire;
	
	private String bankAccountNo;
	
	private String bankAccountName;
	
	private String bankAccountType;
	 
	private String bankCardType;
	 
	private String bankName;
	
	private String bankType;
	
	private String bankProvince;
	
	private String bankCity;
	 
	private String certType;
	 
	private String certNo;
	
	private String bankTelephoneNo;
	
	private String userCode;
	 

	private String custId;
	 
	private String userStatus;
	 
	private String custStatue;
	
	private String isNeedContract;
	
	private String onlineUrl;
	private String offlineUrl;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 
	@JsonIgnore   
	private Date createdDate;
}
