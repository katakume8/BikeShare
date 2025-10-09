package com.bikeshare.lab4;

import com.bikeshare.service.validation.AgeValidator;
import com.bikeshare.service.validation.IDNumberValidator;
import com.bikeshare.service.auth.BankIDService;
import com.bikeshare.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Lab 4: Mutation Testing Improvement Template
 * 
 * GOAL: Increase mutation score by writing focused tests using mocks
 * 
 * STRATEGY:
 * 1. Run mutation testing: mvn clean test org.pitest:pitest-maven:mutationCoverage -Pcoverage-comparison
 * 2. Open target/pit-reports/index.html and analyze survived mutations (red lines)
 * 3. Write targeted tests using mocks to kill specific mutations
 * 4. Focus on boundary conditions, error scenarios, and logic verification
 * 5. Re-run mutation testing to measure improvement
 * 
 * HINTS:
 * - Look for mutations in AgeValidator.java (lines 43-44 are known survivors)
 * - Use mocks to create precise test scenarios
 * - Test both positive and negative cases
 * - Verify that tests fail when mutations are present
 * 
 * CURRENT BASELINE (from CoverageEx.java):
 * - AgeValidator: 94% line coverage, 75% mutation coverage
 * - 2 mutations survived in birthday logic
 * - Your job: Kill those mutations!
 * - Or Kill other mutations you find interesting
 * 
 * IF NOT POSSIBLE TO KILL THE MUTANTS? DEAD CODE? WHAT NOW?
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Lab 4: Mutation Testing Improvement")
public class MutationImprovementTest {

    @Mock
    private IDNumberValidator mockIdValidator;
    
    @Mock
    private BankIDService mockBankIdService;
    
    @InjectMocks
    private AgeValidator ageValidator;
    
    @BeforeEach
    void setUp() {
        // MockitoExtensions handles mock initialization
        // Add any common setup here if needed
    }
    
    @Test
    @DisplayName("Should kill boundary mutation: exactly 18 years old")
    void shouldKillBoundaryMutation_Exactly18() {
        String exactly18ID = "200710062384"; // Figure out the right format
        when(mockIdValidator.isValidIDNumber(exactly18ID)).thenReturn(true);
        when(mockBankIdService.authenticate(exactly18ID)).thenReturn(true);
        
        boolean result = ageValidator.isAdult(exactly18ID);
        
        assertTrue(result, "Person exactly 18 should be adult");
        verify(mockIdValidator).isValidIDNumber(exactly18ID);
        verify(mockBankIdService).authenticate(exactly18ID);
    }
    
    @Test
    @DisplayName("Should kill conditional mutation: invalid ID handling")
    void shouldKillConditionalMutation_InvalidId() {
        String invalidId = "invalid123";
        when(mockIdValidator.isValidIDNumber(invalidId)).thenReturn(false);
        
        IllegalArgumentException exception = assertThrows(
           IllegalArgumentException.class,
           () -> ageValidator.isAdult(invalidId)
        );
        
        assertEquals("Invalid ID number", exception.getMessage());
        verify(mockIdValidator).isValidIDNumber(invalidId);
        verifyNoInteractions(mockBankIdService); // Important: BankID not called
    }
    
    @Test
    @DisplayName("Should kill authentication mutation: auth failure handling")
    void shouldKillAuthenticationMutation_AuthFailure() {
        String validId = "200101010000";
        when(mockIdValidator.isValidIDNumber(validId)).thenReturn(true);
        when(mockBankIdService.authenticate(validId)).thenReturn(false);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ageValidator.isAdult(validId)
        );
        
        assertEquals("Authentication failed", exception.getMessage());
        verify(mockIdValidator).isValidIDNumber(validId);
        verify(mockBankIdService).authenticate(validId);
    }
    
    // These are related to birthday adjustment logic
    @Test
    @DisplayName("Should kill birthday logic mutation: person before birthday")
    void shouldKillBirthdayMutation_PersonBeforeBirthday() {
        String preBirthdayId = "200712312384"; // Figure out the right format
        when(mockIdValidator.isValidIDNumber(preBirthdayId)).thenReturn(true);
        when(mockBankIdService.authenticate(preBirthdayId)).thenReturn(true);

        boolean result = ageValidator.isAdult(preBirthdayId);

        assertFalse(result, "Person is not 18 years yet");
        verify(mockIdValidator).isValidIDNumber(preBirthdayId);
        verify(mockBankIdService).authenticate(preBirthdayId);
    }




    @Test
    @DisplayName("Should test User class constructor with valid id number")
    void shouldTestConstructorWithValidID() {
        String validId = "850709-9805";
        User user = new User(validId, "test@example.com", "John", "Doe");
        assertEquals(validId, user.getUserId());
    }

    @Test
    @DisplayName("Should kill person number validation mutation: Invalid number")
    void shouldTestConstructorThrowingForInvalidPersonNumber() {
        String invalidId = "111111111111111111111111111";
        assertThrows(IllegalArgumentException.class,
                () -> new User(invalidId, "test@example.com", "John", "Doe")
        );
    }

    @Test
    @DisplayName("Should kill person number validation mutation: null number ")
    void shouldTestConstructorThrowingForNullPersonNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(null, "test@example.com", "John", "Doe")
        );
    }

    @Test
    @DisplayName("Should kill person number validation mutation: empty number ")
    void shouldTestConstructorThrowingForEmptyPersonNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("", "test@example.com", "John", "Doe")
        );
    }

    @Test
    @DisplayName("Should kill person email validation mutation")
    void shouldTestConstructorEmailMutations() {
        String invalidEmail = "84959.com";
        assertThrows(IllegalArgumentException.class,
                () -> new User("850709-9805", invalidEmail, "olle", "persson"),
                "Expected invalid email to throw exception"
        );
    }

    @Test
    @DisplayName("Should kill first name mutation: Null first name")
    void shouldTestConstructorFirstNameNullMutation() {
        assertThrows(IllegalArgumentException.class,
                ()-> new User("850709-9805", "test@test.com", null, "persson"),
                "Expected null name to throw exception"
        );
    }

    @Test
    @DisplayName("Should kill first name mutation: Empty first name")
    void shouldTestConstructorFirstNameEmptyMutation() {
        assertThrows(IllegalArgumentException.class,
                ()-> new User("850709-9805", "test@test.com", "", "persson"),
                "Expected null name to throw exception"
        );
    }

    @Test
    @DisplayName("Should kill last name mutation: Null last name")
    void shouldTestConstructorLastNameNullMutation() {
        assertThrows(IllegalArgumentException.class,
                ()-> new User("850709-9805", "test@test.com", "Olle", null),
                "Expected null name to throw exception"
        );
    }

    @Test
    @DisplayName("Should kill last name mutation: Empty last name")
    void shouldTestConstructorLastNameEmptyMutation() {
        assertThrows(IllegalArgumentException.class,
                ()-> new User("850709-9805", "test@test.com", "Olle", ""),
                "Expected null name to throw exception"
        );
    }

    @Test
    void shouldNotActivateIfStatusIsNotPendingVerification() {
        String userId = "940406-1344";
        User user = new User(userId, "test@test.com", "Foo", "Bar");
        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        user.deactivate();
        assertThrows(IllegalStateException.class, user::activate);
    }

    @Test
    void shouldNotActivateIfEmailIsNotVerified() {
        String userId = "940406-1344";
        User user = new User(userId, "test@test.com", "Foo", "Bar");
        assertThrows(IllegalStateException.class, user::activate);
    }

    @Test
    @DisplayName("Should test User class suspend method working correctly")
    void shouldTestUserSuspendMethodWorkingCorrectly() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.suspend("Banned");
        assertEquals(User.UserStatus.SUSPENDED, testUser.getStatus());
        assertEquals(1, testUser.getSuspensionCount());
    }

    @Test
    @DisplayName("Should kill status mutation: User already suspended")
    void shouldTestSuspendMethodWhileUserIsSuspended() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.suspend("Banned");
        assertThrows(IllegalStateException.class, () -> testUser.suspend("Banned"));
    }

    @Test
    @DisplayName("Should kill status mutation: User inactive")
    void shouldTestSuspendMethodWhileUserIsInactive() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.deactivate();
        assertThrows(IllegalStateException.class, () -> testUser.suspend("Banned"));
    }

    @Test
    @DisplayName("Should kill currentRideId mutation")
    void shouldTestSuspendMethodWhileUserHasNoCurrentRideId() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.verifyEmail();
        testUser.activate();
        testUser.addFunds(100);
        testUser.startRide("1");

        assertThrows(IllegalStateException.class, () -> testUser.suspend("Banned"));
    }

    @Test
    @DisplayName("Should test user class reactive method working correctly")
    void shouldTestUserClassReactiveMethodWorkingCorrectly() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.suspend("Banned");
        testUser.reactivate();
        assertEquals(User.UserStatus.ACTIVE, testUser.getStatus());
    }

    @Test
    @DisplayName("Should kill userStatus not being suspended mutation")
    void shouldThrowWhenReactivatingMethodWhileUserIsSuspended() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        assertThrows(IllegalStateException.class, testUser::reactivate);
    }

    @Test
    void shouldNotDeactivateIfTheUserHasAnActiveRide() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");
        user.verifyEmail();
        user.activate();
        user.addFunds(1000);
        user.startRide("R001");

        assertThrows(IllegalStateException.class, user::deactivate);
    }

    @Test
    @DisplayName("Should kill addFounds <= zero mutation: Amount below zero")
    void shouldThrowWhenAddingFoundsLessThanZero() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        assertThrows(IllegalArgumentException.class,()-> testUser.addFunds(-100));
    }

    @Test
    @DisplayName("Should kill addFounds <= zero mutation: Boundary value")
    void shouldThrowWhenAddingFoundsJustBelowZero() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        assertThrows(IllegalArgumentException.class,()-> testUser.addFunds(0));
    }

    @Test
    @DisplayName("Should kill addFounds > 1000 mutation: Amount over 1000")
    void shouldThrowWhenAddingFoundsMoreThanThousand() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        assertThrows(IllegalArgumentException.class,()-> testUser.addFunds(1001));
    }

    @Test
    @DisplayName("Should kill addFounds 1000 mutation: Amount at 1000")
    void shouldNotThrowWhenAddingThousand() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.addFunds(1000);
        assertEquals(1000, testUser.getAccountBalance());
    }

    @Test
    @DisplayName("Should test user class deductFunds method working correctly")
    void shouldTestUserClassDeductFoundsMethodWorkingCorrectly() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.addFunds(200);
        testUser.deductFunds(100);
        assertEquals(100, testUser.getAccountBalance());
        assertEquals(100,testUser.getTotalSpent());
    }

    @Test
    @DisplayName("Should kill deductFunds with invalid parameter mutation: Amount under zero should throw")
    void shouldThrowWhenDeductFundsUnderZero() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.addFunds(100);
        assertThrows(IllegalArgumentException.class,()-> testUser.deductFunds(-10));
    }

    @Test
    @DisplayName("Should kill deductFunds with invalid parameter mutation: Amount at zero should not throw")
    void shouldNotThrowWhenDeductFundsWithZero() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.addFunds(100);
        testUser.deductFunds(0);
        assertEquals(100, testUser.getAccountBalance());
    }

    @Test
    @DisplayName("Should kill deductFunds account balance below amount mutation: " +
            "deducting founds more then the balance should throw")
    void shouldThrowWhenDeductFundsOverTotalBalance() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.addFunds(100);
        testUser.deductFunds(100);
        assertThrows(IllegalStateException.class,()-> testUser.deductFunds(200));
    }

    @Test
    @DisplayName("Should kill deductFunds account balance below amount mutation: " +
            "deducting founds more then the balance should throw")
    void shouldThrowWhenDeductFundsOverTotalBalance1() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.addFunds(100);
        testUser.deductFunds(100);
        assertEquals(0, testUser.getAccountBalance());;
    }

    @Test
    void shouldThrowExceptionIfMembershipTypeIsNull() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");

        assertThrows(IllegalArgumentException.class, () -> user.updateMembership(null));
    }

    @Test
    void shouldThrowExceptionUpdatingMembershipIfStatusIsNotActive() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");

        assertThrows(IllegalStateException.class, () -> user.updateMembership(User.MembershipType.PREMIUM));
    }

    @Test
    void shouldThrowIfPhoneNumberHasInvalidPattern() {
        String userId = "940406-1344";
        User user = new User(userId, "test@test.com", "Foo", "Bar");
        assertThrows(IllegalArgumentException.class, () -> user.setPhoneNumber("123456"));
    }

    @Test
    void shouldVerifyingEmailShouldRemoveStatusPendingVerification() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");
        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        assertNotEquals(User.UserStatus.PENDING_VERIFICATION, user.getStatus());
    }

    @Test
    void shouldThrowExceptionIfPhoneNumberNotSet() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");

        assertThrows(IllegalStateException.class, user::verifyPhone);
    }

    @Test
    void shouldThrowExceptionStartingRideIfStatusIsNotActive() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");

        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        user.addFunds(100);
        user.deactivate();

        assertThrows(IllegalStateException.class, () -> user.startRide("R001"));
    }

    @Test
    void shouldThrowExceptionStartingRideWhenRideIsAlreadyActive() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");

        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        user.addFunds(100);
        user.startRide("TEST RIDE");

        assertThrows(IllegalStateException.class, () -> user.startRide(null));
    }

    @Test
    void shouldThrowExceptionStartingRideWithTooLowAccountBalance() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");
        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        user.addFunds(4.0);

        assertThrows(IllegalStateException.class, () -> user.startRide("R001"));
    }

    @Test
    void shouldStartRideIfBalanceIsOverBoundary() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");
        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        user.addFunds(5);

        assertDoesNotThrow(() -> user.startRide("R001"));
    }

    @Test
    void shouldThrowExceptionEndingARideNotCurrentlyRiding() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");
        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();

        assertThrows(IllegalStateException.class, user::endRide);
    }

    @Test
    void shouldIncreaseTheNumberOfRidesWhenEndingRide() {
        User user = new User("940406-1344", "test@test.com", "Foo", "Bar");
        user.setPhoneNumber("070-3512485");
        user.verifyPhone();
        user.verifyEmail();
        user.addFunds(100);
        user.startRide("R001");
        user.endRide();

        assertEquals(1, user.getTotalRides());
    }

    @Test
    @DisplayName("Should test User class calculateDiscount working correctly")
    void shouldTestUserClassCalculateDiscount() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.verifyEmail();
        testUser.activate();

        testUser.updateMembership(User.MembershipType.PREMIUM);
        assertEquals(0.15,testUser.calculateDiscount());
        testUser.updateMembership(User.MembershipType.CORPORATE);
        assertEquals(0.10,testUser.calculateDiscount());
        testUser.updateMembership(User.MembershipType.STUDENT);
        assertEquals(0.20,testUser.calculateDiscount());
    }

    @Test
    @DisplayName("Should kill loyalty discount mutation: rides over 100")
    void shouldUpdateLoyaltyDiscountOverHundredRides() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.verifyEmail();
        testUser.activate();
        testUser.addFunds(100);

        for(int i = 0; i<100;i++){
            testUser.startRide(""+i);
            testUser.endRide();
        }
        assertEquals(0.03,testUser.calculateDiscount());
        testUser.startRide("last");
        testUser.endRide();
        assertEquals(0.05,testUser.calculateDiscount());
    }

    @Test
    @DisplayName("Should kill loyalty discount mutation: rides under 50")
    void shouldNotUpdateLoyaltyDiscountUnder50Rides() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");
        testUser.verifyEmail();
        testUser.activate();
        testUser.addFunds(100);
        for(int i = 0; i<50;i++){
            testUser.startRide(""+i);
            testUser.endRide();
        }
        assertEquals(0.0,testUser.calculateDiscount());
    }

    @Test
    @DisplayName("Should test User class isValidPersonNumber working correctly")
    void shouldTestUserClassIsValidPersonNumber() {
        User testUser = new User("850709-9805", "test@test.com", "Olle", "Persson");

    }

    @Test
    void shouldRetrieveCorrectUserParameters() {
        String userId = "940406-1344";
        String email = "test@test.com";
        String firstName = "Foo";
        String lastName = "Bar";
        String phoneNumber = "0703512485";

        User user = new User(userId, email, firstName, lastName);
        user.setPhoneNumber(phoneNumber);
        user.verifyPhone();
        user.verifyEmail();

        assertAll(
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(email, user.getEmail()),
                () -> assertEquals(phoneNumber, user.getPhoneNumber()),
                () -> assertEquals(firstName, user.getFirstName()),
                () -> assertEquals(lastName, user.getLastName()),
                () -> assertEquals(User.UserStatus.ACTIVE, user.getStatus()),
                () -> assertEquals(0.00, user.getAccountBalance()),
                () -> assertEquals(0, user.getTotalRides()),
                () -> assertEquals(0, user.getTotalSpent()),
                () -> assertTrue(user.isEmailVerified()),
                () -> assertTrue(user.isPhoneVerified()),
                () -> assertEquals(0, user.getSuspensionCount())
        );
    }
}
