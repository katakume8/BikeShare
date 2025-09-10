package com.bikeshare.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a bike station in the bike sharing system.
 * Demonstrates testing scenarios involving:
 * - Collections and threading (Lab 5 - CI/CD considerations)
 * - Capacity management (Lab 2 - boundary testing)
 * - Concurrent operations (Lab 6 - sustainability/performance)
 */
public class Station {
    
    public enum StationStatus {
        ACTIVE,
        MAINTENANCE,
        INACTIVE,
        FULL,
        EMPTY
    }
    
    private final String stationId;
    private final String name;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final int capacity;
    
    private StationStatus status;
    private final Map<String, Bike> availableBikes;
    private final Set<String> reservedBikeIds;
    private int totalDocks;
    private boolean chargingAvailable;
    private double chargingRate; // per hour
    
    public Station(String stationId, String name, String address, 
                   double latitude, double longitude, int capacity) {
        if (stationId == null || stationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be null or empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (capacity > 100) {
            throw new IllegalArgumentException("Capacity cannot exceed 100");
        }
        if (!isValidCoordinate(latitude, longitude)) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        
        this.stationId = stationId.trim();
        this.name = name.trim();
        this.address = address != null ? address.trim() : "";
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.totalDocks = capacity;
        
        this.status = StationStatus.ACTIVE;
        this.availableBikes = new ConcurrentHashMap<>();
        this.reservedBikeIds = ConcurrentHashMap.newKeySet();
        this.chargingAvailable = false;
        this.chargingRate = 0.0;
    }
    
    // Business Logic Methods
    
    /**
     * Adds a bike to the station.
     * @param bike the bike to add
     * @throws IllegalArgumentException if bike is null
     * @throws IllegalStateException if station is full or bike is not available
     */
    public synchronized void addBike(Bike bike) {
        if (bike == null) {
            throw new IllegalArgumentException("Bike cannot be null");
        }
        if (status == StationStatus.INACTIVE) {
            throw new IllegalStateException("Cannot add bike to inactive station");
        }
        if (isFull()) {
            throw new IllegalStateException("Station is full");
        }
        if (bike.getStatus() != Bike.BikeStatus.AVAILABLE) {
            throw new IllegalStateException("Can only add available bikes to station");
        }
        if (availableBikes.containsKey(bike.getBikeId())) {
            throw new IllegalStateException("Bike already at this station");
        }
        
        availableBikes.put(bike.getBikeId(), bike);
        bike.setCurrentStationId(this.stationId);
        updateStationStatus();
    }
    
    /**
     * Removes a bike from the station.
     * @param bikeId the ID of the bike to remove
     * @return the removed bike
     * @throws IllegalArgumentException if bike ID is null
     * @throws IllegalStateException if bike is not at station or is reserved
     */
    public synchronized Bike removeBike(String bikeId) {
        if (bikeId == null) {
            throw new IllegalArgumentException("Bike ID cannot be null");
        }
        if (!availableBikes.containsKey(bikeId)) {
            throw new IllegalStateException("Bike not found at this station");
        }
        if (reservedBikeIds.contains(bikeId)) {
            throw new IllegalStateException("Cannot remove reserved bike");
        }
        
        Bike bike = availableBikes.remove(bikeId);
        bike.setCurrentStationId(null);
        updateStationStatus();
        
        return bike;
    }
    
    /**
     * Reserves a bike at the station.
     * @param bikeId the ID of the bike to reserve
     * @throws IllegalArgumentException if bike ID is null
     * @throws IllegalStateException if bike is not available at station
     */
    public synchronized void reserveBike(String bikeId) {
        if (bikeId == null) {
            throw new IllegalArgumentException("Bike ID cannot be null");
        }
        if (!availableBikes.containsKey(bikeId)) {
            throw new IllegalStateException("Bike not available at this station");
        }
        if (reservedBikeIds.contains(bikeId)) {
            throw new IllegalStateException("Bike already reserved");
        }
        
        Bike bike = availableBikes.get(bikeId);
        bike.reserve();
        reservedBikeIds.add(bikeId);
    }
    
    /**
     * Cancels a bike reservation.
     * @param bikeId the ID of the bike to unreserve
     * @throws IllegalArgumentException if bike ID is null
     * @throws IllegalStateException if bike is not reserved
     */
    public synchronized void cancelReservation(String bikeId) {
        if (bikeId == null) {
            throw new IllegalArgumentException("Bike ID cannot be null");
        }
        if (!reservedBikeIds.contains(bikeId)) {
            throw new IllegalStateException("Bike is not reserved");
        }
        
        reservedBikeIds.remove(bikeId);
        // Note: Bike status change handled by bike itself
    }
    
    /**
     * Gets an available bike of the specified type.
     * @param preferredType preferred bike type (null for any)
     * @return available bike or null if none found
     */
    public synchronized Bike getAvailableBike(Bike.BikeType preferredType) {
        if (status != StationStatus.ACTIVE) {
            return null;
        }
        
        // First try to find preferred type
        if (preferredType != null) {
            for (Bike bike : availableBikes.values()) {
                if (bike.getType() == preferredType && 
                    bike.isAvailable() && 
                    !reservedBikeIds.contains(bike.getBikeId())) {
                    return bike;
                }
            }
        }
        
        // If no preferred type found, return any available bike
        for (Bike bike : availableBikes.values()) {
            if (bike.isAvailable() && !reservedBikeIds.contains(bike.getBikeId())) {
                return bike;
            }
        }
        
        return null;
    }
    
    /**
     * Gets all bikes currently at the station.
     * @return unmodifiable collection of bikes
     */
    public Collection<Bike> getAllBikes() {
        return Collections.unmodifiableCollection(availableBikes.values());
    }
    
    /**
     * Gets available bikes by type.
     * @param bikeType the bike type to filter by
     * @return list of available bikes of the specified type
     */
    public List<Bike> getAvailableBikesByType(Bike.BikeType bikeType) {
        return availableBikes.values().stream()
            .filter(bike -> bike.getType() == bikeType)
            .filter(bike -> bike.isAvailable())
            .filter(bike -> !reservedBikeIds.contains(bike.getBikeId()))
            .toList();
    }
    
    /**
     * Calculates distance to another station.
     * @param other the other station
     * @return distance in kilometers
     */
    public double distanceTo(Station other) {
        if (other == null) {
            throw new IllegalArgumentException("Other station cannot be null");
        }
        
        return calculateDistance(this.latitude, this.longitude, 
                               other.latitude, other.longitude);
    }
    
    /**
     * Enables charging capability for electric bikes.
     * @param hourlyRate charging rate per hour
     * @throws IllegalArgumentException if rate is negative
     */
    public void enableCharging(double hourlyRate) {
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Charging rate cannot be negative");
        }
        
        this.chargingAvailable = true;
        this.chargingRate = hourlyRate;
    }
    
    /**
     * Disables charging capability.
     */
    public void disableCharging() {
        this.chargingAvailable = false;
        this.chargingRate = 0.0;
    }
    
    /**
     * Charges all electric bikes at the station.
     * @param chargeAmount amount to charge each bike
     * @throws IllegalStateException if charging not available
     */
    public synchronized void chargeElectricBikes(double chargeAmount) {
        if (!chargingAvailable) {
            throw new IllegalStateException("Charging not available at this station");
        }
        
        for (Bike bike : availableBikes.values()) {
            if (bike.getType() == Bike.BikeType.ELECTRIC && 
                bike.getStatus() == Bike.BikeStatus.AVAILABLE) {
                try {
                    bike.chargeBattery(chargeAmount);
                } catch (IllegalStateException e) {
                    // Skip bikes that can't be charged
                }
            }
        }
    }
    
    // Status management
    
    /**
     * Sets station to maintenance mode.
     */
    public void setMaintenance() {
        this.status = StationStatus.MAINTENANCE;
    }
    
    /**
     * Activates station from maintenance.
     */
    public void activate() {
        this.status = StationStatus.ACTIVE;
        updateStationStatus();
    }
    
    /**
     * Deactivates the station.
     */
    public void deactivate() {
        this.status = StationStatus.INACTIVE;
    }
    
    // Helper methods
    
    private void updateStationStatus() {
        if (status == StationStatus.MAINTENANCE || status == StationStatus.INACTIVE) {
            return; // Don't change status if in maintenance or inactive
        }
        
        int availableCount = (int) availableBikes.values().stream()
            .filter(bike -> bike.isAvailable())
            .filter(bike -> !reservedBikeIds.contains(bike.getBikeId()))
            .count();
            
        if (availableCount == 0) {
            this.status = StationStatus.EMPTY;
        } else if (availableBikes.size() >= capacity) {
            this.status = StationStatus.FULL;
        } else {
            this.status = StationStatus.ACTIVE;
        }
    }
    
    private static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance calculation
        final double R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    // Getters
    
    public String getStationId() {
        return stationId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public StationStatus getStatus() {
        return status;
    }
    
    public int getAvailableBikeCount() {
        return (int) availableBikes.values().stream()
            .filter(bike -> bike.isAvailable())
            .filter(bike -> !reservedBikeIds.contains(bike.getBikeId()))
            .count();
    }
    
    public int getTotalBikeCount() {
        return availableBikes.size();
    }
    
    public int getAvailableDocks() {
        return capacity - availableBikes.size();
    }
    
    public boolean isFull() {
        return availableBikes.size() >= capacity;
    }
    
    public boolean isEmpty() {
        return getAvailableBikeCount() == 0;
    }
    
    public boolean isChargingAvailable() {
        return chargingAvailable;
    }
    
    public double getChargingRate() {
        return chargingRate;
    }
    
    public Set<String> getReservedBikeIds() {
        return Collections.unmodifiableSet(reservedBikeIds);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(stationId, station.stationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(stationId);
    }
    
    @Override
    public String toString() {
        return String.format("Station{id='%s', name='%s', status=%s, bikes=%d/%d}", 
                stationId, name, status, getAvailableBikeCount(), capacity);
    }
}
