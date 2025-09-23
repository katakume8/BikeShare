package com.bikeshare.lectures;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.bikeshare.service.auth.BankIDService;
import com.bikeshare.service.validation.AgeValidator;
import com.bikeshare.service.validation.IDNumberValidator;

/**
 * Basic Unit Testing examples using real Swedish personnummer.
 * Demonstrates: Fixtures, Different Assertions, and Parameterized Tests 
 */
@DisplayName("Basic Unit Testing - Real Swedish Personnummer")
public class BasicUnitTesting {

    // ========== TEST FIXTURES ==========
    private static int testCount;
    private AgeValidator ageValidator;
    
    // Real Swedish personnummer (YYMMDD-NNNN format)
    private static final String ADULT_2000 = "000101-2384"; // Born 2000
    private static final String ADULT_2001 = "010101-2391"; // Born 2001
    private static final String MINOR_2010 = "100101-2391"; // Born 2010
    private static final String MINOR_2008 = "080101-2394"; // Born 2008
    
    @BeforeAll
    static void setupTestSuite() {
        System.out.println("🚀 Starting Basic Unit Testing");
        testCount = 0;
    }
    
    @BeforeEach
    void setupEachTest() {
        testCount++;
        System.out.println("🔧 Test #" + testCount + " - Setting up");
        
        // Create real implementations
        BasicIDValidator idValidator = new BasicIDValidator();
        BasicBankIDService bankIdService = new BasicBankIDService(true);
        this.ageValidator = new AgeValidator(idValidator, bankIdService);
    }
    
    @AfterEach
    void cleanupEachTest() {
        System.out.println("🧹 Test #" + testCount + " completed");
    }
    
    @AfterAll
    static void teardownTestSuite() {
        System.out.println("🏁 All tests completed. Total: " + testCount + " tests");
    }

    /**
     * Convert YYMMDD-NNNN format to YYYYMMDDNNNN format
     */
    private String convertToFullFormat(String shortFormat) {
        String withoutDash = shortFormat.replace("-", "");
        String yearPart = withoutDash.substring(0, 2);
        int year = Integer.parseInt(yearPart);
        
        String fullYear;
        if (year >= 0 && year <= 25) {
            fullYear = "20" + yearPart;  // 00-25 = 2000-2025
        } else {
            fullYear = "19" + yearPart;  // 26-99 = 1926-1999
        }
        
        String restOfID = withoutDash.substring(2);
        return fullYear + restOfID;
    }

    // ========== 1. BASIC AAA PATTERN ==========

    @Test
    @DisplayName("1. Basic AAA Pattern - Adult validation")
    void testBasicAAA_AdultValidation() {
        // ===== ARRANGE =====
        String adultPersonnummer = convertToFullFormat(ADULT_2000);
        
        // ===== ACT =====
        boolean result = ageValidator.isAdult(adultPersonnummer);
        
        // ===== ASSERT =====
        assertTrue(result, "Person born in 2000 should be adult");
    }

    // ========== 2. DIFFERENT ASSERTION TYPES ==========

    @Test
    @DisplayName("2a. assertTrue - Adult should return true")
    void testAssertTrue_Adult() {
        String adultId = convertToFullFormat(ADULT_2001);
        boolean result = ageValidator.isAdult(adultId);
        
        assertTrue(result, "Adult should return true");
        assertTrue(result == true, "Result should be exactly true");
    }

    @Test
    @DisplayName("2b. assertFalse - Minor should return false")
    void testAssertFalse_Minor() {
        String minorId = convertToFullFormat(MINOR_2010);
        boolean result = ageValidator.isAdult(minorId);
        
        assertFalse(result, "Minor should return false");
        assertFalse(result == true, "Result should not be true");
    }

    @Test
    @DisplayName("2c. assertSame vs assertNotSame")
    void testAssertSame_ObjectIdentity() {
        AgeValidator validator1 = ageValidator;
        AgeValidator validator2 = ageValidator; // Same reference
        AgeValidator validator3 = new AgeValidator(new BasicIDValidator(), new BasicBankIDService(true));
        
        assertSame(validator1, validator2, "Same reference should be identical");
        assertNotSame(validator1, validator3, "Different instances should not be same");
    }

    @Test
    @DisplayName("2d. assertNotNull vs assertNull")
    void testAssertNull_Examples() {
        assertNotNull(ageValidator, "AgeValidator should not be null");
        assertNotNull(ADULT_2000, "Adult ID should not be null");
        
        String nullString = null;
        assertNull(nullString, "Null string should be null");
    }

    @Test
    @DisplayName("2e. assertEquals vs assertNotEquals")
    void testAssertEquals_Examples() {
        String id1 = convertToFullFormat(ADULT_2000);
        String id2 = convertToFullFormat(ADULT_2000); // Same value
        String id3 = convertToFullFormat(ADULT_2001); // Different value
        
        assertEquals(id1, id2, "Same converted IDs should be equal");
        assertNotEquals(id1, id3, "Different IDs should not be equal");
        assertEquals(12, id1.length(), "Converted ID should be 12 characters");
    }

    // ========== 3. PARAMETERIZED TESTS==========

    @ParameterizedTest
    @DisplayName("3a. Parameterized - Multiple valid adults")
    @ValueSource(strings = {
        "000101-2384", // 2000 - 25 years old
        "010101-2391", // 2001 - 24 years old  
        "020101-2390", // 2002 - 23 years old
        "030101-2381", // 2003 - 22 years old
        "040101-2380"  // 2004 - 21 years old
    })
    void testParameterized_ValidAdults(String personnummer) {
        String convertedId = convertToFullFormat(personnummer);
        boolean result = ageValidator.isAdult(convertedId);
        
        assertTrue(result, "Person with ID " + personnummer + " should be adult");
    }

    @ParameterizedTest
    @DisplayName("3b. Parameterized - Multiple minors")
    @ValueSource(strings = {
        "080101-2394", // 2008 - 17 years old
        "090101-2393", // 2009 - 16 years old
        "100101-2391", // 2010 - 15 years old
        "110101-2398", // 2011 - 14 years old
        "120101-2395"  // 2012 - 13 years old
    })
    void testParameterized_Minors(String personnummer) {
        String convertedId = convertToFullFormat(personnummer);
        boolean result = ageValidator.isAdult(convertedId);
        
        assertFalse(result, "Person with ID " + personnummer + " should be minor");
    }

    @ParameterizedTest
    @DisplayName("3c. Parameterized - Invalid formats")
    @ValueSource(strings = {"", "abc", "123", "1234567890123", "invalid-format"})
    void testParameterized_InvalidFormats(String invalidId) {
        assertThrows(IllegalArgumentException.class, 
            () -> ageValidator.isAdult(invalidId),
            "Invalid ID '" + invalidId + "' should throw exception");
    }

    // ========== 4. EXCEPTION TESTING ==========

    @Test
    @DisplayName("4a. assertThrows - Invalid ID format")
    void testAssertThrows_InvalidFormat() {
        String invalidId = "not-valid-format";
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ageValidator.isAdult(invalidId),
            "Should throw exception for invalid format"
        );
        
        assertEquals("Invalid ID number", exception.getMessage());
    }

    @Test
    @DisplayName("4b. assertThrows - Authentication failure")
    void testAssertThrows_AuthFailure() {
        BasicBankIDService failingBankId = new BasicBankIDService(false);
        AgeValidator failingValidator = new AgeValidator(new BasicIDValidator(), failingBankId);
        String validId = convertToFullFormat(ADULT_2000);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> failingValidator.isAdult(validId),
            "Should throw exception when auth fails"
        );
        
        assertEquals("Authentication failed", exception.getMessage());
    }

    // ========== 5. TIMEOUT TESTING ==========

    @Test
    @DisplayName("5a. assertTimeout - Normal execution")
    void testAssertTimeout_NormalExecution() {
        String validId = convertToFullFormat(ADULT_2001);
        
        boolean result = assertTimeout(Duration.ofSeconds(1), () -> {
            return ageValidator.isAdult(validId);
        }, "Should complete within 1 second");
        
        assertTrue(result);
    }

    @Test
    @DisplayName("5b. assertTimeout - With delay")
    void testAssertTimeout_WithDelay() {
        String validId = convertToFullFormat(ADULT_2001);
        
        boolean result = assertTimeout(Duration.ofMillis(200), () -> {
            Thread.sleep(10); // Small delay
            return ageValidator.isAdult(validId);
        }, "Should handle small processing delays");
        
        assertTrue(result);
    }

    // ========== 6. COMPREHENSIVE TEST ==========

    @Test
    @DisplayName("6. Comprehensive test with multiple assertions")
    void testComprehensive_MultipleAssertions() {
        String adultId = convertToFullFormat("050101-2397"); // 2005, ~20 years old
        boolean result = ageValidator.isAdult(adultId);
        
        assertAll("Comprehensive assertions",
            () -> assertTrue(result, "Should be adult"),
            () -> assertNotNull(ageValidator, "Validator should not be null"),
            () -> assertEquals(12, adultId.length(), "ID should be 12 characters"),
            () -> assertTrue(adultId.startsWith("2005"), "Should start with 2005")
        );
    }

    // ========== HELPER CLASSES ==========

    static class BasicIDValidator implements IDNumberValidator {
        @Override
        public boolean isValidIDNumber(String idNumber) {
            if (idNumber == null || idNumber.length() != 12) {
                return false;
            }
            
            for (int i = 0; i < idNumber.length(); i++) {
                if (!Character.isDigit(idNumber.charAt(i))) {
                    return false;
                }
            }
            
            return true;
        }
    }

    static class BasicBankIDService implements BankIDService {
        private final boolean shouldAuthenticate;

        public BasicBankIDService(boolean shouldAuthenticate) {
            this.shouldAuthenticate = shouldAuthenticate;
        }

        @Override
        public boolean authenticate(String idNumber) {
            return shouldAuthenticate;
        }
    }
}