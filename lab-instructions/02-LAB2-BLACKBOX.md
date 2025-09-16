# Lab 2 — Black Box Testing (User class)

## High-level plan
This lab has two parts. Follow the steps in order.  
Part 1: design minimal black-box test sets on paper (no coding).  
Part 2: implement your tests in a single JUnit file: `UserBlackBoxTest.java`.

---

## Part 1: Test Design (on paper)

### Activity 2.1 — Equivalence Partitioning (User)
Task:
1. From `docs/REQUIREMENTS.md`, identify input domains and validity rules for **email**, **name**, **phone number**, and **fund addition** (account balance). Cite the rule ID/source when you write the class.   
2. Define valid/invalid equivalence classes for each input.  
3. Select a **minimal** set of test cases that covers all classes (one test per class).  
4. Document as a table: *Input → Expected outcome* (accept/reject + message).

### Activity 2.2 — Boundary Value Analysis (User)
Task:
1. List numeric/string boundaries implied by the requirements (e.g., **min/max fund addition**, **name length**).   
2. Choose test cases **at**, **just below**, and **just above** each boundary.  
3. Document as a minimal boundary table.

### Activity 2.3 — Decision Table Testing (User membership → discount)
Task:
1. From requirements, extract membership types and discount intent (BASIC, PREMIUM, VIP, STUDENT, CORPORATE).   
2. Define conditions (membership type, ride-count bands if applicable) and the expected discount/action.  
3. Create a compact decision table and pick a minimal covering set of rules to test.


### (Optional) Activity 2.4 — User Error Scenarios (User)
Task:
1. Describe at least three realistic error scenarios (e.g., **insufficient balance on debit**, **negative deposit**, **invalid email update**).  
2. For each: steps, inputs, expected error message/exception, and final state (*state unchanged on failure*).

---

## Part 2: Testing Challenges (hands-on, one file)

> Implement everything in **`src/test/java/com/bikeshare/lab2/UserBlackBoxTest.java`**.

- **Challenge 2.1 — EP tests:** Implement tests for email, name, and fund addition classes.  
  - Ask GenAI to generate test methods for Phone number. It is correct? How it is different from your design?   
- **Challenge 2.2 — BVA tests:** Implement boundary tests for fund addition.
- **Challenge 2.3 — Decision-table tests:** Implement a compact set for membership → discount.  
- (Optional)**Challenge 2.4 — Scenario tests:** Implement the error scenarios and verify messages + state.
- **Optional:** Test another class of your choice (e.g., `Ride`, `Bike`, `Station`).
- 
---

## Getting Started
1. Start the app: `mvn exec:exec`, open `http://localhost:8080`.  
2. Read `docs/REQUIREMENTS.md` for **User** constraints.   
3. Locate `User` at `src/main/java/com/bikeshare/model/User.java`.  
4. Create/replace `src/test/java/com/bikeshare/lab2/UserBlackBoxTest.java`.  
5. Run tests: `mvn test -Plab2` (or in your IDE).  
6. (Optional) Coverage: `mvn test jacoco:report -Plab2` and aim for **~60% of `User`**.

---

## Deliverables
- Part 1: your minimal test design tables (EP, BVA, decision table, scenarios).  
- Part 2: a single JUnit file `UserBlackBoxTest.java` with the four challenge groups.  
- Coverage report evidence (screenshot or HTML path is fine).
- Reflect on your experience: preparation vs. implementation, surprises, lessons.

---
