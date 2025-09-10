package com.bikeshare.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bikeshare.model.Bike;
import com.bikeshare.model.BikeType;
import com.bikeshare.repository.BikeRepository;

/**
 * Simple in-memory implementation of BikeRepository for demonstration purposes.
 */
public class InMemoryBikeRepository implements BikeRepository {
    
    private final Map<String, Bike> bikes = new ConcurrentHashMap<>();
    
    @Override
    public Bike save(Bike bike) {
        bikes.put(bike.getBikeId(), bike);
        return bike;
    }
    
    @Override
    public Optional<Bike> findById(String bikeId) {
        return Optional.ofNullable(bikes.get(bikeId));
    }
    
    @Override
    public List<Bike> findAll() {
        return new ArrayList<>(bikes.values());
    }
    
    @Override
    public List<Bike> findByType(BikeType bikeType) {
        return bikes.values().stream()
                .filter(bike -> switch (bike.getType()) {
                    case STANDARD -> bikeType == BikeType.STANDARD;
                    case ELECTRIC -> bikeType == BikeType.ELECTRIC;
                    case PREMIUM -> bikeType == BikeType.MOUNTAIN || bikeType == BikeType.CARGO;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Bike> findAvailableBikes() {
        return bikes.values().stream()
                .filter(Bike::isAvailable)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Bike> findByStationId(String stationId) {
        return bikes.values().stream()
                .filter(bike -> Objects.equals(bike.getCurrentStationId(), stationId))
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return bikes.size();
    }
    
    @Override
    public long countAvailable() {
        return bikes.values().stream()
                .mapToLong(bike -> bike.isAvailable() ? 1 : 0)
                .sum();
    }
    
    @Override
    public boolean deleteById(String bikeId) {
        return bikes.remove(bikeId) != null;
    }
    
    @Override
    public boolean existsById(String bikeId) {
        return bikes.containsKey(bikeId);
    }
}
