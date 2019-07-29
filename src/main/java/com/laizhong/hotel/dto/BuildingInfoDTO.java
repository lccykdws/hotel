package com.laizhong.hotel.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingInfoDTO {
	private String buildingCode;
	private String buildingName;
	private List<FloorInfoDTO> floorList;
}
