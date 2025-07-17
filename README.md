# Account Management RESTful Service

## System Requirements
- Java 17 or higher

## Setup & Run

### 1. Clone the repository
```
git clone https://github.com/arturspelniks/account_management.git
cd account_management
```

### 2. Run the JAR file
```
java -jar account-management-0.0.1-SNAPSHOT.jar
```
The service will start on [http://localhost:8080](http://localhost:8080).

### 3. API Endpoints

#### 1. List accounts for a client
- **Endpoint:** `GET /api/clients/{clientId}/accounts`
- **Request Example:**
  - `GET /api/clients/1/accounts`
- **Response Example:**
```json
[
  {
    "id": 5,
    "balance": 500.00,
    "currency": "USD"
  },
  {
    "id": 6,
    "balance": 515.00,
    "currency": "EUR"
  }
]
```

#### 2. List transactions for an account (paged)
- **Endpoint:** `GET /api/accounts/{accountId}/transactions?offset={offset}&limit={limit}`
- **Request Example:**
  - `GET /api/accounts/10/transactions?offset=0&limit=2`
- **Response Example:**
```json
[
  {
    "amount": -1.00,
    "description": "Transfer to account 2",
    "timestamp": "2025-07-16T16:04:23.73671",
    "currency": "USD"
  },
  {
    "amount": 1.00,
    "description": "Transfer from account 2",
    "timestamp": "2025-07-16T16:04:23.73671",
    "currency": "USD"
  }
]
```

#### 3. Transfer funds between accounts
- **Endpoint:** `POST /api/accounts/transfer`
- **Request Example:**
```json
{
  "fromAccountId": 1,
  "toAccountId": 6,
  "amount": 5,
  "currency": "EUR"
}
```
- **Response Example (success):**
Response is empty on success.

- **Response Example (error):**
```json
{
  "message": "Transfer amount must be positive"
}
```

## Available Clients
The application has two predefined clients with static IDs:
- **Alice**: ID 1
- **Bob**: ID 2

## Supported Currencies
The application supports following currencies:
- USD (US Dollar)
- EUR (Euro)
- GBP (British Pound)
- JPY (Japanese Yen)

## Database Migration
Flyway is used for DB schema versioning. Migration scripts are in `src/main/resources/db/migration/`.

## Notes
- Currency rates are fetched from https://api.currencyapi.com/v3/latest
- API key is required for currency conversion API call and is stored in `application.properties` as `currency-api.apikey`
- The app is resilient to external currency API failures: currencies are periodically cached in the database as well as initialized with default rates
- Application uses in-memory H2 DB
- 