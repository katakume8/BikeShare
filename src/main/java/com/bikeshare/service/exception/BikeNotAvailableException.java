package com.bikeshare.service.exception;

/**
 * Exception thrown when a bike is not available for the requested operation.
 */
public class BikeNotAvailableException extends RuntimeException {
    
    public BikeNotAvailableException(String message) {
        super(message);
    }
    
    public BikeNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
