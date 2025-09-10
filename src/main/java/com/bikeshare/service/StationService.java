package com.bikeshare.service;

import java.util.List;

import com.bikeshare.model.Station;
import com.bikeshare.repository.BikeRepository;
import com.bikeshare.repository.StationRepository;
import com.bikeshare.service.exception.InvalidStationOperationException;
import com.bikeshare.service.exception.StationNotFoundException;

/**
 * Service class for station-related business operations.
 * Handles station management, capacity monitoring, and location services.
 */
public class StationService {
    
    private final StationRepository stationRepository;
    private final BikeRepository bikeRepository;
    
    /**
     * Constructor for StationService.
     * @param stationRepository The station repository for data access
     * @param bikeRepository The bike repository for bike-related data
     */
    public StationService(StationRepository stationRepository, BikeRepository bikeRepository) {
        this.stationRepository = stationRepository;
        this.bikeRepository = bikeRepository;
    }
    
    /**
     * Creates a new station in the system.
     * @param stationId The unique identifier for the station
     * @param name The name of the station
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param capacity The maximum number of bikes the station can hold
     * @return The created station
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Station createStation(String stationId, String name, double latitude, 
                               double longitude, int capacity) {
        validateStationParameters(stationId, name, latitude, longitude, capacity);
        
        if (stationRepository.existsById(stationId)) {
            throw new IllegalArgumentException("Station with ID " + stationId + " already exists");
        }
        
    Station station = new Station(stationId, name, "", latitude, longitude, capacity);
        return stationRepository.save(station);
    }
    
    /**
     * Finds a station by its ID.
     * @param stationId The station ID to search for
     * @return The station if found
     * @throws StationNotFoundException if station is not found
     */
    public Station findStationById(String stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException("Station not found with ID: " + stationId));
    }
    
    /**
     * Gets all stations in the system.
     * @return List of all stations
     */
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
    
    /**
     * Finds stations by name (partial match).
     * @param name The name or partial name to search for
     * @return List of matching stations
     */
    public List<Station> findStationsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be null or empty");
        }
        return stationRepository.findByNameContaining(name.trim());
    }
    
    /**
     * Finds stations within a geographical area.
     * @param minLatitude Minimum latitude
     * @param maxLatitude Maximum latitude
     * @param minLongitude Minimum longitude
     * @param maxLongitude Maximum longitude
     * @return List of stations within the area
     */
    public List<Station> findStationsInArea(double minLatitude, double maxLatitude, 
                                          double minLongitude, double maxLongitude) {
        validateGeographicalBounds(minLatitude, maxLatitude, minLongitude, maxLongitude);
        return stationRepository.findStationsInArea(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }
    
    /**
     * Finds the nearest stations to a given location.
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param maxDistance Maximum distance in kilometers
     * @param limit Maximum number of stations to return
     * @return List of nearest stations
     */
    public List<Station> findNearestStations(double latitude, double longitude, 
                                           double maxDistance, int limit) {
        validateCoordinates(latitude, longitude);
        if (maxDistance <= 0) {
            throw new IllegalArgumentException("Max distance must be positive");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return stationRepository.findNearestStations(latitude, longitude, maxDistance, limit);
    }
    
    /**
     * Gets stations with available bikes for rental.
     * @return List of stations with available bikes
     */
    public List<Station> getStationsWithAvailableBikes() {
        return stationRepository.findStationsWithAvailableBikes();
    }
    
    /**
     * Gets stations with available capacity for bike returns.
     * @return List of stations with available capacity
     */
    public List<Station> getStationsWithAvailableCapacity() {
        return stationRepository.findStationsWithAvailableCapacity();
    }
    
    /**
     * Updates the current bike count at a station.
     * @param stationId The station ID
     * @param bikeCount The new bike count
     * @throws StationNotFoundException if station is not found
     * @throws InvalidStationOperationException if bike count is invalid
     */
    public void updateStationBikeCount(String stationId, int bikeCount) {
        Station station = findStationById(stationId);
        
        if (bikeCount < 0) {
            throw new InvalidStationOperationException("Bike count cannot be negative");
        }
        
        if (bikeCount > station.getCapacity()) {
            throw new InvalidStationOperationException("Bike count cannot exceed station capacity");
        }
        
        // Adjust count by adding/removing bikes to reach desired value
        int current = station.getTotalBikeCount();
        if (bikeCount > current) {
            int toAdd = bikeCount - current;
            for (int i = 0; i < toAdd; i++) {
                // Add placeholder available bike to station inventory
                station.addBike(new com.bikeshare.model.Bike("AUTO-" + System.nanoTime(), com.bikeshare.model.Bike.BikeType.STANDARD));
            }
        } else if (bikeCount < current) {
            int toRemove = current - bikeCount;
            // Remove arbitrary bikes
            for (com.bikeshare.model.Bike b : station.getAllBikes()) {
                if (toRemove == 0) break;
                station.removeBike(b.getBikeId());
                toRemove--;
            }
        }
        stationRepository.save(station);
    }
    
    /**
     * Adds a bike to a station.
     * @param stationId The station ID
     * @throws StationNotFoundException if station is not found
     * @throws InvalidStationOperationException if station is at capacity
     */
    public void addBikeToStation(String stationId) {
        Station station = findStationById(stationId);
        
    if (station.isFull()) {
            throw new InvalidStationOperationException("Station is at full capacity");
        }
    // Add a placeholder bike for count increment
    station.addBike(new com.bikeshare.model.Bike("AUTO-" + System.nanoTime(), com.bikeshare.model.Bike.BikeType.STANDARD));
        stationRepository.save(station);
    }
    
    /**
     * Removes a bike from a station.
     * @param stationId The station ID
     * @throws StationNotFoundException if station is not found
     * @throws InvalidStationOperationException if station has no bikes
     */
    public void removeBikeFromStation(String stationId) {
        Station station = findStationById(stationId);
        
        if (station.getTotalBikeCount() == 0) {
            throw new InvalidStationOperationException("Station has no bikes to remove");
        }
        // Remove any one bike by id
        com.bikeshare.model.Bike any = station.getAllBikes().stream().findFirst().orElse(null);
        if (any != null) {
            station.removeBike(any.getBikeId());
        }
        stationRepository.save(station);
    }
    
    /**
     * Calculates the distance between two stations.
     * @param stationId1 First station ID
     * @param stationId2 Second station ID
     * @return Distance in kilometers
     * @throws StationNotFoundException if either station is not found
     */
    public double calculateDistanceBetweenStations(String stationId1, String stationId2) {
        Station station1 = findStationById(stationId1);
        Station station2 = findStationById(stationId2);
        
        return calculateDistance(station1.getLatitude(), station1.getLongitude(),
                               station2.getLatitude(), station2.getLongitude());
    }
    
    /**
     * Gets station statistics.
     * @return StationStatistics object containing various metrics
     */
    public StationStatistics getStationStatistics() {
        List<Station> allStations = stationRepository.findAll();
        
        long totalStations = allStations.size();
    long stationsWithBikes = allStations.stream().filter(s -> s.getAvailableBikeCount() > 0).count();
    long stationsWithCapacity = allStations.stream().filter(s -> !s.isFull()).count();
        
        int totalCapacity = allStations.stream()
                .mapToInt(Station::getCapacity)
                .sum();
    int totalBikes = allStations.stream().mapToInt(Station::getTotalBikeCount).sum();
        
        return new StationStatistics(totalStations, stationsWithBikes, stationsWithCapacity, 
                                   totalCapacity, totalBikes);
    }
    
    /**
     * Validates station creation parameters.
     */
    private void validateStationParameters(String stationId, String name, double latitude, 
                                         double longitude, int capacity) {
        if (stationId == null || stationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be null or empty");
        }
        validateCoordinates(latitude, longitude);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Station capacity must be positive");
        }
    }
    
    /**
     * Validates geographical coordinates.
     */
    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }
    
    /**
     * Validates geographical bounds.
     */
    private void validateGeographicalBounds(double minLat, double maxLat, double minLon, double maxLon) {
        validateCoordinates(minLat, minLon);
        validateCoordinates(maxLat, maxLon);
        if (minLat >= maxLat) {
            throw new IllegalArgumentException("Minimum latitude must be less than maximum latitude");
        }
        if (minLon >= maxLon) {
            throw new IllegalArgumentException("Minimum longitude must be less than maximum longitude");
        }
    }
    
    /**
     * Calculates distance between two coordinates using Haversine formula.
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
    
    /**
     * Data class for station statistics.
     */
    public static class StationStatistics {
        private final long totalStations;
        private final long stationsWithBikes;
        private final long stationsWithCapacity;
        private final int totalCapacity;
        private final int totalBikes;
        
        public StationStatistics(long totalStations, long stationsWithBikes, long stationsWithCapacity,
                               int totalCapacity, int totalBikes) {
            this.totalStations = totalStations;
            this.stationsWithBikes = stationsWithBikes;
            this.stationsWithCapacity = stationsWithCapacity;
            this.totalCapacity = totalCapacity;
            this.totalBikes = totalBikes;
        }
        
        public long getTotalStations() { return totalStations; }
        public long getStationsWithBikes() { return stationsWithBikes; }
        public long getStationsWithCapacity() { return stationsWithCapacity; }
        public int getTotalCapacity() { return totalCapacity; }
        public int getTotalBikes() { return totalBikes; }
        
        public double getOccupancyRate() {
            return totalCapacity > 0 ? (double) totalBikes / totalCapacity : 0.0;
        }
        
        public double getStationUtilizationRate() {
            return totalStations > 0 ? (double) stationsWithBikes / totalStations : 0.0;
        }
    }
}
