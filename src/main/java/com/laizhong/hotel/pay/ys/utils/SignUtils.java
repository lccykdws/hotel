package com.laizhong.hotel.pay.ys.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.laizhong.hotel.constant.HotelConstant;
import com.laizhong.hotel.controller.Urls;

/**
 * 签名验签工具
 * 
 * @author chang
 *
 */
public class SignUtils {

		public  static Map<String, Object> certMap = new java.util.HashMap<String, Object>();
		
		
		public static Map<String, String> getYsHeaderMap(String method,String notifyUrl){
			Map<String, String> paramsMap = new HashMap<String, String>();
	        paramsMap.put("method",method);
	        paramsMap.put("partner_id",HotelConstant.YSPAY_PARTNER_ID);
	        paramsMap.put("timestamp", DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
	        paramsMap.put("charset","UTF-8");
	        paramsMap.put("sign_type","RSA");
	        if(StringUtils.isNotBlank(notifyUrl)) {
	        	 paramsMap.put("notify_url",notifyUrl);
	        }	       
	        paramsMap.put("version","3.0");
	        return paramsMap;
		}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || MyStringUtils.isEmpty(value) || key.equalsIgnoreCase("sign")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 遍历以及根据重新排序
	 * 
	 * @param sortedParams
	 * @return
	 */
	public static String getSignContent(Map<String, String> sortedParams) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(sortedParams.keySet());
		Collections.sort(keys);
		int index = 0;
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = sortedParams.get(key);
			if (MyStringUtils.areNotEmpty(key, value)) {
				content.append((index == 0 ? "" : "&") + key + "=" + value);
				index++;
			}
		}
		return content.toString();
	}

	/**
	 * 签名
	 * 
	 * @param content
	 * @param charset
	 * @param pfxCertFileInputStream
	 * @return
	 * @throws Exception
	 */
	public static String rsaSign(String content, String charset, InputStream pfxCertFileInputStream) throws Exception {
		try {
			System.out.println("进入签名方法：content[" + content + "], charset[" + charset + "]");

			PrivateKey priKey = getPrivateKeyFromPKCS12(HotelConstant.PASSWORD_PARTNER_PKCS12, pfxCertFileInputStream);

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initSign(priKey);

			if (MyStringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			byte[] signed = signature.sign();

			String sign = new String(Base64.encodeBase64(signed), charset);

			System.out.println("进入签名完：content[" + content + "], charset[" + charset + "], sign[" + sign + "]");

			return sign;
		} catch (Exception e) {
			System.out.println("签名失败：content[" + content + "], charset[" + charset + "]");
			throw new Exception("RSAcontent = " + content + "; charset = " + charset, e);
		}
	}		
	
	/**
	 * 把参数签名
	 * 
	 * @param params
	 * @param charset
	 * @param pfxCertFile
	 * @return
	 * @throws Exception
	 */
	public static String rsaSign(Map<String, String> params, String charset, String pfxCertFile){
		String signContent = getSignContent(params);
        String sign = "";
        InputStream is = null;
        try {
            is = new FileInputStream(pfxCertFile);
            sign = rsaSign(signContent, charset, is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

	public static boolean rsaCheckContent(InputStream publicCertFileInputStream, Map<String, String> params,
			String sign, String charset) throws Exception {
		String content = MyStringUtils.createLinkString(SignUtils.paraFilter(params));

		return rsaCheckContent(publicCertFileInputStream, content, sign, charset);
	}

	/**
	 * 验签
	 * 
	 * @param publicCertFileInputStream
	 * @param content
	 * @param sign
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static boolean rsaCheckContent(InputStream publicCertFileInputStream, String content, String sign,
			String charset) throws Exception {
		System.out.println("进入验证签名方法: content[" + content + "], sign[" + sign + "], charset[" + charset + "]");
		boolean bFlag = false;
		try {
			java.security.Signature signetcheck = java.security.Signature.getInstance("SHA1WithRSA");
			signetcheck.initVerify(getPublicKeyFromCert(publicCertFileInputStream));
			signetcheck.update(content.getBytes(charset));
			if (signetcheck.verify(Base64.decodeBase64(sign.getBytes(charset)))) {
				// 跑不进条件语句里面
				bFlag = true;
				System.out.println("解密成功");
				System.out.println("sign:" + Base64.decodeBase64(sign.getBytes(charset)).toString());
			}
			System.out.println("进入验证签名方法: content[" + content + "], sign[" + sign + "], charset[" + charset
					+ "], result[" + bFlag + "]");
		} catch (Exception e) {
			System.out.println("验证签名异常" + ": content[" + content + "], sign[" + sign + "], charset[" + charset + "]");
			throw new Exception("验证签名异常");
		}

		return bFlag;
	}
	/**
	 * 验签
	 * @param sign 密文
	 * @param responseBody 明文
	 * @param charset 编码
	 * @param pfxCertFile 银盛公钥证书地址
	 * @return
	 */
	public static boolean verifyJsonSign(String sign, String responseBody, String charset,String pfxCertFile) {
		boolean isSign = false;
		try {
			/*		通过file读取证书*/
			File file = new File(pfxCertFile);
			InputStream publicCertFileInputStream = new FileInputStream(file);
			isSign = SignUtils.rsaCheckContent(publicCertFileInputStream, responseBody, sign, charset);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("读取公钥失败，请检查银盛公钥证书文件是否存在");
		} catch (Exception e) {
			throw new RuntimeException("读取公钥失败，请检查银盛公钥证书文件是否存在");
		}
		return isSign;
	}
	/**
	 * 读取公钥，x509格式
	 * 
	 * @param ins
	 * @return
	 * @throws Exception
	 * @see
	 */
	public static PublicKey getPublicKeyFromCert(InputStream ins) throws Exception {
		 Map<String, Object> certMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();

		PublicKey pubKey = (PublicKey) certMap.get("PublicKey");
		if (pubKey != null) {
			return pubKey;
		}

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cac = cf.generateCertificate(ins);
			pubKey = cac.getPublicKey();
			certMap.put("PublicKey", pubKey);
		} catch (Exception e) {
			if (ins != null)
				ins.close();
			throw e;
		} finally {
			if (ins != null) {
				ins.close();
			}
		}

		return pubKey;
	}

	/**
	 * 读取PKCS12格式的key（私钥）pfx格式
	 * 
	 * @param password
	 * @param ins
	 * @return
	 * @throws Exception
	 * @see
	 */
	public static PrivateKey getPrivateKeyFromPKCS12(String password, InputStream ins) throws Exception {
		 Map<String, Object> certMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		PrivateKey priKey = (PrivateKey) certMap.get("PrivateKey");
		if (priKey != null) {
			return priKey;
		}

		KeyStore keystoreCA = KeyStore.getInstance("PKCS12");
		try {
			// 读取CA根证书
			keystoreCA.load(ins, password.toCharArray());

			Enumeration<?> aliases = keystoreCA.aliases();
			String keyAlias = null;
			if (aliases != null) {
				while (aliases.hasMoreElements()) {
					keyAlias = (String) aliases.nextElement();
					// 获取CA私钥
					priKey = (PrivateKey) (keystoreCA.getKey(keyAlias, password.toCharArray()));
					if (priKey != null) {
						certMap.put("PrivateKey", priKey);
						break;
					}
				}
			}
		} catch (Exception e) {
			if (ins != null)
				ins.close();
			throw e;
		} finally {
			if (ins != null) {
				ins.close();
			}
		}

		return priKey;
	}

}
