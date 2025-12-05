# Assigment-Day33-ApiRestAssured

API automation testing project using Java, Gradle, Rest Assured, and TestNG.

## Requirements
- Java 11+ (JDK)
- Gradle Wrapper (included: `gradlew` / `gradlew.bat`)
- Internet access to download dependencies (first run)

## Tech Stack
- Rest Assured 5.5.6
- TestNG 7.11.0
- Hamcrest 2.2
- Jackson Databind 2.18.0
- Apache POI 5.2.3 (if needed for Excel-based data)

## Project Structure
```
src/
  main/java/
    body/
      auth/              # Request bodies for Auth
      sportCategory/     # Request bodies for Sport Category
    utils/               # Config reader, common utilities
  resources/
    config.properties    # baseUrl, email, password (consider env vars in CI)
  test/java/
    base/                # BaseTest (sets RestAssured.baseURI)
    runner/
      testng.xml         # TestNG suite
    tests/
      auth/              # Login tests
      sportCategory/     # CRUD tests for sport categories
```

## Configuration
- File: `src/resources/config.properties`
  - `baseUrl=https://sport-reservation-2-api-bootcamp.do.dibimbing.id/api/v1`
  - `email=...`, `password=...`
- Recommendation: For CI, use environment variables and update `ConfigReader` to read from env first, then fall back to properties.

## How to Run
- Windows:
  ```bash
  ./gradlew clean test

## Reports
- Results: `build/test-results/test`
- HTML report: `build/reports/tests/test/index.html`



See also: `TEST_PLAN.md` for detailed test strategy, scope, and case inventory.
