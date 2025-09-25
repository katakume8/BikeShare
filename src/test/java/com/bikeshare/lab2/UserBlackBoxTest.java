package com.bikeshare.lab2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bikeshare.model.User;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 2 Template: Black Box Testing for User class
 * 
 
 * 
 * TODO for students:
 * - Challenge 2.1: Add Equivalence Partitioning tests for email validation, name, telephone number (With GenAI help), and fund addition
 * - Challenge 2.2: Add Boundary Value Analysis tests for fund addition
 * - Challenge 2.3: Add Decision Table tests for phone number validation
 * - Optional Challenge 2.4: Add error scenario tests
 */

// This test is just an example to get you started. You will need to add more tests as per the challenges.
@DisplayName("Verify name handling in User class")
class UserBlackBoxTest {
    User user;

    @BeforeEach
    void setUp() {
        String expectedFirstName = "John";
        String expectedLastName = "Doe";
        String validEmail = "john.doe@example.com";
        String validPersonnummer = "901101-1237"; // Valid Swedish personnummer
        String validPhoneNumber = "0701234567";

        this.user = new User(validPersonnummer, validEmail, expectedFirstName, expectedLastName);
        user.setPhoneNumber(validPhoneNumber);
        user.verifyEmail();
        user.activate();
    }

    @Test
    @DisplayName("Should store and retrieve user names correctly")
    void shouldStoreAndRetrieveUserNamesCorrectly() {
        // Arrange - Set up test data
        String expectedFirstName = "John";
        String expectedLastName = "Doe";
        String validEmail = "john.doe@example.com";
        String validPersonnummer = "901101-1237"; // Valid Swedish personnummer
        
        // Act - Execute the method under test
        User user = new User(validPersonnummer, validEmail, expectedFirstName, expectedLastName);
        String actualFirstName = user.getFirstName();
        String actualLastName = user.getLastName();
        String actualFullName = user.getFullName();
        
        // Assert - Verify the expected outcome
        assertNotNull(user, "User should be created successfully");
        assertEquals(expectedFirstName, actualFirstName, "First name should match");
        assertEquals(expectedLastName, actualLastName, "Last name should match");
        assertEquals("John Doe", actualFullName, "Full name should be formatted correctly");
    }
    
    // TODO: Challenge 2.1 - Add Equivalence Partitioning tests for email validation
    // Hint: Test valid emails (user@domain.com) and invalid emails (missing @, empty, etc.)

    @Test
    void shouldAllowValidEmail() {
        assertEquals("john.doe@example.com",user.getEmail(), "Email should be valid");
    }
    @Test
    void shouldDenyInvalidEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> user.setEmail("invalidEmail"),
                "Email should be invalid");
    }
    
    // TODO: Challenge 2.2 - Add Boundary Value Analysis tests for fund addition
    // Hint: Test minimum (0.01), maximum (1000.00), and invalid amounts (0, negative, > 1000)

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1000.00})
    @DisplayName("Should be able to add the minimum and maximum amount of funds")
    void shouldBeAbleToAddMinimumFunds(final double fundsToAdd) {
        double accountBalance = user.getAccountBalance();
        double amountToAdd = 0.01;
        user.addFunds(amountToAdd);
        assertEquals(accountBalance + amountToAdd, user.getAccountBalance(), "Account balance should match");
    }

    @Test
    @DisplayName("Should reject funds added below the minimum amount")
    void shouldRejectFundsAddedBelowMinimumFunds() {
        assertThrows(IllegalArgumentException.class, () -> user.addFunds(0.00));
    }

    @Test
    @DisplayName("Should reject funds added above the maximum amount")
    void shouldRejectFundsAddedAboveMaximumFunds() {
        assertThrows(IllegalArgumentException.class, () -> user.addFunds(1001.00));
    }

    // TODO: Challenge 2.3 - Add Decision Table tests for phone number validation
    // Hint: Test Swedish phone formats (+46701234567, 0701234567) and invalid formats

    @Test
    void shouldAllowValidSwedishPhoneNumberFormat() {
        assertEquals("0701234567",user.getPhoneNumber());
    }

    @Test
    void shouldAllowInternationalPhoneNumberFormat() {
        user.setPhoneNumber("+46701234567");
        assertEquals("+46701234567",user.getPhoneNumber());
    }
    @Test
    void shouldDenyInvalidPhoneNumber() {
        assertThrows(IllegalArgumentException.class,() ->
                user.setPhoneNumber("586499874489"));
    }

    // TODO: Challenge 2.4 - Add error scenario tests
    // Hint: Test insufficient balance, invalid inputs, state violations
}
