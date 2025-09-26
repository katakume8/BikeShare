package com.bikeshare.lab3;

import com.bikeshare.model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test class for the station class")
class StationTest {

    Station station;

    @Test
    @BeforeEach
    void setUp() {
        this.station =
                new Station("22", "test station", "address", 2, 2, 2);
    }

    @Test
    void shouldBeAbleToCreateStationWithValidArguments() {
        Station testStation =
                new Station("22", "test station", "address", 2, 2, 2);

        assertAll("Station properties",
                () -> assertEquals("22", testStation.getStationId()),
                () -> assertEquals("test station", testStation.getName()),
                () -> assertEquals("address", testStation.getAddress()),
                () -> assertEquals(2, testStation.getLatitude()),
                () -> assertEquals(2, testStation.getLongitude()),
                () -> assertEquals(2, testStation.getCapacity())
        );
    }

    @Test
    void shouldThrowWhenCreatingStationWithNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "test station", "address", 2, 2, -2);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithToHigherCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "test station", "address", 2, 2, 200);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithNullID() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station(null, "name","aaa" , 2, 2, 2);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithEmptyID() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("", "name","aaa" , 2, 2, 2);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", null,"aaa" , 2, 2, 2);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "","aaa" , 2, 2, 2);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithInvalidLongitudeNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "ddd","aaa" , 2, -190, 2);
        });
    }
    @Test
    void shouldThrowWhenCreatingStationWithInvalidLongitudePositive() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "ddd","aaa" , 2, 190, 2);
        });
    }

    @Test
    void shouldThrowWhenCreatingStationWithInvalidLatitudeNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "ddd","aaa" , -102, 2, 2);
        });
    }
    @Test
    void shouldThrowWhenCreatingStationWithInvalidLatitudePositive() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Station("22", "ddd","aaa" , 102, 2, 2);
        });
    }


    @Test
    void shouldSetAddressToEmptyWhenCreatedWithNull() {
        Station testStation = new Station("22", "test station", null, 2, 2, 2);
        assertEquals("", testStation.getAddress());
    }

    @Test
    void shouldReturnCorrectDistance(){
        Station testStation = new Station("22", "test station", "address", -90, -180, 2);
        double distance = station.distanceTo(testStation);
        assertEquals(10229.933251299402, distance);
    }

    @Test
    void shouldThrowWhenGettingDistanceFromStationWitOtherStationBeingNUll() {
        assertThrows(IllegalArgumentException.class, () -> {
            station.distanceTo(null);
        });
    }

    @Test
    void shouldBeAbleToEnableCharging(){
        station.enableCharging(10);
        assertTrue(station.isChargingAvailable());
    }

    @Test
    void shouldThrowWhenEnablingChargingIfHourlyRateIsNegative(){
        assertThrows(IllegalArgumentException.class, () -> {
            station.enableCharging(-1);
        });
    }

    @Test
    void shouldBeAbleToDisableCharging(){
        station.enableCharging(10);
        station.disableCharging();
        assertFalse(station.isChargingAvailable());
    }

    @Test
    void shouldSetChargingRateToZeroWhenDisablingCharging(){
        station.enableCharging(10);
        station.disableCharging();
        assertEquals(0,station.getChargingRate());
    }

    @Test
    void shouldBeAbleToSetStationForMaintenance(){
        station.setMaintenance();
        assertEquals(Station.StationStatus.MAINTENANCE, station.getStatus());
    }

    @Test
    void shouldBeEqualWhenComparing(){
        assertEquals(station, station);
    }

    @Test
    void shouldBeEqualWhenComparingSameStationID(){
        Station sameId = new Station("22",
                "test station", null, 2, 2, 2);
        assertEquals(station, sameId);
    }
    @Test
    void shouldNotBeEqualWhenDifferentStationId(){
        Station differentID = new Station("21",
                "test station", "address", -90, -180, 2);
        assertNotEquals(station, differentID);
    }

    @Test
    void shouldReturnProperHashCode(){
        int test = station.hashCode();
        assertEquals(test, station.hashCode());
    }
}
