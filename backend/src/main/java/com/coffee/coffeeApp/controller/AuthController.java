package com.coffee.coffeeApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.coffee.coffeeApp.security.AuthService;
import com.coffee.coffeeApp.dto.LoginRequestDto;
import com.coffee.coffeeApp.dto.LoginResponseDto;
import com.coffee.coffeeApp.dto.SignupResponseDto;
import com.coffee.coffeeApp.dto.SignupRequestDto;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
		return ResponseEntity.ok(authService.login(loginRequestDto));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
		return ResponseEntity.ok(authService.signup(signupRequestDto));
	}
}
