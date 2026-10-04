# RateForge Starter

Starter scaffold for a distributed API rate-limiting and usage-metering project.

## Modules
- `api-key-service` — client registration, API key generation/hash storage, validation
- `protected-service` — sample protected business API
- `api-gateway` — single entry point and routing base

## Intentionally NOT implemented yet
These are the learning/core phases we will build next:
- Redis distributed rate limiter
- Gateway rate-limit filter
- Kafka usage events
- Usage metering consumer/service
- Idempotency / Retry / DLQ
- Docker Compose

## Local prerequisites
- Java 17+
- Maven
- PostgreSQL

Create database:
```sql
CREATE DATABASE rateforge;
```

Environment variables (optional):
- `RATEFORGE_DB_URL`
- `RATEFORGE_DB_USER`
- `RATEFORGE_DB_PASSWORD`

Defaults:
- DB: `jdbc:postgresql://localhost:5432/rateforge`
- user: `postgres`
- password: `postgres`

## Start order
1. api-key-service : 8081
2. protected-service : 8082
3. api-gateway : 8080

## Test
Create client:
```http
POST http://localhost:8080/api/clients
Content-Type: application/json

{
  "clientName": "ABC Technologies",
  "email": "admin@abc.com",
  "plan": "PRO"
}
```

The raw API key is returned only on creation.

Validate key:
```http
GET http://localhost:8080/api/clients/validate
X-API-KEY: rf_live_...
```

Protected demo endpoint:
```http
GET http://localhost:8080/api/demo/orders
```

The Redis rate limiter will be added at the gateway before this endpoint.
# RateForge
