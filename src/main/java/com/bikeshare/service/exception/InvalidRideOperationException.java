package com.bikeshare.service.exception;

/**
 * Exception thrown when an invalid ride operation is attempted.
 */
public class InvalidRideOperationException extends RuntimeException {
    
    public InvalidRideOperationException(String message) {
        super(message);
    }
    
    public InvalidRideOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
