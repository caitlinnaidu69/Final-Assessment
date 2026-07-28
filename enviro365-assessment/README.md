# Enviro365 Investments — Withdrawal Notice System

Junior Developer Assessment (2026) — Full-stack solution: Spring Boot backend + HTML/JS frontend.

## What this does

Investors can:
- view their portfolio (details + products/policies they hold),
- submit a withdrawal notice against a product, validated against business rules,
- view their withdrawal history,
- download their withdrawal history as a CSV statement, with optional filtering by status and date range.

## Tech stack

- **Backend:** Java 17, Spring Boot 3.3 (Web, Data JPA, Validation), H2 in-memory database, Maven
- **Frontend:** plain HTML/CSS/JavaScript (`fetch` calls straight to the REST API — no build step, no framework)

## Project structure

enviro365-assessment/
├── backend/
│ ├── pom.xml
│ └── src/
│ ├── main/java/com/enviro/assessment/junior/caitlinnaidu/
│ │ ├── Enviro365Application.java
│ │ ├── config/ (CORS config, sample-data loader)
│ │ ├── controller/ (REST endpoints)
│ │ ├── dto/ (request/response payloads)
│ │ ├── entity/ (JPA entities + enums)
│ │ ├── exception/ (global exception handling)
│ │ ├── repository/ (Spring Data JPA repositories)
│ │ └── service/ (business logic — where the rules live)
│ ├── main/resources/application.properties
│ └── test/java/.../service/WithdrawalServiceTest.java
├── frontend/
│ └── index.html (self-contained — open directly in a browser)
└── README.md


The package is already named `com.enviro.assessment.junior.caitlinnaidu`, per the assessment's package naming instruction (`com.enviro.assessment.junior.yourname`).

## Running the backend

Requires Java 17+ and Maven (or use the Maven wrapper if you generate one via `mvn -N io.takari:maven:wrapper`).

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Two demo investors are seeded automatically on startup (see `config/DataLoader.java`):

| Investor | Age | Products |
|---|---|---|
| id 1 — Thandi Nkosi | 71 | Retirement Annuity (R500,000), Unit Trust (R120,000) |
| id 2 — Sipho Dlamini | 36 | Retirement Annuity (R80,000), Savings Plan (R25,000) |

Investor 2 is deliberately under 65, so trying to withdraw from their retirement annuity demonstrates the age business rule being rejected.

The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:enviro365db`, user `sa`, no password) if you want to inspect the data directly.

## Running the frontend

No build step needed — just open `frontend/index.html` in a browser (double-click it, or serve it with any static server, e.g. `python3 -m http.server` from the `frontend/` folder). It calls the backend at `http://localhost:8080/api`, so make sure the backend is running first.

## API reference

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/investors/{investorId}/portfolio` | Investor details + list of products with balances |
| POST | `/api/withdrawals` | Submit a withdrawal notice. Body: `{ "productId": 1, "amount": 5000.00 }` |
| GET | `/api/withdrawals/investor/{investorId}` | Withdrawal history for an investor, newest first |
| GET | `/api/reports/investor/{investorId}/withdrawals.csv?status=&from=&to=` | CSV download of withdrawal history. All query params optional: `status` = `APPROVED` or `REJECTED`, `from`/`to` = `yyyy-MM-dd` |

### Example: successful withdrawal

POST /api/withdrawals
{ "productId": 1, "amount": 10000.00 }

201 Created
{
"id": 1,
"productId": 1,
"productName": "Retirement Annuity - RA001",
"amount": 10000.00,
"balanceAtRequest": 500000.00,
"status": "APPROVED",
"reason": null,
"requestedAt": "2026-07-19 10:15:00"
}


### Example: rejected withdrawal (business rule violation)

POST /api/withdrawals
{ "productId": 3, "amount": 5000.00 } // productId 3 belongs to Sipho, age 36

400 Bad Request
{
"timestamp": "2026-07-19T10:16:00",
"status": 400,
"error": "Business Rule Violation",
"messages": ["Retirement annuity withdrawals are only allowed for investors over the age of 65. Investor is currently 36."]
}


## Business rules (implemented in `WithdrawalService`)

1. **Retirement withdrawals only allowed if age > 65** — checked only for `RETIREMENT_ANNUITY` products; age is derived from the investor's date of birth, never stored as a raw number, so it's always accurate.
2. **Withdrawal must not exceed balance.**
3. **Withdrawal must not exceed 90% of balance** — a stricter cap that applies across all product types.

A rule violation throws `BusinessRuleException`, which `GlobalExceptionHandler` converts into a `400` response with a clear message. Nothing is written to the database for a rejected request — the `withdrawal_notices` table only ever records successful withdrawals, which keeps the history/CSV export clean.

## Advanced requirements implemented (3+ required, this solution includes 5)

- **Global exception handling** — `exception/GlobalExceptionHandler.java`, a `@RestControllerAdvice` covering business-rule violations, missing resources, bean-validation failures, and a catch-all fallback.
- **DTO layer** — entities are never returned directly from controllers; `dto/` contains dedicated request/response records for every endpoint.
- **Input validation** — `@Valid` + Bean Validation annotations (`@NotNull`, `@DecimalMin`) on `WithdrawalRequestDto`.
- **Unit tests** — `WithdrawalServiceTest` covers all three business rules (pass and fail cases) plus the not-found case, using Mockito to isolate the service from the database.
- **UI validation** — the withdrawal form uses HTML5 `required`/`min`/`step` constraints, and surfaces backend validation/business-rule errors inline instead of a generic failure message.

## Design decisions & assumptions (for the follow-up interview)

- **Age is derived, not stored**, from `dateOfBirth` — avoids the classic bug of a stored age going stale.
- **Rejected withdrawals are not persisted.** I considered logging every attempt (including rejected ones) for a full audit trail, but chose to keep the history table as "successful withdrawals only" for simplicity within scope; the API error response still gives the user full feedback on why a request failed. This is one of the first things I'd revisit for a production system — an audit log of *attempts*, not just successes, is usually valuable for compliance in a financial system.
- **CSV filtering is done in-memory** over the (small, demo-scale) H2 dataset rather than pushed into the SQL query — a deliberate simplification for this assessment's scope; a production version handling large volumes would filter at the repository/query level instead.
- **CORS is wide open** (`allowedOriginPatterns("*")`) purely so the static frontend file can call the API from any origin during local development/demo — would be locked down to a specific origin in a real deployment.
- **Balance snapshot on the withdrawal notice** (`balanceAtRequest`) — stored so that historical statements stay accurate even though the running balance on the product changes after each withdrawal.

## AI usage disclosure

I used Claude (Anthropic) to draft the initial project scaffolding — entities, DTOs, services, controllers, exception handling, the frontend, and this README — based on the assessment brief. I then set the project up locally, renamed the package to match my own name, resolved a build issue caused by a stale `target` folder after that rename, and verified the API endpoints and business rules manually by running the app and testing each scenario in the browser (including confirming the age-based rejection rule against Sipho's retirement annuity). I can explain and defend every design decision in this README, including the trade-offs listed under "Design decisions & assumptions" above.

## Screenshots

![Portfolio dashboard](screenshots/portfolio-dashboard.png)
![Successful withdrawal](screenshots/withdrawal-success.png)
![Rejected withdrawal - age rule](screenshots/withdrawal-rejected-age-rule.png)
![Withdrawal history](screenshots/withdrawal-history.png)
![CSV export](screenshots/csv-export.png)