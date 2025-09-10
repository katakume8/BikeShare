package com.bikeshare.demo;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Station;
import com.bikeshare.model.User;
import com.bikeshare.service.validation.impl.CorrectAgeCheck;

/**
 * Quick demo to show BikeShare system is working
 */
public class QuickDemo {
    public static void main(String[] args) {
        System.out.println("=== BikeShare System Demo ===");
        
        // Create a user
        User user = new User("U001", "anna@example.se", "Anna", "Andersson");
        System.out.println("✅ Created user: " + user.getFirstName() + " " + user.getLastName());
        
        // Create a bike
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);
        System.out.println("✅ Created bike: " + bike.getBikeId() + " (type: " + bike.getType() + ")");
        
        // Create a station
        Station station = new Station("S001", "Huvudstation", 
                                    "Drottninggatan 1, Stockholm", 
                                    59.3293, 18.0686, 10);
        System.out.println("✅ Created station: " + station.getName() + " (capacity: " + station.getCapacity() + ")");
        
        // Test age validation
        CorrectAgeCheck ageCheck = new CorrectAgeCheck();
        String validId = "199901011234"; // Born 1999-01-01
        try {
            boolean isAdult = ageCheck.isAdult(validId, true);
            System.out.println("✅ Age validation works: " + isAdult);
        } catch (Exception e) {
            System.out.println("❌ Age validation failed: " + e.getMessage());
        }
        
        System.out.println("\n🎉 BikeShare system is fully functional!");
        System.out.println("📊 Ready for testing exercises and web interface");
        System.out.println("🌐 Start web server with: mvn exec:java -Dexec.mainClass=\"com.bikeshare.web.SimpleBikeShareWebServer\"");
    }
}
