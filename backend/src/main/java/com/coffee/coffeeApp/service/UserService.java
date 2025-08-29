package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.UserDto;
import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Create new user
    public UserDto createUser(UserDto userDto) {
        validateUserDto(userDto);
        
        // Check if username already exists
        if (userRepository.existsByUsernameAndIsActiveTrue(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        
        // Generate ID if not provided
        if (userDto.getId() == null || userDto.getId().isEmpty()) {
            userDto.setId(UUID.randomUUID().toString());
        }
        
        User user = convertToEntity(userDto);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    // Get user by ID
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(String id) {
        return userRepository.findById(Integer.parseInt(id))
                .filter(User::getIsActive)
                .map(this::convertToDto);
    }
    
    // Get user by username
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsernameAndIsActiveTrue(username)
                .map(this::convertToDto);
    }
    
    // Get all active users
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get users by role
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(String role) {
        validateRole(role);
        return userRepository.findByRoleAndIsActiveTrue(role)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get facility users
    @Transactional(readOnly = true)
    public List<UserDto> getFacilityUsers() {
        return userRepository.findFacilityUsers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get admin users
    @Transactional(readOnly = true)
    public List<UserDto> getAdminUsers() {
        return userRepository.findAdminUsers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Update user
    public UserDto updateUser(String id, UserDto userDto) {
        User existingUser = userRepository.findById(Integer.parseInt(id))
                .filter(User::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        
        validateUserDto(userDto);
        
        // Check if username is being changed and if new username exists
        if (!existingUser.getUsername().equals(userDto.getUsername()) &&
            userRepository.existsByUsernameAndIsActiveTrue(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        
        // Update fields
        existingUser.setUsername(userDto.getUsername());
        existingUser.setRole(userDto.getRole());
        
        User savedUser = userRepository.save(existingUser);
        return convertToDto(savedUser);
    }
    
    // Soft delete user
    public void deleteUser(String id) {
        User user = userRepository.findById(Integer.parseInt(id))
                .filter(User::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    // Reactivate user
    public UserDto reactivateUser(String id) {
        User user = userRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        
        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    // Check if user exists
    @Transactional(readOnly = true)
    public boolean userExists(String id) {
        return userRepository.findById(Integer.parseInt(id))
                .map(User::getIsActive)
                .orElse(false);
    }
    
    // Check if username exists
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameAndIsActiveTrue(username);
    }
    
    // Get user statistics
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.findByIsActiveTrue().size();
        long facilityUsers = userRepository.countByRole("FACILITY");
        long adminUsers = userRepository.countByRole("ADMIN");
        
        return new UserStatistics(totalUsers, facilityUsers, adminUsers);
    }
    
    // Get recently created users
    @Transactional(readOnly = true)
    public List<UserDto> getRecentlyCreatedUsers(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return userRepository.findUsersByCreationDateRange(since, LocalDateTime.now())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Validation methods
    private void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userDto.getRole() == null || userDto.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        validateRole(userDto.getRole());
    }
    
    private void validateRole(String role) {
        if (!"FACILITY".equals(role) && !"ADMIN".equals(role)) {
            throw new IllegalArgumentException("Invalid role. Must be FACILITY or ADMIN");
        }
    }
    
    // Conversion methods
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(String.valueOf(user.getId()));
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreationDate(user.getCreationDate());
        dto.setLastUpdate(user.getLastUpdate());
        return dto;
    }
    
    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setId(Integer.parseInt(dto.getId()));
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return user;
    }
    
    // Inner class for statistics
    public static class UserStatistics {
        private final long totalUsers;
        private final long facilityUsers;
        private final long adminUsers;
        
        public UserStatistics(long totalUsers, long facilityUsers, long adminUsers) {
            this.totalUsers = totalUsers;
            this.facilityUsers = facilityUsers;
            this.adminUsers = adminUsers;
        }
        
        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getFacilityUsers() { return facilityUsers; }
        public long getAdminUsers() { return adminUsers; }
    }
}