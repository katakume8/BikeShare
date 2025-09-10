package com.bikeshare.service.validation;

/**
 * Abstraction for age checks used by the web demo. Enables swapping in buggy
 * implementations for teaching bug-hunting exercises without changing public APIs.
 */
public interface AgeCheck {
    /**
     * Returns true if the person is adult (>= 18), else false.
     * Implementations may throw IllegalArgumentException for invalid input or failed auth.
     */
    boolean isAdult(String idNumber, boolean authenticated);
}
