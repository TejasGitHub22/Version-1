package com.coffee.coffeeApp.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.coffee.coffeeApp.repository.UserRepository;

//this class will fetch the user from the database...
@Service 
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return userRepository.findByUsernameAndIsActiveTrue(username)
		        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

}
