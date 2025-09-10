package com.bikeshare.service.exception;

/**
 * Exception thrown when an invalid bike operation is attempted.
 */
public class InvalidBikeOperationException extends RuntimeException {
    
    public InvalidBikeOperationException(String message) {
        super(message);
    }
    
    public InvalidBikeOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
