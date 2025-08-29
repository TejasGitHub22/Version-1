package com.coffee.coffeeApp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.coffee.coffeeApp.entity.User;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class AuthUtil {
	
	//secret key
	@Value("${jwt.secretKey}")
	public String jwtSecretKey;
	
	//encoding algorithm
	public SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
	}
	
	//User object for getting payload 
	public String generateAccessToken(User user) {
		return Jwts.builder()
				.subject(user.getUsername())
				.claim("role", user.getRole().toString())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000*60*15))  //token expires after 15 mins
				.signWith(getSecretKey())
				.compact();
	}

	public Claims getClaimsFromToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getSecretKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims;
	}
}
