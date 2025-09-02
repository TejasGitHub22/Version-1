package com.coffee.coffeeApp.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.coffee.coffeeApp.dto.LoginRequestDto;
import com.coffee.coffeeApp.dto.LoginResponseDto;
import com.coffee.coffeeApp.dto.SignupRequestDto;
import com.coffee.coffeeApp.dto.SignupResponseDto;
import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.entity.Facility;
import com.coffee.coffeeApp.repository.UserRepository;
import com.coffee.coffeeApp.repository.FacilityRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final AuthUtil authUtil;
	private final UserRepository userRepository;
	private final FacilityRepository facilityRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(AuthenticationManager authenticationManager, AuthUtil authUtil, UserRepository userRepository,
			FacilityRepository facilityRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.authUtil = authUtil;
		this.userRepository = userRepository;
		this.facilityRepository = facilityRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		// authenticating the user...
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

		// getting the object of authenticated user...
		User user = (User) authentication.getPrincipal();

		// getting the JWT token...
		String token = authUtil.generateAccessToken(user);

		// Prepare facility information for response
		String facilityId = null;
		String facilityName = null;
		if (user.getFacility() != null) {
			facilityId = user.getFacility().getId().toString();
			facilityName = user.getFacility().getName();
		}

		return new LoginResponseDto(token, user.getId().toString(), user.getRole(), facilityId, facilityName);
	}

	public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
		User isUserExisting = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

		if (isUserExisting != null)
			throw new IllegalArgumentException("User already exists");

		// Check if email already exists
		User isEmailExisting = userRepository.findByEmail(signupRequestDto.getEmail()).orElse(null);

		if (isEmailExisting != null)
			throw new IllegalArgumentException("Email already exists");

		// Validate role and facility assignment
		User.Role role = normalizeRole(signupRequestDto.getRole());
		Facility facility = null;

		if (role == User.Role.FACILITY) {
			if (signupRequestDto.getFacilityId() == null) {
				throw new IllegalArgumentException("Facility ID is required for FACILITY users");
			}
			facility = facilityRepository.findById(signupRequestDto.getFacilityId())
					.orElseThrow(() -> new IllegalArgumentException("Facility not found"));
		} else if (role == User.Role.ADMIN && signupRequestDto.getFacilityId() != null) {
			throw new IllegalArgumentException("ADMIN users cannot be assigned to a facility");
		}

		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

		User newUser = new User(
				signupRequestDto.getUsername(),
				signupRequestDto.getEmail(),
				encodedPassword,
				signupRequestDto.getRole(),
				true,
				facility);

		userRepository.save(newUser);

		return new SignupResponseDto(newUser.getId().toString(), newUser.getUsername());
	}

	private User.Role normalizeRole(String input) {
		if (input == null)
			throw new IllegalArgumentException("Role is required");
		String normalized = input.trim().toUpperCase();
		if (normalized.startsWith("ROLE_")) {
			normalized = normalized.substring(5);
		}
		if ("TECHNICIAN".equals(normalized)) {
			normalized = "FACILITY";
		}
		return User.Role.valueOf(normalized);
	}
}
