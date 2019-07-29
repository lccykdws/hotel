package com.laizhong.hotel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;




/**
 *  基于Apache HttpClient请求
 */
@Slf4j
public class HttpClientUtil {
	// 申明http请求
	static CloseableHttpClient httpclient = HttpClients.createDefault();
	static HttpPost httpPost;
	static HttpGet httpGet;
	static CloseableHttpResponse response;
	/**
	 * 发送http Post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param parmar
	 *            Map 参数
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, String> parmar) throws Exception {
		// 创建实例
		log.info("[开始发送post请求,请求地址={},参数={}]", url, parmar);
		
		httpPost = new HttpPost(url);
		// 把参数封装起来
		List<NameValuePair> formarms = new ArrayList<>();
		Iterator<String> it = parmar.keySet().iterator();
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			formarms.add(new BasicNameValuePair(key, parmar.get(key)));
		}
		UrlEncodedFormEntity urlEntity;
		String msg = null;
		try {
			// 发起请求
			httpclient = HttpClients.createDefault();
			urlEntity = new UrlEncodedFormEntity(formarms, "UTF-8");
			httpPost.setEntity(urlEntity);
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 得到结果
				msg = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			 
			log.error("Http Post Send Error,Msg={}", e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("Http Post Close Error,Msg={}", e.getMessage());
				throw new Exception(e.getMessage());
			}
		}
		return msg;
	}
	public static String httpPost(String url, JSONObject jsonParam ) throws Exception {
		// 创建实例
		log.info("[开始发送post请求,请求地址={},参数={}]", url, jsonParam);

		httpPost = new HttpPost(url);
		String msg = null;
		try {
			// 发起请求
			httpclient = HttpClients.createDefault();
			 StringEntity strEntiry = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			 strEntiry.setContentEncoding("UTF-8");    
			 strEntiry.setContentType("application/json"); 
			httpPost.setEntity(strEntiry);
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 得到结果
				msg = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			log.error("[请求失败,错误信息={}]", e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("Http Post Close Error,Msg={}", e.getMessage());
				throw new Exception(e.getMessage());
			}
		}
		return msg;
	}
	public static String httpPost(String url, JSONObject jsonParam,Map<String,String> header ) throws Exception {
		// 创建实例
		log.info("begin Sent Http Post Request,url={},paramt={}", url, JSONObject.toJSONString(jsonParam, SerializerFeature.SortField));

		httpPost = new HttpPost(url);
		String msg = null;
		try {
			// 发起请求
			httpclient = HttpClients.createDefault();
			 StringEntity strEntiry = new StringEntity(JSONObject.toJSONString(jsonParam, SerializerFeature.SortField),"utf-8");//解决中文乱码问题
			 strEntiry.setContentEncoding("UTF-8");    
			 strEntiry.setContentType("application/json"); 
			httpPost.setEntity(strEntiry);
			//设置请求头
			for(String k:header.keySet()){
				httpPost.setHeader(k,header.get(k));
			}	
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 得到结果
				msg = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			log.error("Http Post Send Error,Msg={}", e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("Http Post Close Error,Msg={}", e.getMessage());
				throw new Exception(e.getMessage());
			}
		}
		return msg;
	}
	/**
	 * POST请求
	 * @param url 请求地址
	 * @param parmar 参数
	 * @param header 请求头设置
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, String> parmar,Map<String,String> header) throws Exception {
		// 创建实例
		log.info("begin Sent Http Post Request,url={},paramt={},header={}", url, parmar,header);

		httpPost = new HttpPost(url);
		// 把参数封装起来
		List<NameValuePair> formarms = new ArrayList<>();
		
		Iterator<String> it = parmar.keySet().iterator();
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			formarms.add(new BasicNameValuePair(key, parmar.get(key)));
		}
		UrlEncodedFormEntity urlEntity;
		String msg = null;
		try {
			// 发起请求
			httpclient = HttpClients.createDefault();
			urlEntity = new UrlEncodedFormEntity(formarms, "UTF-8");
			httpPost.setEntity(urlEntity);
			//设置请求头
			for(String k:header.keySet()){
				httpPost.setHeader(k,header.get(k));
			}			
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 得到结果
				msg = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			log.error("Http Post Send Error,Msg={}", e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("Http Post Close Error,Msg={}", e.getMessage());
				throw new Exception(e.getMessage());
			}
		}
		return msg;
	}
	/**
	 * Http Get请求
	 * 
	 * @param url
	 *            请求路径
	 * @param paramr
	 *            map 参数
	 * @return String json 返回值
	 * @throws Exception
	 */
	public static String httpGet(String url) throws Exception {
		 
		log.info("begin Sent Http Get Request,url={}", url);
		httpGet = new HttpGet(url);
		String msg = null;
		httpclient = HttpClients.createDefault();
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				msg = EntityUtils.toString(entity);				
			}
			log.info("end Http Get Request,result={}", entity);
		} catch (IOException e) {
			throw new Exception("初始化get方法IO异常," + e.getMessage());

		}
		return msg;
	}
	public static String post(String parameterData,String url) throws IOException{
      	URL localURL = new URL(url);   
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;  
        
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("application/json", "GBK");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));
		
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        outputStream = httpURLConnection.getOutputStream();
        outputStreamWriter = new OutputStreamWriter(outputStream);
        
        outputStreamWriter.write(parameterData.toString());
        outputStreamWriter.flush();
        
        inputStream = httpURLConnection.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);
        reader = new BufferedReader(inputStreamReader);
        
        while ((tempLine = reader.readLine()) != null) {
            resultBuffer.append(java.net.URLDecoder.decode(tempLine, "UTF-8"));
        }
        
      return resultBuffer.toString();
	}

}
