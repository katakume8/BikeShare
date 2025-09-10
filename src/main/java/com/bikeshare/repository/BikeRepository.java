package com.bikeshare.repository;

import com.bikeshare.model.Bike;
import com.bikeshare.model.BikeType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for bike data access operations.
 * Provides CRUD operations and filtering capabilities for bikes.
 */
public interface BikeRepository {
    
    /**
     * Saves a bike to the repository.
     * @param bike The bike to save
     * @return The saved bike
     */
    Bike save(Bike bike);
    
    /**
     * Finds a bike by its ID.
     * @param bikeId The bike ID to search for
     * @return Optional containing the bike if found, empty otherwise
     */
    Optional<Bike> findById(String bikeId);
    
    /**
     * Finds all bikes in the repository.
     * @return List of all bikes
     */
    List<Bike> findAll();
    
    /**
     * Finds bikes by their type.
     * @param bikeType The type of bikes to find
     * @return List of bikes of the specified type
     */
    List<Bike> findByType(BikeType bikeType);
    
    /**
     * Finds all available bikes.
     * @return List of available bikes
     */
    List<Bike> findAvailableBikes();
    
    /**
     * Finds all bikes at a specific station.
     * @param stationId The station ID
     * @return List of bikes at the station
     */
    List<Bike> findByStationId(String stationId);
    
    /**
     * Counts total number of bikes.
     * @return Total bike count
     */
    long count();
    
    /**
     * Counts available bikes.
     * @return Available bike count
     */
    long countAvailable();
    
    /**
     * Deletes a bike by its ID.
     * @param bikeId The bike ID to delete
     * @return true if deleted, false if not found
     */
    boolean deleteById(String bikeId);
    
    /**
     * Checks if a bike exists by its ID.
     * @param bikeId The bike ID to check
     * @return true if exists, false otherwise
     */
    boolean existsById(String bikeId);
}
