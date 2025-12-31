# Identity Service - Project Skeleton Summary

## Overview
Spring Boot 3 microservice for authentication, authorization, and access control.
- **Java Version:** 17
- **Framework:** Spring Boot 3.2.0
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **Authentication:** JWT (JJWT 0.12.3)
- **Container:** Docker with 256MB JVM heap limit

---

## Project Structure

```
identity-service/
├── pom.xml                                    # Maven configuration (Java 17, Spring Boot 3)
├── Dockerfile                                 # Multi-stage Docker build
├── .gitignore                                 # Git ignore patterns
├── README.md                                  # Existing service documentation
├── src/
│   ├── main/
│   │   ├── java/com/school/identity/
│   │   │   ├── IdentityServiceApplication.java     # @SpringBootApplication entry point
│   │   │   ├── config/                             # Configuration classes (PLACEHOLDER)
│   │   │   ├── controller/
│   │   │   │   ├── AuthenticationController.java   # POST/GET /api/v1/auth/**
│   │   │   │   ├── AdminController.java            # POST /api/v1/admin/**
│   │   │   │   └── InternalController.java         # GET /internal/**
│   │   │   ├── domain/                             # JPA entities
│   │   │   │   ├── User.java                       # User entity with all required fields
│   │   │   │   ├── Role.java                       # Role entity (ADMIN, TEACHER, etc.)
│   │   │   │   ├── Permission.java                 # Permission entity (fine-grained)
│   │   │   │   ├── UserStatus.java                 # Enum: ACTIVE, INACTIVE, BLOCKED
│   │   │   │   └── RoleStatus.java                 # Enum: ACTIVE, INACTIVE
│   │   │   ├── dto/                                # Data Transfer Objects
│   │   │   │   ├── SignUpRequest.java
│   │   │   │   ├── SignUpResponse.java
│   │   │   │   ├── SignInRequest.java
│   │   │   │   ├── SignInResponse.java
│   │   │   │   ├── ForgotPasswordRequest.java
│   │   │   │   ├── ResetPasswordRequest.java
│   │   │   │   ├── CurrentUserResponse.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── repository/                         # Spring Data JPA repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── PermissionRepository.java
│   │   │   ├── security/                           # Security & JWT utilities (PLACEHOLDER)
│   │   │   └── service/                            # Business logic services
│   │   │       ├── AuthenticationService.java      # Authentication logic
│   │   │       ├── UserService.java                # User management
│   │   │       └── RoleService.java                # Role & permission management
│   │   └── resources/
│   │       └── application.yml                     # Spring Boot configuration with env vars
│   └── test/                                       # Test directory (empty)
└── target/                                        # Maven build output (generated)
```

---

## Key Features (Current State)

### ✅ Implemented
- **Maven POM:** Java 17, Spring Boot 3.2.0, Spring Security, Spring Data JPA, JJWT
- **Entities:** User, Role, Permission with all required columns per README.md
- **Repositories:** UserRepository, RoleRepository, PermissionRepository
- **DTOs:** All request/response DTOs for 6 auth endpoints + error handling
- **Controllers:** AuthenticationController, AdminController, InternalController (empty stubs)
- **Services:** AuthenticationService, UserService, RoleService (empty stubs)
- **Configuration:** application.yml with environment variable placeholders
- **Docker:** Multi-stage build with 256MB JVM heap limit
- **Validation:** Jakarta validation annotations on all DTOs

### ⏳ Not Yet Implemented (Next: WORKFLOW 2)
- Authentication logic (password hashing, JWT generation)
- Sign up / Sign in / Sign out / Forgot password / Reset password endpoints
- Role and permission assignment logic
- JWT token validation & extraction
- Security filters and interceptors
- Database migrations/schema
- Unit & integration tests

---

## Environment Variables

All configurable via environment variables in `application.yml`:

| Variable | Default | Purpose |
|----------|---------|---------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/identity_service` | PostgreSQL connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `password` | Database password |
| `HIBERNATE_DDL_AUTO` | `validate` | Hibernate DDL strategy |
| `SHOW_SQL` | `false` | Enable SQL logging |
| `SERVER_PORT` | `8080` | Application port |
| `LOG_LEVEL` | `INFO` | Root log level |
| `LOG_LEVEL_APP` | `DEBUG` | Application package log level |
| `JWT_SECRET` | `your-secret-key-change-in-production` | JWT signing secret |
| `JWT_EXPIRATION` | `86400000` | Token expiration (ms, 24h) |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token expiration (ms, 7d) |
| `TENANT_ID` | `default` | School/tenant identifier |

---

## Validation Rules (Already in DTOs)

### Username
- 3-50 characters
- Pattern: `^[a-zA-Z0-9_-]+$`
- Unique

### Email
- Valid email format (RFC 5322)
- Unique

### Password
- Minimum 8 characters
- Complex password validation to be added in service layer

### Phone
- Required
- Format validation to be added in service layer

### First/Last Name
- 1-100 characters
- Required

---

## Code Quality Checklist

✅ No business logic present (structure only)  
✅ No cross-domain code  
✅ Correct package naming: `com.school.identity.*`  
✅ Constructor injection ready (no field injection)  
✅ No Lombok @Data used  
✅ Validation annotations on DTOs  
✅ JPA entities with proper mappings  
✅ One bounded context (Identity, Authentication, Authorization)  
✅ No calls to other business services  
✅ Dockerfile with 256MB JVM limit  
✅ Application configuration via environment variables  

---

## Next Steps (WORKFLOW 2)

Ready to add business logic for:

1. **Sign Up** - User registration with password hashing
2. **Sign In** - JWT token generation with required claims
3. **Sign Out** - Token invalidation mechanism
4. **Get Current User** - Extract JWT claims and fetch user details
5. **Forgot Password** - Email-based reset token generation
6. **Reset Password** - Secure token validation and password update
7. **Role Assignment** - Assign roles to users
8. **Permission Management** - Create/assign permissions to roles

---

## Build & Run

```bash
# Build
mvn clean package

# Run
java -Xmx256m -jar target/identity-service-1.0.0.jar

# Docker Build
docker build -t identity-service:latest .

# Docker Run
docker run -e DB_URL=jdbc:postgresql://postgres:5432/identity_service \
           -e DB_USERNAME=postgres \
           -e DB_PASSWORD=secure_password \
           -e JWT_SECRET=your_secret_key \
           -p 8080:8080 \
           identity-service:latest
```

---

## Architecture Compliance

✅ Microservice architecture  
✅ Java 17, Spring Boot 3.x, Maven  
✅ REST APIs with DTOs  
✅ Constructor injection (ready)  
✅ Single bounded context  
✅ No shared database tables  
✅ No cross-service calls (internal APIs ready)  
✅ Stateless JWT authentication  
✅ Max 256MB JVM heap  
✅ One entity per service (User, Role, Permission)  

---

## Git Commit Ready

```
chore: initialize identity-service skeleton

- Spring Boot 3.2.0 with Java 17
- Maven build configuration
- Domain entities: User, Role, Permission
- Repository layer with JPA
- DTO layer with validation annotations
- Empty service & controller stubs
- Docker configuration with 256MB JVM limit
- application.yml with environment variable placeholders
- No business logic implemented
```

