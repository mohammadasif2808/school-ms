# Swagger/OpenAPI - Quick Reference

## What Was Implemented

### 1. Maven Dependency
- springdoc-openapi-starter-webmvc-ui 2.1.0

### 2. Configuration Class
- OpenApiConfig.java — JWT Bearer security scheme definition

### 3. Application Configuration
- application.yml — Swagger UI and OpenAPI settings

---

## Quick Start

### Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### Get JWT Token

1. Find `POST /api/v1/auth/signin`
2. Click "Try it out"
3. Enter username and password
4. Click "Execute"
5. Copy `accessToken` from response

### Use Token in Swagger UI

1. Click green "Authorize" button
2. Paste token (without "Bearer " prefix)
3. Click "Authorize"
4. Now all protected endpoints include token automatically

### Test Protected Endpoint

1. Find `GET /api/v1/auth/me`
2. Click "Try it out"
3. Click "Execute"
4. View authenticated user profile

---

## Configuration

### Enable/Disable Swagger UI

**Development:**
```yaml
springdoc:
  swagger-ui:
    enabled: true
```

**Production:**
```yaml
springdoc:
  swagger-ui:
    enabled: false
```

### Environment Variable Override

```bash
SWAGGER_UI_ENABLED=false  # Disable in production
```

---

## Endpoints

| URL | Purpose |
|-----|---------|
| `/swagger-ui.html` | Interactive API documentation |
| `/v3/api-docs` | OpenAPI JSON |
| `/v3/api-docs.yaml` | OpenAPI YAML |

---

## Bearer Token Usage

**In Swagger UI:**
- Click Authorize
- Paste token (just the token, no "Bearer " prefix)
- Automatically added to all requests in Authorization header

**Example Authorization Header:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## Features

✅ Interactive API testing
✅ JWT Bearer token support
✅ Token persistence across page reloads
✅ Automatic endpoint discovery
✅ Request/response schemas
✅ Error documentation
✅ Security scheme definition
✅ Optional / disable-able

---

## Configuration Options

| Setting | Default | Purpose |
|---------|---------|---------|
| enabled | true | Enable/disable Swagger UI |
| show-models | true | Show request/response models |
| persist-authorization | true | Remember token on page reload |
| doc-expansion | list | Operation detail expansion |
| show-extensions | true | Show custom extensions |

---

## Testing without UI (curl)

### Get Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"SecureP@ss123"}' \
  | jq '.accessToken'
```

### Use Token

```bash
TOKEN="<from-above>"
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

---

## Security

✅ JWT Bearer in Authorization header
✅ Token stored in browser memory only
✅ Token expires per JWT configuration
✅ Optional in production (can disable)
✅ No credentials in URL

---

## No Code Changes

✅ Controllers — Unchanged
✅ Services — Unchanged
✅ Business logic — Unchanged
✅ Existing endpoints — Same behavior

---

## Status

✅ Swagger UI integrated
✅ JWT authentication configured
✅ OpenAPI spec reflected
✅ Zero compilation errors
✅ Ready to use

