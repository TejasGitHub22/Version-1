package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class SignupResponseDto {
	String id;
	String username;
	
	public SignupResponseDto(String id, String username) {
		this.id = id;
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
