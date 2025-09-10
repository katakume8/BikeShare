package com.bikeshare.service;

import java.util.List;

import com.bikeshare.model.Bike;
import com.bikeshare.model.BikeType;
import com.bikeshare.model.Station;
import com.bikeshare.repository.BikeRepository;
import com.bikeshare.service.exception.BikeNotAvailableException;
import com.bikeshare.service.exception.BikeNotFoundException;
import com.bikeshare.service.exception.InvalidBikeOperationException;

/**
 * Service class for bike-related business operations.
 * Handles bike management, availability, and maintenance operations.
 */
public class BikeService {
    
    private final BikeRepository bikeRepository;
    
    /**
     * Constructor for BikeService.
     * @param bikeRepository The bike repository for data access
     */
    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }
    
    /**
     * Creates a new bike in the system.
     * @param bikeId The unique identifier for the bike
     * @param bikeType The type of bike
     * @return The created bike
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Bike createBike(String bikeId, BikeType bikeType) {
        validateBikeId(bikeId);
        validateBikeType(bikeType);
        
        if (bikeRepository.existsById(bikeId)) {
            throw new IllegalArgumentException("Bike with ID " + bikeId + " already exists");
        }
        
        // Map model BikeType to Bike.BikeType (STANDARD/ELECTRIC/PREMIUM)
        Bike.BikeType mappedType = switch (bikeType) {
            case STANDARD -> Bike.BikeType.STANDARD;
            case ELECTRIC -> Bike.BikeType.ELECTRIC;
            case MOUNTAIN, CARGO -> Bike.BikeType.PREMIUM;
        };
        Bike bike = new Bike(bikeId, mappedType);
        return bikeRepository.save(bike);
    }
    
    /**
     * Finds a bike by its ID.
     * @param bikeId The bike ID to search for
     * @return The bike if found
     * @throws BikeNotFoundException if bike is not found
     */
    public Bike findBikeById(String bikeId) {
        return bikeRepository.findById(bikeId)
                .orElseThrow(() -> new BikeNotFoundException("Bike not found with ID: " + bikeId));
    }
    
    /**
     * Gets all available bikes.
     * @return List of available bikes
     */
    public List<Bike> getAvailableBikes() {
        return bikeRepository.findAvailableBikes();
    }
    
    /**
     * Gets available bikes of a specific type.
     * @param bikeType The type of bikes to find
     * @return List of available bikes of the specified type
     */
    public List<Bike> getAvailableBikesByType(BikeType bikeType) {
        return bikeRepository.findByType(bikeType).stream()
                .filter(Bike::isAvailable)
                .toList();
    }
    
    /**
     * Gets all bikes at a specific station.
     * @param stationId The station ID
     * @return List of bikes at the station
     */
    public List<Bike> getBikesAtStation(String stationId) {
        return bikeRepository.findByStationId(stationId);
    }
    
    /**
     * Rents a bike to a user.
     * @param bikeId The bike ID to rent
     * @param userId The user ID renting the bike
     * @throws BikeNotFoundException if bike is not found
     * @throws BikeNotAvailableException if bike is not available
     */
    public void rentBike(String bikeId, String userId) {
        Bike bike = findBikeById(bikeId);
        
        if (!bike.isAvailable()) {
            throw new BikeNotAvailableException("Bike " + bikeId + " is not available for rent");
        }
        
        // In current model, starting ride is represented by startRide()
        bike.startRide();
        bikeRepository.save(bike);
    }
    
    /**
     * Returns a bike to a station.
     * @param bikeId The bike ID to return
     * @param station The station where the bike is returned
     * @throws BikeNotFoundException if bike is not found
     * @throws InvalidBikeOperationException if bike is not currently rented
     */
    public void returnBike(String bikeId, Station station) {
        Bike bike = findBikeById(bikeId);
        
        if (bike.isAvailable()) {
            throw new InvalidBikeOperationException("Bike " + bikeId + " is not currently rented");
        }
        
        if (station.isFull()) {
            throw new InvalidBikeOperationException("Station " + station.getStationId() + " has no available capacity");
        }
        
        // End ride and mark distance as 0 for service-level return
        bike.endRide(0.0);
        bike.setCurrentStationId(station.getStationId());
        bikeRepository.save(bike);
    }
    
    /**
     * Marks a bike as needing maintenance.
     * @param bikeId The bike ID
     * @param reason The reason for maintenance
     * @throws BikeNotFoundException if bike is not found
     * @throws InvalidBikeOperationException if bike is currently rented
     */
    public void markForMaintenance(String bikeId, String reason) {
        Bike bike = findBikeById(bikeId);
        
        if (!bike.isAvailable()) {
            throw new InvalidBikeOperationException("Cannot mark rented bike for maintenance");
        }
        
    // Current model marks bike as broken and needs maintenance
    bike.markAsBroken();
        bikeRepository.save(bike);
    }
    
    /**
     * Completes maintenance on a bike.
     * @param bikeId The bike ID
     * @throws BikeNotFoundException if bike is not found
     * @throws InvalidBikeOperationException if bike is not under maintenance
     */
    public void completeMaintenance(String bikeId) {
        Bike bike = findBikeById(bikeId);
        
    if (!bike.needsMaintenance()) {
            throw new InvalidBikeOperationException("Bike " + bikeId + " is not under maintenance");
        }
        
    bike.completeMaintenance();
        bikeRepository.save(bike);
    }
    
    /**
     * Gets bike statistics.
     * @return BikeStatistics object containing various metrics
     */
    public BikeStatistics getBikeStatistics() {
        long totalBikes = bikeRepository.count();
        long availableBikes = bikeRepository.countAvailable();
        List<Bike> allBikes = bikeRepository.findAll();
        
    long rentedBikes = allBikes.stream().filter(b -> !b.isAvailable()).count();
    long maintenanceBikes = allBikes.stream().filter(Bike::needsMaintenance).count();
        
        return new BikeStatistics(totalBikes, availableBikes, rentedBikes, maintenanceBikes);
    }
    
    /**
     * Validates bike ID.
     * @param bikeId The bike ID to validate
     * @throws IllegalArgumentException if bike ID is invalid
     */
    private void validateBikeId(String bikeId) {
        if (bikeId == null || bikeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bike ID cannot be null or empty");
        }
    }
    
    /**
     * Validates bike type.
     * @param bikeType The bike type to validate
     * @throws IllegalArgumentException if bike type is invalid
     */
    private void validateBikeType(BikeType bikeType) {
        if (bikeType == null) {
            throw new IllegalArgumentException("Bike type cannot be null");
        }
    }
    
    /**
     * Data class for bike statistics.
     */
    public static class BikeStatistics {
        private final long totalBikes;
        private final long availableBikes;
        private final long rentedBikes;
        private final long maintenanceBikes;
        
        public BikeStatistics(long totalBikes, long availableBikes, long rentedBikes, long maintenanceBikes) {
            this.totalBikes = totalBikes;
            this.availableBikes = availableBikes;
            this.rentedBikes = rentedBikes;
            this.maintenanceBikes = maintenanceBikes;
        }
        
        public long getTotalBikes() { return totalBikes; }
        public long getAvailableBikes() { return availableBikes; }
        public long getRentedBikes() { return rentedBikes; }
        public long getMaintenanceBikes() { return maintenanceBikes; }
        
        public double getAvailabilityRate() {
            return totalBikes > 0 ? (double) availableBikes / totalBikes : 0.0;
        }
        
        public double getUtilizationRate() {
            return totalBikes > 0 ? (double) rentedBikes / totalBikes : 0.0;
        }
    }
}
