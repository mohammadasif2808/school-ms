# Academic Core Service - Quick Start Guide

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Getting Started

### 1. Clone and Navigate
```bash
cd school-ms/services/academic-core-service
```

### 2. Verify Setup
```bash
mvn clean compile
```

Expected output: `BUILD SUCCESS`

### 3. Run the Application
```bash
mvn spring-boot:run
```

The service will start on **port 8081**.

### 4. Access Swagger UI
Open your browser and navigate to:
```
http://localhost:8081/swagger-ui.html
```

You'll see all 28 API endpoints organized by tags.

## Project Structure Overview

```
academic-core-service/
├── src/main/java/com/school/academic/
│   ├── controller/          # REST endpoints (7 controllers)
│   ├── service/             # Business logic interfaces (7 services)
│   │   └── impl/            # Service implementations (stubs)
│   ├── dto/
│   │   ├── request/         # Request DTOs (15 files)
│   │   └── response/        # Response DTOs (12 files)
│   ├── config/              # Spring configuration
│   └── exception/           # Exception handling
└── docs/
    ├── domain-model.md      # FROZEN - Domain authority
    ├── openapi-v1.yaml      # FROZEN - API contract
    └── GENERATION_SUMMARY.md
```

## Available Endpoints

### Students
- `GET    /api/v1/students` - List students
- `POST   /api/v1/students` - Create student
- `GET    /api/v1/students/{id}` - Get student
- `POST   /api/v1/students/{id}/guardians` - Link guardian

### Parents
- `POST   /api/v1/parents` - Create parent

### Staff
- `GET    /api/v1/staff` - List staff
- `POST   /api/v1/staff` - Create staff
- `GET    /api/v1/staff/{id}/assignments` - Get assignments

### Academic Structure
- `GET/POST /api/v1/academic-years`
- `GET/POST /api/v1/classes`
- `GET/POST /api/v1/sections`
- `GET/POST /api/v1/class-sections`
- `PUT     /api/v1/class-sections/{id}`

### Enrollment
- `GET    /api/v1/enrollments`
- `POST   /api/v1/enrollments`
- `PUT    /api/v1/enrollments/roll-numbers`
- `POST   /api/v1/enrollments/promote`

### Curriculum
- `GET/POST /api/v1/subjects`
- `GET/POST /api/v1/curriculum/subject-assignments`
- `POST     /api/v1/curriculum/staff-assignments`

### Classrooms
- `GET/POST /api/v1/classrooms`

## Current State

### ✅ What Works
- Application starts successfully
- Swagger UI displays all endpoints
- All endpoints are accessible
- Request validation is configured
- Exception handling is in place

### ⚠️ What Throws Exceptions
All endpoints currently throw `UnsupportedOperationException` because:
- No database is configured
- No business logic is implemented
- No entities or repositories exist

**This is by design** - it's a clean skeleton ready for implementation.

## Next Steps for Developers

### Immediate Tasks
1. **Add Database Configuration**
   - Add `spring-boot-starter-data-jpa` to `pom.xml`
   - Add database driver (PostgreSQL/MySQL)
   - Configure `application.properties` with DB credentials

2. **Create JPA Entities**
   - Create entity classes in `com.school.academic.entity` package
   - Map to domain model from `docs/domain-model.md`
   - Define relationships and constraints

3. **Create Repositories**
   - Create repository interfaces in `com.school.academic.repository`
   - Extend `JpaRepository` for each entity

4. **Implement Services**
   - Replace `throw new UnsupportedOperationException()` with actual logic
   - Use repositories to perform CRUD operations
   - Add mappers to convert between entities and DTOs

5. **Add Tests**
   - Create unit tests for services
   - Create integration tests for controllers
   - Add repository tests

### Example: Implementing Student Creation

#### 1. Create Entity
```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String admissionNumber;
    
    // ... other fields
}
```

#### 2. Create Repository
```java
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByAdmissionNumber(String admissionNumber);
}
```

#### 3. Implement Service
```java
@Service
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    
    @Override
    public StudentResponse createStudent(CreateStudentRequest request) {
        Student student = new Student();
        student.setAdmissionNumber(request.getAdmissionNumber());
        // ... map other fields
        
        Student saved = studentRepository.save(student);
        return mapToResponse(saved);
    }
}
```

## Testing the API

### Using cURL

Create a student:
```bash
curl -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "admissionNumber": "2026001",
    "firstName": "John",
    "lastName": "Doe",
    "dob": "2010-05-15",
    "gender": "Male",
    "joiningDate": "2026-01-10"
  }'
```

### Using Swagger UI
1. Navigate to `http://localhost:8081/swagger-ui.html`
2. Click on an endpoint (e.g., `POST /api/v1/students`)
3. Click "Try it out"
4. Fill in the request body
5. Click "Execute"

## Configuration

### application.properties
Located at: `src/main/resources/application.properties`

Current settings:
- Port: `8081`
- Base path: `/api/v1`
- Swagger UI: `/swagger-ui.html`

### Adding Database
Add to `application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/schooldb
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## Common Issues

### Issue: Application won't start
**Solution**: Ensure Java 17 is installed and JAVA_HOME is set correctly.

### Issue: Port 8081 already in use
**Solution**: Change port in `application.properties`:
```properties
server.port=8082
```

### Issue: Compilation errors
**Solution**: Run `mvn clean install` to download all dependencies.

## Resources

- **Domain Model**: `docs/domain-model.md` - Understand the entities and relationships
- **API Contract**: `docs/openapi-v1.yaml` - Complete API specification
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa

## Support

For questions or issues:
1. Check the domain model and OpenAPI contract first
2. Review existing code for patterns
3. Consult Spring Boot documentation
4. Follow the architectural patterns already established

## Important Notes

⚠️ **DO NOT modify**:
- `docs/domain-model.md` - This is FROZEN
- `docs/openapi-v1.yaml` - This is FROZEN
- DTO field names or types (must match OpenAPI)
- Controller endpoint paths or methods

✅ **SAFE to modify**:
- Service implementations
- Add new utility classes
- Add mappers
- Add entities and repositories
- Add tests
- Configuration files

---

**Happy Coding! 🚀**

The skeleton is ready. Now build something amazing on top of it!

