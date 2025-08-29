package com.coffee.coffeeApp.repository;

import com.coffee.coffeeApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Find users by role
    List<User> findByRoleAndIsActiveTrue(String role);
    
    //Finding user by username(Active or inactive)
    Optional<User> findByUsername(String username);
    
    // Find user by username
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    
    //Find by userId
    Optional<User> findById(Integer id);
    
    // Check if username exists
    boolean existsByUsernameAndIsActiveTrue(String username);
    
    // Find facility users (users with FACILITY role)
    @Query("SELECT u FROM User u WHERE u.role = 'FACILITY' AND u.isActive = true")
    List<User> findFacilityUsers();
    
    // Find admin users (users with ADMIN role)
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.isActive = true")
    List<User> findAdminUsers();
    
    // Find users created within date range
    @Query("SELECT u FROM User u WHERE u.creationDate BETWEEN :startDate AND :endDate AND u.isActive = true")
    List<User> findUsersByCreationDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    // Count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    Long countByRole(@Param("role") String role);
    
    // Find recently updated users
    @Query("SELECT u FROM User u WHERE u.lastUpdate >= :since AND u.isActive = true ORDER BY u.lastUpdate DESC")
    List<User> findRecentlyUpdatedUsers(@Param("since") LocalDateTime since);
    
    // Soft delete user (set isActive to false)
    @Query("UPDATE User u SET u.isActive = false, u.lastUpdate = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void softDeleteUser(@Param("userId") Integer userId);
    
    // Reactivate user
    @Query("UPDATE User u SET u.isActive = true, u.lastUpdate = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void reactivateUser(@Param("userId") Integer userId);
}