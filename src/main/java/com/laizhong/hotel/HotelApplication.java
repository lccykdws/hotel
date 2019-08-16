package com.laizhong.hotel;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class HotelApplication {

    public static void main(String[] args) {           
      // SpringApplication.run(HotelApplication.class, args);
    	 new SpringApplicationBuilder(HotelApplication.class)
         .beanNameGenerator((def,reg)->def.getBeanClassName())
         .run(args);
    }
  

}
