package com.bikeshare.service.exception;

/**
 * Exception thrown when a station is not found in the system.
 */
public class StationNotFoundException extends RuntimeException {
    
    public StationNotFoundException(String message) {
        super(message);
    }
    
    public StationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
