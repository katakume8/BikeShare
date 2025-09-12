# Beginner's Guide to BikeShare Testing Project 🚲👨‍🎓

## 🎯 What is This Project?

This project teaches you **software testing** using a **bike sharing system** (like Lime or Bird scooters). You'll learn to write tests that check if the code works correctly.

### 🌟 Perfect for Beginners!

- **No prior testing experience needed**
- **Step-by-step guidance**
- **Clear examples with explanations**
- **Progressive difficulty (Easy → Hard)**

## 🚀 Quick Start for Absolute Beginners

### Step 1: Setup (5 minutes)
```bash
# 1. Make sure you have Java 21 and Maven installed
java -version    # Should show Java 21
mvn -version     # Should show Maven 3.8+

# 2. Clone and setup project
git clone <repository-url>
cd bikeshare-testing-project
mvn clean compile
```

### Step 2: Run Your First Test (2 minutes)
```bash
mvn test 
```

If you see "BUILD SUCCESS" - congratulations! 🎉

### Step 3: Open the Code
Open the project in your favorite editor:
- **VS Code**: `code .`
- **IntelliJ IDEA**: Import as Maven project
- **Eclipse**: Import existing Maven project

## 📚 Learning Path for Beginners

### 🏁 Lab 1: Baby Steps (Week 1-2)
**Files to start with:**
- `src/test/java/com/bikeshare/lab1/BikeBasicTest.java`
- `src/main/java/com/bikeshare/model/BikeType.java`

**What you'll learn:**
- What is a test?
- How to write simple assertions
- Basic JUnit annotations

**Example - Your first test:**
```java
@Test
void myFirstTest() {
    // Arrange: Set up what you need
    BikeType bikeType = BikeType.STANDARD;
    
    // Act: Do something
    String name = bikeType.getDisplayName();
    
    // Assert: Check if it's correct
    assertEquals("Standard Bike", name);
}
```

### 🚴‍♂️ Lab 2: Getting Confident (Week 3-4)
**Files to explore:**
- `src/test/java/com/bikeshare/lab2/UserBlackBoxTest.java`
- `src/main/java/com/bikeshare/model/User.java`

**What you'll learn:**
- Testing with different inputs
- Edge cases and boundary testing
- Email validation, phone validation

### 🏃‍♂️ Lab 3-7: Advanced Topics (Week 5+)
- Code coverage
- Mocking and advanced testing
- Performance testing

## 🎓 Key Concepts Explained Simply

### What is a "Test"?
A test is code that checks if other code works correctly.

```java
// This tests if our bike creation works
@Test
void shouldCreateBike() {
    Bike bike = new Bike("BIKE123", BikeType.STANDARD);
    assertEquals("BIKE123", bike.getId());
}
```

### What is an "Assertion"?
An assertion checks if something is true. If not, the test fails.

```java
assertEquals(expected, actual);    // Checks if two values are equal
assertTrue(condition);             // Checks if something is true
assertNotNull(object);            // Checks if something is not null
```

### Common Testing Patterns

**1. Arrange-Act-Assert (AAA)**
```java
@Test
void testExample() {
    // Arrange: Set up test data
    String bikeId = "BIKE123";
    
    // Act: Call the method being tested
    Bike bike = new Bike(bikeId, BikeType.STANDARD);
    
    // Assert: Check the results
    assertEquals(bikeId, bike.getId());
}
```

**2. Testing Exceptions**
```java
@Test
void shouldThrowExceptionForNullId() {
    // This tests that our code properly rejects bad input
    assertThrows(IllegalArgumentException.class, () -> {
        new Bike(null, BikeType.STANDARD);
    });
}
```

## 🛠️ Beginner's Toolkit

### Essential Commands
```bash
# Run all tests
mvn test

# Run just one test class
mvn test -Dtest=BikeBasicTest

# Run just one test method
mvn test -Dtest=BikeBasicTest#shouldCreateBike

# Compile the code
mvn compile

# Start the web server
mvn exec:java -Dexec.mainClass="com.bikeshare.web.SimpleBikeShareWebServer"
```

### IDE Tips
- **Green arrow**: Run test
- **Red X**: Test failed
- **Green checkmark**: Test passed
- **Coverage**: Shows which code is tested

## 🎯 Learning Challenges (Start Here!)

### Challenge 1: Fix the Broken Test
1. Open `BikeBasicTest.java`
2. Find the test with `fail("Complete this test...")`
3. Replace the `fail()` with actual test code
4. Run the test to see it pass!

### Challenge 2: Write Your Own Test
```java
@Test
@DisplayName("My custom bike test")
void myCustomTest() {
    // TODO: Create a bike and test something about it
    // Hint: Test the bike's status or type
}
```

### Challenge 3: Test Swedish Features
Try testing the Swedish personnummer validation:
```java
@Test
void testSwedishPersonnummer() {
    // This should work (valid Swedish ID)
    User user = new User("800101-8129", "test@example.com", "Anna", "Svensson");
    assertEquals("800101-8129", user.getUserId());
}
```

## 🐛 Common Beginner Mistakes & Solutions

### Mistake 1: Forgetting @Test annotation
❌ **Wrong:**
```java
void myTest() { // Missing @Test!
    assertEquals(1, 1);
}
```

✅ **Correct:**
```java
@Test
void myTest() {
    assertEquals(1, 1);
}
```

### Mistake 2: Wrong import statements
❌ **Wrong:**
```java
import org.junit.Test; // JUnit 4 - old!
```

✅ **Correct:**
```java
import org.junit.jupiter.api.Test; // JUnit 5 - modern!
```

### Mistake 3: Not understanding test failure messages
```
Expected: "Standard Bike"
Actual: "Standard"
```
This means your test expected "Standard Bike" but got "Standard".

## 📖 Beginner Resources

### In This Project
1. **README.md** - Overview and setup
2. **lab-instructions/01-LAB1-FUNDAMENTALS.md** - Your first lab
3. **docs/TECHNICAL_GUIDE.md** - How the code works
4. **CROSS_PLATFORM_COMPATIBILITY.md** - Setup help

### External Resources (Beginner-Friendly)
1. **JUnit 5 User Guide**: https://junit.org/junit5/docs/current/user-guide/
2. **Java Testing Basics**: https://www.baeldung.com/junit-5
3. **Testing Best Practices**: https://martinfowler.com/articles/practical-test-pyramid.html

## 🎉 Success Metrics for Beginners

**Week 1-2 Goals:**
- [ ] Successfully run a test
- [ ] Understand what @Test does
- [ ] Write one simple assertion
- [ ] Fix one broken test

**Week 3-4 Goals:**
- [ ] Write a test from scratch
- [ ] Test both positive and negative cases
- [ ] Understand test naming conventions
- [ ] Use different assertion methods

**Week 5+ Goals:**
- [ ] Write parameterized tests
- [ ] Test exceptions properly
- [ ] Understand test coverage
- [ ] Write meaningful test names

## 🆘 Getting Help

### When Tests Fail
1. **Read the error message carefully**
2. **Check your imports** (JUnit 5 vs JUnit 4)
3. **Look at working examples** in the same file
4. **Run tests individually** to isolate problems

### When Setup Fails
1. **Check Java version**: `java -version` (should be 21+)
2. **Check Maven**: `mvn -version` (should be 3.8+)
3. **Use setup scripts**: They fix common problems automatically

### Code Doesn't Compile
1. **Missing imports**: Your IDE should suggest them
2. **Typos**: Check method names and variables
3. **Missing dependencies**: Run `mvn clean compile`

## 🎊 Congratulations!

You're now ready to start your testing journey! Remember:
- **Start small** with Lab 1 tests
- **Practice regularly** - testing is a skill
- **Don't be afraid to experiment** - you can't break anything
- **Ask questions** - testing is learned by doing

Happy testing! 🚲✨
