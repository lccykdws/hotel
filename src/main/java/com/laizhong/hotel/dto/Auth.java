package com.laizhong.hotel.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Auth implements Serializable {

	private static final long serialVersionUID = -4353116464393072743L;
	
	@NonNull
	private String token;
	
	@NonNull
	private String userName;
	
	@NonNull
	private Date loginDate;

	@NonNull
	private Long expireAt;
}
