# identity-service

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

## 📄 License
Apache 2.0
