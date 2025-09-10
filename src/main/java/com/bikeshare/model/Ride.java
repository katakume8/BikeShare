package com.bikeshare.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a bike ride in the system.
 * Demonstrates complex business logic testing:
 * - Time-based calculations (Lab 3 - structural testing)
 * - Pricing algorithms (Lab 2 - boundary testing)
 * - State transitions (Lab 4 - regression testing)
 */
public class Ride {
    
    public enum RideStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED,
        PAUSED
    }
    
    private final String rideId;
    private final String userId;
    private final String bikeId;
    private final String startStationId;
    
    private String endStationId;
    private RideStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime pauseStartTime;
    private long totalPausedMinutes;
    private double distance;
    private double baseCost;
    private double additionalCost;
    private double discount;
    private double finalCost;
    private User.MembershipType membershipType;
    private boolean freeMinutesUsed;
    private String notes;
    
    public Ride(String rideId, String userId, String bikeId, String startStationId) {
        if (rideId == null || rideId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ride ID cannot be null or empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (bikeId == null || bikeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bike ID cannot be null or empty");
        }
        if (startStationId == null || startStationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Start station ID cannot be null or empty");
        }
        
        this.rideId = rideId.trim();
        this.userId = userId.trim();
        this.bikeId = bikeId.trim();
        this.startStationId = startStationId.trim();
        
        this.status = RideStatus.ACTIVE;
        this.startTime = LocalDateTime.now();
        this.totalPausedMinutes = 0L;
        this.distance = 0.0;
        this.baseCost = 0.0;
        this.additionalCost = 0.0;
        this.discount = 0.0;
        this.finalCost = 0.0;
        this.freeMinutesUsed = false;
    }
    
    // Business Logic Methods
    
    /**
     * Pauses the ride.
     * @throws IllegalStateException if ride is not active
     */
    public void pause() {
        if (status != RideStatus.ACTIVE) {
            throw new IllegalStateException("Can only pause active rides");
        }
        
        this.status = RideStatus.PAUSED;
        this.pauseStartTime = LocalDateTime.now();
    }
    
    /**
     * Resumes a paused ride.
     * @throws IllegalStateException if ride is not paused
     */
    public void resume() {
        if (status != RideStatus.PAUSED) {
            throw new IllegalStateException("Can only resume paused rides");
        }
        
        if (pauseStartTime != null) {
            long pausedMinutes = ChronoUnit.MINUTES.between(pauseStartTime, LocalDateTime.now());
            this.totalPausedMinutes += pausedMinutes;
            this.pauseStartTime = null;
        }
        
        this.status = RideStatus.ACTIVE;
    }
    
    /**
     * Completes the ride.
     * @param endStationId the station where the ride ends
     * @param distance the distance traveled in kilometers
     * @param bikeType the type of bike used
     * @param membershipType the user's membership type
     * @param userDiscount any user-specific discount (0.0 to 1.0)
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if ride cannot be completed
     */
    public void complete(String endStationId, double distance, Bike.BikeType bikeType, 
                        User.MembershipType membershipType, double userDiscount) {
        if (status != RideStatus.ACTIVE && status != RideStatus.PAUSED) {
            throw new IllegalStateException("Can only complete active or paused rides");
        }
        if (endStationId == null || endStationId.trim().isEmpty()) {
            throw new IllegalArgumentException("End station ID cannot be null or empty");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (distance > 100) { // Sanity check
            throw new IllegalArgumentException("Distance seems unrealistic: " + distance + " km");
        }
        if (membershipType == null) {
            throw new IllegalArgumentException("Membership type cannot be null");
        }
        if (userDiscount < 0.0 || userDiscount > 1.0) {
            throw new IllegalArgumentException("User discount must be between 0.0 and 1.0");
        }
        
        // Handle paused state
        if (status == RideStatus.PAUSED && pauseStartTime != null) {
            long pausedMinutes = ChronoUnit.MINUTES.between(pauseStartTime, LocalDateTime.now());
            this.totalPausedMinutes += pausedMinutes;
            this.pauseStartTime = null;
        }
        
        this.endTime = LocalDateTime.now();
        this.endStationId = endStationId.trim();
        this.distance = distance;
        this.membershipType = membershipType;
        this.discount = userDiscount;
        this.status = RideStatus.COMPLETED;
        
        // Calculate costs
        calculateCosts(bikeType);
    }
    
    /**
     * Cancels the ride.
     * @param reason reason for cancellation
     * @throws IllegalStateException if ride cannot be cancelled
     */
    public void cancel(String reason) {
        if (status == RideStatus.COMPLETED || status == RideStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel ride in status: " + status);
        }
        
        this.status = RideStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
        this.notes = reason != null ? reason.trim() : "Cancelled";
        
        // Calculate minimal cancellation fee if ride was active for more than 5 minutes
        long activeMinutes = getActiveMinutes();
        if (activeMinutes > 5) {
            this.finalCost = 1.0; // $1 cancellation fee
        }
    }
    
    // Cost Calculation Methods
    
    private void calculateCosts(Bike.BikeType bikeType) {
        long activeMinutes = getActiveMinutes();
        int freeMinutes = membershipType.getFreeMinutesPerRide();
        
        // Determine if free minutes apply
        if (activeMinutes <= freeMinutes) {
            this.freeMinutesUsed = true;
            this.baseCost = 0.0;
        } else {
            long billableMinutes = activeMinutes - freeMinutes;
            this.freeMinutesUsed = freeMinutes > 0;
            
            // Base rate calculation
            double baseRate = calculateBaseRate(bikeType);
            this.baseCost = billableMinutes * baseRate;
        }
        
        // Additional costs
        calculateAdditionalCosts(activeMinutes, bikeType);
        
        // Apply discounts
        double totalBeforeDiscount = baseCost + additionalCost;
        double discountAmount = totalBeforeDiscount * discount;
        
        this.finalCost = Math.max(0.0, totalBeforeDiscount - discountAmount);
        
        // Round to nearest cent
        this.finalCost = Math.round(finalCost * 100.0) / 100.0;
    }
    
    private double calculateBaseRate(Bike.BikeType bikeType) {
        double baseRate = 0.15; // $0.15 per minute base rate
        
        // Apply bike type multiplier
        baseRate *= (1.0 + bikeType.getPriceMultiplier());
        
        // Peak hour surcharge (7-9 AM, 5-7 PM on weekdays)
        if (isPeakHour()) {
            baseRate *= 1.5;
        }
        
        return baseRate;
    }
    
    private void calculateAdditionalCosts(long activeMinutes, Bike.BikeType bikeType) {
        this.additionalCost = 0.0;
        
        // Long ride surcharge (over 2 hours)
        if (activeMinutes > 120) {
            long surchargeMinutes = activeMinutes - 120;
            this.additionalCost += surchargeMinutes * 0.25; // $0.25 per minute over 2 hours
        }
        
        // Distance-based cost for long rides
        if (distance > 10.0) {
            this.additionalCost += (distance - 10.0) * 0.50; // $0.50 per km over 10km
        }
        
        // Electric bike battery usage fee
        if (bikeType == Bike.BikeType.ELECTRIC && distance > 5.0) {
            this.additionalCost += 2.0; // $2 battery usage fee for long electric rides
        }
    }
    
    private boolean isPeakHour() {
        if (startTime == null) return false;
        
        int hour = startTime.getHour();
        int dayOfWeek = startTime.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        
        // Weekdays only
        if (dayOfWeek >= 1 && dayOfWeek <= 5) {
            return (hour >= 7 && hour < 9) || (hour >= 17 && hour < 19);
        }
        
        return false;
    }
    
    // Utility Methods
    
    /**
     * Gets the total duration of the ride in minutes, excluding paused time.
     * @return active minutes
     */
    public long getActiveMinutes() {
        if (startTime == null) return 0;
        
        LocalDateTime effectiveEndTime = endTime != null ? endTime : LocalDateTime.now();
        long totalMinutes = ChronoUnit.MINUTES.between(startTime, effectiveEndTime);
        
        // Subtract paused time
        return Math.max(0, totalMinutes - totalPausedMinutes);
    }
    
    /**
     * Gets the total duration including paused time.
     * @return total minutes
     */
    public long getTotalMinutes() {
        if (startTime == null) return 0;
        
        LocalDateTime effectiveEndTime = endTime != null ? endTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(startTime, effectiveEndTime);
    }
    
    /**
     * Checks if the ride is currently active.
     * @return true if ride is active
     */
    public boolean isActive() {
        return status == RideStatus.ACTIVE;
    }
    
    /**
     * Checks if the ride is completed.
     * @return true if ride is completed
     */
    public boolean isCompleted() {
        return status == RideStatus.COMPLETED;
    }
    
    /**
     * Gets ride summary for display.
     * @return formatted ride summary
     */
    public String getSummary() {
        if (status == RideStatus.COMPLETED) {
            return String.format("Ride %s: %.1f km in %d min - $%.2f", 
                    rideId, distance, getActiveMinutes(), finalCost);
        } else {
            return String.format("Ride %s: %s (%d min active)", 
                    rideId, status, getActiveMinutes());
        }
    }
    
    // Getters and Setters
    
    public String getRideId() {
        return rideId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getBikeId() {
        return bikeId;
    }
    
    public String getStartStationId() {
        return startStationId;
    }
    
    public String getEndStationId() {
        return endStationId;
    }
    
    public RideStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public long getTotalPausedMinutes() {
        return totalPausedMinutes;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public double getBaseCost() {
        return baseCost;
    }
    
    public double getAdditionalCost() {
        return additionalCost;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public double getFinalCost() {
        return finalCost;
    }
    
    public User.MembershipType getMembershipType() {
        return membershipType;
    }
    
    public boolean isFreeMinutesUsed() {
        return freeMinutesUsed;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return Objects.equals(rideId, ride.rideId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(rideId);
    }
    
    @Override
    public String toString() {
        return String.format("Ride{id='%s', user='%s', bike='%s', status=%s, duration=%d min}", 
                rideId, userId, bikeId, status, getActiveMinutes());
    }
}
