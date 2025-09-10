package com.bikeshare.service.validation;

/**
 * Minimal Swedish personal number validator abstraction.
 */
public interface IDNumberValidator {
    /**
     * Validates ID number format (YYYYMMDDNNNN) and optionally checksum.
     */
    boolean isValidIDNumber(String idNumber);
}
