SERVICE-SPECIFIC CONTEXT — academic-core-service
1 — One-line mission

academic-core-service manages the academic structure and academic placement of people in a school: AcademicYear, Class (Grade), Section, Subject, Student, Parent/Guardian, Staff (institutional profile), Enrollment (student + class + section + year + rollNumber), ClassSection, SubjectAssignment, StaffAssignment, Classroom. It does NOT handle authentication, payroll, attendance, exams, fees, notifications, or document storage.

2 — Non-negotiable invariants (must always hold)

Identity boundary: identity-service is authoritative for users. academic-core-service may store userId references (nullable) but must never attempt to manage credentials/roles.

Enrollment is authoritative for roll numbers: roll numbers belong to Enrollment, not Student. Roll numbers are unique per (ClassSection, AcademicYear).

Promotion is append-only: promotion closes old Enrollment and creates new Enrollment. Do not delete historical enrollments.

AcademicYear must be explicit: all commands that change academic placement (enroll, promote, assign subjects/staff, roll updates) require academicYearId explicitly. No implicit “current year”.

Separation of concerns: do not add HR/payroll fields into Staff; do not add attendance/exam fields into Student/Enrollment.

3 — Project layout & paths (already in repo)

Assume this layout under school-ms/services/academic-core-service/:

docs/
domain-model.md
openapi-v1.yaml
screen-views/...
src/
main/
java/com/school/academic/...
resources/
application.yml
tests/
Dockerfile
docker-compose.yml
README.md


If an agent generates anything, it must update docs/ and respect domain-model.md & openapi-v1.yaml as authoritative.

4 — Environment & config (application.yml variables)

Use environment-driven config. Required env variables (example names):

SPRING_PROFILES_ACTIVE=local
SERVER_PORT=8081
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/academic_core?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=
SPRING_JPA_HIBERNATE_DDL_AUTO=validate   # or update in dev only
LOG_LEVEL=com.school.academic=INFO
JWT_CLAIM_USERID_CLAIM=userId  # how we read identity (kept for later)
FLYWAY_ENABLED=true


Agents must not hardcode credentials, must use env vars.

5 — Database strategy

One schema: academic_core (on shared MySQL RDS or local MySQL).

Migrations: use Flyway (preferred) or Liquibase. All migration SQL goes to src/main/resources/db/migration/.

Table design below is canonical for v1.

5.1 Schema creation (single line)
CREATE SCHEMA IF NOT EXISTS academic_core;

5.2 Tables (recommended columns — add audit columns to each)

Use these as the authoritative table definitions for v1. Include audit columns: created_by, created_at, modified_by, modified_at.

AcademicYear
CREATE TABLE academic_core.academic_year (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
code VARCHAR(32) NOT NULL, -- "2024-25"
name VARCHAR(128) NOT NULL,
start_date DATE,
end_date DATE,
is_active BOOLEAN DEFAULT FALSE,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
modified_by VARCHAR(64),
modified_at TIMESTAMP NULL
);
CREATE UNIQUE INDEX ux_academic_year_code ON academic_core.academic_year(code);

Class (grade)
CREATE TABLE academic_core.class (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
code VARCHAR(32) NOT NULL, -- "10", "KG-1", etc.
name VARCHAR(128) NOT NULL,
description TEXT,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
modified_by VARCHAR(64),
modified_at TIMESTAMP NULL
);
CREATE UNIQUE INDEX ux_class_code ON academic_core.class(code);

Section
CREATE TABLE academic_core.section (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
code VARCHAR(16) NOT NULL, -- "A", "B"
name VARCHAR(64),
description TEXT,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX ux_section_code ON academic_core.section(code);

ClassSection (operational)
CREATE TABLE academic_core.class_section (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
class_id BIGINT NOT NULL,
section_id BIGINT NOT NULL,
academic_year_id BIGINT NOT NULL,
medium VARCHAR(32), -- e.g. "English"
class_teacher_staff_id BIGINT NULL,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_cs_class FOREIGN KEY (class_id) REFERENCES academic_core.class(id),
CONSTRAINT fk_cs_section FOREIGN KEY (section_id) REFERENCES academic_core.section(id),
CONSTRAINT fk_cs_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id),
UNIQUE KEY ux_class_section_year (class_id, section_id, academic_year_id)
);

Subject
CREATE TABLE academic_core.subject (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
code VARCHAR(64),
name VARCHAR(128) NOT NULL,
type VARCHAR(32), -- "theory"/"practical"
is_minor BOOLEAN DEFAULT FALSE,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SubjectAssignment
CREATE TABLE academic_core.subject_assignment (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
subject_id BIGINT NOT NULL,
class_id BIGINT NOT NULL,
academic_year_id BIGINT NOT NULL,
section_id BIGINT NULL, -- optional
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_sa_subject FOREIGN KEY (subject_id) REFERENCES academic_core.subject(id),
CONSTRAINT fk_sa_class FOREIGN KEY (class_id) REFERENCES academic_core.class(id),
CONSTRAINT fk_sa_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id),
UNIQUE KEY ux_sa_class_subject_year_section (subject_id, class_id, academic_year_id, section_id)
);

Staff (institutional profile)
CREATE TABLE academic_core.staff (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
user_id VARCHAR(64) NULL, -- identity-service userId, nullable
staff_code VARCHAR(64),
first_name VARCHAR(128),
last_name VARCHAR(128),
staff_type VARCHAR(64), -- TEACHER, RECEPTIONIST,...
joining_date DATE,
status VARCHAR(32),
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX ux_staff_staff_code ON academic_core.staff(staff_code);

StaffAssignment
CREATE TABLE academic_core.staff_assignment (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
staff_id BIGINT NOT NULL,
subject_assignment_id BIGINT NOT NULL, -- maps to SubjectAssignment or class_section level
class_section_id BIGINT NULL,
academic_year_id BIGINT NOT NULL,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_sa_staff FOREIGN KEY (staff_id) REFERENCES academic_core.staff(id)
);

Student (academic profile)
CREATE TABLE academic_core.student (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
user_id VARCHAR(64) NULL, -- optional link to identity
admission_no VARCHAR(64) NULL,
first_name VARCHAR(128),
last_name VARCHAR(128),
dob DATE,
gender VARCHAR(16),
status VARCHAR(32),
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX ux_student_admission_no ON academic_core.student(admission_no);

Parent / Guardian
CREATE TABLE academic_core.parent (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
user_id VARCHAR(64) NULL,
first_name VARCHAR(128),
last_name VARCHAR(128),
phone VARCHAR(32),
email VARCHAR(128),
relation VARCHAR(64),
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

StudentParent (link)
CREATE TABLE academic_core.student_parent (
student_id BIGINT NOT NULL,
parent_id BIGINT NOT NULL,
relation VARCHAR(64),
PRIMARY KEY (student_id, parent_id),
CONSTRAINT fk_sp_student FOREIGN KEY (student_id) REFERENCES academic_core.student(id),
CONSTRAINT fk_sp_parent FOREIGN KEY (parent_id) REFERENCES academic_core.parent(id)
);

Enrollment
CREATE TABLE academic_core.enrollment (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
student_id BIGINT NOT NULL,
class_section_id BIGINT NOT NULL,
academic_year_id BIGINT NOT NULL,
roll_number INT NULL,
enrollment_status VARCHAR(32) DEFAULT 'ACTIVE', -- ACTIVE, PROMOTED, LEFT
start_date DATE,
end_date DATE,
created_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_enr_student FOREIGN KEY (student_id) REFERENCES academic_core.student(id),
CONSTRAINT fk_enr_class_section FOREIGN KEY (class_section_id) REFERENCES academic_core.class_section(id),
CONSTRAINT fk_enr_year FOREIGN KEY (academic_year_id) REFERENCES academic_core.academic_year(id)
);
CREATE UNIQUE INDEX ux_enrollment_roll ON academic_core.enrollment (class_section_id, academic_year_id, roll_number);
CREATE UNIQUE INDEX ux_enrollment_student_year ON academic_core.enrollment (student_id, academic_year_id);

Classroom
CREATE TABLE academic_core.classroom (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(128),
capacity INT,
status VARCHAR(32),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


Note: all tables should include audit columns (created_by, created_at, modified_by, modified_at) and be managed by Flyway migrations. Use VARCHAR(64) for external userId to keep DB agnostic.

6 — Example seed data (minimal)

Create one academic year, one class, section A, one ClassSection:

INSERT INTO academic_core.academic_year (code, name, start_date, end_date, is_active) VALUES ('2024-25','2024-25', '2024-06-01','2025-05-31', true);
INSERT INTO academic_core.class (code, name) VALUES ('10','Class 10');
INSERT INTO academic_core.section (code, name) VALUES ('A','Section A');
-- assume ids are 1
INSERT INTO academic_core.class_section (class_id, section_id, academic_year_id, medium) VALUES (1,1,1,'English');

7 — OpenAPI / endpoints conventions (enforced)

Base path: /api/v1/academic

All mutations that affect placement accept academicYearId in payload.

Use action endpoints for domain ops:

POST /students — create student (institutional)

POST /students/{id}/enroll — enroll student to classSection (explicit academicYearId)

POST /students/promote — bulk promotion (sourceAcademicYearId, targetAcademicYearId, filter criteria)

POST /enrollments/{id}/assign-roll — assign roll for that enrollment

POST /classsections/{id}/generate-rolls — auto-generate rolls

POST /subject-assignments — assign subject to class/section/year

POST /staff-assignments — assign staff to subject/classSection/year

All endpoints are admin-only logically (role enforced in identity-service / gateway), but the service assumes requests are authenticated and the caller’s identity (userId) will be passed via header if needed.

8 — Validation rules (core)

Enrollment: student cannot have >1 active enrollment for same academicYear.

Roll uniqueness: roll number unique per (class_section_id, academic_year_id).

ClassSection create: cannot create duplicate (class_id, section_id, academic_year_id).

Promotion: ensure target class/section exists and there is no conflicting enrollment.

SubjectAssignment: subject for class+year must exist; if sectionId passed, limit to that section.

Agents must implement validation in service layer and include unit tests.

9 — Logging, metrics, health

Logging: com.school.academic package set to INFO default; allow DEBUG in dev via env.

Health endpoints: expose /actuator/health and /actuator/info. Keep actuator access restricted in prod.

Metrics: expose Micrometer metrics (prometheus).

Error handling: standardized error payloads: { code: "ERR_CODE", message: "human message", details: {...}}.

10 — Testing & quality gates

Tests-first approach for every aggregate (see Workflow 4).

Unit tests: JUnit 5 + Mockito.

Integration tests: Spring Boot Test + Testcontainers for MySQL in CI.

Coverage: aim for >= 70% per aggregate before moving on.

Static analysis: enable Checkstyle/SpotBugs in CI pipeline.

11 — Docker & local run (developer DX)

Dockerfile: multi-stage (maven build => JRE runtime) exposing SERVER_PORT.

docker-compose.yml (dev) includes:

academic-core-service (built image)

mysql service with schema academic_core created on init or run Flyway migration container step.

Local start sequence:

docker compose up -d mysql

Wait DB ready (health check)

mvn -DskipTests package or docker compose up --build academic-core-service

Agents must include Flyway migrations in generated code.

12 — Migration & backwards compatibility policy

v1 API is frozen. All breaking changes must create v2.

DB migrations via Flyway; never modify existing migration files — add new ones.

Seed data for dev only; production provision via administrative scripts.

13 — Agent (AI) behavior & rules (copy-paste for agent header)

When you instruct an AI (Copilot / other), prepend this mini-policy:

AGENT RULES — academic-core-service
1. Respect docs/domain-model.md and docs/openapi-v1.yaml as authoritative.
2. Do NOT change domain or OpenAPI without human approval.
3. Use the DB schema in this context for entity fields.
4. All mutations that affect placement MUST present academicYearId.
5. Roll numbers must be stored on Enrollment.
6. Implement server-side validations, and write tests before code.
7. When generating code, ensure the project compiles (mvn clean compile).
8. When creating migrations, use Flyway and put them under src/main/resources/db/migration.
9. Add TODOs where business decisions remain.
10. Commit messages: scoped, e.g., feat(academic): add ClassSection entity.

14 — Developer & agent workflows (quick)

Design → Contract → Skeleton → Aggregate implementation (tests-first) → Migrations → Deploy

For each aggregate, ask the agent: Implement aggregate: <AggregateName> using Workflow 4 prompt. Always run tests before moving on.

15 — Commit / Branch / PR conventions

Branch: feature/academic/<short-desc> e.g., feature/academic/classsection-entity

Commit prefix: feat(academic):, fix(academic):, docs(academic):

PR description:

Link to domain-model.md

Summary of change

Tests added

Migration files (if any)

PR checklist:

Follows domain-model.md

Tests added and passing

Migrations added

No secrets in code

Build passes locally (mvn -DskipTests=false verify)

16 — Example prompts to give Copilot / Agents (copy-paste)

Generate skeleton (Workflow 3): paste the Workflow 3 prompt and add: Respect docs/domain-model.md and docs/openapi-v1.yaml.

Implement AcademicYear aggregate:

Implement aggregate: AcademicYear
Rules:
- Use JPA entity with table academic_year in schema academic_core.
- Add repository, service, tests (unit+integration).
- Ensure Flyway migration file V0001__create_academic_year.sql added.
- Implement validation: code unique.
- Tests must pass.


Create migration:

Create Flyway SQL migration under src/main/resources/db/migration/V0002__create_class_table.sql matching the schema spec in SERVICE_CONTEXT.

17 — Quick checklist before handing to frontend or staging

Domain frozen and docs updated (domain-model.md + openapi-v1.yaml)

Flyway migrations present and tested locally

Basic skeleton compiles and tests for implemented aggregates pass

Sample seed data available for dev

README.md includes run instructions and env variables

18 — Useful boilerplate snippets (for agent to paste quickly)

Flyway migration header comment:

-- V0003__create_class_section.sql
-- Creates class_section table: class_id, section_id, academic_year_id, medium, class_teacher_staff_id


Standard error response (OpenAPI schema representation):

components:
schemas:
ErrorResponse:
type: object
properties:
code:
type: string
message:
type: string
details:
type: object

19 — Final note to agent & human reviewer

This document is the single source of truth for academic-core-service behavior. Any agent-generated change that conflicts with these items must be flagged in PR description and discussed. Keep changes small and iterative. When in doubt — prefer adding a TODO and opening an issue rather than making a silent assumption.