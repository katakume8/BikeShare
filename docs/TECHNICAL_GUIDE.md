# BikeShare System - Technical Implementation Guide

## 1. Architecture Overview

The Bike```java
public enum BikeType {
    STANDARD("Standard Bike", 0.50),
    ELECTRIC("Electric Bike", 1.00);
    
    private final String description;
    private final double pricePerMinute;ystem follows a layered architecture pattern designed for maintainability, testability, and separation of concerns. This guide explains the architectural decisions and patterns used throughout the codebase.

### 1.1 Architecture Layers

```
┌─────────────────────────────────────┐
│         Presentation Layer          │  ← Web Controllers, REST APIs
├─────────────────────────────────────┤
│         Service Layer              │  ← Business Logic, Use Cases
├─────────────────────────────────────┤
│         Repository Layer           │  ← Data Access, Persistence
├─────────────────────────────────────┤
│         Domain Model Layer         │  ← Entities, Value Objects
└─────────────────────────────────────┘
```

**Benefits of this approach:**
- **Separation of Concerns**: Each layer has a specific responsibility
- **Testability**: Layers can be tested in isolation using mocks
- **Maintainability**: Changes in one layer don't affect others
- **Flexibility**: Easy to swap implementations (e.g., database providers)

### 1.2 Package Structure

```
com.bikeshare/
├── model/              # Domain entities and value objects
│   ├── Bike.java
│   ├── User.java
│   ├── Station.java
│   ├── Ride.java
│   ├── BikeType.java
│   └── MembershipType.java
├── repository/         # Data access interfaces
│   ├── BikeRepository.java
│   ├── UserRepository.java
│   ├── StationRepository.java
│   └── RideRepository.java
├── service/           # Business logic layer
│   ├── BikeService.java
│   ├── UserService.java
│   ├── StationService.java
│   ├── RideService.java
│   ├── NotificationService.java
│   ├── PaymentService.java
│   └── exception/     # Custom exceptions
└── web/              # Embedded Jetty server + Servlets (SimpleBikeShareWebServer)
```

## 2. Domain Model Patterns

### 2.1 Entity Pattern

Entities represent core business objects with identity and lifecycle management.

**Example: Bike Entity**
```java
public class Bike {
    private String id;           // Identity
    private BikeType bikeType;   // Value Object
    private BikeStatus status;   // Enum
    private String currentStationId; // Reference to Station
    
    // Business methods that maintain invariants
    public void rent(String userId) {
        if (!isAvailable()) {
            throw new BikeNotAvailableException("Bike is not available");
        }
        this.status = BikeStatus.RENTED;
        this.currentUserId = userId;
        this.rentedAt = LocalDateTime.now();
    }
}
```

**Key Characteristics:**
- Has unique identity (ID)
- Maintains business invariants
- Encapsulates state changes through methods
- Throws domain exceptions for invalid operations

### 2.2 Value Object Pattern

Value objects represent immutable concepts without identity.

**Example: BikeType Enum**
```java
public enum BikeType {
    STANDARD("Standard Bike", 0.05),
    ELECTRIC("Electric Bike", 0.10);
    
    private final String displayName;
    private final double pricePerMinute;
    
    // Immutable, equality based on values
    // No setters, only getters
}
```

### 2.3 Aggregate Pattern

Aggregates group related entities and enforce consistency boundaries.

**Example: Station Aggregate**
```java
public class Station {
    private String id;
    private List<Bike> bikes;  // Aggregate children
    private int capacity;
    
    // Ensures business rules across the aggregate
    public void addBike(Bike bike) {
        if (bikes.size() >= capacity) {
            throw new StationFullException("Station at capacity");
        }
        bikes.add(bike);
        bike.setCurrentStationId(this.id);
    }
}
```

## 3. Service Layer Patterns

### 3.1 Service Pattern

Services contain business logic that doesn't naturally fit in entities.

**Example: RideService**
```java
@Service
public class RideService {
    private final RideRepository rideRepository;
    private final BikeService bikeService;
    private final UserService userService;
    
    // Constructor injection for dependency inversion
    public RideService(RideRepository rideRepository, 
                      BikeService bikeService, 
                      UserService userService) {
        this.rideRepository = rideRepository;
        this.bikeService = bikeService;
        this.userService = userService;
    }
    
    // Business use case implementation
    public Ride startRide(String userId, String bikeId, String stationId) {
        // Orchestrates multiple domain objects
        User user = userService.findUserById(userId);
        Bike bike = bikeService.findBikeById(bikeId);
        
        // Business rule validation
        validateRideStart(user, bike);
        
        // State changes across multiple aggregates
        bike.rent(userId);
        Ride ride = new Ride(userId, bikeId, stationId);
        
        return rideRepository.save(ride);
    }
}
```

### 3.2 Dependency Injection Pattern

Dependencies are injected rather than created internally.

**Benefits:**
- **Testability**: Easy to inject mocks for testing
- **Flexibility**: Can swap implementations
- **Loose Coupling**: Services don't depend on concrete implementations

### 3.3 Exception Handling Pattern

Custom exceptions provide meaningful error information.

```java
// Base service exception
public abstract class BikeShareException extends RuntimeException {
    protected BikeShareException(String message) { super(message); }
}

// Specific business exceptions
public class BikeNotAvailableException extends BikeShareException {
    public BikeNotAvailableException(String message) { super(message); }
}
```

## 4. Repository Layer Patterns

### 4.1 Repository Pattern

Repositories provide a collection-like interface for data access.

```java
public interface BikeRepository {
    Bike save(Bike bike);
    Optional<Bike> findById(String id);
    List<Bike> findAll();
    List<Bike> findAvailableBikes();
    boolean deleteById(String id);
}
```

**Benefits:**
- **Abstraction**: Hides data access complexity
- **Testability**: Can be easily mocked
- **Flexibility**: Can switch between different storage mechanisms

### 4.2 Specification Pattern (Advanced)

For complex queries, specifications can be used:

```java
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);
}

public class AvailableBikeSpecification implements Specification<Bike> {
    public boolean isSatisfiedBy(Bike bike) {
        return bike.isAvailable() && !bike.needsMaintenance();
    }
}
```

## 5. Testing Patterns

### 5.1 Test Doubles

Different types of test doubles serve different purposes:

**Dummy Objects**: Passed but never used
```java
User dummyUser = new User("dummy", "dummy@example.com", "0000000000");
```

**Fake Objects**: Working implementation with shortcuts
```java
public class InMemoryUserRepository implements UserRepository {
    private Map<String, User> users = new HashMap<>();
    
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
```

**Stubs**: Provide predefined responses
```java
@Mock
private UserRepository userRepository;

@Test
void testUserService() {
    // Stub setup
    when(userRepository.findById("123"))
        .thenReturn(Optional.of(testUser));
    
    // Test execution
    User result = userService.findUserById("123");
}
```

**Mocks**: Verify interactions
```java
@Test
void testRideCreation() {
    rideService.startRide("user1", "bike1", "station1");
    
    // Verify service interactions
    verify(bikeService).rentBike("bike1", "user1");
    verify(rideRepository).save(any(Ride.class));
}
```

### 5.2 Swedish Personnummer Validation

The system validates Swedish personal identity numbers (personnummer) using the Luhn algorithm:

**Format**: YYMMDD-NNNN (e.g., 800101-8129)

**Validation Algorithm**:
```java
private static boolean isValidLuhnChecksum(String digits) {
    int sum = 0;
    for (int i = 0; i < 9; i++) {
        int digit = Character.getNumericValue(digits.charAt(i));
        // Multiply odd positions by 2, even positions by 1
        int multiplier = (i % 2 == 0) ? 2 : 1;
        int product = digit * multiplier;
        
        // If product > 9, sum the digits (e.g., 16 -> 1+6=7)
        if (product > 9) {
            product = (product / 10) + (product % 10);
        }
        sum += product;
    }
    
    // Calculate checksum: (10 - (sum mod 10)) mod 10
    int expectedChecksum = (10 - (sum % 10)) % 10;
    int actualChecksum = Character.getNumericValue(digits.charAt(9));
    
    return expectedChecksum == actualChecksum;
}
```

**Example Validation for 800101-8129**:
- 8×2=16 (1+6=7), 0×1=0, 0×2=0, 1×1=1, 0×2=0, 1×1=1, 8×2=16 (1+6=7), 1×1=1, 2×2=4
- Sum: 7+0+0+1+0+1+7+1+4=21
- Checksum: (10-(21 mod 10)) mod 10 = (10-1) mod 10 = 9
- Valid: checksum (9) matches last digit (9)

**Testing Scenarios**:
```java
@Test
void shouldValidateCorrectPersonnummer() {
    assertDoesNotThrow(() -> new User("800101-8129", "test@example.com", "Test", "User"));
}

@Test
void shouldRejectInvalidChecksum() {
    assertThrows(IllegalArgumentException.class, () -> 
        new User("800101-8128", "test@example.com", "Test", "User"));
}
```

### 5.3 Test Organization Patterns

**Arrange-Act-Assert (AAA) Pattern**
```java
@Test
void shouldCalculateCorrectRideCost() {
    // Arrange
    Bike electricBike = new Bike("E001", BikeType.ELECTRIC);
    User premiumUser = new User("john", "john@example.com", "1234567890");
    premiumUser.setMembershipType(MembershipType.PREMIUM);
    Ride ride = new Ride("R001", "john", "E001", "S001");
    ride.endRide("S002", LocalDateTime.now().plusMinutes(30));
    
    // Act
    double cost = rideService.calculateRideCost(ride);
    
    // Assert
    assertEquals(25.5, cost, 0.01); // 30 minutes * 1.00 * 0.85 (15% discount)
}
```

**Given-When-Then (BDD) Pattern**
```java
@Test
void shouldRejectRideWhenUserHasInsufficientBalance() {
    // Given
    User userWithLowBalance = createUserWithBalance(1.00);
    Bike expensiveBike = createBike(BikeType.ELECTRIC);
    
    // When & Then
    assertThrows(InsufficientBalanceException.class, () -> {
        rideService.startRide(userWithLowBalance.getId(), 
                             expensiveBike.getId(), 
                             "station1");
    });
}
```

## 6. Design Patterns Used

### 6.1 Factory Pattern

Used for creating complex objects with validation:

```java
public class BikeFactory {
    public static Bike createBike(String id, BikeType type) {
        validateBikeId(id);
        validateBikeType(type);
        
        Bike bike = new Bike(id, type);
        bike.setStatus(BikeStatus.AVAILABLE);
        bike.setCreatedAt(LocalDateTime.now());
        
        return bike;
    }
}
```

### 6.2 Strategy Pattern

For different pricing strategies:

```java
public interface PricingStrategy {
    double calculatePrice(Ride ride);
}

public class StandardPricingStrategy implements PricingStrategy {
    public double calculatePrice(Ride ride) {
        return ride.getDurationMinutes() * ride.getBike().getType().getRate();
    }
}

public class PeakHourPricingStrategy implements PricingStrategy {
    public double calculatePrice(Ride ride) {
        double basePrice = ride.getDurationMinutes() * ride.getBike().getType().getRate();
        return basePrice * 1.25; // 25% surcharge
    }
}
```

### 6.3 Observer Pattern

For notifications:

```java
public interface RideEventListener {
    void onRideStarted(RideEvent event);
    void onRideCompleted(RideEvent event);
}

public class NotificationService implements RideEventListener {
    public void onRideStarted(RideEvent event) {
        sendNotification(event.getUserId(), "Ride started");
    }
}
```

### 6.4 Builder Pattern

For complex test data creation:

```java
public class UserTestDataBuilder {
    private String name = "John Doe";
    private String email = "john@example.com";
    private String phone = "1234567890";
    private double balance = 100.0;
    private MembershipType membership = MembershipType.BASIC;
    
    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserTestDataBuilder withBalance(double balance) {
        this.balance = balance;
        return this;
    }
    
    public User build() {
        User user = new User(name, email, phone);
        user.addFunds(balance);
        user.setMembershipType(membership);
        return user;
    }
}

// Usage in tests
User testUser = new UserTestDataBuilder()
    .withName("Alice")
    .withBalance(50.0)
    .build();
```

## 7. Testing Strategy by Lab

### 7.1 Lab 1: Fundamentals of QA and Test
Focus on basic unit testing patterns:
- Simple getter/setter testing
- Constructor validation
- Basic business rule testing
- Exception testing

### 7.2 Lab 2: Specification Based Testing
Focus on black-box testing techniques:
- Boundary value analysis
- Equivalence partitioning
- Decision table testing
- Parameterized tests

### 7.3 Lab 3: Structural Testing & Coverage
Focus on white-box testing:
- Statement coverage
- Branch coverage
- Path coverage
- Cyclomatic complexity

### 7.4 Lab 4: Regression and Advanced Test
Focus on advanced testing techniques:
- Mutation testing
- Integration testing
- Test maintenance
- CI/CD integration

### 7.5 Lab 5: Sustainability and Society
Focus on performance and sustainability:
- Performance testing
- Resource usage monitoring
- Memory leak detection
- Energy consumption analysis

### 7.6 Lab 6: Testing AI and AI for Testing
Focus on AI-related testing:
- Property-based testing
- AI-assisted test generation
- Model testing
- Test data generation

## 8. Common Anti-Patterns to Avoid

### 8.1 Testing Anti-Patterns

**❌ Test Interdependence**
```java
// Bad: Tests depend on execution order
@Test
void createUser() { /* creates user with ID "123" */ }

@Test
void updateUser() { /* assumes user "123" exists */ }
```

**✅ Independent Tests**
```java
@Test
void shouldUpdateUser() {
    // Arrange: Create test data
    User user = createTestUser();
    userRepository.save(user);
    
    // Act & Assert
    userService.updateUser(user.getId(), "New Name");
    assertEquals("New Name", user.getName());
}
```

**❌ Excessive Mocking**
```java
// Bad: Mocking value objects
@Mock
private BikeType mockBikeType;
when(mockBikeType.getPricePerMinute()).thenReturn(0.50);
```

**✅ Use Real Objects When Simple**
```java
// Good: Use real value objects
BikeType standardType = BikeType.STANDARD;
assertEquals(0.50, standardType.getPricePerMinute());
```

### 8.2 Design Anti-Patterns

**❌ Anemic Domain Model**
```java
// Bad: All business logic in services
public class Bike {
    private String id;
    private BikeStatus status;
    // Only getters/setters, no behavior
}

public class BikeService {
    public void rentBike(Bike bike, String userId) {
        if (bike.getStatus() == BikeStatus.AVAILABLE) {
            bike.setStatus(BikeStatus.RENTED);
            bike.setUserId(userId);
        }
    }
}
```

**✅ Rich Domain Model**
```java
// Good: Business logic in domain objects
public class Bike {
    public void rent(String userId) {
        if (!isAvailable()) {
            throw new BikeNotAvailableException("Bike not available");
        }
        this.status = BikeStatus.RENTED;
        this.currentUserId = userId;
        this.rentedAt = LocalDateTime.now();
    }
}
```

## 9. Development Environment Setup

### 9.1 Required Tools
- **Java 21+**: Runtime environment (LTS version)
- **Maven 3.8+**: Build tool and dependency management
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git**: Version control

**Note**: Java 21 is required as it's the current LTS version and provides modern language features that enhance the learning experience.

### 9.2 Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BikeServiceTest

# Run with coverage
mvn test jacoco:report

# Run mutation testing
mvn test pitest:mutationCoverage
```

### 9.3 Project Build Commands
```bash
# Clean and compile
mvn clean compile

# Run all quality checks
mvn clean verify

# Generate all reports
mvn clean verify site
```

### 9.4 Web Interface (Embedded Jetty)

- Entry point: `com.bikeshare.web.SimpleBikeShareWebServer`
- Start via Maven (multiple options):

    **Option 1: Using exec plugin with default configuration**
    ```bash
    mvn exec:java
    ```

    **Option 2: Specifying main class explicitly**
    ```bash
    mvn exec:java -Dexec.mainClass=com.bikeshare.web.SimpleBikeShareWebServer
    ```

    **Option 3: With custom port (Unix/Linux/MacOS)**
    ```bash
    mvn exec:java -Dexec.mainClass=com.bikeshare.web.SimpleBikeShareWebServer -Dexec.args="8081"
    ```

    **Option 4: With custom port (Windows PowerShell)**
    ```powershell
    mvn exec:java "-Dexec.mainClass=com.bikeshare.web.SimpleBikeShareWebServer" "-Dexec.args=8081"
    ```

- The server logs the exact URL it bound to after start (it resolves the real port from Jetty), so follow that URL rather than assuming the default.

#### Swappable demo implementations (for teaching)

- The validation endpoint `/api/validate/age` uses an `AgeCheck` abstraction via `AgeCheckFactory`.
- Enable an intentionally buggy implementation for exercises with `-Ddemo.buggy=true` or env `DEMO_BUGGY=true`.
- Default is `CorrectAgeCheck` delegating to `AgeValidator` with proper ID/auth checks.

This technical guide provides the foundation for understanding the BikeShare system architecture and testing patterns. Students should refer to this document when implementing test cases and understanding the codebase structure.

---

**Document Version**: 1.0  
**Last Updated**: August 23, 2025  
**Target Audience**: Students and Teaching Assistants
