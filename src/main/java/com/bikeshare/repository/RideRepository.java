package com.bikeshare.repository;

import com.bikeshare.model.Ride;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ride data access operations.
 */
public interface RideRepository {
    
    /**
     * Saves a ride to the repository.
     * @param ride The ride to save
     * @return The saved ride
     */
    Ride save(Ride ride);
    
    /**
     * Finds a ride by its ID.
     * @param rideId The ride ID to search for
     * @return Optional containing the ride if found, empty otherwise
     */
    Optional<Ride> findById(String rideId);
    
    /**
     * Finds all rides in the repository.
     * @return List of all rides
     */
    List<Ride> findAll();
    
    /**
     * Finds all rides for a specific user.
     * @param userId The user ID
     * @return List of rides for the user
     */
    List<Ride> findByUserId(String userId);
    
    /**
     * Finds all rides for a specific bike.
     * @param bikeId The bike ID
     * @return List of rides for the bike
     */
    List<Ride> findByBikeId(String bikeId);
    
    /**
     * Finds active (ongoing) rides for a user.
     * @param userId The user ID
     * @return List of active rides for the user
     */
    List<Ride> findActiveRidesByUserId(String userId);
    
    /**
     * Finds completed rides for a user.
     * @param userId The user ID
     * @return List of completed rides for the user
     */
    List<Ride> findCompletedRidesByUserId(String userId);
    
    /**
     * Finds rides within a date range.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of rides within the date range
     */
    List<Ride> findRidesBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Finds rides that started from a specific station.
     * @param stationId The start station ID
     * @return List of rides that started from the station
     */
    List<Ride> findByStartStationId(String stationId);
    
    /**
     * Finds rides that ended at a specific station.
     * @param stationId The end station ID
     * @return List of rides that ended at the station
     */
    List<Ride> findByEndStationId(String stationId);
    
    /**
     * Finds rides with duration longer than specified minutes.
     * @param minutes Minimum duration in minutes
     * @return List of rides longer than the specified duration
     */
    List<Ride> findRidesLongerThan(int minutes);
    
    /**
     * Finds rides with cost higher than specified amount.
     * @param amount Minimum cost amount
     * @return List of rides with cost higher than the amount
     */
    List<Ride> findRidesWithCostHigherThan(double amount);
    
    /**
     * Counts total number of rides.
     * @return Total ride count
     */
    long count();
    
    /**
     * Counts active rides.
     * @return Active ride count
     */
    long countActiveRides();
    
    /**
     * Counts completed rides.
     * @return Completed ride count
     */
    long countCompletedRides();
    
    /**
     * Deletes a ride by its ID.
     * @param rideId The ride ID to delete
     * @return true if deleted, false if not found
     */
    boolean deleteById(String rideId);
    
    /**
     * Checks if a ride exists by its ID.
     * @param rideId The ride ID to check
     * @return true if exists, false otherwise
     */
    boolean existsById(String rideId);
    
    /**
     * Calculates total revenue from completed rides.
     * @return Total revenue amount
     */
    double calculateTotalRevenue();
    
    /**
     * Calculates total ride duration for all completed rides.
     * @return Total duration in minutes
     */
    long calculateTotalRideDuration();
}
