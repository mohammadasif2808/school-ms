# Implementation Documentation

This directory contains technical implementation documentation for controllers, exception handling, and API documentation.

## 📄 Available Documents

### Controller Documentation
| Document | Description |
|----------|-------------|
| [Controller Index](CONTROLLER_INDEX.md) | Controllers overview |
| [Controller Implementation](CONTROLLER_IMPLEMENTATION.md) | Controller implementation details |
| [Controller Integration Guide](CONTROLLER_INTEGRATION_GUIDE.md) | How to use controllers |
| [Controller Quick Reference](CONTROLLER_QUICK_REFERENCE.md) | Quick reference for endpoints |
| [Controller Completion Summary](CONTROLLER_COMPLETION_SUMMARY.md) | Implementation status |

### Exception Handling Documentation
| Document | Description |
|----------|-------------|
| [Global Exception Handler Implementation](GLOBAL_EXCEPTION_HANDLER_IMPLEMENTATION.md) | Exception handling details |
| [Global Exception Handler Quick Reference](GLOBAL_EXCEPTION_HANDLER_QUICK_REFERENCE.md) | Error response reference |
| [Global Exception Handler Completion Summary](GLOBAL_EXCEPTION_HANDLER_COMPLETION_SUMMARY.md) | Implementation status |

### API Documentation (Swagger/OpenAPI)
| Document | Description |
|----------|-------------|
| [Swagger OpenAPI Implementation](SWAGGER_OPENAPI_IMPLEMENTATION.md) | Swagger setup and configuration |
| [Swagger OpenAPI Integration Guide](SWAGGER_OPENAPI_INTEGRATION_GUIDE.md) | How to use Swagger UI |
| [Swagger OpenAPI Quick Reference](SWAGGER_OPENAPI_QUICK_REFERENCE.md) | Swagger quick reference |
| [Swagger OpenAPI Completion Summary](SWAGGER_OPENAPI_COMPLETION_SUMMARY.md) | Implementation status |

## 🎮 Controllers

### AuthenticationController
Handles authentication endpoints:
- `POST /api/v1/auth/signup` - User registration
- `POST /api/v1/auth/signin` - User login
- `POST /api/v1/auth/signout` - User logout
- `GET /api/v1/auth/me` - Get current user
- `POST /api/v1/auth/forgot-password` - Request password reset
- `POST /api/v1/auth/reset-password` - Complete password reset

### AdminController
Handles admin operations:
- Role management (`/api/v1/admin/roles`)
- Permission management (`/api/v1/admin/permissions`)
- Role-permission assignment
- User-role assignment

### InternalController
Internal service-to-service APIs:
- Token validation
- User verification
- Permission checks

## 🔧 Exception Handling

### GlobalExceptionHandler
Centralized exception handling using `@ControllerAdvice`:
- Validation errors → 400 Bad Request
- Authentication errors → 401 Unauthorized
- Authorization errors → 403 Forbidden
- Not found errors → 404 Not Found
- Server errors → 500 Internal Server Error

### Error Response Format
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Invalid input data",
  "details": {
    "field1": "error message",
    "field2": "error message"
  },
  "timestamp": "2026-01-01T02:00:00",
  "method": "POST",
  "path": "/api/v1/auth/signup"
}
```

## 📖 Swagger/OpenAPI

### Access Swagger UI
When service is running: http://localhost:8080/swagger-ui/index.html

### Features
- Interactive API testing
- JWT Bearer token support
- Request/response examples
- Schema documentation
- Try-it-out functionality

## 🔗 Related Documentation

- [Authentication](../features/authentication/) - Auth endpoints
- [Admin APIs](../features/admin-api/) - Admin endpoints
- [Security Architecture](../architecture/) - Security configuration

## 🚀 Quick Start

1. Check [CONTROLLER_INDEX.md](CONTROLLER_INDEX.md) for controller overview
2. Use [SWAGGER_OPENAPI_INTEGRATION_GUIDE.md](SWAGGER_OPENAPI_INTEGRATION_GUIDE.md) for API testing
3. Review [GLOBAL_EXCEPTION_HANDLER_IMPLEMENTATION.md](GLOBAL_EXCEPTION_HANDLER_IMPLEMENTATION.md) for error handling

---

[← Back to Documentation Index](../INDEX.md)

