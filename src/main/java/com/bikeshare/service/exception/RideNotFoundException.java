package com.bikeshare.service.exception;

/**
 * Exception thrown when a ride is not found in the system.
 */
public class RideNotFoundException extends RuntimeException {
    
    public RideNotFoundException(String message) {
        super(message);
    }
    
    public RideNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
