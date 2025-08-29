package com.coffee.coffeeApp.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.repository.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//Slf4j used for logging purpose...
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter{

	private final UserRepository userRepository;
	private final AuthUtil authUtil;
	
	public JwtAuthFilter(UserRepository userRepository, AuthUtil authUtil) {
		this.userRepository = userRepository;
		this.authUtil = authUtil;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String requestTokenHeader = request.getHeader("Authorization");
		if(requestTokenHeader == null|| !requestTokenHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = requestTokenHeader.split("Bearer ")[1];
		Claims claims = authUtil.getClaimsFromToken(token);
		String username = claims.getSubject();
		String role = claims.get("role", String.class);
		
		List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userRepository.findByUsername(username).orElseThrow();
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		}
		
		filterChain.doFilter(request, response);
	}
	
}
