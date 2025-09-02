// package com.coffee.coffeeApp.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import com.coffee.coffeeApp.security.JwtAuthFilter;

// import lombok.RequiredArgsConstructor;

// @Configuration
// @RequiredArgsConstructor
// public class SecurityConfig {

// 	private final JwtAuthFilter jwtAuthFilter;

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
//     	return configuration.getAuthenticationManager();
//     }

//     @Bean 
//     public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
//     	httpSecurity
//     			.csrf(csrfConfig->csrfConfig.disable())
//     			.sessionManagement(sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//     			.authorizeHttpRequests(auth->auth
//     					.requestMatchers("/api/auth/**").permitAll()
//     					.anyRequest().authenticated()
// //    					.requestMatchers("/admin/**").hasRole("ADMIN")
// //    					.requestMatchers("/facility/**").hasRole("FACILITY")
//     			)
//     			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//     	return httpSecurity.build();
//     }
// }

package com.coffee.coffeeApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.coffee.coffeeApp.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(csrfConfig -> csrfConfig.disable())
				.cors(cors -> {
				})
				.sessionManagement(
						sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/health/**").permitAll()
						// ADMIN endpoints - can access all facilities and admin features
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						// FACILITY endpoints - can only access their assigned facility
						.requestMatchers("/api/facility/**").hasRole("FACILITY")
						// Public endpoints for basic information
						.requestMatchers(org.springframework.http.HttpMethod.GET,
								"/api/facilities/**",
								"/api/machines/**",
								"/api/usage/**",
								"/api/alerts/**")
						.authenticated()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	// 🔥 Global CORS configuration
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// Broaden CORS to support local dev (localhost/127.0.0.1 and various ports) and
		// dev servers
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
