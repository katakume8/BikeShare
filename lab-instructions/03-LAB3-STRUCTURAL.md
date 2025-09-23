# Lab 3 — Structural Testing & Coverage Analysis

## Learning Objectives
After completing this lab, you will understand:
- **Statement Coverage**: Execute every line of code
- **Branch Coverage**: Test all decision paths (if/else, switch)
- **Coverage Tools**: Use JaCoCo for coverage measurement

---

## Part 1: Baseline Coverage Analysis

### Step 1 — Run Coverage Report
1. **Run tests with coverage**:
   ```bash
   mvn clean test jacoco:report
   ```
2. **Open coverage report**: `target/site/jacoco/index.html`
3. **Record baseline coverage**:
   ```
   Current Coverage (Before Lab 3):
   - Total Line Coverage: ____%
   - Total Branch Coverage: ____%
   ```

### Step 2 — Choose Classes to Test
Look at the coverage report and choose classes with low coverage. Good options:
- `Bike.java` - Business entity with state logic
- `Station.java` - Capacity management
- `BikeType.java` - Simple enum with methods
- Any service class you find interesting

---

## Part 2: Implement Structural Tests

### Challenge 3.1 — Single Class Testing
**File**: Create `BikeStructuralTest.java` (or `[YourClass]StructuralTest.java`)

**Goal**: Test one class to improve its coverage

**What to test**:
- All public methods
- Different input values
- If/else branches
- Exception scenarios

### Challenge 3.2 — Two-Class Integration Testing  
**File**: Create `StationBikeIntegrationTest.java` (or `[Class1][Class2]IntegrationTest.java`)

**Goal**: Test how two classes work together

**What to test**:
- Methods that use both classes
- Business logic between classes
- Error scenarios involving both classes

---

## Part 3: Measure Improvements

### Step 3 — Check Your Progress
1. **Run coverage again**:
   ```bash
   mvn test jacoco:report -Dtest=*lab3*
   ```

2. **Record improvements**:
   ```
   Coverage After Lab 3:
   - Total Line Coverage: ____%
   - Total Branch Coverage: ____%
   - Your class coverage improvement: ____% → ____%
   ```

---

## Getting Started

1. **Create lab3 directory**: 
   ```bash
   mkdir -p src/test/java/com/bikeshare/lab3
   ```

2. **Look at the example**: Check `UserBlackBoxTest.java` in lab2 for the template style

3. **Start simple**: Pick one class, create one test method, then add more

---

## Coverage Tools

### Running Coverage
```bash
# Run all tests with coverage
mvn clean test jacoco:report

# Run only lab3 tests
mvn test jacoco:report -Dtest=*lab3*

# View HTML report
start target/site/jacoco/index.html  # Windows
```

### Understanding JaCoCo Colors
- **Green**: Code is covered
- **Yellow**: Partial branch coverage
- **Red**: Code not covered

---
