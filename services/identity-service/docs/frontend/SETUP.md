# Frontend Developer Guide

## Quick Start (60 seconds)

```bash
cd services/identity-service
docker compose up
# Wait for: "Started IdentityServiceApplication"
# Then visit: http://localhost:8080/swagger-ui/index.html
```

## What You Get

- ✅ MySQL database (auto-initialized)
- ✅ Identity service API (running on :8080)
- ✅ Swagger UI (interactive API docs)
- ✅ JWT authentication (fully configured)
- ✅ Health monitoring (auto-healing)

## Documentation

1. **This file** - Overview
2. **QUICK_REFERENCE.md** - Commands & endpoints
3. **TROUBLESHOOTING.md** - Common issues & solutions

## API Base URL

```
http://localhost:8080
```

## Common Commands

```bash
# Start backend
docker compose up

# View logs (real-time)
docker compose logs -f identity-service

# Stop backend (data persists)
docker compose down

# Reset database (fresh start)
docker compose down -v

# Verify health
./health-check.sh  (or health-check.bat on Windows)
```

## Main Endpoints

### Authentication (Public)
- `POST /api/v1/auth/signup` - Register
- `POST /api/v1/auth/signin` - Login
- `POST /api/v1/auth/forgot-password` - Password reset request
- `POST /api/v1/auth/reset-password` - Complete password reset

### Protected (Requires JWT)
- `GET /api/v1/auth/me` - Get current user
- `POST /api/v1/auth/signout` - Logout

### Admin (Requires permission)
- `GET /api/v1/admin/roles` - List roles
- `POST /api/v1/admin/roles` - Create role
- `GET /api/v1/admin/permissions` - List permissions
- `POST /api/v1/admin/permissions` - Create permission

See **QUICK_REFERENCE.md** for full list and examples.

## API Documentation

**Interactive Swagger UI:** http://localhost:8080/swagger-ui/index.html

Use Swagger to:
- Browse all endpoints
- View request/response schemas
- Try endpoints directly
- Copy curl commands

## Default Credentials

```
MySQL:
  Host: localhost:3306
  User: identity_user
  Pass: identity_password
  DB: identity_service
```

## Password Requirements

Passwords must contain:
- ✅ Min 8 characters
- ✅ At least 1 UPPERCASE letter
- ✅ At least 1 lowercase letter
- ✅ At least 1 digit
- ✅ At least 1 special character (@$!%*?&)

**Valid:** `Test@Pass123!`  
**Invalid:** `password`

## First Test - Sign Up

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@Pass123!",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "+1234567890"
  }'
```

## Authentication Flow

1. **Sign Up** (create account)
   ```
   POST /api/v1/auth/signup
   ```

2. **Sign In** (get JWT token)
   ```
   POST /api/v1/auth/signin
   → Response: { "accessToken": "eyJhbGc..." }
   ```

3. **Use Token** (for protected endpoints)
   ```
   GET /api/v1/auth/me
   Header: Authorization: Bearer eyJhbGc...
   ```

## Need Help?

- **Quick command?** → QUICK_REFERENCE.md
- **Troubleshooting?** → TROUBLESHOOTING.md
- **API docs?** → Swagger UI
- **Verify setup?** → health-check.sh/bat

## Next Steps

1. Start backend: `docker compose up`
2. Open Swagger UI: http://localhost:8080/swagger-ui/index.html
3. Try sign-up endpoint
4. Get JWT token from sign-in
5. Start building your frontend!

---

See parent directory for other documentation.

