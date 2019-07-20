package com.laizhong.hotel.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

 

import lombok.extern.slf4j.Slf4j;

/**
 * 基于Apache HttpClient请求
 */
@Slf4j
public class HttpClientUtil {

	
	public static String doPostJson(String url, String json) throws Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			httpPost.setHeader("HTTP Method","POST");
			httpPost.setHeader("Connection","Keep-Alive");
			httpPost.setHeader("Content-Type","application/json;charset=utf-8");
			StringEntity entity = new StringEntity(json);

			entity.setContentType("application/json;charset=utf-8");
			httpPost.setEntity(entity);

			// 执行http请求
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(response!=null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultString;
	}
	
	
	/**
	 * POST请求
	 * @param url 地址
	 * @param parmar 参数
	 * @param contentType 发送报文编码
	 * @param contentEncoding 接收报文编码
	 * @return
	 */
	public static String httpPost(String url, Map<String, Object> parmar,String contentType,String contentEncoding)  {
		
		log.info("[发起POST请求,URL={},参数={},ContentType={},ContentEncoding={}]",url,parmar,contentType,contentEncoding);
		CloseableHttpClient httpclient ;		
		httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		
		int connectTimeout = 5000; //默认5秒超时
		
		RequestConfig  requestConfig  = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
	
		httpPost.setConfig(requestConfig);
		
		// 把参数封装起来
		List<NameValuePair> formarms = new ArrayList<>();
		
		Iterator<String> it = parmar.keySet().iterator();
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			formarms.add(new BasicNameValuePair(key, parmar.get(key).toString()));
		}
		UrlEncodedFormEntity urlEntity;
		String msg = null;
		try {
			// 发起请求
			httpclient = HttpClients.createDefault();
			urlEntity = new UrlEncodedFormEntity(formarms,contentEncoding);
			 
			if(!StringUtils.isBlank(contentType)){
				urlEntity.setContentType(contentType);
			}
			
			httpPost.setEntity(urlEntity);
						
			CloseableHttpResponse response = httpclient.execute(httpPost);
			   StatusLine status = response.getStatusLine();  
	            int state = status.getStatusCode();  
	            if (state == HttpStatus.SC_OK) {  
	            	HttpEntity entity = response.getEntity();
	    			if (entity != null) {
	    				// 得到结果
	    				msg = EntityUtils.toString(entity, contentEncoding);
	    				log.info("[获取HTTP POST结果,返回报文={}]",msg);
	    			}
	            }else{
	            	log.info("[HTTP POST请求失败,返回状态码={}",state);
	            }
			
		} catch (Exception e) {
			log.error("[HTTP POST请求失败,错误原因={}",e);
			 
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("[HTTP POST请求失败,错误原因={}",e);
				 
			}
		}
		return msg;
	}
	 
	/**
	 * 发送post请求
	 * @param url 地址
	 * @param content 报文内容
	 * @param header 请求头
	 * @param contentType 
 	 * @param contentEncoding
	 * @param isHttps 是否https请求
	 * @param timeout 请求超时时间，默认五秒
	 * @return
	 * @throws BusinessException
	 */
	public static String httpPost(String url, String content,Map<String,String> header,String contentType,String contentEncoding,boolean isHttps,String timeout )   {
	 
		log.info("[发起POST请求,URL={},参数={},header头={},ContentType={},ContentEncoding={}]",url,content,header,contentType,contentEncoding);
		CloseableHttpClient httpclient ;
		if(isHttps){
			httpclient = createSSLClientDefault();
		}else{
			httpclient = HttpClients.createDefault();
		}
		HttpPost httpPost = new HttpPost(url);
		
		int connectTimeout = 5000; //默认5秒超时
		if(!StringUtils.isBlank(timeout)){
			connectTimeout = Integer.parseInt(timeout);
		}
		RequestConfig  requestConfig  = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
	
		httpPost.setConfig(requestConfig);
		
		String msg = null;
		try {
			 
			 StringEntity strEntiry = new StringEntity(content,contentEncoding);
			 if(!StringUtils.isBlank(contentEncoding)){
				 strEntiry.setContentEncoding(contentEncoding);  
			 }
			 if(!StringUtils.isBlank(contentType)){
				 strEntiry.setContentType(contentType);  
			 }
			
			httpPost.setEntity(strEntiry);
			if(null!=header){
				//设置请求头
				for(String k:header.keySet()){
					httpPost.setHeader(k,header.get(k));
				}
			}
		
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 得到结果
				msg = EntityUtils.toString(entity, contentEncoding);
				log.info("[获取HTTP POST结果,返回报文={}]",msg);
			}
		} catch (Exception e) {
			log.error("[HTTP POST请求失败,错误原因={}",e);					
			 
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("[HTTP POST请求失败,错误原因={}",e);				 
			}
		}
		return msg;
	}
	
	/**
	 * 发送post请求
	 * @param url 地址
	 * @param parmar Map参数
	 * @param header 请求头
	 * @param contentType 
	 * @param contentEncoding
	 * @param contentEncoding
	 * @param isHttps 是否https请求
	 * @param timeout 请求超时时间，默认五秒
	 * @return
	 * @throws BusinessException
	 */
	public static String httpPost(String url, Map<String, Object> parmar,Map<String,String> header,String contentType,String contentEncoding,boolean isHttps,String timeout)  {
		
		log.info("[发起POST请求,URL={},参数={},header头={},ContentType={},ContentEncoding={}]",url,parmar,header,contentType,contentEncoding);
		CloseableHttpClient httpclient ;
		if(isHttps){
			httpclient = createSSLClientDefault();
		}else{
			httpclient = HttpClients.createDefault();
		}
		
		HttpPost httpPost = new HttpPost(url);
		
		int connectTimeout = 5000; //默认5秒超时
		if(!StringUtils.isBlank(timeout)){
			connectTimeout = Integer.parseInt(timeout);
		}
		RequestConfig  requestConfig  = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
	
		httpPost.setConfig(requestConfig);
		
		// 把参数封装起来
		List<NameValuePair> formarms = new ArrayList<>();
		
		Iterator<String> it = parmar.keySet().iterator();
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			formarms.add(new BasicNameValuePair(key, parmar.get(key).toString()));
		}
		UrlEncodedFormEntity urlEntity;
		String msg = null;
		try {
			// 发起请求
			httpclient = HttpClients.createDefault();
			urlEntity = new UrlEncodedFormEntity(formarms,contentEncoding);
			 
			if(!StringUtils.isBlank(contentType)){
				urlEntity.setContentType(contentType);
			}
			
			httpPost.setEntity(urlEntity);
			if(null!=header){
				//设置请求头
				for(String k:header.keySet()){
					httpPost.setHeader(k,header.get(k));
				}	
			}					
			CloseableHttpResponse response = httpclient.execute(httpPost);
			   StatusLine status = response.getStatusLine();  
	            int state = status.getStatusCode();  
	            if (state == HttpStatus.SC_OK) {  
	            	HttpEntity entity = response.getEntity();
	    			if (entity != null) {
	    				// 得到结果
	    				msg = EntityUtils.toString(entity, contentEncoding);
	    				log.info("[获取HTTP POST结果,返回报文={}]",msg);
	    			}
	            }else{
	            	log.info("[HTTP POST请求失败,返回状态码={}",state);
	            }
			
		} catch (Exception e) {
			log.error("[HTTP POST请求失败,错误原因={}",e);
			 
		} finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("[HTTP POST请求失败,错误原因={}",e);
				 
			}
		}
		return msg;
	}
	 
	/**
	 * 发送GET请求
	 * @param url 地址
	 * @param header
	 * @param contentEncoding
	 * @return
	 * @throws BusinessException
	 */
	public static String httpGet(String url,Map<String,String> header,String responseEncoding) {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		log.info("[发起HTTP GET请求,URL={},header头={},ResponseEncoding={}]",url,header,responseEncoding);
		
		HttpGet httpGet = new HttpGet(url);
		String msg = null;
		httpclient = HttpClients.createDefault();
		try {
			if(null!=header){
				//设置请求头
				for(String k:header.keySet()){
					httpGet.setHeader(k,header.get(k));
				}	
			}		
			
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				msg = EntityUtils.toString(entity, responseEncoding);
				log.info("[获取HTTP GET结果,返回报文={}]",msg);
			}
			 
		} catch (Exception e) {
			log.error("[HTTP GET请求失败,错误原因={}",e);		 
		}finally {
			try {
				// 释放资源
				httpclient.close();
			} catch (IOException e) {
				log.error("[HTTP GET请求失败,错误原因={}",e);			 
			}
		}
		return msg;
	}
	
	public static CloseableHttpClient createSSLClientDefault(){

        try {
            //SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            // 在JSSE中，证书信任管理器类就是实现了接口X509TrustManager的类。我们可以自己实现该接口，让它信任我们指定的证书。
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            //信任所有
                X509TrustManager x509mgr = new X509TrustManager() {

                    //　　该方法检查客户端的证书，若不信任该证书则抛出异常
                    public void checkClientTrusted(X509Certificate[] xcs, String string) {
                    }
                    // 　　该方法检查服务端的证书，若不信任该证书则抛出异常
                    public void checkServerTrusted(X509Certificate[] xcs, String string) {
                    }
                    // 　返回受信任的X509证书数组。
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[] { x509mgr }, null);
                ////创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //  HttpsURLConnection对象就可以正常连接HTTPS了，无论其证书是否经权威机构的验证，只要实现了接口X509TrustManager的类MyX509TrustManager信任该证书。
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();


        } catch (KeyManagementException e) {

            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }

        // 创建默认的httpClient实例.
        return  HttpClients.createDefault();

    }
	 
	 
}
