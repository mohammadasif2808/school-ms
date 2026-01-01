# identity-service

## 📚 Documentation Guide

**New to this service?** Start here:

| Role | Start Here | Next |
|------|-----------|------|
| **Frontend Developer** | [START_HERE.md](START_HERE.md) | [docs/frontend/SETUP.md](docs/frontend/SETUP.md) |
| **Backend Developer** | This README | [docs/implementation/](docs/implementation/) |
| **DevOps/Deployment** | [docs/deployment/DOCKER_GUIDE.md](docs/deployment/DOCKER_GUIDE.md) | [docs/deployment/CHECKLIST.md](docs/deployment/CHECKLIST.md) |

**All documentation:**
- 📘 [docs/frontend/](docs/frontend/) - Frontend developer guides
- 🐳 [docs/deployment/](docs/deployment/) - Docker & deployment
- 🏗️ [docs/architecture/](docs/architecture/) - Architecture decisions
- 🛠️ [docs/implementation/](docs/implementation/) - Implementation details

See [DOCS_ORGANIZATION.md](DOCS_ORGANIZATION.md) for the complete documentation structure.

---

## Purpose
The identity-service is the **central authentication, authorization, and access-control authority**
for the School Management System.

It defines:
- Who a user is
- What role they have
- What permissions they are granted
- Whether they are allowed to access a feature

This service is SECURITY-CRITICAL and must remain strictly scoped.

---

## Bounded Context
Identity, Authentication, Authorization & Access Control (RBAC)

---

## Core Responsibilities

### 1. Authentication
- Sign up (user registration)
- Sign in (username/email + password)
- Sign out (token invalidation / logout handling)
- Get current authenticated user
- Refresh JWT token (optional, future-ready)
- Forgot password (email-based)
- Reset password (secure token-based)

---

### 2. Authorization (RBAC + Permissions)

#### Roles
- Admin
- Teacher
- Student
- Guardian
- Accountant
- Librarian
- (Extensible)

#### Permissions
Permissions are **fine-grained capabilities**, grouped by functional area.

Examples:
- USER_VIEW
- USER_CREATE
- STUDENT_EDIT
- ATTENDANCE_MARK
- EXAM_CREATE
- FEE_COLLECT

Roles are assigned **multiple permissions**.

A user gains permissions through:
- Role assignment
- Super Admin override

---

### 3. User Management
- Create user
- Update user details
- Activate / deactivate user
- Assign roles
- Assign permissions (indirectly via roles)
- Upload avatar (metadata only, no media storage)

---

## Data Ownership (SYSTEM OF RECORD)

This service OWNS the following entities:
- Users
- Roles
- Permissions
- Role–Permission mappings
- User–Role mappings

No other service may store or modify this data.

---

## Explicitly Forbidden (NON-NEGOTIABLE)

This service MUST NOT:
- Store student, teacher, guardian profiles
- Contain academic logic
- Contain attendance logic
- Contain exam or result logic
- Contain fee or payment logic
- Call other business microservices
- Access any database schema except its own

---

## User Entity – Required Columns

Every user MUST have:

### Core Identity Fields
- id (UUID)
- username
- first_name
- last_name
- email
- phone
- password_hash
- avatar_url (optional)
- is_super_admin (boolean)
- status (ACTIVE / INACTIVE / BLOCKED)

### Audit & System Fields
- created_by
- created_at
- last_modified_by
- last_modified_at
- inserted_at
- updated_at
- is_deleted (soft delete)

---

## Role Entity – Required Columns

- id
- name
- description
- status
- created_by
- created_at
- updated_at

---

## Permission Entity – Required Columns

- id
- code (e.g. STUDENT_VIEW)
- module (e.g. STUDENT, ATTENDANCE)
- description
- created_at

---

## API Design Rules

### Public APIs (`/api/v1/auth/**`)
These are called by frontend / gateway.

Required endpoints:
- POST `/api/v1/auth/signup`
- POST `/api/v1/auth/signin`
- POST `/api/v1/auth/signout`
- GET  `/api/v1/auth/me`
- POST `/api/v1/auth/forgot-password`
- POST `/api/v1/auth/reset-password`

---

### Admin APIs (`/api/v1/admin/**`)
Role & permission management.

Examples:
- POST `/api/v1/admin/roles`
- GET  `/api/v1/admin/roles`
- POST `/api/v1/admin/permissions`
- ASSIGN permissions to role
- ASSIGN roles to user

---

### Internal APIs (`/internal/**`)
Used by API Gateway or trusted services only.
Minimal and read-only where possible.

---

## JWT Contract (CRITICAL)

JWT tokens issued by this service MUST include:

```json
{
  "userId": "uuid",
  "username": "string",
  "role": "ADMIN | TEACHER | STUDENT | GUARDIAN | ...",
  "permissions": ["STUDENT_VIEW", "ATTENDANCE_MARK"],
  "tenantId": "school-id",
  "iat": "<issued-at>",
  "exp": "<expiry>"
}
```

---

## 📚 Documentation

Comprehensive documentation is available in the `docs/` directory:

- **[📖 Documentation Index](docs/INDEX.md)** - Complete documentation navigation
- **[🏗️ Architecture](docs/architecture/)** - Security configuration and service design
- **[🔐 Features](docs/features/)** - Authentication, Authorization, JWT, Password Reset, Admin APIs
- **[💻 Implementation](docs/implementation/)** - Controllers, Exception Handling, Swagger/OpenAPI

### Quick Links
- [Authentication Guide](docs/features/authentication/AUTHSERVICE_INDEX.md)
- [RBAC Implementation](docs/features/authorization/RBAC_IMPLEMENTATION.md)
- [JWT Quick Reference](docs/features/jwt/JWT_QUICK_REFERENCE.md)
- [Admin APIs Quick Reference](docs/features/admin-api/ADMIN_APIS_QUICK_REFERENCE.md)
- [Swagger UI](http://localhost:8080/swagger-ui/index.html) (when running)

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Running the Service
```bash
# Set environment variables
export DB_URL=jdbc:mysql://localhost:3306/identity_service
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key

# Run the service
mvn spring-boot:run
```

### Access Swagger UI
Once running, visit: http://localhost:8080/swagger-ui/index.html

---

## 🚀 Frontend Developer Setup

### Requirements
- Docker
- Docker Compose
- No Java, Maven, or MySQL installation required

### Quick Start

1. **Navigate to identity-service directory:**
   ```bash
   cd services/identity-service
   ```

2. **Start the backend with one command:**
   ```bash
   docker compose up
   ```

   This will:
   - Build the Spring Boot application
   - Start MySQL database
   - Create database schema automatically
   - Start identity-service on port 8080
   - Set up health checks

3. **Wait for startup** (first run takes ~2-3 minutes):
   ```
   identity-service | 2026-01-01 12:00:00 - Started IdentityServiceApplication in X.XXX seconds
   ```

### Access the Backend

- **API Base URL:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Health Check:** `http://localhost:8080/actuator/health`

### Default Credentials

| Property | Value |
|----------|-------|
| **MySQL Host** | localhost |
| **MySQL Port** | 3306 |
| **MySQL User** | identity_user |
| **MySQL Password** | identity_password |
| **Database** | identity_service |
| **JWT Secret** | `your-super-secret-key-...` (dev-only) |

### Important API Endpoints

#### Authentication
- **Sign Up:** `POST /api/v1/auth/signup`
- **Sign In:** `POST /api/v1/auth/signin`
- **Sign Out:** `POST /api/v1/auth/signout`
- **Get Current User:** `GET /api/v1/auth/me`
- **Forgot Password:** `POST /api/v1/auth/forgot-password`
- **Reset Password:** `POST /api/v1/auth/reset-password`

#### Admin (Protected)
- **Create Role:** `POST /api/v1/admin/roles`
- **List Roles:** `GET /api/v1/admin/roles`
- **Create Permission:** `POST /api/v1/admin/permissions`
- **List Permissions:** `GET /api/v1/admin/permissions`
- **Assign Role to User:** `POST /api/v1/admin/users/{userId}/roles`

### Stopping and Resetting

**Stop the services:**
```bash
docker compose down
```

**Stop and remove all data (clean reset):**
```bash
docker compose down -v
```

### Troubleshooting

**Port 8080 already in use:**
```bash
# Stop other services or change the port in docker-compose.yml
docker compose down
```

**MySQL connection failed:**
```bash
# Check MySQL is healthy
docker compose logs mysql

# Restart MySQL
docker compose restart mysql
```

**Application won't start:**
```bash
# View application logs
docker compose logs identity-service

# Rebuild application
docker compose build --no-cache identity-service
docker compose up
```

### Development Notes

- **Database changes:** Applied automatically on startup (Hibernate auto-update)
- **JWT tokens:** Generated at sign-in, valid for 24 hours
- **First user created:** Automatically becomes super admin (for admin API access)
- **Swagger UI:** Available in development, can be disabled via `SWAGGER_UI_ENABLED=false`

### Environment Variables (Customizable)

Edit `docker-compose.yml` to change:
- `JWT_SECRET` - Change in production!
- `JWT_EXPIRATION` - Token lifetime (milliseconds)
- `LOG_LEVEL` - Logging verbosity (INFO, DEBUG, ERROR)
- `DB_PASSWORD` - MySQL password

---

## 📄 License
Apache 2.0
