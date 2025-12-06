# Assigment-Day33-ApiRestAssured

[![CI](https://github.com/OWNER/REPO/actions/workflows/ci.yml/badge.svg)](https://github.com/OWNER/REPO/actions/workflows/ci.yml)

API automation testing project using Java, Gradle, Rest Assured, and TestNG for Auth, Sport Category, and Sport Activity APIs. HTML reporting via ExtentReports.

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
- ExtentReports 4.1.7 (HTML report)

## Project Structure
```
src/
  main/java/
    body/
      auth/              # Request bodies for Auth
      sportCategory/     # Request bodies for Sport Category
      sportActivity/     # Request bodies for Sport Activity
    utils/               # Config reader, common utilities
  resources/
    config.properties    # baseUrl, email, password (consider env vars in CI)
  test/java/
    base/                # BaseTest (sets RestAssured.baseURI, init ExtentReports)
    runner/
      testng.xml         # TestNG suite
    tests/
      auth/              # Login tests
      sportCategory/     # CRUD tests for sport categories
      sportActivity/     # CRUD tests for sport activities
reports/
  AutomationReport.html  # ExtentReports HTML output
```

## Configuration
- File: `src/resources/config.properties`
  - `baseUrl=https://sport-reservation-2-api-bootcamp.do.dibimbing.id/api/v1`
  - `email=...`, `password=...`
- Recommendation: For CI, use environment variables and update `ConfigReader` to read from env first, then fall back to properties.

## How to Run
- Windows:
```bash
gradlew.bat clean test
```

- Linux/Mac:
```bash
./gradlew clean test
```

## Reports
- Results: `build/test-results/test`
- HTML report: `build/reports/tests/test/index.html`
- ExtentReports: `reports/AutomationReport.html`

## CI/CD (GitHub Actions)
- Workflow file: `.github/workflows/ci.yml` (update if you used a different filename)
- Triggers: on `push` and `pull_request` to `main`
- JDK: Temurin 11
- Cache: Gradle caches enabled for faster builds
- Commands executed:
  - Windows: `gradlew.bat clean test`
  - Linux/Mac: `./gradlew clean test`
- Artifacts published per run:
  - `build/test-results/test`
  - `build/reports/tests/test`
  - `reports/AutomationReport.html`
- Secrets (optional, recommended for CI): set in Settings → Secrets and variables → Actions
  - `BASE_URL`, `EMAIL`, `PASSWORD` (the `ConfigReader` should read env first, then fallback to `src/resources/config.properties`)
- Viewing results: GitHub → Actions → select a run → Summary and download artifacts
- Skip CI: include `[skip ci]` in the commit message

Tip: Update the badge link at the top by replacing `OWNER/REPO` with your GitHub namespace and repository name.

See also: `TEST_PLAN.md` for detailed test strategy, scope, and case inventory.
