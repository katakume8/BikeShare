package com.bikeshare.lectures;

import com.bikeshare.service.auth.BankIDService;
import com.bikeshare.service.validation.AgeValidator;
import com.bikeshare.service.validation.IDNumberValidator;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Coverage examples for beginners learning software testing.
 * Shows statement, branch, exception, and timeout coverage using real Swedish personnummer.
 */
public class CoverageEx {

    private AgeValidator ageValidator;

    @BeforeEach
    void setUp() {
        // We use hardcoded valid Swedish personnummer in the tests
        
        // Create test objects
        SimpleIDValidator idValidator = new SimpleIDValidator();
        SimpleBankID bankID = new SimpleBankID(true); // always works
        this.ageValidator = new AgeValidator(idValidator, bankID);
    }

    // --- STATEMENT COVERAGE: Tests the happy path ---
    @Test
    void test_adult_person_returns_true() {
        // Arrange: Get a valid adult ID (born in 1990 = ~35 years old)
        String adultID = convertToFullFormat("900101-2384");

        // Act: Check if adult
        boolean result = ageValidator.isAdult(adultID);

        // Assert: Should be true for adults
        assertTrue(result, "Adult should return true");
    }

    // --- BRANCH COVERAGE: Tests the false branch ---
    @Test
    void test_child_person_returns_false() {
        // Arrange: Get a valid child ID (born in 2015 = ~10 years old) 
        String childID = convertToFullFormat("150101-2381");

        // Act: Check if adult
        boolean result = ageValidator.isAdult(childID);

        // Assert: Should be false for children
        assertFalse(result, "Child should return false");
    }

    // --- EXCEPTION COVERAGE: Tests invalid ID exception ---
    @Test
    void test_invalid_id_throws_exception() {
        // Arrange: Use clearly invalid ID
        String invalidID = "not-a-real-id";

        // Act & Assert: Should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ageValidator.isAdult(invalidID)
        );
        
        assertEquals("Invalid ID number", exception.getMessage());
    }

    // --- EXCEPTION COVERAGE: Tests authentication failure ---
    @Test
    void test_auth_failure_throws_exception() {
        // Arrange: Create validator that always fails authentication
        SimpleIDValidator idValidator = new SimpleIDValidator();
        SimpleBankID failingBankID = new SimpleBankID(false); // always fails
        AgeValidator failingValidator = new AgeValidator(idValidator, failingBankID);
        
        String validID = convertToFullFormat("900101-2384");

        // Act & Assert: Should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> failingValidator.isAdult(validID)
        );
        
        assertEquals("Authentication failed", exception.getMessage());
    }

    // --- TIMEOUT COVERAGE: Tests method performance ---
    @Test
    void test_method_completes_quickly() {
        // Arrange: Get a valid adult ID
        String adultID = convertToFullFormat("800101-2382");

        // Act & Assert: Should complete within 100ms
        assertTimeout(java.time.Duration.ofMillis(100), () -> {
            boolean result = ageValidator.isAdult(adultID);
            assertTrue(result, "Should return true for adult");
        });
    }

    // --- BRANCH COVERAGE: Tests birthday logic branch ---
    @Test
    void test_person_just_turned_18() {
        // Arrange: Someone who just turned 18 (born in 2007)
        String recentAdultID = convertToFullFormat("070101-2389");

        // Act: Check if adult
        boolean result = ageValidator.isAdult(recentAdultID);

        // Assert: Should be true for 18-year-old
        assertTrue(result, "18-year-old should be adult");
    }

    // ======================
    // Helper methods
    // ======================

    /**
     * Converts Swedish personnummer from YYMMDD-NNNN to YYYYMMDDNNNN format
     */
    private String convertToFullFormat(String shortFormat) {
        // Remove the dash: "900101-2384" -> "9001012384"
        String withoutDash = shortFormat.replace("-", "");
        
        // Get the year part (first 2 digits)
        String yearPart = withoutDash.substring(0, 2);
        int year = Integer.parseInt(yearPart);
        
        // Convert 2-digit year to 4-digit year
        String fullYear;
        if (year >= 0 && year <= 25) {
            fullYear = "20" + yearPart;  // 00-25 = 2000-2025
        } else {
            fullYear = "19" + yearPart;  // 26-99 = 1926-1999
        }
        
        // Combine: full year + month + day + last 4 digits
        String restOfID = withoutDash.substring(2); // MMDDNNNN
        return fullYear + restOfID;
    }



    // ======================
    // Test support classes
    // ======================

    /**
     * ID validator - checks basic format requirements
     */
    static class SimpleIDValidator implements IDNumberValidator {
        @Override
        public boolean isValidIDNumber(String idNumber) {
            // Check basic requirements
            if (idNumber == null) {
                return false;
            }
            if (idNumber.length() != 12) {
                return false;
            }
            
            // Check all characters are digits
            for (int i = 0; i < idNumber.length(); i++) {
                if (!Character.isDigit(idNumber.charAt(i))) {
                    return false;
                }
            }
            
            return true;
        }
    }

    /**
     * BankID service - can be set to always pass or always fail
     */
    static class SimpleBankID implements BankIDService {
        private boolean shouldPass;

        public SimpleBankID(boolean shouldPass) {
            this.shouldPass = shouldPass;
        }

        @Override
        public boolean authenticate(String idNumber) {
            return shouldPass;
        }
    }
}