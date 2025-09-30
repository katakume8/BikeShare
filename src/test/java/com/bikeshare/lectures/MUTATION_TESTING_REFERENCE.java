// Mutation Testing Quick Reference for BikeShare
// ==============================================
// 
// What is Mutation Testing?
// - Introduces small bugs (mutations) into your code
// - Checks if your tests catch these bugs
// - Evaluates the QUALITY of your test suite
//
// Key Concepts:
// - KILLED mutation: Test caught the bug ✅
// - SURVIVED mutation: Test missed the bug ❌ (potential real bug!)
// - Line Coverage: Which code was executed
// - Mutation Coverage: Which mutations were killed
// - Test Strength: Quality of your tests
//
// BikeShare Results (AgeValidator):
// - Line Coverage: 94% (looked great!)
// - Mutation Coverage: 75% (revealed problems!)
// - 2 Mutations SURVIVED = 2 real bugs found
//
// Running Mutation Tests:
// mvn clean test org.pitest:pitest-maven:mutationCoverage -Pcoverage-comparison
// 
// View Results: target/pit-reports/index.html
//
// The Big Lesson: 94% Coverage ≠ Good Tests!
// Coverage shows WHAT was executed
// Mutations show IF your tests verify logic
//
// Survived Mutations in Our Code:
// Line 43: if (today.isBefore(birthDate.plusYears(age))) { // ← Not properly tested
// Line 44:     age--; // ← Never executed in tests (NO_COVERAGE)
// }
//
// The Fix: Add test for pre-birthday scenarios
// @Test
// void testPersonBeforeBirthday() {
//     String preBirthdayID = "200012310000"; // Born Dec 31, tested before birthday
//     assertFalse(validator.isAdult(preBirthdayID));
// }
//
// Best Practices:
// 1. Aim for >80% mutation coverage on critical code
// 2. Investigate every survived mutation (it's a potential bug!)
// 3. Design tests to verify logic, not just execute code
// 4. Use both coverage AND mutation testing
//
// Maven Profiles Available:
// - coverage-comparison: Analyze CoverageEx.java results
// - mutation-demo: Quick demo analysis
//
// Colors in HTML Report:
// 🟢 Green: Mutation killed (good test)
// 🔴 Red: Mutation survived (weak test)
// ⚫ Gray: No coverage (code not executed)
//
// Example Mutations:
// Original: return age >= 18;
// Mutation 1: return age > 18;     ✅ KILLED by boundary test
// Mutation 2: return true;         ✅ KILLED by child test  
// Mutation 3: if (false) { age--; } ❌ SURVIVED - missing test case!
//
// Key Files:
// - CoverageEx.java: The test class that revealed the bugs
// - AgeValidator.java: The class under test
// - pom.xml: PIT configuration
// - target/pit-reports/: Mutation test results