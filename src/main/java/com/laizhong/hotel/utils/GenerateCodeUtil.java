package com.laizhong.hotel.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GenerateCodeUtil {

	public static String[] chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	public static String generateShortUuid() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0xA]);
		}
		return shortBuffer.toString();
	}
	
	public static String generateTradeNo(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String prefix = format.format(new Date());
		String tradeNo = UUIDUtil.getUid(prefix);
		return tradeNo;
	}
	
}
