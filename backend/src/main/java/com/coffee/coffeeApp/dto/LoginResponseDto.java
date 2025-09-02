package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
	String jwt;
	String userId;
	String role;
	String facilityId; // null for ADMIN users
	String facilityName; // null for ADMIN users

	public LoginResponseDto(String jwt, String userId, String role, String facilityId, String facilityName) {
		this.jwt = jwt;
		this.userId = userId;
		this.role = role;
		this.facilityId = facilityId;
		this.facilityName = facilityName;
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

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
}
