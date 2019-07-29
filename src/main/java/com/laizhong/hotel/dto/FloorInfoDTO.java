package com.laizhong.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloorInfoDTO {
	private String floorCode;
	private String floorName;
	
	public FloorInfoDTO() {};
	public FloorInfoDTO(String floorCode,String floorName) {
		this.floorCode = floorCode;
		this.floorName = floorName;
	}
}
