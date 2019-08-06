package com.laizhong.hotel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.laizhong.hotel.config.MyFilter;

@SpringBootApplication
public class HotelApplication {

    public static void main(String[] args) {           
       SpringApplication.run(HotelApplication.class, args);
    }
    /**
     * 注册过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
 
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new MyFilter());
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/api/app/**");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
 
    }

}
