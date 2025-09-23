package com.bikeshare.lab3;

import com.bikeshare.model.BikeType;
import com.bikeshare.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 3 Template: Integration Testing for BikeType and User.MembershipType
 * 
 * TODO for students:
 * - Test methods that use both classes: Calculate ride costs with different combinations
 * - Test business logic between classes: Compare membership benefits (free minutes)
 * - Test error scenarios involving both classes: Edge cases (rides exactly matching free minutes)
 * - Test different input values: Expensive bike types with different memberships
 * - Optional: Create helper methods to calculate costs and compare results
 */

// This test is just an example to get you started. You will need to add more tests as per the challenges.
@DisplayName("Lab 3: BikeType + MembershipType Integration Testing")
public class BikeTypeMembershipBasicTest {
    
    @Test
    @DisplayName("Should calculate zero cost for short ride with BASIC membership")
    void shouldCalculateZeroCostForShortRideWithBasicMembership() {
        // Arrange - Set up test data
        User.MembershipType basicMembership = User.MembershipType.BASIC;
        BikeType standardBike = BikeType.STANDARD;
        int rideMinutes = 30; // Short ride within free minutes
        
        // Act - Calculate cost using both classes
        int freeMinutes = basicMembership.getFreeMinutesPerRide();
        double bikeRate = standardBike.getPricePerMinute();
        int chargeableMinutes = Math.max(0, rideMinutes - freeMinutes);
        double actualCost = chargeableMinutes * bikeRate;
        
        // Assert - Verify the integration works correctly
        assertEquals(0.0, actualCost, 0.01, "Short ride should be free with BASIC membership");
        assertTrue(rideMinutes <= freeMinutes, "Ride minutes should be within free minutes");
    }
    
    // TODO: Test methods that use both classes - Add tests for calculating ride costs with different combinations
    // Hint: Test PREMIUM membership with ELECTRIC bike, STUDENT membership with CARGO bike, etc.
    
    // TODO: Test business logic between classes - Add tests comparing membership benefits
    // Hint: Test that PREMIUM has more free minutes than BASIC, CORPORATE has most free minutes
    
    // TODO: Test error scenarios involving both classes - Add tests for edge cases
    // Hint: Test rides that are exactly the same length as free minutes, rides just over free minutes
    
    // TODO: Test different input values - Add tests for expensive bike types with different memberships
    // Hint: Test how CARGO bike (most expensive) costs differ across membership types
    
    // TODO: Optional - Create helper method to calculate ride costs
    // Hint: private double calculateRideCost(MembershipType membership, BikeType bikeType, int minutes)
}