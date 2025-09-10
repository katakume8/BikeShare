package com.bikeshare.service.auth;

/**
 * BankID authentication service abstraction for Swedish context.
 * Keep it simple and mock-friendly for tests (Mockito ready).
 */
public interface BankIDService {

    /**
     * Authenticates a user by Swedish personal number (personnummer).
     * @param personalNumber 12-digit YYYYMMDDNNNN
     * @return true if authentication succeeds
     */
    boolean authenticate(String personalNumber);
}
