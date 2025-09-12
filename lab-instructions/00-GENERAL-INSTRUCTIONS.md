## BikeShare Testing Project — General Instructions

## 📋 Overview

Welcome to the BikeShare Testing Project. This repository is a teaching-oriented Java project that uses a realistic Shared Bike System domain to guide students through progressive software testing labs. The materials are organized as six labs that build on each other. Labs are released incrementally so students can focus on the current learning objectives.

## 🎯 Learning Objectives

By completing the labs you will:

- Understand fundamental testing terminology and concepts
- Apply black-box and white-box testing techniques
- Produce and interpret code coverage reports
- Implement regression and integration tests
- Integrate tests with CI/CD pipelines
- Consider ethical and sustainability aspects of testing

## 🏗 Project Structure (student view)

```
.
├── .github/                 # CI workflows (student-friendly)
├── docs/                   # Guides and maps for students
├── lab-instructions/       # Lab-specific instructions (start here)
├── src/
│   ├── main/java/com/bikeshare/   # Application code (read-only for tests)
│   └── test/java/com/bikeshare/   # Test packages where you implement tests
│       ├── lab1/           # Lab 1 tests
│       ├── lab2/           # Lab 2 tests
│       ├── lab3/           # Lab 3 tests
│       ├── lab4/           # Lab 4 tests
│       ├── lab5/           # Lab 5 tests
│       └── lab6/           # Lab 6 tests
├── pom.xml                 # Maven build and test config
└── README.md               # Project overview and quick start
```

## 📚 Lab Overview

There are six progressive labs:

1. Lab 1 — Fundamentals: basic terminology and first tests
2. Lab 2 — Black box testing: equivalence classes, boundary values
3. Lab 3 — Structural testing: coverage-driven testing, white-box techniques
4. Lab 4 — Regression testing: test maintenance and automation
5. Lab 5 — Advanced tools: mocking, CI/CD, and tooling
6. Lab 6 — Sustainability: ethical and environmental testing practices

Each lab contains a detailed instruction file in `lab-instructions/` and starter test templates in `src/test/java/com/bikeshare/lab*/`.

## 🛠 Technical Stack

- Java 17+
- Maven
- JUnit 5
- Mockito
- JaCoCo (coverage reporting)
- GitHub Actions (CI; student-friendly workflows)

## 📊 Testing Metrics and Goals

- Lab 1: introductory — focus on learning rather than coverage
- Lab 2: target ~60% line coverage for practiced black-box techniques
- Lab 3: aim for high line and branch coverage (instructor-specified targets)
- Labs 4–6: increasing expectations (regression, tooling, sustainability)

> Note: Student repository workflows are configured to generate coverage reports for feedback but not to fail the CI on low coverage. Coverage targets are educational goals, not immediate blockers in this repo.

## 🚀 Getting Started (student steps)

1. Clone the repository:

```bash
git clone <repository-url>
cd <repo>
```

2. Build the project and run tests:

```bash
mvn clean compile
mvn test
```

3. Start the demo web app (optional):

```bash
mvn exec:java
# then visit http://localhost:8080
```

4. Begin with Lab 1 by opening `lab-instructions/01-LAB1-FUNDAMENTALS.md` and the corresponding test files under `src/test/java/com/bikeshare/lab1/`.






Happy testing!

