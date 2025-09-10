package com.bikeshare.repository;

import com.bikeshare.model.Station;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for station data access operations.
 */
public interface StationRepository {
    
    /**
     * Saves a station to the repository.
     * @param station The station to save
     * @return The saved station
     */
    Station save(Station station);
    
    /**
     * Finds a station by its ID.
     * @param stationId The station ID to search for
     * @return Optional containing the station if found, empty otherwise
     */
    Optional<Station> findById(String stationId);
    
    /**
     * Finds all stations in the repository.
     * @return List of all stations
     */
    List<Station> findAll();
    
    /**
     * Finds stations by name (partial match, case-insensitive).
     * @param name The name or partial name to search for
     * @return List of matching stations
     */
    List<Station> findByNameContaining(String name);
    
    /**
     * Finds stations within a geographical area.
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return List of stations within the area
     */
    List<Station> findStationsInArea(double minLatitude, double maxLatitude, 
                                   double minLongitude, double maxLongitude);
    
    /**
     * Finds stations with available capacity (can accept returned bikes).
     * @return List of stations with available capacity
     */
    List<Station> findStationsWithAvailableCapacity();
    
    /**
     * Finds stations with available bikes.
     * @return List of stations that have bikes available for rental
     */
    List<Station> findStationsWithAvailableBikes();
    
    /**
     * Counts total number of stations.
     * @return Total station count
     */
    long count();
    
    /**
     * Deletes a station by its ID.
     * @param stationId The station ID to delete
     * @return true if deleted, false if not found
     */
    boolean deleteById(String stationId);
    
    /**
     * Checks if a station exists by its ID.
     * @param stationId The station ID to check
     * @return true if exists, false otherwise
     */
    boolean existsById(String stationId);
    
    /**
     * Finds the nearest stations to a given location.
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param maxDistance Maximum distance in kilometers
     * @param limit Maximum number of stations to return
     * @return List of nearest stations within the specified distance
     */
    List<Station> findNearestStations(double latitude, double longitude, 
                                    double maxDistance, int limit);
}
