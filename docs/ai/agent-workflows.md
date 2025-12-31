# Agent Workflows
School Management System (SMS)

This document defines the ONLY approved ways of working with AI agents
(ChatGPT, GitHub Copilot, Gemini, Cursor, etc.) on this project.

AI must NEVER be used freely or conversationally.
Every task MUST follow one of the workflows below.

If a task does not match a workflow, STOP and decide manually.

---

## WORKFLOW 1: Create a New Microservice

### When to Use
- Creating a microservice for the first time
- Example: identity-service, attendance-service

### Human Responsibilities (MANDATORY)
Before asking AI, YOU must decide:
- Service name
- Bounded context
- Responsibilities
- Explicitly what this service must NOT do

AI is NOT allowed to decide architecture.

### Steps

1. Tell AI the global constraints:
    - This is a microservice system
    - Java 17, Spring Boot 3
    - REST APIs
    - Stateless JWT authentication
    - One bounded context per service
    - No shared database tables

2. Tell AI the service-specific scope.
   Example:



You are working ONLY on identity-service.
Responsibilities:

Authentication

JWT issuance
Forbidden:

Academic logic

Attendance

Fees
Confirm readiness.



3. Ask AI to generate ONLY:
- Project skeleton
- Package structure
- Empty controllers/services
- Dockerfile
- application.yml (no secrets)

4. Human review:
- No business logic present
- No cross-domain code
- Correct naming and structure

5. Commit the code:


chore: initialize <service-name>



---

## WORKFLOW 2: Add a New Feature

### When to Use
- Adding any business functionality
- Example: create student, mark attendance, generate invoice

### Rules
- A feature belongs to EXACTLY ONE service
- AI must not move features between services

### Steps

1. Decide which service owns the feature (human decision).

2. Define the API first:
- Endpoint path
- Request DTO
- Response DTO

3. Ask AI to generate code in THIS ORDER:
- Entity (if required)
- Repository
- Service method
- Controller

4. Validate:
- Controllers contain no business logic
- Validation annotations exist on DTOs
- No cross-service calls unless explicitly required

5. Commit:



feat(<service-name>): <feature-name>


---

## WORKFLOW 3: Cross-Service Interaction (HIGH RISK)

### When to Use
- One service needs data owned by another service
- Example: attendance-service needs student existence check

### Rules
- Data owner service ALWAYS provides the API
- Consumer service NEVER accesses another database
- Only IDs are exchanged between services

### Steps

1. Decide:
- Producer service (data owner)
- Consumer service

2. Producer service:
- Add an internal API (prefix `/internal`)
- Return minimal data (IDs, flags only)

3. Consumer service:
- Call the internal API
- Handle failure gracefully

4. Verify:
- No shared tables
- No circular service calls
- No data duplication

---

## WORKFLOW 4: AI QUALITY AUDIT

### When to Use
- End of the day
- End of a major feature
- Before deployment

### Steps

1. Ask AI:


Review the current codebase for:

Architecture violations

Cross-service coupling

Security issues

Performance risks


2. Review AI feedback manually.

3. Fix critical issues immediately.

4. Do NOT blindly apply AI suggestions.

---

## NON-NEGOTIABLE RULES

AI must NEVER:
- Create or merge services
- Change architecture decisions
- Introduce new technologies
- Suggest monolithic designs
- Suggest Kubernetes, Kafka, or event sourcing

Architecture decisions belong ONLY to humans.
