# Representative Code Map by Lab

Use this as a lecture companion: each lab points to concrete classes, endpoints, and tests to demonstrate the topic.

## Lab 1 — Fundamentals
- Quality Attributes: `src/test/java/com/bikeshare/lab1/QualityAttributesTest.java`
- Fault Analysis scaffold: `src/test/java/com/bikeshare/lab1/FaultAnalysisTest.java`
- Terminology Guide: `lab-instructions/01-LAB1-FUNDAMENTALS.md`

Core domain classes for examples:
- `com.bikeshare.model.User`, `Bike`, `Station`, `Ride`

## Lab 2 — Black‑Box Testing
- Web/API for functional testing: `SimpleBikeShareWebServer` (GET `/api/*`)
- UI triggers in `src/main/resources/webapp/index.html` (buttons + modals)
- Add tests under `src/test/java/com/bikeshare/lab2/`

## Lab 3 — Structural Testing
- Targets: complex methods in `User`, services in `com.bikeshare.service.*`
- Coverage via JaCoCo (pom already configured)
- Add tests in `src/test/java/com/bikeshare/lab3/`

## Lab 4 — Regression (suggested targets)
- Service interfaces designed for mocking: `PaymentService`, `BankIDService`, `SwishPaymentService`
- Repository abstractions: `com.bikeshare.repository.*` + in-memory impls under `com.bikeshare.web.*`

## Lab 5 — Tools
- CI snippets in lab docs
- Static analysis (SpotBugs plugin in pom)

## Lab 6 — Sustainability/Ethics
- Focus on test selection, performance, and energy; tie back to endpoints & data loader.

---

# Data Component: Where is data stored?

By default the sample server uses in-memory repositories for quick startup in labs:
- `com.bikeshare.web.InMemoryUserRepository`, `InMemoryBikeRepository`, `InMemoryStationRepository`, `InMemoryRideRepository`

Seed data:
- `com.bikeshare.web.DataLoader` demonstrates how to preload stations, bikes, and a user.

Switching to DB (H2)
- H2 dependency exists in `pom.xml`. For a real DB, add repository implementations backed by JDBC/JPA and initialize schema on startup.
- Example runtime flags (optional):
  - `-Dh2.tcp=true` to enable TCP for H2 (if you add it)

Loading data
- In-memory: call `new DataLoader(...).loadDemoData()` once during server boot.
- Database: run SQL migrations (Flyway/Liquibase) or a custom seeder on startup.

---

# Swedish Context Integrations

Authentication (BankID):
- Interface: `com.bikeshare.service.auth.BankIDService` (mock in tests)
- Web endpoint demo to validate age via BankID flow: `POST /api/validate/age`

Payments (Swish):
- Interface: `com.bikeshare.service.payment.SwishPaymentService` (mock in tests)
- Web demo endpoints:
  - `POST /api/payments/swish/request`
  - `POST /api/payments/swish/status`

Age validation sample (lecture):
- `com.bikeshare.service.validation.AgeValidator` mirrors the AlcoholPurchaseValidator flow but checks >= 18.
- `com.bikeshare.service.validation.IDNumberValidator` abstraction for personal number.

---

# How to Run

**Prerequisites**: Java 21+ and Maven 3.8+

1. Build and test
   - `mvn clean test`
2. Start the web server (default 8080; pass a port to override)
   - Run `SimpleBikeShareWebServer` from your IDE or via Maven exec.
3. Open the UI
   - Use the startup URL the server logs after boot (default <http://localhost:8080>)
4. Try backend logic from the UI
   - Age check form (BankID simulated)
   - Swish request + status stubs

---

# Mocking with Mockito (quick examples)

```java
@Test
void adult_whenBankIDAuthenticates_andAge18() {
    var idv = mock(IDNumberValidator.class);
    var bank = mock(BankIDService.class);
    when(idv.isValidIDNumber("200001011234")).thenReturn(true);
    when(bank.authenticate("200001011234")).thenReturn(true);
    var validator = new AgeValidator(idv, bank);
    assertTrue(validator.isAdult("200001011234"));
}
```
