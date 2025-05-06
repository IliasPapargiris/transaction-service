# transaction-service

Spring Boot service for ingesting and aggregating transaction data with JWT security, PostgreSQL, and Prometheus monitoring.

---

## 🧩 Features

- REST endpoints to ingest and query transactions  
- JWT-based authentication with Spring Security  
- Role-based access (ADMIN support)  
- Aggregated reporting by user and country  
- PostgreSQL for persistent storage  
- Flyway for automated DB migrations  
- Actuator endpoints with Prometheus metrics  
- Docker & Docker Compose support  
- Optional Terraform config for ECS Fargate deployment

---

## 📦 Tech Stack

- Java 21  
- Spring Boot 3.2.1  
- Spring Security & JWT (JJWT library)  
- PostgreSQL + JPA (Hibernate)  
- Flyway for database migrations  
- Springdoc OpenAPI + Swagger UI  
- Micrometer + Prometheus  
- Docker + Docker Compose  
- Testcontainers for integration testing  
- (Optional) Terraform for AWS ECS Fargate

---

## 🚀 Getting Started

### ✅ Prerequisites

Before running this project, make sure you have:

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- (Optional) Terraform CLI for AWS deployment

---

### 🔧 Clone & Build

```bash
git clone https://github.com/IliasPapargiris/transaction-service.git
cd transaction-service
./mvnw clean install -DskipTests
```

---

### 🐳 Running with Docker Compose

To run the application with PostgreSQL locally:

```bash
docker-compose up --build
```

This will:

- Start a PostgreSQL container on port 5432  
- Start the Spring Boot app on port 8080  
- Apply Flyway migrations automatically

---

## 🔐 Authentication

This service uses JWT-based authentication.

### Login Endpoint

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@okto.com",
  "password": "AdminPassword123"
}
```

Use the returned token in the `Authorization` header like this:

```http
Authorization: Bearer <your_token_here>
```

---

## 📑 API Docs (Swagger)

After starting the app, access API documentation via:

```
http://localhost:8080/swagger-ui.html
```

---

## 📊 Monitoring & Metrics

These actuator endpoints are exposed:

- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`

Make sure your `application.properties` or `application.yml` includes:

```properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.metrics.export.prometheus.enabled=true
management.endpoint.prometheus.enabled=true
```

---

## 🧪 Testing

Run all tests:

```bash
./mvnw test
```

Integration tests use Testcontainers to spin up a real PostgreSQL environment.

---

## 🛠️ Flyway Migrations

Flyway SQL scripts live under:

```
src/main/resources/db/migration/
```

They run automatically at app startup to manage DB schema.

The migrations also pre-populate essential data including supported countries, currencies, and currency mappings. This allows the app to work out of the box with ISO-compliant entries.

---

## ☁️ (Optional) Deploy to AWS ECS with Terraform

Terraform template is included under `infra/terraform`.

### Steps:

```bash
cd infra/terraform
terraform init
terraform apply
```

You must have AWS CLI configured with appropriate credentials.

---

## 📂 Project Structure

```plaintext
src/
├── main/
│   └── java/com/okto/transactionservice/
│       ├── controller/
│       ├── service/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       ├── exception/
│       ├── config/
│       ├── security/
│       ├── util/
│       └── TransactionServiceApplication.java
├── test/
│   └── java/com/okto/transactionservice/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       └── fixtures/    
```

---

## 🧠 Project Design & Data Validation

This service is built with a strong emphasis on **data integrity**, **global standards**, and **secure transaction handling**.

### 🌍 ISO-Based Country & Currency Modeling

Countries and currencies are stored using standardized identifiers:
- **Country codes** follow [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2), e.g., `GR`, `US`
- **Currency codes** follow [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217), e.g., `USD`, `EUR`

Using these standards:
- Promotes interoperability with external systems
- Reduces ambiguity by using globally recognized identifiers
- Supports compact storage and multi-region use cases

### 💱 Country-Currency Flexibility

The `country_supported_currencies` table models a many-to-many relationship, enabling real-world flexibility such as:
- Countries that support multiple currencies
- Shared currencies across countries

Uniqueness constraints and foreign key relationships ensure referential integrity and consistency in mappings.

### ✅ Input Validation

At the API level, all incoming transaction requests are validated using Jakarta Bean Validation annotations:

- `countryCode`: must be 2 uppercase letters (`[A-Z]{2}`), matching ISO 3166-1
- `currency`: must be 3 uppercase letters (`[A-Z]{3}`), matching ISO 4217
- `amount`: must be at least `0.01`
- Fields like `userId`, `timestamp`, etc., are required

This two-layer approach — DTO validation and DB constraints — ensures that only clean, structured, and valid data flows into the system.

### 👤 Predefined Admin Credentials

For convenience and testing purposes, the following admin user is inserted via Flyway on startup:

```
email:    admin@okto.com
password: AdminPassword123
```

You can use these credentials to authenticate via the Swagger UI login endpoint and access all protected resources with `ROLE_ADMIN`.

---



---

## 🙋 Author

**Ilias Papargiris**  
[GitHub Profile →](https://github.com/IliasPapargiris)
