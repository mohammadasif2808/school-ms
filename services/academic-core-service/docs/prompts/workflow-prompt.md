You are acting as a SENIOR BACKEND ARCHITECT
specializing in Domain-Driven Design (DDD) for
institutional systems (School Management Systems).

This task is DOMAIN MODELING ONLY.
DO NOT generate code, APIs, repositories, DTOs, or database schemas.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📁 PROJECT STRUCTURE CONTEXT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Repository root:
school-ms/
├── services/
│    ├── identity-service/        (already implemented & live)
│    ├── academic-core-service/   (CURRENT FOCUS)
│    │    ├── screen-views/        (IMPORTANT)
│    │    │    ├── student_page_view.png
│    │    │    ├── add_student_screen.png
│    │    │    ├── staff_screen_view.png
│    │    │    ├── add_staff_screen.png
│    │    │    ├── parent_screen_view.png
│    │    │    ├── manage_roll_number_screen.png
│    │    │    ├── promote_student_screen.png
│    │    │    ├── add_subject_screen.png
│    │    │    ├── subject_screen.png
│    │    │    ├── class_screen.png
│    │    │    ├── add_class_screen.png
│    │    │    ├── classroom_screen.png
│    │    │    └── add_classroom_screen.png
│    │
│    └── other-future-services/
└── docs/

You MUST consider the UI intent shown in `screen-views/`
when finalizing the domain model.
UI reflects REAL institutional workflows,
but UI structure MUST NOT be copied directly into backend entities.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎯 SERVICE BEING DESIGNED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Service Name (LOCKED):
academic-core-service

Purpose:
Manage the academic structure and academic placement
of people inside a school.

This service answers:
"Who studies what, where, and in which academic year?"

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔐 IDENTITY & AUTH BOUNDARY (LOCKED)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

- identity-service already exists and is LIVE
- identity-service handles:
    - Authentication
    - JWT issuance
    - Roles & permissions (RBAC)
- academic-core-service:
    - NEVER creates users
    - NEVER handles login or signup
    - MAY reference identity-service `userId`
    - userId references are OPTIONAL / nullable

Students, Staff, Parents are created
by the institution — NOT by self-signup.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📌 DOMAIN RULES (MANDATORY)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. UI ≠ Backend Model
    - UI may flatten concepts
    - Backend must normalize correctly

2. A person can EXIST without LOGIN access

3. Academic placement is TEMPORAL
    - AcademicYear matters everywhere

4. Roll Number:
    - NOT global
    - NOT a Student attribute
    - BELONGS to Enrollment

5. Promotion:
    - Ends one enrollment
    - Creates a new enrollment
    - History MUST be preserved

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ FINAL IN-SCOPE DOMAIN ENTITIES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Academic Structure:
- AcademicYear
- Class (Grade)
- Section
- Subject

People (Institutional Profiles):
- Student
- Parent / Guardian
- Staff
    - Covers Teacher, Receptionist, Driver, etc.
    - NO payroll or HR logic here

Core Academic Glue:
- Enrollment
    - student + class + section + academicYear
    - rollNumber
- ClassSection
    - class + section + academicYear
    - medium
    - classTeacher (staff reference)
- SubjectAssignment
    - subject + class + academicYear
- StaffAssignment
    - staff + subject + classSection + academicYear

Physical Infrastructure:
- Classroom
    - physical room
    - capacity
    - status

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🚫 EXPLICITLY OUT OF SCOPE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Even if UI shows these, DO NOT include them:

- Authentication / Login
- Roles & permissions logic
- Payroll
- Salary
- Leaves
- Bank accounts
- Transport
- Hostel
- Attendance
- Exams
- Fees
- Notifications
- Document storage (only references allowed)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🧠 YOUR TASK (STRICT)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Produce the FINAL DOMAIN MODEL for academic-core-service.

You MUST output:

1. List of entities with:
    - Responsibility
    - Key attributes (conceptual, not DB-level)
2. Relationships between entities
    - Cardinality (1–1, 1–N, N–M)
3. Lifecycle rules:
    - Enrollment lifecycle
    - ClassSection lifecycle
4. Mandatory vs Optional fields
5. Which entities reference identity-service `userId`
6. Explicit invariants and constraints

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🚨 STRICT RULES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

❌ DO NOT generate:
- Code
- REST APIs
- Controllers
- Repositories
- Database schemas
- DTOs

❌ DO NOT assume frontend requirements beyond what
is visible in `screen-views/`

❌ DO NOT merge HR, payroll, or operational concerns
into academic-core-service

This output will be used to FREEZE THE DOMAIN
before moving to OpenAPI and code generation.
