You are acting as a PRINCIPAL BACKEND ENGINEER
generating a CLEAN, COMPILABLE CODE SKELETON
from FROZEN DOMAIN & API CONTRACTS.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📌 CONTEXT (AUTHORITATIVE — MUST RESPECT)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Service name:
academic-core-service

Project path:
school-ms/services/academic-core-service/

AUTHORITATIVE REFERENCE FILES (MUST BE RESPECTED):
- docs/domain-model.md        (FROZEN — domain authority)
- docs/openapi-v1.yaml        (FROZEN — API authority)
- Any other `.md` files in:
  school-ms/services/academic-core-service/docs/

⚠️ These `.md` files are NOT suggestions.
They are CONTRACTS.
Generated code MUST align with them.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔐 IDENTITY & SECURITY BOUNDARY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

- identity-service already exists
- JWT is validated upstream
- academic-core-service:
    - MUST NOT implement authentication
    - MUST NOT issue or validate JWTs
    - MAY later extract userId / roles from SecurityContext
- NO security config should be added now

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎯 OBJECTIVE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Generate a COMPLETE, CLEAN PROJECT SKELETON
that STRICTLY IMPLEMENTS the OpenAPI v1 contract
and ALIGNS with the frozen domain model.

This is STRUCTURAL SCAFFOLDING ONLY.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ WHAT TO GENERATE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1️⃣ Package Structure (MANDATORY)

Use standard layered architecture:

com.school.academic
├── AcademicCoreServiceApplication
├── config
├── controller
├── service
│    └── impl
├── dto
│    ├── request
│    └── response
├── exception
├── mapper
├── util

2️⃣ Controllers
- One controller per OpenAPI tag
- Controllers MUST:
    - Contain only endpoint mappings
    - Delegate immediately to service layer
    - Match HTTP method + path EXACTLY
    - Match request/response DTOs EXACTLY
- NO business logic in controllers

3️⃣ Service Layer
- Create SERVICE INTERFACES per domain area
- Create IMPLEMENTATION classes
- Method signatures MUST align with OpenAPI operations
- Method bodies:
    - Throw UnsupportedOperationException
    - OR contain TODO comments

4️⃣ DTOs
- Request & Response DTOs ONLY
- Fields MUST match OpenAPI schemas exactly
- Use Bean Validation annotations if defined
- NO mapping logic
- NO extra fields

5️⃣ Exception Handling
- GlobalExceptionHandler (basic skeleton)
- Custom domain exceptions (empty bodies allowed)

6️⃣ Configuration
- Minimal Spring Boot config
- Basic OpenAPI / Swagger config ONLY

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🧪 COMPILATION & ERROR CORRECTION REQUIREMENT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

After generating the skeleton:

1. Perform a COMPILATION SANITY CHECK:
    - Ensure all imports resolve
    - Ensure all method signatures match
    - Ensure no missing DTOs or services
    - Ensure package names are consistent

2. If any errors or inconsistencies are detected:
    - FIX them immediately
    - Re-run the correction mentally
    - Repeat until the project would compile cleanly

3. DO NOT stop at first attempt if errors exist.

Goal:
→ A skeleton that would pass `mvn clean compile`
without errors.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🚫 STRICTLY FORBIDDEN
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

❌ NO business logic
❌ NO JPA entities
❌ NO repositories
❌ NO database config
❌ NO security config
❌ NO inferred fields
❌ NO OpenAPI changes
❌ NO domain redesign
❌ NO pagination unless explicitly defined
❌ NO test code yet

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📌 CODE QUALITY RULES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

- Java 17
- Spring Boot 3
- Constructor-based injection only
- Clean naming aligned with OpenAPI
- Clear TODO markers for future workflows
- Minimal but complete
- MUST COMPILE

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🧠 FINAL INSTRUCTION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Treat this as ENTERPRISE-GRADE scaffolding.

The output will be:
- Reviewed by humans
- Built upon incrementally
- Used by frontend & tests

Generate ONLY what is necessary
for a clean, compilable foundation.
Nothing more.
