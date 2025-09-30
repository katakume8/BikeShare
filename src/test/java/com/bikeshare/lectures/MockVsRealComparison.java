package com.bikeshare.lectures;

import com.bikeshare.service.auth.BankIDService;
import com.bikeshare.service.validation.AgeValidator;
import com.bikeshare.service.validation.IDNumberValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comparison between using REAL objects vs MOCKS/STUBS in testing.
 * This class demonstrates why mocking is powerful for testing business logic
 * without depending on complex validation or external services.
 * 
 * Key Learnings:
 * 1. Mocks let us control dependencies and focus on the logic we're testing
 * 2. We still need data in the right format for business logic (YYYYMMDDNNNN for Swedish IDs)
 * 3. But we can control whether validation passes or fails regardless of format
 * 4. This gives us the best of both worlds: realistic data + controlled behavior
 * 
 * IMPORTANT: Notice how mocking doesn't mean we can use completely random data.
 * The business logic (age calculation) still needs parseable dates, but we can
 * control the validation and authentication steps independently.
 */
@ExtendWith(MockitoExtension.class)
public class MockVsRealComparison {

    // Mocked dependencies - controlled by us
    @Mock
    private IDNumberValidator mockIDValidator;
    
    @Mock
    private BankIDService mockBankIDService;
    
    // Real dependencies - actual implementation
    private IDNumberValidator realIDValidator;
    private BankIDService realBankIDService;
    
    @BeforeEach
    void setUp() {
        // Setup real implementations (like in CoverageEx.java)
        realIDValidator = new RealIDValidator();
        realBankIDService = new RealBankIDService(true); // always passes
    }

    // ============================================================================
    // PART 1: TESTING WITH REAL OBJECTS (like CoverageEx.java)
    // Problems: Complex setup, real validation logic, hard to test edge cases
    // ============================================================================

    @Test
    @DisplayName("REAL OBJECTS: Adult validation with actual Swedish personnummer")
    void testAdultValidation_WithRealObjects() {
        // Arrange: Need REAL Swedish personnummer that passes validation
        AgeValidator validator = new AgeValidator(realIDValidator, realBankIDService);
        String realAdultID = "199001012384"; // Must be valid format YYYYMMDDNNNN (12 digits)
        
        // Act: Uses real validation logic
        boolean result = validator.isAdult(realAdultID);
        
        // Assert: Depends on real ID validation working correctly
        assertTrue(result, "Real adult ID should return true");
        
        // Problems with this approach:
        // 1. Must use REAL valid Swedish personnummer (12 digits)
        // 2. Test breaks if ID validation logic changes
        // 3. Hard to test edge cases (what if BankID is down?)
        // 4. Slow - real validation might be complex
    }

    @Test
    @DisplayName("REAL OBJECTS: Child validation - need real valid child ID")
    void testChildValidation_WithRealObjects() {
        // Arrange: Need REAL child personnummer (complex to generate)
        AgeValidator validator = new AgeValidator(realIDValidator, realBankIDService);
        String realChildID = "201501012381"; // Must be valid format YYYYMMDDNNNN (12 digits)
        
        // Act & Assert
        boolean result = validator.isAdult(realChildID);
        assertFalse(result, "Real child ID should return false");
        
        // Problem: If real ID validator rejects this format, test fails
        // even though our AGE LOGIC might be correct!
    }

    // ============================================================================
    // PART 2: TESTING WITH MOCKS/STUBS 
    // Benefits: Simple setup, fast, reliable, test exactly what we want
    // ============================================================================

    @Test
    @DisplayName("MOCKED: Adult validation - focus on age logic only")
    void testAdultValidation_WithMocks() {
        // Arrange: Use ID format that the business logic can parse, but control validation
        String adultFormatID = "199001010000";  // Format: YYYYMMDDNNNN (adult born 1990)
        
        // STUB: Make ID validator always return true (we control validation)
        when(mockIDValidator.isValidIDNumber(adultFormatID)).thenReturn(true);
        
        // STUB: Make BankID always authenticate successfully  
        when(mockBankIDService.authenticate(adultFormatID)).thenReturn(true);
        
        // Create validator with mocked dependencies
        AgeValidator validator = new AgeValidator(mockIDValidator, mockBankIDService);
        
        // Act: Test our age logic with controlled dependencies
        boolean result = validator.isAdult(adultFormatID);
        
        // Assert: Focus ONLY on age validation logic
        assertTrue(result, "Should return true for adult when dependencies work");
        
        // Verify interactions happened as expected
        verify(mockIDValidator).isValidIDNumber(adultFormatID);
        verify(mockBankIDService).authenticate(adultFormatID);
        
        // Benefits:
        // 1. Control validation behavior without real validation logic!
        // 2. Fast execution
        // 3. Tests age logic with controlled dependencies
        // 4. Easy to understand and maintain
    }

    @Test
    @DisplayName("MOCKED: Child validation - control the outcome")
    void testChildValidation_WithMocks() {
        // Arrange: Use child format ID but control validation behavior
        String childFormatID = "201501010000";  // Format: YYYYMMDDNNNN (child born 2015)
        
        // STUB: Control what dependencies return
        when(mockIDValidator.isValidIDNumber(childFormatID)).thenReturn(true);
        when(mockBankIDService.authenticate(childFormatID)).thenReturn(true);
        
        AgeValidator validator = new AgeValidator(mockIDValidator, mockBankIDService);
        
        // Act: The validator will determine this is a child based on birth year
        boolean result = validator.isAdult(childFormatID);
        
        // Assert: Test the age determination logic
        assertFalse(result, "Should return false for child when dependencies work");
        
        // Verify both services were called
        verify(mockIDValidator).isValidIDNumber(childFormatID);
        verify(mockBankIDService).authenticate(childFormatID);
    }

    // ============================================================================
    // PART 3: TESTING ERROR CONDITIONS - Much easier with mocks!
    // ============================================================================

    @Test
    @DisplayName("MOCKED: Invalid ID - easy to simulate")
    void testInvalidID_WithMocks() {
        // Arrange: Make ID validator reject any ID format (we control this!)
        String invalidID = "999999999999";  // Valid format but we'll make validator reject it
        when(mockIDValidator.isValidIDNumber(invalidID)).thenReturn(false);
        // No need to setup BankID - should not be called
        
        AgeValidator validator = new AgeValidator(mockIDValidator, mockBankIDService);
        
        // Act & Assert: Should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.isAdult(invalidID)
        );
        
        assertEquals("Invalid ID number", exception.getMessage());
        
        // Verify ID validator was called, but BankID was NOT
        verify(mockIDValidator).isValidIDNumber(invalidID);
        verifyNoInteractions(mockBankIDService);
        
        // Benefit: Easy to test error conditions by controlling mock behavior!
    }

    @Test
    @DisplayName("MOCKED: Authentication failure - simulate external service down")
    void testAuthenticationFailure_WithMocks() {
        // Arrange: ID passes validation but BankID service fails
        String validFormatID = "199001010000";  // Valid format
        when(mockIDValidator.isValidIDNumber(validFormatID)).thenReturn(true);
        when(mockBankIDService.authenticate(validFormatID)).thenReturn(false);  // Simulate auth failure
        
        AgeValidator validator = new AgeValidator(mockIDValidator, mockBankIDService);
        
        // Act & Assert: Should throw authentication exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.isAdult(validFormatID)
        );
        
        assertEquals("Authentication failed", exception.getMessage());
        
        // Verify both services were called in correct order
        verify(mockIDValidator).isValidIDNumber(validFormatID);
        verify(mockBankIDService).authenticate(validFormatID);
        
        // Benefit: Easy to simulate external service failures!
    }

    // ============================================================================
    // PART 4: HELPER METHOD COMPARISON - Real vs Mock approaches
    // ============================================================================

    @Test
    @DisplayName("COMPARISON: Using helper method with real objects")
    void helperMethodComparison_RealObjects() {
        // Test with real implementation - need 12-digit format
        boolean adultResult = testAgeValidationWithRealObjects("199001012384", true);
        boolean childResult = testAgeValidationWithRealObjects("201501012381", false);
        
        assertTrue(adultResult, "Real adult test should pass");
        assertTrue(childResult, "Real child test should pass");
        
        // Problems: Need real IDs, dependent on implementation details
    }

    @Test
    @DisplayName("COMPARISON: Using helper method with mocks")
    void helperMethodComparison_MockObjects() {
        // Test with mocked dependencies - much more flexible!
        boolean adultResult = testAgeValidationWithMocks("199001010000", true, true);   // Adult format
        boolean childResult = testAgeValidationWithMocks("201501010000", false, true);  // Child format
        boolean errorResult = testAgeValidationWithMocks("999999999999", false, false); // Auth fails
        
        assertTrue(adultResult, "Mocked adult test should pass");
        assertTrue(childResult, "Mocked child test should pass"); 
        assertTrue(errorResult, "Mocked error test should pass");
        
        // Benefits: Control mock behavior, full control over test scenarios
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Helper method using REAL objects - requires valid Swedish personnummer
     */
    private boolean testAgeValidationWithRealObjects(String realID, boolean expectedResult) {
        try {
            AgeValidator validator = new AgeValidator(realIDValidator, realBankIDService);
            boolean result = validator.isAdult(realID);
            return result == expectedResult;
        } catch (Exception e) {
            // Real objects might throw unexpected exceptions
            System.out.println("Real object test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method using MOCKS - works with any ID string
     */
    private boolean testAgeValidationWithMocks(String anyID, boolean shouldBeAdult, boolean shouldAuthenticate) {
        try {
            // Setup mocks for this specific test
            when(mockIDValidator.isValidIDNumber(anyID)).thenReturn(true);
            when(mockBankIDService.authenticate(anyID)).thenReturn(shouldAuthenticate);
            
            AgeValidator validator = new AgeValidator(mockIDValidator, mockBankIDService);
            
            if (!shouldAuthenticate) {
                // Expect authentication exception
                assertThrows(IllegalArgumentException.class, () -> validator.isAdult(anyID));
                return true;
            } else {
                // Normal flow
                boolean result = validator.isAdult(anyID);
                return result == shouldBeAdult;
            }
        } catch (Exception e) {
            System.out.println("Mock test failed: " + e.getMessage());
            return false;
        }
    }

    // ============================================================================
    // DEMONSTRATION: Side-by-side comparison
    // ============================================================================

    @Test
    @DisplayName("SIDE-BY-SIDE: Real vs Mock comparison")
    void sideBySideComparison() {
        System.out.println("\n=== REAL OBJECTS vs MOCKS COMPARISON ===");
        
        // Real objects approach
        System.out.println("\n1. REAL OBJECTS:");
        System.out.println("   - Need valid Swedish personnummer: 19900101234");
        System.out.println("   - Test breaks if ID validation changes");
        System.out.println("   - Hard to test edge cases");
        System.out.println("   - Slower execution");
        
        // Mock objects approach  
        System.out.println("\n2. MOCK OBJECTS:");
        System.out.println("   - Any ID string works: 'fake-id-123'");
        System.out.println("   - Test only our age logic");
        System.out.println("   - Easy to test all scenarios");
        System.out.println("   - Fast and reliable");
        
        // The test itself - both should work, but mocks are more flexible
        AgeValidator realValidator = new AgeValidator(realIDValidator, realBankIDService);
        AgeValidator mockedValidator = new AgeValidator(mockIDValidator, mockBankIDService);
        
        // Setup mock behavior
        when(mockIDValidator.isValidIDNumber(anyString())).thenReturn(true);
        when(mockBankIDService.authenticate(anyString())).thenReturn(true);
        
        // Both approaches should work for basic cases
        assertDoesNotThrow(() -> {
            boolean realResult = realValidator.isAdult("199001012384"); // Need real 12-digit ID
            boolean mockResult = mockedValidator.isAdult("199001010000"); // Control via mocks!
            
            System.out.println("\n   Real result: " + realResult);
            System.out.println("   Mock result: " + mockResult);
        });
        
        System.out.println("\n=== CONCLUSION: Mocks give us more control and flexibility! ===\n");
    }

    // ============================================================================
    // Real implementation classes (like the test support classes in CoverageEx)
    // ============================================================================

    /**
     * Real ID validator - actual implementation with Swedish personnummer logic
     */
    static class RealIDValidator implements IDNumberValidator {
        @Override
        public boolean isValidIDNumber(String idNumber) {
            if (idNumber == null || idNumber.length() != 12) {
                return false;
            }
            
            // Check format: YYYYMMDDNNNN (12 digits)
            for (int i = 0; i < idNumber.length(); i++) {
                if (!Character.isDigit(idNumber.charAt(i))) {
                    return false;
                }
            }
            
            // Additional real validation logic could go here
            // (checksum, valid dates, etc.)
            return true;
        }
    }

    /**
     * Real BankID service - simulates external authentication service
     */
    static class RealBankIDService implements BankIDService {
        private boolean alwaysPass;

        public RealBankIDService(boolean alwaysPass) {
            this.alwaysPass = alwaysPass;
        }

        @Override
        public boolean authenticate(String idNumber) {
            if (!alwaysPass) {
                return false;
            }
            
            // Simulate some authentication logic
            // In real life, this might make network calls, etc.
            try {
                Thread.sleep(10); // Simulate network delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return true;
        }
    }
}