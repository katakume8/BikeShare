# Lab 1 — Fundamentals

## High-level plan

This lab has two parts. Follow the steps in order. Part 1 focuses on quality attributes and fault analysis. Part 2 gets you set up with the repository and a first test run.

---

## Part 1: Quality Attributes and Bug Analysis

### Activity 1.1: Quality Analysis of Real Systems

Task:

1. Choose a company from the [Y Combinator Directory](https://www.ycombinator.com/companies/). Filter for batches from the last three years (after 2022). Select a company whose name starts with the first letter of your name (or close to it if unavailable).

2. Identify a digital product, service, or app offered by this company.

3. Select the top 5 quality characteristics from the ISO 25010:2023 standard most relevant to the chosen product. Justify your choices.

4. For each selected characteristic, write a brief statement summarizing a relevant quality attribute. Example: for accessibility, "Information or visual elements should not rely solely on color to convey meaning."

5. Select five sub-characteristics from your chosen quality characteristics and suggest a method to test the associated quality requirement. Ensure the requirements are testable.

### Activity 1.2: Bug Analysis and Testing Strategy

Task:

1. Choose an item from the [List of software bugs](https://en.wikipedia.org/wiki/List_of_software_bugs) on Wikipedia.

2. For the selected bug, identify the following:

   - **Human Error:** Describe the human mistake that likely led to the bug.
   - **Fault:** Identify the fault in the code or system configuration.
   - **Failure:** Explain how the fault manifested as a failure in the system.
   - **Runtime Error:** Discuss any runtime errors that occurred due to the bug.

3. What testing actions could have detected the fault before release? Are these actions unit tests, integration tests, system tests, or acceptance tests? What quality attributes would these tests address?

---

## Part 2: Repository setup & First Test (hands-on)

### Getting Started

1. Navigate to the BikeShare repository.

2. Start the web interface: `mvn exec:java` then visit `http://localhost:8080`.

3. Review the system architecture in `docs/TECHNICAL_GUIDE.md`.

4. Examine the requirements in `docs/REQUIREMENTS.md`.

### Challenge 1: Clone the repo and get familiar

Objective: Clone the student repository locally and explore the project layout, key documentation, and core domain classes.

Tasks (high level):

- Clone the repository to your machine and open it in your IDE.
- Start the web interface (`mvn exec:exec`) and confirm you can reach the app at `http://localhost:8080`.
- Read `docs/TECHNICAL_GUIDE.md` and `docs/REQUIREMENTS.md` and locate core model classes under `src/main/java/com/bikeshare/model`.
- Note 3 things you found interesting or unclear.

### Challenge 2: Follow the FIRST_TEST_TUTORIAL

Follow the `FIRST_TEST_TUTORIAL.md` instructions in the repo to write and run your first test.


- Open `FIRST_TEST_TUTORIAL.md` and follow the step-by-step guide.
- Implement any needed test skeletons (the tutorial explains file locations); run the test(s) locally.


### Challenge 3: ISO-based quality attribute test case (non-code)

Choose three quality characteristic from ISO 25010 and design one test case for each (no code) that validates a measurable quality attribute.

- State the chosen ISO 25010 characteristic (e.g., Performance Efficiency).
- Provide a concise, testable requirement (e.g., "Availability check for a station returns within 100 ms in 95% of requests under normal load").
- Describe the test level (unit/integration/system/acceptance) and the test method (how you would measure it).
- Specify acceptance criteria and how to measure success.


### Optional Challenge 4 (Test the constructor)

Objective: (Optional) Add focused unit tests for the `Bike` constructor to verify input validation, trimming behaviour, and default state initialization.

Reference: `src/test/java/com/bikeshare/lab1/BikeBasicTest.java` contains skeletons — implement or extend them as you prefer.

Suggested tasks:

- Verify that null or empty bike IDs throw IllegalArgumentException with a clear message.
- Verify trimming or rejection of whitespace-only IDs according to the constructor contract.
- Assert default property values after construction (status, totalRides, totalDistance, battery for electric bikes).
- Add parameterized tests for all `Bike.BikeType` values where applicable.

This challenge is optional. Attempt it after completing Challenges 1–3 if time permits.



---

