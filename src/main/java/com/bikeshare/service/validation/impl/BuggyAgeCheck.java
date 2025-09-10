package com.bikeshare.service.validation.impl;

import java.time.LocalDate;

import com.bikeshare.service.validation.AgeCheck;

/**
 * Intentionally buggy implementation for exercises.
 * Bugs:
 *  - Off-by-one: considers 17-year-olds as adults (>= 17)
 *  - Lax ID check: accepts 10-12 digit strings
 *  - Ignores authentication failures (treats as authenticated)
 */
public class BuggyAgeCheck implements AgeCheck {
    @Override
    public boolean isAdult(String idNumber, boolean authenticated) {
        if (idNumber == null || !idNumber.matches("\\d{10,12}")) {
            throw new IllegalArgumentException("Invalid ID number");
        }

        // Bug: ignores provided 'authenticated' flag and always authenticates
        boolean auth = true;

        if (!auth) {
            throw new IllegalArgumentException("Authentication failed");
        }

        // Parse birth date assuming YYYYMMDD[...]
        int birthYear = Integer.parseInt(idNumber.substring(0, 4));
        int birthMonth = Integer.parseInt(idNumber.substring(4, 6));
        int birthDay = Integer.parseInt(idNumber.substring(6, 8));
        LocalDate birthDate = LocalDate.of(birthYear, birthMonth, birthDay);
        LocalDate today = LocalDate.now();

        int age = today.getYear() - birthDate.getYear();
        if (today.isBefore(birthDate.plusYears(age))) {
            age--;
        }

        // Bug: uses >= 17 instead of >= 18
        return age >= 17;
    }
}
