# SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

## Project Title

**School Management System (SMS) – Microservice Architecture**

---

### Version

**v1.0 (Foundational Architecture & MVP Scope)**

### Status

**Approved for Implementation**

---

## 1. INTRODUCTION

### 1.1 Purpose

This document defines the functional and non-functional requirements for a School Management System built using a cost-optimized microservice architecture.

This SRS serves as:

- A shared understanding between humans and AI agents
- A scope lock to prevent over-engineering
- A long-term vision document guiding incremental delivery

This system is intended to support up to 100,000 registered users with ≤100 requests per second, while prioritizing cost efficiency over high availability.

### 1.2 Intended Audience

- Software Architects
- Backend Engineers
- AI Coding Agents (LLMs)
- DevOps Engineers
- Product Owners (non-technical)

### 1.3 System Overview

The system manages:

- Academic structure
- Students & staff
- Attendance
- Exams & assessments
- Fees & finance
- Notifications & communication

The system is built using:

- Java 17
- Spring Boot 3
- Microservices
- REST APIs
- JWT-based authentication
- Single DB instance (schema-per-service)

---

## 2. ARCHITECTURAL OVERVIEW

### 2.1 Architectural Style

- Microservices Architecture
- Domain-Driven Design (DDD)
- Stateless Services
- API Gateway Pattern

### 2.2 Services Overview

| Type           | Service Name             |
|----------------|-------------------------|
| Business       | identity-service        |
| Business       | academic-core-service   |
| Business       | attendance-service      |
| Business       | assessment-service      |
| Business       | finance-service         |
| Business       | notification-service    |
| Infrastructure | api-gateway             |

- **Total Services:** 7  
- **Business Services:** 6  
- **Infrastructure Services:** 1

### 2.3 Deployment Constraints

- Single EC2 Spot Instance (t3.small)
- Docker + Docker Compose
- External database (schema-per-service)
- Downtime tolerance: 30+ minutes
- No auto-scaling initially

---

## 3. DOMAIN MODEL & BOUNDED CONTEXTS

### 3.1 Identity & Access Context

**Service:** identity-service

**Responsibilities:**

- User authentication
- Role-based authorization
- JWT token issuance
- Password management

**Actors:**

- Admin
- Teacher
- Student
- Guardian

### 3.2 Academic Core Context

**Service:** academic-core-service

**Responsibilities:**

- Student lifecycle
- Guardian mapping
- Teacher management
- Class, section, subject
- Academic year
- Timetable

This is the system of record for academic entities.

### 3.3 Attendance Context

**Service:** attendance-service

**Responsibilities:**

- Student attendance records
- Teacher attendance
- Attendance aggregation

_Attendance service never owns student data, only references IDs._

### 3.4 Assessment Context

**Service:** assessment-service

**Responsibilities:**

- Exam creation
- Question bank
- Online/offline exams
- Results & grading
- Certificates

### 3.5 Finance Context

**Service:** finance-service

**Responsibilities:**

- Fee structures
- Invoicing
- Payments
- Fines (library, transport, hostel)

### 3.6 Communication Context

**Service:** notification-service

**Responsibilities:**

- Email notifications
- SMS alerts
- Announcements
- Event-driven notifications

---

## 4. FUNCTIONAL REQUIREMENTS

### 4.1 Authentication & Authorization

- Users must authenticate using username/password
- JWT tokens must be issued upon login
- Tokens must contain:
  - userId
  - role
  - tenant/schoolId
- All services must validate JWT via gateway

### 4.2 Academic Management

- Admin can create classes, sections, subjects
- Admin can enroll students
- Admin can assign teachers
- Guardians can view student profile (read-only)

### 4.3 Attendance Management

- Teachers can mark attendance
- Attendance is recorded per class per day
- Attendance can be queried monthly

### 4.4 Examination Management

- Admin can create exams
- Teachers can add questions
- Students can take exams (future scope)
- Results must be published

### 4.5 Fee Management

- Fees must be configurable per class
- Invoices must be generated per student
- Payments must be recorded
- Outstanding balances must be visible

### 4.6 Notification Management

- System must notify:
  - Attendance events
  - Exam results
  - Fee due reminders
- Notifications can be asynchronous

---

## 5. NON-FUNCTIONAL REQUIREMENTS

### 5.1 Performance

- ≤100 RPS sustained
- Average API latency ≤300ms
- JVM heap ≤256MB per service

### 5.2 Scalability

- Vertical scaling initially
- Horizontal scaling later (future)
- Services must be independently deployable

### 5.3 Availability

- Downtime acceptable up to 30 minutes
- No HA required initially
- Spot instance interruptions tolerated

### 5.4 Security

- Passwords hashed using bcrypt
- No session storage
- No sensitive data in logs
- HTTPS only

### 5.5 Maintainability

- Clear service ownership
- No cross-service DB access
- Clean layered architecture

---

## 6. DATA MANAGEMENT

### 6.1 Database Strategy

- Single DB instance
- Separate schema per service
- No foreign keys across schemas

### 6.2 Data Ownership Rule

- A service may only write to its own schema
- Cross-service data access is via APIs only

### 6.3 Schema Naming Convention

- Each service must use a dedicated schema matching the service name in snake_case
  - `identity_service` for identity-service
  - `academic_core` for academic-core-service
  - `attendance_service` for attendance-service
  - `assessment_service` for assessment-service
  - `finance_service` for finance-service
  - `notification_service` for notification-service

### 6.4 Database Migration Strategy

- **Flyway** is the mandated migration tool for all services
- All schema changes must be version-controlled via migration scripts
- Hibernate DDL-auto must be set to `validate` in production
- Local development may use `update` mode for convenience

---

## 7. API DESIGN PRINCIPLES

- RESTful APIs
- JSON payloads
- DTO-based contracts
- Versioned endpoints (`/api/v1`)
- Internal APIs prefixed with `/internal`

### 7.1 Inter-Service Communication

- Services **must** communicate via HTTP REST APIs only
- No direct database access to other service schemas
- No shared message queues or event buses in MVP
- HTTP timeouts must be configured per downstream service
- Circuit breaker pattern optional but recommended for resilience

### 7.2 Database Connection Requirements

- All services must use environment variables for database configuration:
  - `DB_HOST` - Database server hostname/IP
  - `DB_PORT` - Database server port (default: 3306)
  - `DB_USER` - Database user
  - `DB_PASS` - Database password
  - `DB_NAME` - Schema name (service-specific)
- Connection pooling must use HikariCP
- Max connection pool size: 10 for MVP (single EC2 instance)
- All datasource configs must support both local and production environments

---

## 8. OPERABILITY & OBSERVABILITY REQUIREMENTS

### 8.1 Health Checks

- All services must expose `/actuator/health` endpoint
- Health check response must include:
  - `status`: UP or DOWN
  - `components.db`: database connectivity
- Used by Docker health checks and load balancers

### 8.2 Actuator Configuration

Production actuator exposure must be limited to:
- `health` - service health status
- `info` - service metadata
- `prometheus` (optional) - metrics export
- `health/liveness` and `health/readiness` for orchestration

Endpoints to **exclude** from production: 
- `env` - environment variables (security)
- `configprops` - configuration properties
- `beans` - bean definitions

### 8.3 Logging Standards

- Log level for application code: `INFO` in production, `DEBUG` in local
- Log level for Spring Framework: `WARN` in production
- Log level for Hibernate SQL: `WARN` in production
- No sensitive data in logs (passwords, tokens, PII)
- Logs must be consumable by centralized log aggregation tools (future)

### 8.4 Metrics & Monitoring

- Services must export Prometheus-compatible metrics (via Micrometer)
- Minimum metrics: request count, response time, error count
- Service-specific metrics: 
  - Authentication: login attempts, token issuance
  - Academic: student enrollments, attendance records
  - Assessment: exam submissions, result processing

---

## 9. DEVELOPMENT & DELIVERY PLAN

### 9. IMPLEMENTATION ORDER

### 9.1 Implementation Sequence

1. identity-service
2. api-gateway
3. academic-core-service
4. attendance-service
5. assessment-service
6. finance-service
7. notification-service

### 9.2 Estimated Timeline (10–14 hrs/day with AI)

| Phase                   | Duration      |
|-------------------------|--------------|
| Architecture & setup    | 3–4 days     |
| Identity + Gateway      | 7 days       |
| Academic Core           | 7–10 days    |
| Remaining services      | 14–18 days   |
| Integration & deployment| 5–7 days     |

**Total: ~5–6 weeks**

---

## 10. OUT OF SCOPE (EXPLICIT)

- Mobile apps
- Push notifications
- Analytics dashboards
- AI-based grading
- Multi-tenancy (beyond logical tenant ID)

---

## 11. LONG-TERM VISION

Future enhancements may include:

- Kafka-based eventing
- Independent DB instances
- Kubernetes deployment
- Mobile clients
- Reporting microservice

_These are explicitly excluded from MVP._

---

## 12. AI USAGE GUIDELINES (CRITICAL)

AI agents must:

- Respect service boundaries
- Follow schema-per-service rule
- Avoid introducing new services
- Avoid shared libraries for business logic
- Use Java 17 + Spring Boot 3 only

---

## 13. IMPLEMENTATION FINDINGS & COMPLIANCE (v1.0)

### 13.1 Identity-Service Compliance

**Status:** ✅ COMPLIANT with minor documentation notes

- ✅ Java 17 + Spring Boot 3.2.0
- ✅ JWT-based authentication implemented (JJWT library)
- ✅ Constructor injection enforced
- ✅ DTO-based API contracts
- ✅ Role-based authorization (Role, Permission entities)
- ✅ Password hashing with bcrypt
- ⚠️ **NOTE:** No explicit Flyway migration setup (relies on Hibernate DDL-auto in local)
  - **Action:** Future versions should migrate to Flyway for production-grade schema versioning
- ⚠️ **NOTE:** Database schema not explicitly defined in @Table annotations (defaults to root schema)
  - **Action:** Recommend using `@Table(name = "users", schema = "identity_service")` pattern for clarity
- ✅ Actuator endpoints configured with proper exposure limits
- ✅ Separate application profiles for local and production (application-local.yml, application-prod.yml)

### 13.2 Academic-Core-Service Compliance

**Status:** ✅ COMPLIANT

- ✅ Java 17 + Spring Boot 3.2.1
- ✅ Proper schema-per-service implementation (`academic_core` schema explicitly defined)
- ✅ Flyway migration framework configured correctly
- ✅ Database configuration uses environment variables (DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME)
- ✅ Constructor injection enforced
- ✅ DTO-based API contracts (StudentResponse, CreateStudentRequest, etc.)
- ✅ Controller → Service → Repository pattern
- ✅ Actuator endpoints configured
- ✅ Prometheus metrics support
- ✅ HikariCP connection pooling (max-pool-size: 10)
- ✅ Separate profiles for local and production

### 13.3 Critical Implementation Notes for Future Services

1. **Database Schema Pattern:** Follow academic-core-service pattern:
   ```java
   @Table(name = "entity_name", schema = "service_name_snake_case")
   ```

2. **Database Configuration:** Always use environment variables:
   ```yaml
   datasource:
     url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME}?useSSL=false&serverTimezone=UTC
     username: ${DB_USER}
     password: ${DB_PASS}
   ```

3. **Migration Framework:** Implement Flyway migrations from day one (not lazy-loaded DDL-auto)

4. **Connection Pooling:** Default to HikariCP with max-pool-size: 10 for MVP

5. **Health Checks:** Ensure `/actuator/health` includes database component status

### 13.4 API Gateway Implementation Status

**Status:** ⏳ PENDING

- Currently a placeholder directory (.keep file only)
- Must implement before progressing to remaining business services
- Should include:
  - JWT token validation (calls identity-service /internal endpoint)
  - Request routing to downstream services
  - Rate limiting (future)
  - Request/response logging (future)
