package com.laizhong.hotel.dto;

import java.util.List;

import com.laizhong.hotel.model.YsAccountImage;

import lombok.Data;

@Data
public class YsAccountDTO {
	 
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

	private List<YsAccountImage> imgs;
}
