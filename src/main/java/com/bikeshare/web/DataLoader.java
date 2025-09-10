package com.bikeshare.web;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Station;
import com.bikeshare.model.User;
import com.bikeshare.repository.BikeRepository;
import com.bikeshare.repository.StationRepository;
import com.bikeshare.repository.UserRepository;

/**
 * Simple data loader to populate in-memory repositories for demos.
 * Replace with DB seeding if a database is used.
 */
public class DataLoader {
    private final BikeRepository bikes;
    private final StationRepository stations;
    private final UserRepository users;

    public DataLoader(BikeRepository bikes, StationRepository stations, UserRepository users) {
        this.bikes = bikes;
        this.stations = stations;
        this.users = users;
    }

    public void loadDemoData() {
        // Minimal seed; extend as needed for labs
    Station s1 = new Station("S-001", "Slussen", "", 59.319, 18.074, 20);
    Station s2 = new Station("S-002", "KTH Campus", "", 59.349, 18.073, 25);
        stations.save(s1);
        stations.save(s2);

    Bike b1 = new Bike("B-001", Bike.BikeType.STANDARD);
    b1.setCurrentStationId(s1.getStationId());
    bikes.save(b1);
    Bike b2 = new Bike("B-002", Bike.BikeType.ELECTRIC);
    b2.setCurrentStationId(s1.getStationId());
    bikes.save(b2);
    Bike b3 = new Bike("B-003", Bike.BikeType.PREMIUM);
    b3.markAsBroken();
    bikes.save(b3);

        User u = new User("U-001", "student@example.se", "Anna", "Svensson");
        users.save(u);
    }
}
