package com.bikeshare.repository;

import com.bikeshare.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Demonstrates repository pattern testing (Lab 5 - Integration testing).
 */
public interface UserRepository {
    
    /**
     * Saves a user.
     * @param user the user to save
     * @return saved user
     */
    User save(User user);
    
    /**
     * Finds a user by ID.
     * @param userId the user ID
     * @return optional user
     */
    Optional<User> findById(String userId);
    
    /**
     * Finds a user by email.
     * @param email the user's email
     * @return optional user
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds users by status.
     * @param status the user status
     * @return list of users with the specified status
     */
    List<User> findByStatus(User.UserStatus status);
    
    /**
     * Finds users by membership type.
     * @param membershipType the membership type
     * @return list of users with the specified membership
     */
    List<User> findByMembershipType(User.MembershipType membershipType);
    
    /**
     * Finds users registered between two dates.
     * @param startDate start date
     * @param endDate end date
     * @return list of users registered in the date range
     */
    List<User> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Finds all users.
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Deletes a user.
     * @param userId the user ID
     */
    void deleteById(String userId);
    
    /**
     * Checks if a user exists.
     * @param userId the user ID
     * @return true if user exists
     */
    boolean existsById(String userId);
    
    /**
     * Counts total number of users.
     * @return user count
     */
    long count();
}
