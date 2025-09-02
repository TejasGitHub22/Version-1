package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NotBlank(message = "Username is required")
	@Column(name = "username", nullable = false)
	private String username;

	@NotBlank(message = "Email is required")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Password is required")
	@Column(name = "password", nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "Role is required")
	@Column(name = "role", nullable = false)
	private Role role;

	@NotNull(message = "Active status is required")
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;

	// Many-to-One relationship with Facility (for FACILITY users)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "facility_id")
	private Facility facility;

	@CreationTimestamp
	@Column(name = "creationDate", nullable = false, updatable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@Column(name = "lastUpdate", nullable = false)
	private LocalDateTime lastUpdate;

	// enums...
	public enum Role {
		FACILITY, ADMIN
	}

	public User() {

	}

	public User(@NotBlank(message = "Username is required") String username,
			@NotBlank(message = "Email is required") String email,
			@NotBlank(message = "Password is required") String password,
			@NotNull(message = "Role is required") String role,
			@NotNull(message = "Active status is required") Boolean isActive,
			Facility facility) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = parseRole(role);
		this.isActive = isActive;
		this.facility = facility;
		this.creationDate = LocalDateTime.now();
		this.lastUpdate = LocalDateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
		return role.toString();
	}

	public void setRole(String role) {
		this.role = parseRole(role);
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", username='" + username + '\'' +
				", role='" + role + '\'' +
				", isActive=" + isActive +
				", creationDate=" + creationDate +
				", lastUpdate=" + lastUpdate +
				'}';
	}

	private Role parseRole(String inputRole) {
		if (inputRole == null) {
			throw new IllegalArgumentException("Role is required");
		}
		String normalized = inputRole.trim().toUpperCase();
		if (normalized.startsWith("ROLE_")) {
			normalized = normalized.substring(5);
		}
		// Backward compatibility: map TECHNICIAN to FACILITY
		if ("TECHNICIAN".equals(normalized)) {
			normalized = "FACILITY";
		}
		try {
			return Role.valueOf(normalized);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Invalid role. Use ADMIN or FACILITY");
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(() -> "ROLE_" + role.name());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isActive;
	}
}