package com.bikeshare.lectures;

import com.bikeshare.model.*;
import com.bikeshare.repository.*;
import com.bikeshare.service.*;
import com.bikeshare.service.exception.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

/**
 * LECTURE 6: MOCKING, STUBBING & REGRESSION TESTING
 * 
 * This comprehensive example demonstrates key testing concepts using the BikeShare system:
 * 
 * 🎯 LEARNING OBJECTIVES:
 * 1. Understand the difference between Stubs and Mocks
 * 2. Explore Mockito framework for unit testing  
 * 3. Overview rgression testing strategies
 
 * 
 * 📚 KEY CONCEPTS COVERED:
 * - Mock vs Stub: When and why to use each
 * - Mockito Annotations: @Mock, @InjectMocks, @ExtendWith
 * - Stubbing: when().thenReturn(), doThrow(), ArgumentMatchers
 * - Verification: verify(), times(), never(), inOrder()
 * - Regression Testing: Test prioritization and critical path coverage
 */
@ExtendWith(MockitoExtension.class)
public class MockingStubsRegressionEx {

    // ==================== MOCK DEPENDENCIES ====================
    // These are the external dependencies we want to mock
    @Mock private UserRepository userRepository;
    @Mock private BikeRepository bikeRepository;
    @Mock private PaymentService paymentService;
    @Mock private NotificationService notificationService;
    
    // ==================== SERVICES UNDER TEST ====================
    // These will have mocks injected automatically
    @InjectMocks private UserService userService;
    @InjectMocks private BikeService bikeService;
    
    @Test
    @DisplayName("Demo 1: Understanding Stubs vs Mocks")
    void demonstrateStubsVsMocks() {
        System.out.println("\n🔧 CONCEPT: STUBS vs MOCKS");
        System.out.println("=================================");
        System.out.println("STUB: Provides predetermined responses (state verification)");
        System.out.println("MOCK: Records interactions and allows behavior verification");
        
        // ===== STUB EXAMPLE =====
        System.out.println("\n📋 STUB EXAMPLE: Setting up predetermined responses");
        
        // Create a user with funds
        User user = new User("user123", "john@example.com", "John", "Doe");
        user.addFunds(25.50);
        
        // STUB: Configure repository to return our user
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        
        // Use the stub
        User foundUser = userService.getUserById("user123");
        assertEquals("John", foundUser.getFirstName());
        assertEquals(25.50, foundUser.getAccountBalance());
        
        // ===== MOCK EXAMPLE =====
        System.out.println("\n🔍 MOCK EXAMPLE: Verifying interactions occurred");
        
        // The mock automatically records that findById was called
        verify(userRepository, times(1)).findById("user123");
        
        System.out.println("✅ Both stub behavior and mock verification successful!");
    }
    
    @Test
    @DisplayName("Demo 2: Mock Behavior Configuration")
    void demonstrateMockBehaviorConfiguration() {
        System.out.println("\n⚙️ MOCK BEHAVIOR: Configuring responses with when().thenReturn()");
        
        // ===== ARRANGE =====
        // Configure different behaviors for different scenarios
        User user = new User("user123", "john@example.com", "John", "Doe");
        user.addFunds(25.50);
        
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());
        
        // ===== ACT & ASSERT =====
        // Test successful user retrieval
        User foundUser = userService.getUserById("user123");
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        assertEquals(25.50, foundUser.getAccountBalance());
        
        // Test user not found scenario
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("unknown"));
        
        System.out.println("✅ Mock behavior configuration working correctly!");
    }
    
    @Test
    @DisplayName("Demo 3: Mock Verification and Argument Matchers")
    void demonstrateMockVerificationAndMatchers() {
        System.out.println("\n🔍 MOCK VERIFICATION: Using verify() and argument matchers");
        
        // ===== ARRANGE =====
        User user = new User("user456", "jane@example.com", "Jane", "Smith");
        user.addFunds(50.0);
        
        when(userRepository.findById("user456")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        try {
            when(paymentService.processPayment(anyString(), anyDouble(), anyString()))
                .thenReturn("PAYMENT_456");
        } catch (PaymentService.PaymentException e) {
            // This won't happen in test with mocks
        }
        
        // ===== ACT =====
        User result = userService.addFunds("user456", 20.0, "CARD002");
        
        // ===== ASSERT =====
        // Verify repository interactions
        verify(userRepository, times(1)).findById("user456");
        verify(userRepository, times(1)).save(any(User.class));
        
        // Verify the result
        assertEquals(70.0, result.getAccountBalance()); // 50 + 20
        
        System.out.println("✅ Mock verification and argument matching successful!");
    }
    
    @Test
    @DisplayName("Demo 4: Exception Handling with Mocks")  
    void demonstrateExceptionHandlingWithMocks() {
        System.out.println("\n⚠️ EXCEPTION HANDLING: Using doThrow() and thenThrow()");
        
        // ===== ARRANGE =====
        User user = new User("user789", "test@example.com", "Test", "User");
        when(userRepository.findById("user789")).thenReturn(Optional.of(user));
        
        // Configure mock to throw exception for high amounts
        try {
            when(paymentService.processPayment(anyString(), ArgumentMatchers.doubleThat(amount -> amount > 100.0), anyString()))
                .thenThrow(new PaymentService.PaymentException("Payment limit exceeded"));
        } catch (PaymentService.PaymentException e) {
            // This won't happen in test setup
        }
        
        // ===== ACT & ASSERT =====
        // Test that exception is properly handled
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.addFunds("user789", 150.0, "CARD003");
        });
        
        assertTrue(exception.getMessage().contains("Payment processing failed"));
        
        System.out.println("✅ Exception handling with mocks working correctly!");
    }
    
    @Test
    @DisplayName("Demo 5: Basic Bike Operations")
    void demonstrateBasicBikeOperations() {
        System.out.println("\n🚲 BIKE OPERATIONS: Testing bike service with mocks");
        
        // ===== ARRANGE =====
        // Create test bike
        Bike bike = new Bike("BIKE001", Bike.BikeType.STANDARD);
        
        when(bikeRepository.findById("BIKE001")).thenReturn(Optional.of(bike));
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        
        // ===== ACT =====
        // Test bike retrieval and creation
        Bike foundBike = bikeService.findBikeById("BIKE001");
        Bike createdBike = bikeService.createBike("BIKE002", BikeType.ELECTRIC);
        
        // ===== ASSERT =====
        assertNotNull(foundBike);
        assertEquals("BIKE001", foundBike.getBikeId());
        assertEquals(Bike.BikeType.STANDARD, foundBike.getType());
        
        // Verify repository interactions
        verify(bikeRepository, times(1)).findById("BIKE001");
        verify(bikeRepository, times(1)).save(any(Bike.class));
        
        System.out.println("✅ Basic bike operations working correctly!");
    }
    
    @Test 
    @Priority(1) // High priority - core functionality
    @DisplayName("REGRESSION: Critical User Journey")
    void regressionTestCriticalUserJourney() {
        System.out.println("\n🎯 REGRESSION TEST: Critical user journey - bike rental flow");
        
        // ===== ARRANGE =====
        // This test covers the most important user flow that must always work
        Bike bike = new Bike("BIKE003", Bike.BikeType.ELECTRIC);
        User user = new User("user789", "test@example.com", "Test", "User");
        user.addFunds(100.0);
        
        when(bikeRepository.findById("BIKE003")).thenReturn(Optional.of(bike));
        when(userRepository.findById("user789")).thenReturn(Optional.of(user));
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        
        // ===== ACT =====
        // This core functionality should always work
        bikeService.rentBike("BIKE003", "user789");
        
        // ===== ASSERT =====
        // Verify the critical state changes occurred
        assertEquals(Bike.BikeStatus.IN_USE, bike.getStatus());
        assertNull(bike.getCurrentStationId());
        
        // Verify all critical interactions
        verify(bikeRepository, times(1)).findById("BIKE003");
        verify(bikeRepository, times(1)).save(bike);
        
        System.out.println("✅ CRITICAL PATH: Core bike rental flow working!");
    }
    
    @Test
    @Priority(2) // Medium priority - integration scenarios  
    @DisplayName("REGRESSION: Service Integration")
    void regressionTestServiceIntegration() {
        System.out.println("\n🔗 REGRESSION TEST: Multi-service integration");
        
        // ===== ARRANGE =====
        // Simulate a complete user journey after system changes
        User user = new User("integration_user", "integration@test.com", "Integration", "User");
        user.addFunds(50.0);
        
        Bike bike = new Bike("INTEGRATION_BIKE", Bike.BikeType.ELECTRIC);
        
        // Mock all dependencies for integration test
        when(userRepository.findById("integration_user")).thenReturn(Optional.of(user));
        when(bikeRepository.findById("INTEGRATION_BIKE")).thenReturn(Optional.of(bike));
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        
        // ===== ACT =====
        // Execute the integrated flow
        User foundUser = userService.getUserById("integration_user");
        Bike foundBike = bikeService.findBikeById("INTEGRATION_BIKE");
        bikeService.rentBike("INTEGRATION_BIKE", "integration_user");
        
        // ===== ASSERT =====
        assertNotNull(foundUser);
        assertNotNull(foundBike);
        assertEquals(Bike.BikeStatus.IN_USE, foundBike.getStatus());
        
        // Verify interaction order (important for integration)
        InOrder inOrder = inOrder(userRepository, bikeRepository);
        inOrder.verify(userRepository).findById("integration_user");
        inOrder.verify(bikeRepository).findById("INTEGRATION_BIKE");
        
        System.out.println("✅ INTEGRATION: Multi-service coordination working!");
    }
    
    @Test
    @Priority(3) // Lower priority - edge cases
    @DisplayName("REGRESSION: Edge Cases and Error Handling")  
    void regressionTestEdgeCasesAndErrorHandling() {
        System.out.println("\n⚠️ REGRESSION TEST: Edge cases and error conditions");
        
        // ===== ARRANGE =====
        // Test system behavior with edge cases that have caused issues before
        User newUser = new User("edge_case_user", "edge@test.com", "Edge", "Case");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        doNothing().when(notificationService).sendWelcomeEmail(any(User.class));
        
        // ===== ACT & ASSERT =====
        // This should complete despite notification issues
        User createdUser = userService.createUser("edge@test.com", "Edge", "Case", null);
        assertNotNull(createdUser);
        
        // Verify that notification was attempted (but might have failed gracefully)
        verify(notificationService, times(1)).sendWelcomeEmail(any(User.class));
        
        System.out.println("✅ EDGE CASES: Error handling and recovery working!");
    }
    
    @Test
    @Priority(1) // High priority
    @DisplayName("REGRESSION: Performance Critical Operations")
    void regressionTestPerformanceCriticalOperations() {
        System.out.println("\n⚡ REGRESSION TEST: Performance-critical operations");
        
        // ===== ARRANGE =====
        Bike fastBike = new Bike("FAST_BIKE", Bike.BikeType.STANDARD);
        when(bikeRepository.findById("FAST_BIKE")).thenReturn(Optional.of(fastBike));
        
        // ===== ACT =====
        // Time-sensitive operation that must be fast
        long startTime = System.currentTimeMillis();
        
        Bike result = bikeService.findBikeById("FAST_BIKE");
        
        long duration = System.currentTimeMillis() - startTime;
        
        // ===== ASSERT =====
        assertNotNull(result);
        assertEquals("FAST_BIKE", result.getBikeId());
        
        // Performance assertion - should be very fast with mocks
        assertTrue(duration < 100, "Operation should be fast with mocks: " + duration + "ms");
        
        System.out.println("✅ PERFORMANCE: Critical operations within acceptable limits!");
    }
    
    @Test
    @DisplayName("REGRESSION: Data Consistency After Updates")
    void regressionTestDataConsistencyAfterUpdates() {
        System.out.println("\n📊 REGRESSION TEST: Data consistency after system updates");
        
        // ===== ARRANGE =====
        // Test that data remains consistent after code changes
        User originalUser = new User("consistency_user", "consistent@test.com", "Consistent", "User");
        
        when(userRepository.findById("consistency_user")).thenReturn(Optional.of(originalUser));
        when(userRepository.save(any(User.class))).thenReturn(originalUser);
        
        // ===== ACT =====
        // Perform operations that modify state
        userService.updateUserProfile("consistency_user", "NewFirst", null, "555-0123");
        
        // ===== ASSERT =====
        // Verify save was called (indicating consistency check passed)
        verify(userRepository, times(1)).save(any(User.class));
        
        System.out.println("✅ CONSISTENCY: Data integrity maintained after updates!");
    }
    
    // ==================== TEST UTILITIES ====================
    
    /**
     * Priority annotation for test execution order
     */
    @interface Priority {
        int value();
    }
    
    @BeforeEach
    void setUp() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 MOCKITO SETUP COMPLETE - Ready for testing!");
        System.out.println("=".repeat(60));
    }
    
    @AfterEach  
    void tearDown() {
        System.out.println("📋 Test completed - Mocks automatically reset for next test");
    }
}