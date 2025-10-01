# Lab 4: Mutation Testing & Test Quality

## Lab 4: Improving Test Quality with Mocks and Mutation Testing

### Overview
In this lab, you will learn how to use mutation testing to evaluate and improve your test quality. You'll explore how mocking can help you write better tests that catch more bugs, and use PIT mutation testing to measure your progress.

### Activities
- Run mutation testing using PIT (PITest) on BikeShare code
- Analyze mutation testing reports to identify weak tests
- Use Mockito to create focused unit tests that kill more mutations
- Understand the difference between coverage and test quality
- Write tests that verify logic, not just execute code

---

## Pre-Lab Preparation (20 minutes)

1. **Review Lecture Materials**: Review Lecture 6 (Mocking) and Lecture 7 (Mutation Testing) Slides
2. **Understand the Goal**: Learn how mocks help create focused tests that kill more mutations
3. **Explore Examples**: Look at `CoverageEx.java` to see current mutation results

---

## Lab Activities

### Part 1: Understanding Current Mutation Results

#### Step 1: Run Baseline Mutation Testing
```bash
# Run mutation testing on current code
mvn clean test org.pitest:pitest-maven:mutationCoverage -Pcoverage-comparison

# Open the report
target/pit-reports/index.html  # Windows

```

#### Step 2: Analyze Current Results
1. **Record baseline metrics**:
   ```
   Current Mutation Coverage: ____%
   Test Strength: ____%
   Survived Mutations: ____
   ```

2. **Identify weak spots**:
   - Look for red lines (survived mutations)
   - Focus on AgeValidator and User classes
   - Note which mutations survived and why

---

### Part 2: Improve Tests with Mocks 

#### Challenge 4.1: Kill More Mutations
**File**: Edit `src/test/java/com/bikeshare/lab4/MutationImprovementTest.java`

**Your Mission**: Increase the mutation score using mocks!

**Strategy**:
1. **Analyze survived mutations** in the HTML report
2. **Write focused tests** using Mockito to kill specific mutations
3. **Target boundary conditions** (>=, >, ==, !=)
4. **Test error scenarios** that current tests miss
5. **Verify edge cases** that survived mutations reveal

**Hints**:
- Use `@Mock` and `@InjectMocks` for clean test setup
- Mock dependencies to control exact scenarios
- Focus on logic verification, not just execution
- Test both positive and negative cases

---

### Part 3: Measure Your Progress

#### Step 3: Run Mutation Testing Again
# Run mutation testing with your new tests
mvn clean test org.pitest:pitest-maven:mutationCoverage -Pmutation-demo

# Compare results
target/pit-reports/index.html


#### Step 4: Document Improvements
Record your progress:

After Lab 4:
- Mutation Coverage: ____% (was ____%)
- Test Strength: ____% (was ____%)  
- Survived Mutations: ____ (was ____)
- Mutations Killed: ____ new mutations killed


---

## Testing Challenge Details

### Challenge 4.1: Mutation Score Improvement

**Objective**: Use mocking to write tests that kill more mutations

**What to Focus On**:
1. **Boundary Conditions**: Test exact boundaries (18 years old, not 17 or 19)
2. **Error Handling**: Test all exception scenarios with controlled inputs
3. **Logic Verification**: Ensure your tests verify behavior, not just execute code
4. **Edge Cases**: Target the specific scenarios that current tests miss

**Example Mutation Scenarios to Target**:
- `age >= 18` changed to `age > 18` (boundary mutation)
- `if (!valid)` changed to `if (valid)` (conditional mutation)
- `return true` changed to `return false` (return value mutation)
- Method calls removed or changed (void method mutation)

**Using Mocks Effectively**:
```java
// Example approach - mock dependencies for precise control
@Mock
private IDNumberValidator mockValidator;

@Mock  
private BankIDService mockBankID;

@Test
void shouldKillBoundaryMutation() {
    // Arrange: Set up exact scenario
    String exactly18ID = "200501010000"; // Born exactly 18 years ago
    when(mockValidator.isValidIDNumber(exactly18ID)).thenReturn(true);
    when(mockBankID.authenticate(exactly18ID)).thenReturn(true);
    
    // Act: Test the boundary
    boolean result = validator.isAdult(exactly18ID);
    
    // Assert: Verify correct behavior
    assertTrue(result); // This kills the >= vs > mutation
}
```

---

## Resources

### Key Files to Study
- `src/test/java/com/bikeshare/lectures/CoverageEx.java` - Current test approach
- `src/test/java/com/bikeshare/lectures/MUTATION_TESTING_REFERENCE.java` - Quick reference
- `target/pit-reports/index.html` - Mutation test results

### Maven Commands

# Run your lab4 tests only
mvn test -Dtest=*lab4*

# Run mutation testing with all tests
mvn clean test org.pitest:pitest-maven:mutationCoverage

# Run with specific profile
mvn clean test org.pitest:pitest-maven:mutationCoverage -Pmutation-demo


### Mockito Quick Reference
```java
// Basic setup
@ExtendWith(MockitoExtension.class)
@Mock private Dependency mockDep;
@InjectMocks private ClassUnderTest classUnderTest;

// Stubbing behavior
when(mockDep.method(anyString())).thenReturn(expectedValue);
when(mockDep.method(eq("specific"))).thenThrow(new Exception());

// Verification
verify(mockDep).method(anyString());
verify(mockDep, never()).unwantedMethod();
```

---
