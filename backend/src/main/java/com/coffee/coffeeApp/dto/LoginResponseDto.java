package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
	String jwt;
	String userId;
	String role;
	
	public LoginResponseDto(String jwt, String userId, String role) {
		this.jwt = jwt;
		this.userId = userId;
		this.role = role;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
