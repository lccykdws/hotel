package com.laizhong.hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer   {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {        
    	String os = System.getProperty("os.name");
    	if (os.toLowerCase().startsWith("win")) {  //如果是Windows系统
    		   registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    	} else {  //linux 和mac
	    	registry.addResourceHandler("/static/**").addResourceLocations("file:/root/apps/hotel/static/");
	    	 
    	}
    }
 
}
