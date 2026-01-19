You are a senior backend engineer working on a Spring Boot–based microservices system.

Context:
- Repository root: school-ms
- Services path: school-ms/services/
- Current service: academic-core-service
- This service already contains:
    - Domain model documentation (.md)
    - OpenAPI contract at:
      school-ms/services/academic-core-service/docs/openapi-front-office.yaml

Your task is to implement a CONTRACT-FIRST skeleton for the Front Office module.

----------------------------------------
GOALS
----------------------------------------

1. Generate Spring Boot skeleton code STRICTLY based on the OpenAPI contract.
2. Do NOT invent APIs, fields, or flows beyond the contract.
3. Focus on structure, correctness, and compile safety — NOT business logic.

----------------------------------------
WHAT TO GENERATE
----------------------------------------

For academic-core-service:

1. Controllers
    - One controller per Front Office module:
        - VisitorController
        - PhoneCallController
        - HalfDayNoticeController
        - PostalController
        - AdmissionEnquiryController
        - ComplaintController
    - Each controller must match OpenAPI paths exactly.

2. DTOs
    - Request DTOs
    - Response DTOs
    - Enum classes
    - Pagination wrapper DTO

3. Packages
   Use this structure strictly:

   com.school.academic.frontoffice
   ├── controller
   ├── dto
   ├── enums
   ├── mapper
   ├── service
   ├── repository
   └── entity

4. Entities
    - Create JPA entities matching the domain model (.md)
    - Add audit fields (createdAt, updatedAt, createdBy)

5. Repositories
    - Spring Data JPA repositories
    - No custom queries yet unless required by listing filters

----------------------------------------
DATABASE & MIGRATIONS
----------------------------------------

1. Use Flyway for DB migrations.
2. Generate initial SQL migration files under:
   src/main/resources/db/migration
3. Each entity must have a corresponding table.
4. Use snake_case table and column names.

----------------------------------------
SECURITY & CONTEXT
----------------------------------------

1. Assume authentication is already handled by identity-service.
2. Add placeholders for:
    - schoolId
    - academicYearId
3. Do NOT implement authorization logic yet.

----------------------------------------
QUALITY GATES (MANDATORY)
----------------------------------------

Before finalizing:
- Ensure code compiles
- Ensure no unused imports
- Ensure naming consistency
- Ensure controllers match OpenAPI exactly

----------------------------------------
OUTPUT
----------------------------------------

- Generate code files directly in correct folders.
- If any compile or design issue appears, FIX IT before proceeding.
- Do not stop halfway.

This is a foundation step — correctness matters more than speed.
