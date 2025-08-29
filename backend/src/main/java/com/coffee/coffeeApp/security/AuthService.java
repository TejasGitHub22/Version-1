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
import com.coffee.coffeeApp.repository.UserRepository;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final AuthUtil authUtil;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public AuthService(AuthenticationManager authenticationManager, AuthUtil authUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.authUtil = authUtil;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		//authenticating the user...
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
		);
		
		//getting the object of authenticated user...
		User user = (User) authentication.getPrincipal();
			
		//getting the JWT token...
		String token = authUtil.generateAccessToken(user);
		
		return new LoginResponseDto(token, user.getId().toString());
	}
	
	public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
		User isUserExisting = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);
		
		if(isUserExisting != null) throw new IllegalArgumentException("User already exists");
		
		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
		
		User newUser = new User(
				signupRequestDto.getUsername(),
				encodedPassword,
				signupRequestDto.getRole(),
				true
		);
		
		userRepository.save(newUser);
		
		return new SignupResponseDto(newUser.getId().toString(), newUser.getUsername());
	}
}
