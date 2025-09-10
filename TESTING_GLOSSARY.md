# Common Testing Terms - Simple Explanations 📚

## What These Words Mean (For Beginners)

### Testing Words

**Test** 🧪
- Code that checks if other code works correctly
- Like checking your math homework

**Assertion** ✅
- A check that something is true
- If it's false, the test fails
- Example: "Check that 2 + 2 equals 4"

**Test Case** 📝
- One specific thing you want to test
- Like "check that a bike can be created"

**Test Suite** 📂
- A group of related tests
- Like all tests for bikes together

### JUnit Words

**@Test** 🏷️
- Tells Java "this is a test method"
- Put it above your test methods

**assertEquals** ⚖️
- Checks if two things are equal
- `assertEquals(expected, actual)`

**assertTrue** ✅
- Checks if something is true
- `assertTrue(bike.isAvailable())`

**assertFalse** ❌
- Checks if something is false
- `assertFalse(bike.isBroken())`

**assertNotNull** 🚫
- Checks that something exists (is not null)
- `assertNotNull(bike.getId())`

### Testing Patterns

**Arrange-Act-Assert (AAA)** 🎯
```java
// Arrange: Set up what you need
String bikeId = "BIKE123";

// Act: Do the thing you're testing
Bike bike = new Bike(bikeId, BikeType.STANDARD);

// Assert: Check if it worked
assertEquals(bikeId, bike.getId());
```

**Given-When-Then** 📖
- Another way to think about tests
- Given: Starting situation
- When: Something happens  
- Then: Expected result

### Error Words

**Compilation Error** 🔴
- Your code has typos or syntax errors
- Fix these first before running tests

**Test Failure** ❌
- Test ran but the assertion failed
- The code didn't behave as expected

**Exception** 💥
- Something went wrong while running
- Like dividing by zero

### BikeShare Domain Words

**Bike** 🚲
- The main object in our system
- Has an ID, type, and status

**BikeType** 🏷️
- Different kinds of bikes: STANDARD, ELECTRIC, MOUNTAIN, CARGO
- Each has a different price per minute

**User** 👤
- Person who rents bikes
- Has name, email, phone number

**Personnummer** 🆔
- Swedish personal ID number
- Format: YYMMDD-NNNN (like 800101-8129)

**Station** 🚏
- Place where bikes are parked
- Users pick up and drop off bikes here

### IDE Words

**IDE** 💻
- Integrated Development Environment
- Your code editor (VS Code, IntelliJ, Eclipse)

**Maven** 📦
- Tool that builds and manages Java projects
- Handles dependencies and running tests

**Green/Red** 🟢🔴
- Green: Test passed ✅
- Red: Test failed ❌

### File Structure Words

**src/main/java** 📁
- Where the actual application code lives
- The code you're testing

**src/test/java** 📁
- Where your test code lives
- Tests the code in src/main/java

**pom.xml** ⚙️
- Maven configuration file
- Lists dependencies and project settings

### Advanced Words (Don't worry about these yet!)

**Mock** 🎭
- Fake object used in testing
- Learn this in Lab 3+

**Coverage** 📊
- How much of your code is tested
- Learn this in Lab 3+

**Integration Test** 🔗
- Tests multiple components together
- Learn this in Lab 4+

**Regression Test** 🔄
- Tests that prevent old bugs from coming back
- Learn this in Lab 4+

## Quick Reference Card 🗂️

Copy this for easy reference:

```java
// Basic test structure
@Test
void myTest() {
    // Arrange
    // Act  
    // Assert
}

// Common assertions
assertEquals(expected, actual);     // Check equal
assertTrue(condition);              // Check true
assertFalse(condition);            // Check false
assertNotNull(object);             // Check not null
assertThrows(Exception.class, () -> doSomething()); // Check exception
```

## When You See Errors... 🆘

**"Cannot find symbol"**
- You have a typo in a method name
- Or missing import statement

**"AssertionFailedError"**
- Your test ran but the assertion failed
- Check what you expected vs what actually happened

**"BUILD FAILURE"**
- Code doesn't compile
- Fix syntax errors first

**"No tests found"**
- Missing @Test annotation
- Check your test file name ends with "Test"

Remember: Everyone was a beginner once! 🌱
