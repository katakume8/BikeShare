package com.bikeshare.service.exception;

/**
 * Exception thrown when a user already has an active ride and tries to start another.
 */
public class ActiveRideExistsException extends RuntimeException {
    
    public ActiveRideExistsException(String message) {
        super(message);
    }
    
    public ActiveRideExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
