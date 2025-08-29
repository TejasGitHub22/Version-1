package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
	String jwt;
	String userId;
	
	public LoginResponseDto(String jwt, String userId) {
		this.jwt = jwt;
		this.userId = userId;
	}
	
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
