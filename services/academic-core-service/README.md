# Academic Core Service

## Overview
The Academic Core Service is the source of truth for institutional hierarchy and people management within the school management system. It manages students, staff, parents, academic structure (years, classes, sections), enrollments, curriculum, and classrooms.

## Status
**SKELETON PHASE** - Compilable structure with no business logic implementation.

## Technology Stack
- Java 17
- Spring Boot 3.2.1
- Spring Web MVC
- Bean Validation
- SpringDoc OpenAPI (Swagger)
- **Pure Java** (No Lombok - explicit getters/setters)

## Project Structure
```
com.school.academic
├── AcademicCoreServiceApplication.java
├── config/
│   └── OpenApiConfig.java
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
- All service interfaces and implementations (stubs)
- All request and response DTOs
- Bean validation annotations
- Global exception handling
- OpenAPI/Swagger configuration
- Constructor-based dependency injection

⏳ **Not Yet Implemented:**
- Business logic (methods throw `UnsupportedOperationException`)
- JPA entities and repositories
- Database configuration
- Data persistence
- Security configuration
- Unit and integration tests

## Next Steps

The following phases should be implemented in order:

1. **Domain Layer**: Create JPA entities matching the domain model
2. **Persistence Layer**: Create repositories and database configuration
3. **Business Logic**: Implement service layer methods
4. **Testing**: Add unit and integration tests
5. **Security**: Integrate with identity-service for authentication context

## Notes

- No authentication/authorization logic is included (handled upstream by identity-service)
- All service methods currently throw `UnsupportedOperationException`
- The skeleton is designed to compile cleanly with `mvn clean compile`
- Constructor-based dependency injection is used throughout
- DTOs match OpenAPI schema definitions exactly

