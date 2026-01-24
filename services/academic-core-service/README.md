# Academic Core Service

## Overview
The Academic Core Service is the source of truth for institutional hierarchy and people management within the school management system. It manages students, staff, parents, academic structure (years, classes, sections), enrollments, curriculum, and classrooms.

## Status
**IMPLEMENTED** - Full business logic with repository layer, validation, and tests.

## Technology Stack
- Java 17
- Spring Boot 3.2.1
- Spring Web MVC
- Spring Data JPA
- MySQL 8.x (schema: `academic_core`)
- Flyway (database migrations)
- Bean Validation
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito (testing)
- Testcontainers (integration testing)
- Micrometer + Prometheus (metrics)
- **Pure Java** (No Lombok - explicit getters/setters)

## Project Structure
```
com.school.academic
├── AcademicCoreServiceApplication.java
├── config/
│   └── OpenApiConfig.java
├── domain/                              # JPA Entities
│   ├── AcademicYear.java
│   ├── BaseEntity.java
│   ├── Classroom.java
│   ├── ClassSection.java
│   ├── Enrollment.java
│   ├── GradeClass.java
│   ├── Parent.java
│   ├── Section.java
│   ├── Staff.java
│   ├── StaffAssignment.java
│   ├── Student.java
│   ├── StudentParent.java
│   ├── Subject.java
│   └── SubjectAssignment.java
├── repository/                          # Spring Data JPA Repositories
│   ├── AcademicYearRepository.java
│   ├── ClassroomRepository.java
│   ├── ClassSectionRepository.java
│   ├── EnrollmentRepository.java
│   ├── GradeClassRepository.java
│   ├── ParentRepository.java
│   ├── SectionRepository.java
│   ├── StaffAssignmentRepository.java
│   ├── StaffRepository.java
│   ├── StudentParentRepository.java
│   ├── StudentRepository.java
│   ├── SubjectAssignmentRepository.java
│   └── SubjectRepository.java
├── controller/
│   ├── AcademicStructureController.java
│   ├── ClassroomController.java
│   ├── CurriculumController.java
│   ├── EnrollmentController.java
│   ├── ParentController.java
│   ├── StaffController.java
│   └── StudentController.java
├── service/
│   ├── AcademicStructureService.java
│   ├── ClassroomService.java
│   ├── CurriculumService.java
│   ├── EnrollmentService.java
│   ├── ParentService.java
│   ├── StaffService.java
│   ├── StudentService.java
│   └── impl/
│       ├── AcademicStructureServiceImpl.java
│       ├── ClassroomServiceImpl.java
│       ├── CurriculumServiceImpl.java
│       ├── EnrollmentServiceImpl.java
│       ├── ParentServiceImpl.java
│       ├── StaffServiceImpl.java
│       └── StudentServiceImpl.java
├── dto/
│   ├── request/
│   │   ├── BulkPromoteRequest.java
│   │   ├── CreateAcademicYearRequest.java
│   │   ├── CreateClassRequest.java
│   │   ├── CreateClassroomRequest.java
│   │   ├── CreateClassSectionRequest.java
│   │   ├── CreateEnrollmentRequest.java
│   │   ├── CreateParentRequest.java
│   │   ├── CreateSectionRequest.java
│   │   ├── CreateStaffAssignmentRequest.java
│   │   ├── CreateStaffRequest.java
│   │   ├── CreateStudentRequest.java
│   │   ├── CreateSubjectAssignmentRequest.java
│   │   ├── CreateSubjectRequest.java
│   │   ├── LinkGuardianRequest.java
│   │   ├── UpdateClassSectionRequest.java
│   │   └── UpdateRollNumberRequest.java
│   └── response/
│       ├── AcademicYearResponse.java
│       ├── ClassroomResponse.java
│       ├── ClassResponse.java
│       ├── ClassSectionResponse.java
│       ├── EnrollmentResponse.java
│       ├── ParentResponse.java
│       ├── SectionResponse.java
│       ├── StaffAssignmentResponse.java
│       ├── StaffResponse.java
│       ├── StudentResponse.java
│       ├── SubjectAssignmentResponse.java
│       └── SubjectResponse.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

## Building the Project

```bash
mvn clean compile
```

## Running the Application

```bash
mvn spring-boot:run
```

The service will start on port **8081**.

## API Documentation

Once the application is running, access the Swagger UI at:
- http://localhost:8081/swagger-ui.html

OpenAPI JSON specification:
- http://localhost:8081/api-docs

## API Endpoints

### Students
- `GET /api/v1/students` - List students with filters
- `POST /api/v1/students` - Create student
- `GET /api/v1/students/{id}` - Get student details
- `POST /api/v1/students/{id}/guardians` - Link guardian

### Parents
- `POST /api/v1/parents` - Create parent profile

### Staff
- `GET /api/v1/staff` - List staff
- `POST /api/v1/staff` - Create staff
- `GET /api/v1/staff/{id}/assignments` - Get staff assignments

### Academic Structure
- `GET /api/v1/academic-years` - List academic years
- `POST /api/v1/academic-years` - Create academic year
- `GET /api/v1/classes` - List classes
- `POST /api/v1/classes` - Create class
- `GET /api/v1/sections` - List sections
- `POST /api/v1/sections` - Create section
- `GET /api/v1/class-sections` - List class sections
- `POST /api/v1/class-sections` - Create class section
- `PUT /api/v1/class-sections/{id}` - Update class section

### Enrollment
- `GET /api/v1/enrollments` - Get enrollments
- `POST /api/v1/enrollments` - Create enrollment
- `PUT /api/v1/enrollments/roll-numbers` - Bulk update roll numbers
- `POST /api/v1/enrollments/promote` - Bulk promote students

### Curriculum
- `GET /api/v1/subjects` - List subjects
- `POST /api/v1/subjects` - Create subject
- `GET /api/v1/curriculum/subject-assignments` - List subject assignments
- `POST /api/v1/curriculum/subject-assignments` - Assign subject to class
- `POST /api/v1/curriculum/staff-assignments` - Assign staff to teach subject

### Classrooms
- `GET /api/v1/classrooms` - List classrooms
- `POST /api/v1/classrooms` - Create classroom

## Authoritative References

This service strictly implements:
- **Domain Model**: `docs/domain-model.md` (FROZEN)
- **OpenAPI Contract**: `docs/openapi-v1.yaml` (FROZEN v1.0)

## Current Implementation Status

✅ **Completed:**
- Complete package structure
- All controllers with proper endpoint mappings
- All service interfaces and full implementations
- All request and response DTOs
- Bean validation annotations
- Global exception handling with standardized ErrorResponse
- OpenAPI/Swagger configuration
- Constructor-based dependency injection
- JPA entities with audit columns
- Spring Data JPA repositories
- Flyway database migrations (13 migration files)
- Business logic with domain invariants
- Unit tests for AcademicStructureService and EnrollmentService
- Dockerfile for containerization
- Actuator health and metrics endpoints

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile (local/prod) | `local` |
| `SERVER_PORT` | Server port | `8081` |
| `SPRING_DATASOURCE_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/academic_core...` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | (empty) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode | `validate` |
| `LOG_LEVEL` | Log level for com.school.academic | `INFO` |
| `FLYWAY_ENABLED` | Enable Flyway migrations | `true` |

## Running Locally

### Prerequisites
- Java 17
- Maven 3.8+
- MySQL 8.x running with schema `academic_core`

### Database Setup
```bash
# Connect to MySQL and create schema
mysql -u root -p
CREATE SCHEMA IF NOT EXISTS academic_core;
```

### Running the Application
```bash
# Set environment variables
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/academic_core?useSSL=false&allowPublicKeyRetrieval=true
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=yourpassword

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package -DskipTests
java -jar target/academic-core-service-1.0.0.jar
```

### Running with Docker
```bash
# Build image
docker build -t academic-core-service .

# Run container
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/academic_core \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  academic-core-service
```

## Running Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Key Domain Invariants

1. **Identity boundary**: identity-service is authoritative for users. This service stores `userId` references only.
2. **Enrollment is authoritative for roll numbers**: Roll numbers belong to Enrollment, not Student.
3. **Roll uniqueness**: Roll number unique per (class_section_id, academic_year_id).
4. **One enrollment per year**: Student cannot have >1 active enrollment for same academicYear.
5. **Promotion is append-only**: Promotion closes old Enrollment and creates new Enrollment. Never delete.
6. **AcademicYear must be explicit**: All placement commands require academicYearId explicitly.
7. **Curriculum consistency**: Cannot assign staff to teach subject if subject not mapped to class for that year.

## Authoritative References

This service strictly implements:
- **Domain Model**: `docs/domain-model.md` (FROZEN)
- **OpenAPI Contract**: `docs/openapi-v1.yaml` (FROZEN v1.0)
- **Prompt Context**: `docs/prompts/prompt.md`

## Notes

- No authentication/authorization logic included (handled upstream by api-gateway/identity-service)
- Max JVM heap: 256MB (optimized for cost-constrained deployment)
- Flyway migrations are in `src/main/resources/db/migration/`
- Constructor-based dependency injection used throughout
- DTOs match OpenAPI schema definitions exactly



