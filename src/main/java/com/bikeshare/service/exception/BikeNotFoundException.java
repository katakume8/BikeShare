package com.bikeshare.service.exception;

/**
 * Exception thrown when a bike is not found.
 */
public class BikeNotFoundException extends RuntimeException {
    
    public BikeNotFoundException(String message) {
        super(message);
    }
    
    public BikeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
