# AI RULES — SCHOOL MANAGEMENT SYSTEM

These rules are NON-NEGOTIABLE.

## Architecture
- Microservices only
- 6 business services + API Gateway
- One bounded context per service
- Stateless services (JWT only)
- Schema-per-service database model
- No shared database tables
- No cross-service DB access

## Technology
- Java 17
- Spring Boot 3.x
- REST APIs only
- Maven
- Docker

## Coding Rules
- Constructor injection only
- No field injection
- No Lombok @Data
- DTO-based APIs
- Controller → Service → Repository
- Validation annotations mandatory

## Performance
- Max JVM heap: 256MB per service
- No blocking calls in controllers

## Forbidden
- Monolith suggestions
- Kubernetes (early stage)
- Kafka (early stage)
- Shared business libraries
