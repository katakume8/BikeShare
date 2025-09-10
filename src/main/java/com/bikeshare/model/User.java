package com.bikeshare.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a user in the bike sharing system.
 * Demonstrates various testing scenarios:
 * - Input validation (Lab 2 - boundary testing)
 * - Business rules (Lab 3 - structural testing)
 * - State management (Lab 4 - regression testing)
 */
public class User {
    
    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        INACTIVE,
        PENDING_VERIFICATION
    }
    
    public enum MembershipType {
        BASIC("Basic", 5.0, 60),
        PREMIUM("Premium", 10.0, 120),
        STUDENT("Student", 3.0, 45),
        CORPORATE("Corporate", 15.0, 180);
        
        private final String displayName;
        private final double monthlyFee;
        private final int freeMinutesPerRide;
        
        MembershipType(String displayName, double monthlyFee, int freeMinutesPerRide) {
            this.displayName = displayName;
            this.monthlyFee = monthlyFee;
            this.freeMinutesPerRide = freeMinutesPerRide;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getMonthlyFee() {
            return monthlyFee;
        }
        
        public int getFreeMinutesPerRide() {
            return freeMinutesPerRide;
        }
    }
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+46|0)7[0-9]\\d{7}$"
    );
    
    // Swedish personnummer pattern: YYMMDD-NNNN
    private static final Pattern PERSONNUMMER_PATTERN = Pattern.compile(
        "^\\d{6}-\\d{4}$"
    );
    
    private final String userId; // Swedish personnummer (YYMMDD-NNNN) validated with Luhn algorithm
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private MembershipType membershipType;
    private LocalDateTime registrationDate;
    private LocalDateTime lastActiveDate;
    private double accountBalance;
    private int totalRides;
    private double totalSpent;
    private boolean emailVerified;
    private boolean phoneVerified;
    private int suspensionCount;
    private String currentRideId;
    
    public User(String userId, String email, String firstName, String lastName) {
        if (!isValidPersonnummer(userId)) {
            throw new IllegalArgumentException("Invalid Swedish personnummer format or checksum");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        
        this.userId = userId.trim();
        this.email = email.toLowerCase().trim();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.status = UserStatus.PENDING_VERIFICATION;
        this.membershipType = MembershipType.BASIC;
        this.registrationDate = LocalDateTime.now();
        this.accountBalance = 0.0;
        this.totalRides = 0;
        this.totalSpent = 0.0;
        this.emailVerified = false;
        this.phoneVerified = false;
        this.suspensionCount = 0;
    }
    
    // Business Logic Methods
    
    /**
     * Activates the user account after verification.
     * @throws IllegalStateException if user is not pending verification
     */
    public void activate() {
        if (status != UserStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException("User must be pending verification to activate");
        }
        if (!emailVerified) {
            throw new IllegalStateException("Email must be verified before activation");
        }
        
        this.status = UserStatus.ACTIVE;
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Suspends the user account.
     * @param reason reason for suspension
     * @throws IllegalStateException if user is already suspended or inactive
     */
    public void suspend(String reason) {
        if (status == UserStatus.SUSPENDED || status == UserStatus.INACTIVE) {
            throw new IllegalStateException("Cannot suspend user in status: " + status);
        }
        if (currentRideId != null) {
            throw new IllegalStateException("Cannot suspend user with active ride");
        }
        
        this.status = UserStatus.SUSPENDED;
        this.suspensionCount++;
    }
    
    /**
     * Reactivates a suspended user.
     * @throws IllegalStateException if user is not suspended
     */
    public void reactivate() {
        if (status != UserStatus.SUSPENDED) {
            throw new IllegalStateException("User is not suspended");
        }
        
        this.status = UserStatus.ACTIVE;
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        if (currentRideId != null) {
            throw new IllegalStateException("Cannot deactivate user with active ride");
        }
        
        this.status = UserStatus.INACTIVE;
    }
    
    /**
     * Adds funds to the user's account.
     * @param amount amount to add (must be positive)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void addFunds(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > 1000.0) {
            throw new IllegalArgumentException("Cannot add more than $1000 at once");
        }
        
        this.accountBalance += amount;
    }
    
    /**
     * Deducts funds from the user's account.
     * @param amount amount to deduct
     * @throws IllegalArgumentException if amount is negative
     * @throws IllegalStateException if insufficient balance
     */
    public void deductFunds(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (accountBalance < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        this.accountBalance -= amount;
        this.totalSpent += amount;
    }
    
    /**
     * Updates membership type.
     * @param newMembershipType new membership type
     * @throws IllegalArgumentException if membership type is null
     */
    public void updateMembership(MembershipType newMembershipType) {
        if (newMembershipType == null) {
            throw new IllegalArgumentException("Membership type cannot be null");
        }
        if (status != UserStatus.ACTIVE) {
            throw new IllegalStateException("User must be active to change membership");
        }
        
        this.membershipType = newMembershipType;
    }
    
    /**
     * Sets phone number with validation.
     * @param phoneNumber phone number to set
     * @throws IllegalArgumentException if phone number is invalid
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            String cleanPhone = phoneNumber.trim().replaceAll("[\\s()-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
            this.phoneNumber = cleanPhone;
        } else {
            this.phoneNumber = null;
        }
        this.phoneVerified = false;
    }
    
    /**
     * Verifies email address.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        if (status == UserStatus.PENDING_VERIFICATION && phoneVerified) {
            activate();
        }
    }
    
    /**
     * Verifies phone number.
     */
    public void verifyPhone() {
        if (phoneNumber == null) {
            throw new IllegalStateException("Cannot verify null phone number");
        }
        this.phoneVerified = true;
    }
    
    /**
     * Starts a ride for this user.
     * @param rideId the ride ID
     * @throws IllegalStateException if user already has an active ride or is not active
     */
    public void startRide(String rideId) {
        if (status != UserStatus.ACTIVE) {
            throw new IllegalStateException("User must be active to start ride");
        }
        if (currentRideId != null) {
            throw new IllegalStateException("User already has an active ride");
        }
        if (accountBalance < 5.0) { // Minimum balance check
            throw new IllegalStateException("Insufficient balance to start ride");
        }
        
        this.currentRideId = rideId;
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Ends the current ride.
     * @throws IllegalStateException if no active ride
     */
    public void endRide() {
        if (currentRideId == null) {
            throw new IllegalStateException("No active ride to end");
        }
        
        this.currentRideId = null;
        this.totalRides++;
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Calculates discount based on membership and usage.
     * @return discount percentage (0.0 to 1.0)
     */
    public double calculateDiscount() {
        double discount = 0.0;
        
        // Membership discounts
        switch (membershipType) {
            case PREMIUM -> discount += 0.15;
            case STUDENT -> discount += 0.20;
            case CORPORATE -> discount += 0.10;
        }
        
        // Loyalty discounts
        if (totalRides > 100) {
            discount += 0.05;
        } else if (totalRides > 50) {
            discount += 0.03;
        }
        
        return Math.min(discount, 0.30); // Max 30% discount
    }
    
    // Validation methods
    private static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates Swedish personnummer using format YYMMDD-NNNN and Luhn algorithm.
     * @param personnummer the personnummer to validate
     * @return true if valid, false otherwise
     */
    private static boolean isValidPersonnummer(String personnummer) {
        if (personnummer == null || personnummer.trim().isEmpty()) {
            return false;
        }
        
        String cleaned = personnummer.trim();
        if (!PERSONNUMMER_PATTERN.matcher(cleaned).matches()) {
            return false;
        }
        
        // Remove hyphen for Luhn validation
        String digits = cleaned.replace("-", "");
        return isValidLuhnChecksum(digits);
    }
    
    /**
     * Validates checksum using Luhn algorithm.
     * For personnummer: multiply positions 1,3,5,7,9 by 2 and positions 2,4,6,8,10 by 1.
     * @param digits 10-digit string
     * @return true if valid checksum, false otherwise
     */
    private static boolean isValidLuhnChecksum(String digits) {
        if (digits.length() != 10) {
            return false;
        }
        
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            if (digit < 0 || digit > 9) {
                return false;
            }
            
            // Multiply odd positions (1,3,5,7,9) by 2, even positions (2,4,6,8,10) by 1
            int multiplier = (i % 2 == 0) ? 2 : 1;
            int product = digit * multiplier;
            
            // If product > 9, sum the digits (e.g., 16 -> 1+6=7)
            if (product > 9) {
                product = (product / 10) + (product % 10);
            }
            sum += product;
        }
        
        // Calculate checksum: (10 - (sum mod 10)) mod 10
        int expectedChecksum = (10 - (sum % 10)) % 10;
        int actualChecksum = Character.getNumericValue(digits.charAt(9));
        
        return expectedChecksum == actualChecksum;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.toLowerCase().trim();
        this.emailVerified = false;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName.trim();
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName.trim();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public MembershipType getMembershipType() {
        return membershipType;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public LocalDateTime getLastActiveDate() {
        return lastActiveDate;
    }
    
    public double getAccountBalance() {
        return accountBalance;
    }
    
    public int getTotalRides() {
        return totalRides;
    }
    
    public double getTotalSpent() {
        return totalSpent;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public boolean isPhoneVerified() {
        return phoneVerified;
    }
    
    public int getSuspensionCount() {
        return suspensionCount;
    }
    
    public String getCurrentRideId() {
        return currentRideId;
    }
    
    public boolean hasActiveRide() {
        return currentRideId != null;
    }
    
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', status=%s, membership=%s}", 
                userId, getFullName(), email, status, membershipType);
    }
}
