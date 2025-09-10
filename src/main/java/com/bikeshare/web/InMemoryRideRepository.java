package com.bikeshare.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bikeshare.model.Ride;
import com.bikeshare.repository.RideRepository;

/**
 * Simple in-memory implementation of RideRepository for demonstration purposes.
 */
public class InMemoryRideRepository implements RideRepository {
    
    private final Map<String, Ride> rides = new ConcurrentHashMap<>();
    
    @Override
    public Ride save(Ride ride) {
        rides.put(ride.getRideId(), ride);
        return ride;
    }
    
    @Override
    public Optional<Ride> findById(String rideId) {
        return Optional.ofNullable(rides.get(rideId));
    }
    
    @Override
    public List<Ride> findAll() {
        return new ArrayList<>(rides.values());
    }
    
    @Override
    public List<Ride> findByUserId(String userId) {
        return rides.values().stream()
                .filter(ride -> Objects.equals(ride.getUserId(), userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findByBikeId(String bikeId) {
        return rides.values().stream()
                .filter(ride -> Objects.equals(ride.getBikeId(), bikeId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findActiveRidesByUserId(String userId) {
        return rides.values().stream()
                .filter(ride -> Objects.equals(ride.getUserId(), userId))
                .filter(ride -> ride.getEndTime() == null)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findCompletedRidesByUserId(String userId) {
        return rides.values().stream()
                .filter(ride -> Objects.equals(ride.getUserId(), userId))
                .filter(ride -> ride.getEndTime() != null)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findRidesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return rides.values().stream()
                .filter(ride -> {
                    LocalDateTime rideStart = ride.getStartTime();
                    return rideStart.isAfter(startDate) && rideStart.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findByStartStationId(String stationId) {
        return rides.values().stream()
                .filter(ride -> Objects.equals(ride.getStartStationId(), stationId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findByEndStationId(String stationId) {
        return rides.values().stream()
                .filter(ride -> Objects.equals(ride.getEndStationId(), stationId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findRidesLongerThan(int minutes) {
    return rides.values().stream()
        .filter(ride -> ride.getActiveMinutes() > minutes)
        .collect(Collectors.toList());
    }
    
    @Override
    public List<Ride> findRidesWithCostHigherThan(double amount) {
    return rides.values().stream()
        .filter(ride -> ride.getFinalCost() > amount)
        .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return rides.size();
    }
    
    @Override
    public long countActiveRides() {
        return rides.values().stream()
                .mapToLong(ride -> ride.getEndTime() == null ? 1 : 0)
                .sum();
    }
    
    @Override
    public long countCompletedRides() {
        return rides.values().stream()
                .mapToLong(ride -> ride.getEndTime() != null ? 1 : 0)
                .sum();
    }
    
    @Override
    public boolean deleteById(String rideId) {
        return rides.remove(rideId) != null;
    }
    
    @Override
    public boolean existsById(String rideId) {
        return rides.containsKey(rideId);
    }
    
    @Override
    public double calculateTotalRevenue() {
    return rides.values().stream()
        .filter(ride -> ride.getEndTime() != null)
        .mapToDouble(Ride::getFinalCost)
        .sum();
    }
    
    @Override
    public long calculateTotalRideDuration() {
    return rides.values().stream()
        .filter(ride -> ride.getEndTime() != null)
        .mapToLong(Ride::getActiveMinutes)
        .sum();
    }
}
