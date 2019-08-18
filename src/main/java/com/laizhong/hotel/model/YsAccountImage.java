package com.laizhong.hotel.model;

import lombok.Data;

@Data
public class YsAccountImage {
	private int id;
	private String merchantNo;
	
	private String imgUrl;
	
	//图片类型 
	/*00	公民身份证正面	12	军事院校学员证
	01	中国护照	13	武装警察身份证
	02	军人身份证	14	军官证
	03	警官证	15	文职干部证
	04	户口簿	16	军人士兵证
	05	临时身份证	17	武警士兵证
	06	外国护照	18	其他证件
	07	港澳通行证	19	营业执照
	08	台胞通行证	20	税务登记证
	09	离休干部荣誉证	30	公民身份证反面
	10	军官退休证	31	客户协议
	11	文职干部退休证	32	授权书*/

	
	private String imgType;
}
