package com.bikeshare.web;

import com.bikeshare.model.Station;
import com.bikeshare.repository.StationRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation of StationRepository for demonstration purposes.
 */
public class InMemoryStationRepository implements StationRepository {
    
    private final Map<String, Station> stations = new ConcurrentHashMap<>();
    
    @Override
    public Station save(Station station) {
        stations.put(station.getStationId(), station);
        return station;
    }
    
    @Override
    public Optional<Station> findById(String stationId) {
        return Optional.ofNullable(stations.get(stationId));
    }
    
    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }
    
    @Override
    public List<Station> findByNameContaining(String name) {
        return stations.values().stream()
                .filter(station -> station.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Station> findStationsInArea(double minLatitude, double maxLatitude, 
                                          double minLongitude, double maxLongitude) {
        return stations.values().stream()
                .filter(station -> {
                    double lat = station.getLatitude();
                    double lon = station.getLongitude();
                    return lat >= minLatitude && lat <= maxLatitude && 
                           lon >= minLongitude && lon <= maxLongitude;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Station> findStationsWithAvailableCapacity() {
        return stations.values().stream()
                .filter(station -> station.getTotalBikeCount() < station.getCapacity())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Station> findStationsWithAvailableBikes() {
        return stations.values().stream()
                .filter(station -> station.getAvailableBikeCount() > 0)
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return stations.size();
    }
    
    @Override
    public boolean deleteById(String stationId) {
        return stations.remove(stationId) != null;
    }
    
    @Override
    public boolean existsById(String stationId) {
        return stations.containsKey(stationId);
    }
    
    @Override
    public List<Station> findNearestStations(double latitude, double longitude, 
                                           double maxDistance, int limit) {
        return stations.values().stream()
                .map(station -> new StationDistance(station, 
                    calculateDistance(latitude, longitude, 
                                    station.getLatitude(), station.getLongitude())))
                .filter(sd -> sd.distance <= maxDistance)
                .sorted(Comparator.comparing(sd -> sd.distance))
                .limit(limit)
                .map(sd -> sd.station)
                .collect(Collectors.toList());
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for calculating distance between two points on Earth
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private static class StationDistance {
        final Station station;
        final double distance;
        
        StationDistance(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }
    }
}
