package com.bikeshare.service.exception;

/**
 * Base exception for all service layer exceptions.
 * Demonstrates exception hierarchy testing (Lab 4).
 */
public class ServiceException extends Exception {
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
