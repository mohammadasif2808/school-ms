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

---

## 7. API DESIGN PRINCIPLES

- RESTful APIs
- JSON payloads
- DTO-based contracts
- Versioned endpoints (`/api/v1`)
- Internal APIs prefixed with `/internal`

---

## 8. DEVELOPMENT & DELIVERY PLAN

### 8.1 Implementation Order

1. identity-service
2. api-gateway
3. academic-core-service
4. attendance-service
5. assessment-service
6. finance-service
7. notification-service

### 8.2 Estimated Timeline (10–14 hrs/day with AI)

| Phase                   | Duration      |
|-------------------------|--------------|
| Architecture & setup    | 3–4 days     |
| Identity + Gateway      | 7 days       |
| Academic Core           | 7–10 days    |
| Remaining services      | 14–18 days   |
| Integration & deployment| 5–7 days     |

**Total: ~5–6 weeks**

---

## 9. OUT OF SCOPE (EXPLICIT)

- Mobile apps
- Push notifications
- Analytics dashboards
- AI-based grading
- Multi-tenancy (beyond logical tenant ID)

---

## 10. LONG-TERM VISION

Future enhancements may include:

- Kafka-based eventing
- Independent DB instances
- Kubernetes deployment
- Mobile clients
- Reporting microservice

_These are explicitly excluded from MVP._

---

## 11. AI USAGE GUIDELINES (CRITICAL)

AI agents must:

- Respect service boundaries
- Follow schema-per-service rule
- Avoid introducing new services
- Avoid shared libraries for business logic
- Use Java 17 + Spring Boot 3 only