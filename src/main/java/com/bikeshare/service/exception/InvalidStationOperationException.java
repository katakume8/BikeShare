package com.bikeshare.service.exception;

/**
 * Exception thrown when an invalid station operation is attempted.
 */
public class InvalidStationOperationException extends RuntimeException {
    
    public InvalidStationOperationException(String message) {
        super(message);
    }
    
    public InvalidStationOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
