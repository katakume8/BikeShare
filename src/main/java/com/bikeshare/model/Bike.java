package com.bikeshare.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a bike in the bike sharing system.
 * This is a core entity that demonstrates various testing scenarios:
 * - Simple value object testing (Lab 1)
 * - State transitions (Lab 3) 
 * - Boundary conditions (Lab 2)
 */
public class Bike {
    
    public enum BikeStatus {
        AVAILABLE,
        IN_USE,
        MAINTENANCE,
        BROKEN,
        RESERVED
    }
    
    public enum BikeType {
        STANDARD("Standard", 0.0),
        ELECTRIC("Electric", 1.5),
        PREMIUM("Premium", 2.0);
        
        private final String displayName;
        private final double priceMultiplier;
        
        BikeType(String displayName, double priceMultiplier) {
            this.displayName = displayName;
            this.priceMultiplier = priceMultiplier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getPriceMultiplier() {
            return priceMultiplier;
        }
    }
    
    private final String bikeId;
    private BikeStatus status;
    private BikeType type;
    private String currentStationId;
    private double batteryLevel; // 0-100 for electric bikes, -1 for non-electric
    private int totalRides;
    private double totalDistance; // in kilometers
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime lastUsedDate;
    private boolean needsMaintenance;
    
    public Bike(String bikeId, BikeType type) {
        if (bikeId == null || bikeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bike ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Bike type cannot be null");
        }
        
        this.bikeId = bikeId.trim();
        this.type = type;
        this.status = BikeStatus.AVAILABLE;
        this.batteryLevel = type == BikeType.ELECTRIC ? 100.0 : -1.0;
        this.totalRides = 0;
        this.totalDistance = 0.0;
        this.lastMaintenanceDate = LocalDateTime.now();
        this.needsMaintenance = false;
    }
    
    // Business Logic Methods - Great for testing state transitions
    
    /**
     * Reserves the bike for a user.
     * @throws IllegalStateException if bike is not available
     */
    public void reserve() {
        if (status != BikeStatus.AVAILABLE) {
            throw new IllegalStateException("Cannot reserve bike in status: " + status);
        }
        this.status = BikeStatus.RESERVED;
    }
    
    /**
     * Starts a ride with the bike.
     * @throws IllegalStateException if bike is not available or reserved
     */
    public void startRide() {
        if (status != BikeStatus.AVAILABLE && status != BikeStatus.RESERVED) {
            throw new IllegalStateException("Cannot start ride with bike in status: " + status);
        }
        if (type == BikeType.ELECTRIC && batteryLevel < 10.0) {
            throw new IllegalStateException("Electric bike battery too low to start ride");
        }
        
        this.status = BikeStatus.IN_USE;
        this.lastUsedDate = LocalDateTime.now();
    }
    
    /**
     * Ends a ride and updates bike statistics.
     * @param distanceTraveled distance in kilometers
     * @throws IllegalStateException if bike is not in use
     * @throws IllegalArgumentException if distance is negative
     */
    public void endRide(double distanceTraveled) {
        if (status != BikeStatus.IN_USE) {
            throw new IllegalStateException("Cannot end ride for bike not in use");
        }
        if (distanceTraveled < 0) {
            throw new IllegalArgumentException("Distance traveled cannot be negative");
        }
        
        this.status = BikeStatus.AVAILABLE;
        this.totalRides++;
        this.totalDistance += distanceTraveled;
        
        // Update battery for electric bikes
        if (type == BikeType.ELECTRIC) {
            double batteryUsed = distanceTraveled * 2.0; // 2% per km
            this.batteryLevel = Math.max(0, batteryLevel - batteryUsed);
        }
        
        // Check if maintenance is needed
        checkMaintenanceRequirement();
    }
    
    /**
     * Sends bike to maintenance.
     */
    public void sendToMaintenance() {
        if (status == BikeStatus.IN_USE) {
            throw new IllegalStateException("Cannot send bike to maintenance while in use");
        }
        this.status = BikeStatus.MAINTENANCE;
    }
    
    /**
     * Completes maintenance and returns bike to service.
     */
    public void completeMaintenance() {
        if (status != BikeStatus.MAINTENANCE) {
            throw new IllegalStateException("Bike is not in maintenance");
        }
        
        this.status = BikeStatus.AVAILABLE;
        this.lastMaintenanceDate = LocalDateTime.now();
        this.needsMaintenance = false;
        
        // Restore battery for electric bikes
        if (type == BikeType.ELECTRIC) {
            this.batteryLevel = 100.0;
        }
    }
    
    /**
     * Marks bike as broken.
     */
    public void markAsBroken() {
        this.status = BikeStatus.BROKEN;
        this.needsMaintenance = true;
    }
    
    /**
     * Charges the battery (electric bikes only).
     * @param chargeAmount amount to charge (0-100)
     */
    public void chargeBattery(double chargeAmount) {
        if (type != BikeType.ELECTRIC) {
            throw new IllegalStateException("Cannot charge non-electric bike");
        }
        if (chargeAmount < 0 || chargeAmount > 100) {
            throw new IllegalArgumentException("Charge amount must be between 0 and 100");
        }
        
        this.batteryLevel = Math.min(100.0, batteryLevel + chargeAmount);
    }
    
    // Helper method for maintenance logic
    private void checkMaintenanceRequirement() {
        // Maintenance needed every 100 rides or 1000km
        if (totalRides % 100 == 0 || totalDistance >= 1000.0) {
            this.needsMaintenance = true;
        }
        
        // Electric bikes need maintenance if battery degraded
        if (type == BikeType.ELECTRIC && batteryLevel < 5.0) {
            this.needsMaintenance = true;
        }
    }
    
    // Getters
    public String getBikeId() {
        return bikeId;
    }
    
    public BikeStatus getStatus() {
        return status;
    }
    
    public BikeType getType() {
        return type;
    }
    
    public String getCurrentStationId() {
        return currentStationId;
    }
    
    public void setCurrentStationId(String currentStationId) {
        this.currentStationId = currentStationId;
    }
    
    public double getBatteryLevel() {
        return batteryLevel;
    }
    
    public int getTotalRides() {
        return totalRides;
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public LocalDateTime getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }
    
    public LocalDateTime getLastUsedDate() {
        return lastUsedDate;
    }
    
    public boolean needsMaintenance() {
        return needsMaintenance;
    }
    
    public boolean isAvailable() {
        return status == BikeStatus.AVAILABLE && !needsMaintenance;
    }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bike bike = (Bike) o;
        return Objects.equals(bikeId, bike.bikeId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bikeId);
    }
    
    @Override
    public String toString() {
        return String.format("Bike{id='%s', type=%s, status=%s, battery=%.1f%%, rides=%d}", 
                bikeId, type, status, batteryLevel, totalRides);
    }
}
