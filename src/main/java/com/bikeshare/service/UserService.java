package com.bikeshare.service;

import com.bikeshare.model.User;
import com.bikeshare.repository.UserRepository;
import com.bikeshare.service.exception.ServiceException;
import com.bikeshare.service.exception.UserNotFoundException;
import com.bikeshare.service.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users in the bike share system.
 * Demonstrates testing patterns:
 * - Dependency injection and mocking (Lab 3)
 * - Service layer validation (Lab 2)
 * - Exception handling (Lab 4)
 * - Business logic orchestration (Lab 5)
 */
public class UserService {
    
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    
    public UserService(UserRepository userRepository, 
                      NotificationService notificationService,
                      PaymentService paymentService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
    }
    
    /**
     * Creates a new user account.
     * @param email user's email
     * @param firstName user's first name
     * @param lastName user's last name
     * @param phoneNumber user's phone number (optional)
     * @return created user
     * @throws ValidationException if user data is invalid
     * @throws ServiceException if user creation fails
     */
    public User createUser(String email, String firstName, String lastName, String phoneNumber) {
        // Validate input
        validateUserInput(email, firstName, lastName);
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("User with email " + email + " already exists");
        }
        
        // Generate user ID
        String userId = generateUserId();
        
        // Create user
        User user = new User(userId, email, firstName, lastName);
        
        // Set phone number if provided
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Send welcome notification
        try {
            notificationService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            // Log error but don't fail user creation
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
        
        return savedUser;
    }
    
    /**
     * Retrieves a user by ID.
     * @param userId the user ID
     * @return the user
     * @throws UserNotFoundException if user is not found
     */
    public User getUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
    
    /**
     * Retrieves a user by email.
     * @param email the user's email
     * @return the user
     * @throws UserNotFoundException if user is not found
     */
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }
    
    /**
     * Updates user profile information.
     * @param userId the user ID
     * @param firstName new first name (optional)
     * @param lastName new last name (optional)
     * @param phoneNumber new phone number (optional)
     * @return updated user
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if update data is invalid
     */
    public User updateUserProfile(String userId, String firstName, String lastName, String phoneNumber) {
        User user = getUserById(userId);
        
        boolean updated = false;
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            user.setFirstName(firstName);
            updated = true;
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            user.setLastName(lastName);
            updated = true;
        }
        
        if (phoneNumber != null) {
            if (phoneNumber.trim().isEmpty()) {
                user.setPhoneNumber(null);
            } else {
                user.setPhoneNumber(phoneNumber);
            }
            updated = true;
        }
        
        if (!updated) {
            throw new ValidationException("No valid updates provided");
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Activates a user account after email verification.
     * @param userId the user ID
     * @param verificationCode the verification code
     * @return activated user
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if activation fails
     */
    public User activateUser(String userId, String verificationCode) {
        User user = getUserById(userId);
        
        if (user.getStatus() != User.UserStatus.PENDING_VERIFICATION) {
            throw new ValidationException("User is not pending verification");
        }
        
        // Verify the verification code (simplified)
        if (!isValidVerificationCode(userId, verificationCode)) {
            throw new ValidationException("Invalid verification code");
        }
        
        // Mark email as verified and activate
        user.verifyEmail();
        
        User activatedUser = userRepository.save(user);
        
        // Send activation confirmation
        try {
            notificationService.sendActivationConfirmation(activatedUser);
        } catch (Exception e) {
            System.err.println("Failed to send activation confirmation: " + e.getMessage());
        }
        
        return activatedUser;
    }
    
    /**
     * Suspends a user account.
     * @param userId the user ID
     * @param reason reason for suspension
     * @param adminUserId ID of admin performing suspension
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if suspension is not allowed
     */
    public void suspendUser(String userId, String reason, String adminUserId) {
        User user = getUserById(userId);
        
        if (user.hasActiveRide()) {
            throw new ValidationException("Cannot suspend user with active ride");
        }
        
        user.suspend(reason);
        userRepository.save(user);
        
        // Log suspension
        System.out.println(String.format("User %s suspended by %s. Reason: %s", 
                userId, adminUserId, reason));
        
        // Notify user
        try {
            notificationService.sendSuspensionNotification(user, reason);
        } catch (Exception e) {
            System.err.println("Failed to send suspension notification: " + e.getMessage());
        }
    }
    
    /**
     * Reactivates a suspended user.
     * @param userId the user ID
     * @param adminUserId ID of admin performing reactivation
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if reactivation is not allowed
     */
    public void reactivateUser(String userId, String adminUserId) {
        User user = getUserById(userId);
        
        user.reactivate();
        userRepository.save(user);
        
        // Log reactivation
        System.out.println(String.format("User %s reactivated by %s", userId, adminUserId));
        
        // Notify user
        try {
            notificationService.sendReactivationNotification(user);
        } catch (Exception e) {
            System.err.println("Failed to send reactivation notification: " + e.getMessage());
        }
    }
    
    /**
     * Adds funds to a user's account.
     * @param userId the user ID
     * @param amount amount to add
     * @param paymentMethodId payment method identifier
     * @return updated user
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if payment fails
     */
    public User addFunds(String userId, double amount, String paymentMethodId) {
        User user = getUserById(userId);
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new ValidationException("User must be active to add funds");
        }
        
        // Process payment
        try {
            paymentService.processPayment(paymentMethodId, amount, userId);
        } catch (Exception e) {
            throw new ValidationException("Payment processing failed: " + e.getMessage());
        }
        
        // Add funds to account
        user.addFunds(amount);
        
        User updatedUser = userRepository.save(user);
        
        // Send confirmation
        try {
            notificationService.sendFundsAddedNotification(updatedUser, amount);
        } catch (Exception e) {
            System.err.println("Failed to send funds notification: " + e.getMessage());
        }
        
        return updatedUser;
    }
    
    /**
     * Updates user's membership type.
     * @param userId the user ID
     * @param newMembershipType new membership type
     * @return updated user
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if update is not allowed
     */
    public User updateMembership(String userId, User.MembershipType newMembershipType) {
        User user = getUserById(userId);
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new ValidationException("User must be active to change membership");
        }
        
        User.MembershipType oldMembership = user.getMembershipType();
        user.updateMembership(newMembershipType);
        
        User updatedUser = userRepository.save(user);
        
        // Send notification about membership change
        try {
            notificationService.sendMembershipChangeNotification(updatedUser, oldMembership, newMembershipType);
        } catch (Exception e) {
            System.err.println("Failed to send membership change notification: " + e.getMessage());
        }
        
        return updatedUser;
    }
    
    /**
     * Gets all active users.
     * @return list of active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByStatus(User.UserStatus.ACTIVE);
    }
    
    /**
     * Gets users by membership type.
     * @param membershipType the membership type
     * @return list of users with the specified membership
     */
    public List<User> getUsersByMembership(User.MembershipType membershipType) {
        return userRepository.findByMembershipType(membershipType);
    }
    
    /**
     * Gets users registered within a date range.
     * @param startDate start date
     * @param endDate end date
     * @return list of users registered in the date range
     */
    public List<User> getUsersRegisteredBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return userRepository.findByRegistrationDateBetween(startDate, endDate);
    }
    
    /**
     * Deletes a user account (soft delete).
     * @param userId the user ID
     * @param adminUserId ID of admin performing deletion
     * @throws UserNotFoundException if user is not found
     * @throws ValidationException if deletion is not allowed
     */
    public void deleteUser(String userId, String adminUserId) {
        User user = getUserById(userId);
        
        if (user.hasActiveRide()) {
            throw new ValidationException("Cannot delete user with active ride");
        }
        
        if (user.getAccountBalance() > 0) {
            throw new ValidationException("Cannot delete user with positive balance. Please refund first.");
        }
        
        user.deactivate();
        userRepository.save(user);
        
        // Log deletion
        System.out.println(String.format("User %s deleted by %s", userId, adminUserId));
    }
    
    // Helper methods
    
    private void validateUserInput(String email, String firstName, String lastName) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be null or empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name cannot be null or empty");
        }
        
        // Additional email format validation is done in User constructor
    }
    
    private String generateUserId() {
        // Simple ID generation - in real system would be more sophisticated
        return "USER_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    private boolean isValidVerificationCode(String userId, String verificationCode) {
        // Simplified verification - in real system would check against stored codes
        return verificationCode != null && verificationCode.length() == 6 && 
               verificationCode.matches("\\d{6}");
    }
}
