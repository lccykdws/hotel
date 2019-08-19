package com.laizhong.hotel.pay.ys.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("ALL")
@Slf4j
public class Https {
	/**
	 * [HTTPS]
	 * @param urlNotify
	 * @param param
	 * @return
	 */
	public static String httpsSend (String urlNotify, Map<String, String> param) throws Exception {
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		HttpsURLConnection conn = null;
		try {
			TrustManager[] tm = { new YzyueX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tm, null);
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(urlNotify);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(new Https().new TrustAnyHostnameVerifier());
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			StringBuffer sb = new StringBuffer();
			List<String> keys = new ArrayList<String>(param.keySet());
			Collections.sort(keys);
			boolean flag = true;
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				String value = (String) param.get(key);
				if(key.equals("sign")){//URL 传+号到服务器变空格问题解决方案
					value = URLEncoder.encode(value, "UTF-8");
				}
				if(key.equals("biz_content")){//URL 传+号到服务器变空格问题解决方案
					value = URLEncoder.encode(value, "UTF-8");
				}
				if(flag){
				flag = false;
			}else{
				sb.append("&");
			}
			sb.append(key + "=" + value);
			}
			String message = sb.toString();
			log.info("请求地址[" + urlNotify + "],请求数据:\r\n" + message);
			
			os = conn.getOutputStream();
			os.write(message.getBytes("UTF-8"));
			os.close();

			// 将返回的输入流转换成字符串
			is = conn.getInputStream();
			isr = new InputStreamReader(is, "utf-8");
			br = new BufferedReader(isr);
			String str = null;
			StringBuffer rs = new StringBuffer();
			while ((str = br.readLine()) != null) {
				rs.append(str);
			}
			String result = rs.toString();
			log.info("响应数据:\r\n" + result);
			return result;
		} catch (Exception e) {
			log.info("https响应失败"+e.getMessage());
			throw  e;
		}finally{
			if(br != null){
				br.close();
			}
			if(isr != null){
				isr.close();
			}
			if(is != null){
				is.close();
			}
			if(os != null){
				os.close();
			}
			if(isr != null){
				isr.close();
			}
			if(conn != null){
				conn.disconnect();
			}
		}
	}
	
	/**
	 * [HTTPS]
	 * @param urlNotify
	 * @param param
	 * @return
	 */
	public static String httpsSend (String urlNotify, Map<String, String> param,File file) throws Exception {
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		HttpsURLConnection conn = null;
		try {
			TrustManager[] tm = { new YzyueX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tm, null);
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(urlNotify);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(new Https().new TrustAnyHostnameVerifier());
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");		 
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			
			
			HttpClient client = HttpClients.custom().setSSLHostnameVerifier(new Https().new TrustAnyHostnameVerifier()).setSSLContext(sslContext).build();
			 
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			multipartEntityBuilder.addBinaryBody("picFile",file);
			List<String> keys = new ArrayList<String>(param.keySet());
			Collections.sort(keys);
			StringBuffer sb = new StringBuffer();
			boolean flag = true;
			
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				String value = (String) param.get(key);				 
				multipartEntityBuilder.addTextBody(key, value);
				if(flag){
					flag = false;
				}else{
					sb.append("&");
				}
				sb.append(key + "=" + value);
				
			} 			 
			String message = sb.toString();
			log.info("请求地址[" + urlNotify + "],请求数据:\r\n" + message);
			
			os = conn.getOutputStream();
			os.write(message.getBytes("UTF-8"));
			os.close();

			// 将返回的输入流转换成字符串
			is = conn.getInputStream();
			isr = new InputStreamReader(is, "utf-8");
			br = new BufferedReader(isr);
			String str = null;
			StringBuffer rs = new StringBuffer();
			while ((str = br.readLine()) != null) {
				rs.append(str);
			}
			String result = rs.toString();
			log.info("响应数据:\r\n" + result);
			return result;
		} catch (Exception e) {
			log.info("https响应失败"+e.getMessage());
			throw  e;
		}finally{
			if(br != null){
				br.close();
			}
			if(isr != null){
				isr.close();
			}
			if(is != null){
				is.close();
			}
			if(os != null){
				os.close();
			}
			if(isr != null){
				isr.close();
			}
			if(conn != null){
				conn.disconnect();
			}
		}
	}
	
	public static String sendHttpMessage(String url, Map<String, String> map, File file) throws IOException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        String responseMessage = "";
        try {
            if (StringUtils.isEmpty(url) || null == map || map.isEmpty()) {
                return null;
            }

            HttpPost post = new HttpPost(url);
            MultipartEntity entity = new MultipartEntity();

            for (Map.Entry<String, String> entry : map.entrySet()) {

                entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
            }
            entity.addPart("picFile", new FileBody(file));
            post.setEntity(entity);
        	 
			post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			 
            client = HttpClients.createDefault();
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new Exception("请求失败");
            }

            HttpEntity resEntity = response.getEntity();
            responseMessage = null == resEntity ? "" : EntityUtils.toString(resEntity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return responseMessage;
    }
	public class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}


}
