package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
	String username;
	String password;
	
	
	public LoginRequestDto(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
