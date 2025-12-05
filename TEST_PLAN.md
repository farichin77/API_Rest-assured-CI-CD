# Test Plan – API Automation (Rest Assured + TestNG)

## 1. Overview
- **Project**: API automation testing for Sport Reservation API.
- **Tech stack**: Java, Gradle, Rest Assured 5.5.6, TestNG 7.11.0, Hamcrest 2.2, Jackson Databind 2.18.0.
- **Config**: `src/resources/config.properties` with `baseUrl`, `email`, `password`.
- **Runner**: TestNG via `testng.xml` (configured in `build.gradle` test task).

## 2. Objectives
- **Validate** functional correctness of API endpoints covered by the suite (authentication and sport category CRUD).
- **Ensure** response status codes, payload schema/fields, and key business rules are correct.
- **Provide** fast feedback through deterministic and repeatable automated tests.

## 3. Scope
- **In scope**:
  - Auth: login endpoint using credentials from config.
  - Sport Category: Create, Update, Get, Delete endpoints.
  - Positive and key negative tests at API level.
- **Out of scope** (for now):
  - Performance/load, security penetration tests.
  - Full contract/schema validation across all services not present in repo.
  - End-to-end cross-service flows beyond sport category domain.

## 4. Test Items (Endpoints)
- `POST /auth/login` (from `tests.auth.LoginTest`).
- `POST /sport-categories` (create), `PUT /sport-categories/{id}` (update),
  `GET /sport-categories`/`{id}` (retrieve), `DELETE /sport-categories/{id}` (delete)
  (from `tests.sportCategory.*`).

## 5. Test Approach & Strategy
- **Framework**: Rest Assured for HTTP interactions; TestNG for structure, lifecycle, and assertions.
- **Base URI**: Set in `BaseTest#setUp()` from `ConfigReader.getProperty("baseUrl")`.
- **Data models**: Request bodies via Java classes in `src/main/java/body/*`.
- **Assertions**:
  - Status codes (e.g., 200/201/204/400/401/404 as applicable).
  - Response payload fields using Hamcrest matchers.
  - Business rules (e.g., name required, unique constraints if applicable).
- **TestNG suite**: `src/test/java/runner/testng.xml` defines execution order:
  1) Login
  2) Create Sport Category
  3) Update Sport Category
  4) Get Sport Category
  5) Delete Sport Category
- **Idempotency**: Tests create and clean up entities within the run where applicable (delete after create).

## 6. Test Levels & Types
- **Levels**: API/component level.
- **Types**:
  - Functional positive/negative.
  - Smoke (basic reachability and happy paths).
  - Regression (suite as a whole when changes occur).

## 7. Environments
- **Base URL**: from `config.properties` → `baseUrl=https://sport-reservation-2-api-bootcamp.do.dibimbing.id/api/v1`.
- **Auth**: `email` and `password` currently stored in `config.properties`.
- **Recommendation**: For CI/CD, replace secrets with environment variables and load via `ConfigReader` to avoid committing secrets.

## 8. Test Data Management
- **Input data**: Constructed via POJOs (`body.auth.LoginBody`, `body.sportCategory.*`).
- **Dynamic data**: Capture IDs from create responses for subsequent update/get/delete.
- **Cleanup**: Ensure deletions occur even on failure when possible (finally/after hooks) to keep environment clean.
- **Negative cases**: Use invalid/missing fields, wrong types, unauthorized tokens.

## 9. Entry & Exit Criteria
- **Entry**:
  - Test environment reachable (baseUrl up).
  - Valid credentials available.
  - Dependencies compiled successfully.
- **Exit**:
  - All smoke tests pass.
  - No P0/P1 functional defects open for covered endpoints.

## 10. Risks & Mitigations
- **Unstable test data** → Create-isolate-cleanup per run.
- **Auth/token expiration** → Obtain fresh token at suite start (login first), reuse per class if design permits.
- **Environment downtime** → Health-check before run; retry limited times if transient.
- **Secrets in repo** → Move to environment variables in CI.

## 11. Tools
- **Build**: Gradle (`gradlew test`).
- **Test runner**: TestNG (`testng.xml`).
- **Libraries**: Rest Assured, Hamcrest, Jackson.

## 12. Reporting & Metrics
- **Default reports**: Gradle/TestNG reports in `build/test-results/test` and `build/reports/tests/test`.
- **Key metrics**: Pass rate, failure rate by suite/class, time per test, flaky test tracking.

## 13. Test Execution
- **Local**:
  - `./gradlew test` (Linux/Mac) or `gradlew.bat test` (Windows).
  - Ensures TestNG uses `testng.xml` as configured in `build.gradle`.
- **CI suggestion** (informational): run on push/PR; publish `build/reports/tests/test` as artifact.

## 14. Defect Management
- **Logging**: Keep clear assertion messages; log request/response on failure.
- **Tracking**: Log issues in your chosen tracker with steps, request/response samples, environment, and commit hash.

## 15. Test Case Inventory (High-level)
- **Auth**
  - Login valid credentials → 200; token present.
  - Login invalid password → 401/400; error message.
  - Missing fields → 400 validation.
- **Sport Category**
  - Create valid name → 201; id returned; retrievable via GET.
  - Create with missing/empty name → 400.
  - Update existing with valid payload → 200; fields updated.
  - Update non-existing id → 404.
  - Get list → 200; schema basic checks.
  - Get by id existing → 200; matches created.
  - Get by id non-existing → 404.
  - Delete existing → 200/204; subsequent GET → 404.

## 16. Roles & Responsibilities
- **QA Engineer**: Maintain tests, expand coverage, review failures, triage defects.
- **Developer**: Fix defects, provide API contracts/changes.
- **Reviewer**: Code review for tests and testability feedback.

## 17. Schedule & Milestones
- **Day 1-2**: Align contracts, finalize endpoints and negative cases.
- **Day 3-4**: Implement/extend tests, stabilize flakiness.
- **Day 5**: Regression run, finalize report, handover.

## 18. Maintenance
- Update `testng.xml` when adding/removing classes.
- Keep dependencies current in `build.gradle` within compatibility.
- Expand negative/edge cases as new rules appear.

## 19. How to Contribute
- Follow existing package structure:
  - `tests.auth.*`, `tests.sportCategory.*`
  - shared base: `base.BaseTest`
  - helpers: `utils.*`, `body.*`
- Add new tests under appropriate package and register in `testng.xml`.

---

If you want me to tailor this further (e.g., precise status codes/messages per endpoint, data contracts, or CI pipeline steps), share the API spec or examples of responses.
