# Restaurant Reservation System
REST API for managing tables and reservations for a restaurant.  
Backend project developed with **Java 17**, **Spring Boot 4.0** and **PostgreSQL**.

## ✨ Iteration Plan
- Iteration 1 (branch `feature/auth-admin-operations`): Functional restaurant table administration panel
- Iteration 2 (branch `feature/jwt-auth`): JWT authentication and role-based access control (ADMIN/USER)
- Iteration 3 (branch `feature/crud-reservations`): Reservation CRUD with availability and locking strategies
- Iteration 4 (branch `feature/email-oauth2`): OAuth2 (Google/GitHub) and email notifications via AWS SES
  **Current status:** Iteration 1 in progress
- 
## 🛠️ Technologies
- Java 17
- Spring Boot 4.0.x
- Spring Data JPA
- PostgreSQL 16
- H2 (tests only)
- Lombok
- SpringDoc OpenAPI 2.6
- Maven
- Docker / Docker Compose

## 🚀 How to run locally
### Prerequisites
- JDK 17
- Maven (included `./mvnw`)
- Docker and Docker Compose
### Step 1: Start PostgreSQL
```bash
docker compose up -d
```
The `table-reservations` database will be available at `localhost:5432` (user: `postgres`, password: `postgres`).

### Step 2: Run the application
```bash
./mvnw spring-boot:run
```

The API will be listening at `http://localhost:8080`.

## 🧪 Running the tests
```bash
./mvnw test
```

Unit and integration tests use an in-memory H2 database and do not require PostgreSQL.

## 📚 API Documentation
Once the application is running, you can access:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI specification (JSON):** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Available endpoints

| Method | Endpoint | Description | Permissions |
| --- | --- | --- | --- |
| `POST` | `/api/tables/initialize` | Initializes restaurant tables | ADMIN (pending) |
| `GET` | `/api/tables` | Gets the current table configuration | ADMIN (pending) |

## 📦 CI/CD

GitHub Actions is configured to compile and run tests automatically on every push to `develop` and `feature/*` branches.  
*(Railway deployment will be added in iteration 2)*

## 📐 Class diagram (simplified)

```mermaid
classDiagram
    class RestaurantTable {
        +Long id
        +String number
        +TableType type
    }
    class TableType {
        <>        FIXED        LARGE        SMALL    }    RestaurantTable --> TableType
```

## GIT Policy

This project follows [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) for commit messages.  
Main branches: `main`, `develop`.  
Development is done on `feature/*` branches.