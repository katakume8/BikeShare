package com.bikeshare.lab6;

import com.bikeshare.model.Bike;
import com.bikeshare.repository.BikeRepository;
import com.bikeshare.service.BikeService;
import com.bikeshare.service.exception.BikeNotAvailableException;
import com.bikeshare.service.exception.BikeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BikeServiceRentTest {

    private BikeRepository bikeRepository;
    private BikeService bikeService;

    @BeforeEach
    void setUp() {
        bikeRepository = mock(BikeRepository.class);
        bikeService = new BikeService(bikeRepository);
    }

    @Test
    void rentBike_success_marksBikeInUse_and_saves() {
        Bike bike = new Bike("B1", Bike.BikeType.STANDARD);
        when(bikeRepository.findById("B1")).thenReturn(Optional.of(bike));
        when(bikeRepository.save(any(Bike.class))).thenAnswer(inv -> inv.getArgument(0));

        bikeService.rentBike("B1", "U1");

        assertEquals(Bike.BikeStatus.IN_USE, bike.getStatus());
        verify(bikeRepository).save(bike);
    }

    @Test
    void rentBike_bikeNotFound_throws() {
        when(bikeRepository.findById("B2")).thenReturn(Optional.empty());

        assertThrows(BikeNotFoundException.class, () -> bikeService.rentBike("B2", "U1"));
        verify(bikeRepository, never()).save(any());
    }

    @Test
    void rentBike_notAvailable_throws() {
        Bike bike = new Bike("B3", Bike.BikeType.STANDARD);
        // Put bike into IN_USE status
        bike.startRide();
        when(bikeRepository.findById("B3")).thenReturn(Optional.of(bike));

        assertThrows(BikeNotAvailableException.class, () -> bikeService.rentBike("B3", "U1"));
        verify(bikeRepository, never()).save(any());
    }
}
