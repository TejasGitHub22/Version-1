package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
	private String username;
	private String email;
	private String password;
	private String role;
	private Integer facilityId; // Required for FACILITY users

	public SignupRequestDto(String username, String email, String password, String role, Integer facilityId) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.facilityId = facilityId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}
}
