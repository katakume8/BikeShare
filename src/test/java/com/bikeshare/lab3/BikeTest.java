package com.bikeshare.lab3;

import com.bikeshare.model.Bike;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class BikeTest {
    @Test
    void shouldThrowIfNullIsPassedAsBikeIdToConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Bike(null, Bike.BikeType.STANDARD));
    }

    @Test
    void shouldThrowIfEmptyBikeIdIsPassedToConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Bike("", Bike.BikeType.STANDARD));
    }

    @Test
    void shouldThrowIfNullIsPassedAsBikeTypeToConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Bike("B001", null));
    }

    @Test
    void shouldSetCorrectParametersWhenConstructedU() {
        String id = "B001";
        Bike.BikeType type = Bike.BikeType.STANDARD;

        Bike bike = new Bike(id, type);

        assertAll(
                () -> assertEquals(id, bike.getBikeId()),
                () -> assertEquals(type, bike.getType()),
                () -> assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus()),
                () -> assertEquals(-1.0, bike.getBatteryLevel()),
                () -> assertEquals(0, bike.getTotalRides()),
                () -> assertEquals(0.0, bike.getTotalDistance()),
                //() -> assertEquals(LocalDateTime.now(), bike.getLastMaintenanceDate()),
                () -> assertFalse(bike.needsMaintenance())
        );
    }

    @Test
    void shouldHaveFullBatteryWhenBikeConstructed() {
        Bike bike = new Bike("B001", Bike.BikeType.ELECTRIC);

        assertEquals(100.00, bike.getBatteryLevel());
    }

    @Test
    void shouldThrowIfBikeAlreadyReserved() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.reserve();

        assertThrows(IllegalStateException.class, bike::reserve);
    }

    @Test
    void shouldGiveStatusReservedWhenBikeIsReserved() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.reserve();

        assertEquals(Bike.BikeStatus.RESERVED, bike.getStatus());
    }

    @Test
    void shouldStartRideIfReserved() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.reserve();

        assertDoesNotThrow(bike::startRide);
    }

    @Test
    void shouldThrowIfTryingToStartWithBikeAlreadyInUse() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();

        assertThrows(IllegalStateException.class, bike::startRide);
    }

    @Test
    void shouldThrowExceptionIfStartingRideWithElectronicBikeAndTooLowBattery() {
        Bike bike = new Bike("B001", Bike.BikeType.ELECTRIC);

        bike.startRide();
        bike.endRide(50.00);

        assertThrows(IllegalStateException.class, bike::startRide);
    }

    @Test
    void shouldSetStatusToInUseWhenStatingRide() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();

        assertEquals(Bike.BikeStatus.IN_USE, bike.getStatus());
    }

    @Test
    void shouldThrowIfEndingRideWhenBikeIsNotInUse() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        assertThrows(IllegalStateException.class, () -> bike.endRide(50.00));
    }

    @Test
    void shouldThrowIfEndingRideWithNegativeDistance() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();

        assertThrows(IllegalArgumentException.class, () -> bike.endRide(-50.00));
    }

    @Test
    void shouldSetMaintenanceNeededEvery100Rides() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        int numberOfRides = 100;

        for (int i = 0; i < numberOfRides; i++) {
            bike.startRide();
            bike.endRide(1.00);
        }

        assertTrue(bike.needsMaintenance());
    }

    @Test
    void shouldSetMaintenanceNeededWhenDistanceExceed1000Km() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();
        bike.endRide(1200.00);

        assertTrue(bike.needsMaintenance());
    }

    @Test
    void shouldSetCorrectStatusWhenEndingRid() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();
        bike.endRide(20.00);

        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
    }

    @Test
    void shouldThrowExceptionIfBikeInUseIsSentToMaintenance() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();

        assertThrows(IllegalStateException.class, bike::sendToMaintenance);
    }

    @Test
    void shouldSetCorrectStatusWhenSentToMaintenance() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.sendToMaintenance();

        assertEquals(Bike.BikeStatus.MAINTENANCE, bike.getStatus());
    }

    @Test
    void shouldThrowExceptionIfBikeIsNotInMaintenance() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        assertThrows(IllegalStateException.class, bike::completeMaintenance);
    }

    @Test
    void shouldSetCorrectStatusWhenMaintenanceIsCompleted() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.sendToMaintenance();
        bike.completeMaintenance();

        assertEquals(Bike.BikeStatus.AVAILABLE, bike.getStatus());
    }

    @Test
    void shouldRechargeBatteriesWhenMaintenanceIsCompleted() {
        Bike bike = new Bike("B001", Bike.BikeType.ELECTRIC);

        bike.sendToMaintenance();
        bike.completeMaintenance();

        assertEquals(100.00, bike.getBatteryLevel());
    }

    @Test
    void shouldBeCorrectlyMarkedAsBroken() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.markAsBroken();

        assertEquals(Bike.BikeStatus.BROKEN, bike.getStatus());
    }

    @Test
    void shouldThrowIfNoneElectricBikeIsTryingToBeCharged() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        assertThrows(IllegalStateException.class, () -> bike.chargeBattery(100.00));
    }

    @ParameterizedTest
    @ValueSource(doubles = { -1.00, 200.00 })
    void shouldThrowIfChargeAmountIsTooHighOrTooLow(final double chargeAmount) {
        Bike bike = new Bike("B001", Bike.BikeType.ELECTRIC);

        assertThrows(IllegalArgumentException.class, () -> bike.chargeBattery(chargeAmount));
    }

    @Test
    void shouldCorrectlyChargeBatteryLevel() {
        Bike bike = new Bike("B001", Bike.BikeType.ELECTRIC);

        bike.startRide();
        bike.endRide(10.00);
        bike.chargeBattery(20.00);

        assertEquals(100.00, bike.getBatteryLevel());
    }

    @Test
    void shouldSetCorrectStationId() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);
        String stationId = "S001";

        bike.setCurrentStationId(stationId);

        assertEquals(stationId, bike.getCurrentStationId());
    }

    @Test
    void shouldReturnTrueWhenBikeIsAvailableAndDoesNotNeedMaintenance() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        assertTrue(bike.isAvailable());
    }

    @Test
    void shouldReturnFalseWhenBikeIsNotAvailable() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();

        assertFalse(bike.isAvailable());
    }

    @Test
    void shouldReturnFalseWhenBikeNeedsMaintenance() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);

        bike.startRide();
        bike.endRide(1200.00);

        assertFalse(bike.isAvailable());
    }

    @Test
    void shouldBeEqualWhenComparing(){
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);
        assertEquals(bike, new Bike("B001", Bike.BikeType.STANDARD));
    }

    @Test
    void shouldNotBeEqualWhenComparingNull(){
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);
        assertNotEquals(null, bike);
    }

    @Test
    void shouldNotBeEqualWhenDifferentClass(){
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);
        assertNotEquals(new Object(), bike);
    }

    @Test
    void testEqualsSameClassDifferentId() {
        Bike bike1 = new Bike("B001", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("B002", Bike.BikeType.STANDARD);

        assertNotEquals(bike1, bike2);
    }

    @Test
    void shouldBeEqualWhenComparingSameStationID(){
        Bike bike1 = new Bike("B001", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("B001", Bike.BikeType.STANDARD);

        assertEquals(bike1, bike2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentStationId(){
        Bike bike1 = new Bike("B001", Bike.BikeType.STANDARD);
        Bike bike2 = new Bike("B002", Bike.BikeType.STANDARD);

        assertNotEquals(bike1, bike2);
    }

    @Test
    void shouldReturnCorrectHash() {
        Bike bike = new Bike("B001", Bike.BikeType.STANDARD);
        assertEquals(Objects.hash(bike.getBikeId()), bike.hashCode());
    }
}
