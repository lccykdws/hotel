package com.laizhong.hotel.dto;

import java.util.List;

import com.laizhong.hotel.model.RoomImage;
import com.laizhong.hotel.model.RoomInfo;

import lombok.Getter;
import lombok.Setter;

public class RoomDTO extends RoomInfo {
	
	@Getter
	@Setter
	private List<RoomImage> roomImg;
	
	public RoomDTO(RoomInfo room) {
		this.setBedNum(room.getBedNum());
		this.setCreatedDate(room.getCreatedDate());
		this.setHotelCode(room.getHotelCode());
		this.setRoomTypeCode(room.getRoomTypeCode());
		this.setRoomTypeTitle(room.getRoomTypeTitle());
	}
}
