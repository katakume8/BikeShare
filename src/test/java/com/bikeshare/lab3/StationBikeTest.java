package com.bikeshare.lab3;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StationBikeTest {

    Station station;

    @Test
    @BeforeEach
    void setUp() {
        this.station =
                new Station("22", "test station", "address", 2, 2, 2);
    }

    @Test
    void shouldAllowAddingBikesToStation() {
        Bike testBike = new Bike("1", Bike.BikeType.STANDARD);
        station.addBike(testBike);
        assertEquals(1, station.getAllBikes().size());
    }

    @Test
    void shouldThrowWhenAddingBikesToStationWithNullType() {
        assertThrows(IllegalArgumentException.class, () -> {
            station.addBike(null);
        });
    }

    @Test
    void shouldThrowWhenBikeStatusIsInactive() {
        Bike testBike = new Bike("1", Bike.BikeType.STANDARD);
        station.deactivate();
        assertThrows(IllegalStateException.class, () -> {
            station.addBike(testBike);
        });
    }

    @Test
    void shouldThrowWhenStationsIsFull(){
        Bike testBike1 = new Bike("1", Bike.BikeType.STANDARD);
        Bike testBike2 = new Bike("2", Bike.BikeType.STANDARD);
        Bike testBike3 = new Bike("3", Bike.BikeType.STANDARD);
        station.addBike(testBike1);
        station.addBike(testBike2);

        assertThrows(IllegalStateException.class, () -> {
            station.addBike(testBike3);
        });
    }

    @Test
    void shouldThrowWhenAddingNonAvailableBikesToStation() {
        Bike testBike1 = new Bike("1", Bike.BikeType.STANDARD);
        testBike1.reserve();
        assertThrows(IllegalStateException.class, () -> {station.addBike(testBike1);});
    }

    @Test
    void shouldThrowWhenAddingSameBikeIdToStation(){
        Bike testBike1 = new Bike("1", Bike.BikeType.STANDARD);
        station.addBike(testBike1);
        assertThrows(IllegalStateException.class, () -> {
            station.addBike(testBike1);
        });
    }

    @Test
    void shouldThrowWhenTryingToRemoveBikeWillNullId() {
        assertThrows(IllegalArgumentException.class, () -> station.removeBike(null));
    }

    @Test
    void shouldThrowWhenTryingToRemoveNonExistingBikes() {
        Bike bike = new Bike("1", Bike.BikeType.STANDARD);

        assertThrows(IllegalStateException.class, () -> station.removeBike(bike.getBikeId()));
    }

    @Test
    void shouldThrowWhenTryingToRemoveAReservedBike() {
        Bike bike = new Bike("RESERVED1", Bike.BikeType.STANDARD);

        station.addBike(bike);
        station.reserveBike(bike.getBikeId());

        assertThrows(IllegalStateException.class, () -> station.removeBike(bike.getBikeId()));
    }

    @Test
    void shouldRemoveAndReturnAStationedBike() {
        Bike bike = new Bike("TEST2", Bike.BikeType.STANDARD);

        station.addBike(bike);
        assertEquals(bike, station.removeBike(bike.getBikeId()));
    }

    @Test
    void shouldThrowWhenReserveBikeWithNullId(){
        assertThrows(IllegalArgumentException.class,()->station.reserveBike(null));
    }

    @Test
    void shouldThrowWhenReservingNonExistingBike(){
        assertThrows(IllegalStateException.class, () ->station.reserveBike("5"));
    }

    @Test
    void shouldThrowWhenReservingAReservedBike(){
        Bike bike = new Bike("RESERVED1", Bike.BikeType.STANDARD);

        station.addBike(bike);
        station.reserveBike(bike.getBikeId());

        assertThrows(IllegalStateException.class, () ->station.reserveBike(bike.getBikeId()));
    }

    @Test
    void shouldThrowWhenCancelingBikeWithNullId(){
        assertThrows(IllegalArgumentException.class,() -> station.cancelReservation(null));
    }

    @Test
    void shouldThrowWhenCancelingNonReservedBike(){
        Bike bike = new Bike("NOT RESERVED1", Bike.BikeType.STANDARD);
        station.addBike(bike);
        assertThrows(IllegalStateException.class, () -> station.cancelReservation(bike.getBikeId()));
    }

    @Test
    void shouldAllowCancelingReservedBike(){
        Bike bike = new Bike("abc", Bike.BikeType.STANDARD);

        station.addBike(bike);
        station.reserveBike(bike.getBikeId());
        station.cancelReservation(bike.getBikeId());

        assertFalse(station.getReservedBikeIds().contains(bike.getBikeId()));
    }



    // TODO: getAvailableBike

    @Test
    void shouldReturnNullIfStationNotActive() {
        assertNull(station.getAvailableBike(Bike.BikeType.STANDARD));
    }

    @Test
    void shouldReturnNullIfNoBikesAreAvailable() {
        station.activate();
        assertNull(station.getAvailableBike(Bike.BikeType.STANDARD));
    }

    @Test
    void shouldReturnAvailableBikeOfPreferredType() {
        Bike bike = new Bike("STANDARD1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike);

        assertEquals(bike, station.getAvailableBike(Bike.BikeType.STANDARD));
    }

    @Test
    void shouldReturnNullIfNoBikeIsAvailable() {
        Bike bike = new Bike("STANDARD1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike);

        station.reserveBike(bike.getBikeId());

        assertNull(station.getAvailableBike(Bike.BikeType.STANDARD));
    }

    @Test
    void shouldReturnAnyAvailableBikeWhenTypeIsNull() {
        Bike bike = new Bike("STANDARD1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike);

        assertEquals(bike, station.getAvailableBike(null));
    }

    @Test
    void shouldReturnAvailableBikeOfAnyType() {
        Bike bike = new Bike("STANDARD1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike);

        assertEquals(bike, station.getAvailableBike(Bike.BikeType.ELECTRIC));
    }

    // TODO: getAvailableBike



    // TODO: getAvailableBikesByType

    @Test
    void shouldReturnAllAvailableBikesOfStandardType() {
        Bike bike1 = new Bike("S1", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("E1", Bike.BikeType.ELECTRIC);

        station.addBike(bike1);
        station.addBike(bike2);

        assertEquals(List.of(bike1), station.getAvailableBikesByType(Bike.BikeType.STANDARD));
    }

    // TODO: getAvailableBikesByType



    @Test
    void shouldThrowExceptionIfChargingIsNotAvailable() {
        station.disableCharging();

        assertThrows(IllegalStateException.class, () -> station.chargeElectricBikes(100.00));
    }

    @Test
    void shouldChargeAvailableElectricBikes() {
        Bike bike1 = new Bike("E1", Bike.BikeType.ELECTRIC);
        Bike bike2 = new Bike("E2", Bike.BikeType.ELECTRIC);

        station.activate();
        station.enableCharging(1.00);
        station.addBike(bike1);
        station.addBike(bike2);

        bike1.startRide();
        bike1.endRide(10);

        bike2.startRide();

        station.chargeElectricBikes(100.00);

        assertEquals(100.00, bike1.getBatteryLevel());
    }

    @Test
    void shouldNotChargeNonElectricBikes() {
        Bike bike1 = new Bike("E1", Bike.BikeType.STANDARD);

        station.activate();
        station.enableCharging(1.00);
        station.addBike(bike1);

        assertDoesNotThrow(() -> station.chargeElectricBikes(100.00));
    }



    // TODO: getAvailableBikeCount

    @Test
    void shouldReturnTheCorrectAmountOfAvailableBikes() {
        Bike bike1 = new Bike("S1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike1);

        assertEquals(1, station.getAvailableBikeCount());
    }

    @Test
    void shghf() {
        Station test = new Station("gfk", "gjfk", "gfdkl", 10, 10, 10);

        Bike bike1 = new Bike("S1", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("S2", Bike.BikeType.STANDARD);
        Bike bike3 = new Bike("S3", Bike.BikeType.STANDARD);
        Bike bike4 = new Bike("S4", Bike.BikeType.STANDARD);

        test.activate();
        test.addBike(bike1);
        test.addBike(bike2);
        test.addBike(bike3);
        test.addBike(bike4);

        test.reserveBike(bike3.getBikeId());

        bike4.startRide();

        assertEquals(2, test.getAvailableBikeCount());
    }

    // TODO: getAvailableBikeCount



    @Test
    void shouldReturnTheTotalAmountOfAvailableBikes() {
        Bike bike1 = new Bike("S1", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("S2", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike1);
        station.addBike(bike2);

        assertEquals(2, station.getTotalBikeCount());
    }

    @Test
    void shouldReturnTheNumberOfFreeSpaces() {
        Bike bike1 = new Bike("S1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike1);

        assertEquals(1, station.getAvailableDocks());
    }

    @Test
    void shouldReturnTrueWhenStationIsEmpty() {
        assertTrue(station.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenStationIsFull() {
        Bike bike1 = new Bike("S1", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("S2", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike1);
        station.addBike(bike2);

        assertFalse(station.isEmpty());
    }

    @Test
    void shouldReturnCorrectStationString() {
        Bike bike = new Bike("S1", Bike.BikeType.STANDARD);

        station.activate();
        station.addBike(bike);

        assertEquals("Station{id='22', name='test station', status=ACTIVE, bikes=1/2}",
                station.toString());
    }

    @Test
    void shouldNotUpdateStatusWhenStationIsInMaintenance() {
        Bike bike = new Bike("S1", Bike.BikeType.STANDARD);
        station.setMaintenance();
        station.addBike(bike);
        assertEquals(1, station.getAllBikes().size());
    }

    @Test
    void shouldNotUpdateStatusWhenStationIsInactive() {
        Bike bike = new Bike("S1", Bike.BikeType.STANDARD);
        station.addBike(bike);
        station.deactivate();
        station.removeBike("S1");
        assertEquals(Station.StationStatus.INACTIVE, station.getStatus());
    }
}
