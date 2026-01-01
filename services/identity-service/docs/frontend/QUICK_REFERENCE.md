# Quick Reference

## Essential Commands

```bash
# Start everything
docker compose up

# View logs
docker compose logs -f identity-service

# Stop (data persists)
docker compose down

# Reset database
docker compose down -v

# Check health
./health-check.sh
```

## API Endpoints

### Sign Up
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "email": "user@example.com",
    "password": "Test@Pass123!",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "+1234567890"
  }'
```

### Sign In
```bash
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "Test@Pass123!"
  }'
# Returns: { "accessToken": "eyJhbGc..." }
```

### Get Current User
```bash
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Forgot Password
```bash
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'
```

### Reset Password
```bash
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "RESET_TOKEN",
    "password": "NewPass@123!"
  }'
```

### Sign Out
```bash
curl -X POST http://localhost:8080/api/v1/auth/signout \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Admin Endpoints

### Create Role
```bash
curl -X POST http://localhost:8080/api/v1/admin/roles \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "TEACHER",
    "description": "Teacher role"
  }'
```

### List Roles
```bash
curl -X GET http://localhost:8080/api/v1/admin/roles \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### Create Permission
```bash
curl -X POST http://localhost:8080/api/v1/admin/permissions \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "USER_VIEW",
    "description": "View users",
    "module": "USER"
  }'
```

### List Permissions
```bash
curl -X GET http://localhost:8080/api/v1/admin/permissions \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### Assign Role to User
```bash
curl -X POST http://localhost:8080/api/v1/admin/users/USER_ID/roles \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roleIds": ["ROLE_ID_1", "ROLE_ID_2"]
  }'
```

## Access Points

| Resource | URL |
|----------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Health | http://localhost:8080/actuator/health |

## Default Credentials

```
MySQL Host:     localhost:3306
MySQL User:     identity_user
MySQL Password: identity_password
Database:       identity_service
```

## Troubleshooting Quick Fixes

| Issue | Fix |
|-------|-----|
| Port 8080 in use | Change port in docker-compose.yml |
| MySQL won't start | `docker compose logs mysql` |
| App won't start | `docker compose logs identity-service` |
| Want fresh database | `docker compose down -v` |
| Data not persisting | Use `docker compose down` not kill |

See **TROUBLESHOOTING.md** for detailed solutions.

