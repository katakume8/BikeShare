package com.bikeshare.service;

import com.bikeshare.model.User;

/**
 * Service interface for sending notifications.
 * Demonstrates interface-based testing and mocking (Lab 3).
 */
public interface NotificationService {
    
    /**
     * Sends a welcome email to a new user.
     * @param user the new user
     */
    void sendWelcomeEmail(User user);
    
    /**
     * Sends account activation confirmation.
     * @param user the activated user
     */
    void sendActivationConfirmation(User user);
    
    /**
     * Sends suspension notification.
     * @param user the suspended user
     * @param reason reason for suspension
     */
    void sendSuspensionNotification(User user, String reason);
    
    /**
     * Sends reactivation notification.
     * @param user the reactivated user
     */
    void sendReactivationNotification(User user);
    
    /**
     * Sends funds added notification.
     * @param user the user
     * @param amount amount added
     */
    void sendFundsAddedNotification(User user, double amount);
    
    /**
     * Sends membership change notification.
     * @param user the user
     * @param oldMembership previous membership type
     * @param newMembership new membership type
     */
    void sendMembershipChangeNotification(User user, User.MembershipType oldMembership, 
                                        User.MembershipType newMembership);
    
    /**
     * Sends ride completion notification.
     * @param user the user
     * @param rideId the completed ride ID
     * @param cost final cost of the ride
     */
    void sendRideCompletionNotification(User user, String rideId, double cost);
    
    /**
     * Sends low balance warning.
     * @param user the user with low balance
     * @param currentBalance current account balance
     */
    void sendLowBalanceWarning(User user, double currentBalance);
}
