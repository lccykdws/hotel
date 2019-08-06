package com.laizhong.hotel.dto;


import lombok.Data;

@Data
public class UserInfoDTO {
	private String account;
	private String username;
	private String password;
	private String[] role;
}
