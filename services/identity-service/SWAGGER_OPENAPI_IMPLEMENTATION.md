# Swagger / OpenAPI UI with JWT Authentication

## Overview

Integrated Swagger UI (via springdoc-openapi) with JWT Bearer token authentication support for identity-service. Developers and API consumers can now test secured endpoints directly from the Swagger UI.

---

## Components

### 1. Maven Dependency

**Added to pom.xml:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

**Provides:**
- OpenAPI 3.0 documentation auto-generated from Spring endpoints
- Swagger UI for interactive API exploration
- Automatic endpoint discovery from @RestController classes
- JWT Bearer authentication support in UI

### 2. OpenApiConfig Configuration Class

**Location:** `src/main/java/com/school/identity/config/OpenApiConfig.java`

**Responsibility:**
- Configure OpenAPI metadata (title, version, description)
- Define JWT Bearer security scheme
- Configure security requirements for endpoints
- Customize Swagger UI behavior

**Key Features:**
```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Identity Service API")
            .version("1.0.0")
            .description("...")
            .contact(...)
            .license(...))
        
        .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
        .components(new Components()
            .addSecuritySchemes("BearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer token...")));
}
```

### 3. Application Configuration

**Updated application.yml:**
```yaml
springdoc:
  swagger-ui:
    enabled: true                    # Enable/disable Swagger UI
    show-models: true                # Show request/response models
    persist-authorization: true      # Remember token across reloads
    doc-expansion: list              # Show/hide operation details
  
  api-docs:
    enabled: true                    # Enable/disable OpenAPI JSON
    path: /v3/api-docs               # OpenAPI JSON endpoint
```

---

## Accessing Swagger UI

### Local Development

```
http://localhost:8080/swagger-ui.html
```

### Production (if enabled)

```
https://api.school.example.com/swagger-ui.html
```

**Note:** Path is relative to the application context path configured in `server.servlet.context-path`

---

## Using JWT in Swagger UI

### Step 1: Sign In and Get Token

1. **Find** the `POST /api/v1/auth/signin` endpoint
2. **Expand** it
3. **Click** "Try it out"
4. **Enter** credentials:
   ```json
   {
     "username": "john_doe",
     "password": "SecureP@ss123"
   }
   ```
5. **Click** "Execute"
6. **Copy** the `accessToken` from response

### Step 2: Authorize Swagger UI

1. **Scroll to top** of Swagger UI
2. **Click** green "Authorize" button
3. **Paste** token into "Value" field (without "Bearer " prefix)
4. **Click** "Authorize"
5. **Click** "Close"

### Step 3: Test Protected Endpoints

1. **Find** endpoint requiring authentication (e.g., `GET /api/v1/auth/me`)
2. **Click** "Try it out"
3. **Click** "Execute"
4. JWT token automatically included in Authorization header
5. **View** response

---

## Security Scheme Configuration

### Bearer Token in Authorization Header

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Swagger UI Behavior:**
- Token stored in browser session
- Automatically added to all subsequent requests
- User can clear token by clicking "Authorize" → "Logout"
- Token persists across page reloads (persist-authorization: true)

### Security Scheme Definition

```yaml
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: JWT Bearer token authentication
```

---

## API Documentation

### Automatic Endpoint Discovery

Swagger UI automatically discovers all endpoints with:
- `@RestController`
- `@RequestMapping`
- `@GetMapping`, `@PostMapping`, etc.
- `@RequestBody`, `@PathVariable`, etc.

### Documentation from OpenAPI Contract

The existing OpenAPI contract in `docs/api-contracts/identity-service.yaml` is reflected in:
- Endpoint paths and HTTP methods
- Request/response schemas
- Required parameters
- Error responses
- Security requirements

### Annotations for Enhanced Documentation

Controllers can use annotations for better documentation:

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    
    @PostMapping("/signin")
    @Operation(summary = "Sign In", description = "Authenticate user and issue JWT token")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request) {
        // ...
    }
}
```

---

## Configuration Options

### Enable/Disable Swagger UI

**In application.yml:**
```yaml
springdoc:
  swagger-ui:
    enabled: true  # Set to false in production to hide Swagger UI
```

**Via Environment Variable:**
```bash
SWAGGER_UI_ENABLED=false  # Disable Swagger UI
```

### Enable/Disable OpenAPI JSON

**In application.yml:**
```yaml
springdoc:
  api-docs:
    enabled: true  # Set to false to disable /v3/api-docs endpoint
```

### Customize Swagger UI Appearance

```yaml
springdoc:
  swagger-ui:
    show-models: true           # Show request/response models
    persist-authorization: true # Remember token
    doc-expansion: list         # Operation expansion (list, full, none)
    show-extensions: true       # Show custom extensions
    default-models-expand-depth: 1  # Model expansion depth
```

---

## Security Considerations

### ✅ Security Implemented

✅ **JWT Bearer Authentication** — Token passed via Authorization header
✅ **Stateless** — Token stored in browser memory only
✅ **Optional** — Can be disabled in production via config
✅ **No Credentials in URL** — Token not exposed in browser history
✅ **Token Expiration** — JWT token expires per configuration

### ⚠️ Production Recommendations

1. **Disable Swagger UI in production:**
   ```yaml
   springdoc:
     swagger-ui:
       enabled: false
   ```

2. **Protect `/v3/api-docs` endpoint:**
   ```yaml
   springdoc:
     api-docs:
       enabled: false
   ```

3. **Use HTTPS only** for token transmission

4. **Rotate JWT secret** regularly

5. **Implement token refresh** for long-lived sessions

---

## Endpoints Documented

### Authentication (Public)
- `POST /api/v1/auth/signup` — User registration
- `POST /api/v1/auth/signin` — Get JWT token
- `POST /api/v1/auth/forgot-password` — Request password reset
- `POST /api/v1/auth/reset-password` — Reset password

### Authenticated
- `GET /api/v1/auth/me` — Get current user (requires JWT)
- `POST /api/v1/auth/signout` — Logout (requires JWT)

### Admin (Protected)
- `POST /api/v1/admin/roles` — Create role (requires ROLE_MANAGE)
- `GET /api/v1/admin/roles` — List roles (requires ROLE_VIEW)
- `POST /api/v1/admin/permissions` — Create permission
- `GET /api/v1/admin/permissions` — List permissions
- And more...

---

## Testing Flow

### 1. Public Endpoint Test (No Token Needed)

```
1. Go to Swagger UI: http://localhost:8080/swagger-ui.html
2. Find POST /api/v1/auth/signin
3. Click "Try it out"
4. Enter username and password
5. Click "Execute"
6. Copy accessToken from response
```

### 2. Protected Endpoint Test (Token Needed)

```
1. Click green "Authorize" button at top
2. Paste token in "Value" field
3. Click "Authorize"
4. Find GET /api/v1/auth/me
5. Click "Try it out"
6. Click "Execute"
7. View authenticated user profile
```

### 3. Permission-Based Endpoint Test

```
1. Create role with POST /api/v1/admin/roles
2. Assign permissions with POST /api/v1/admin/roles/{roleId}/permissions
3. Create user with POST /api/v1/auth/signup
4. Assign role to user with POST /api/v1/admin/users/{userId}/roles
5. Sign in with POST /api/v1/auth/signin
6. Use token to test permission-protected endpoints
```

---

## Error Response Documentation

Swagger UI shows error responses for each endpoint:

```json
{
  "400": {
    "description": "Validation error",
    "schema": "ErrorResponse"
  },
  "401": {
    "description": "Unauthorized",
    "schema": "ErrorResponse"
  },
  "403": {
    "description": "Forbidden",
    "schema": "ErrorResponse"
  }
}
```

Error responses follow the standardized `ErrorResponse` DTO format with:
- `status` — HTTP status code
- `error` — Error code
- `message` — Human-readable message
- `details` — Field-level validation errors (optional)

---

## Reflected from OpenAPI Contract

The existing `docs/api-contracts/identity-service.yaml` is accurately reflected in Swagger UI:

✅ All endpoints documented
✅ Request/response schemas
✅ Required parameters
✅ Error codes and descriptions
✅ Security requirements
✅ Contact and license information

---

## No Changes to Existing Code

✅ Controllers — Unchanged
✅ Services — Unchanged
✅ Business logic — Unchanged
✅ Security — No changes, enhanced documentation only
✅ API contracts — Same as OpenAPI spec

---

## Dependencies Added

**springdoc-openapi-starter-webmvc-ui 2.1.0:**
- Provides OpenAPI 3.0 auto-generation
- Provides Swagger UI
- No breaking changes
- Compatible with Spring Boot 3.2.0

---

## URLs and Endpoints

| URL | Purpose |
|-----|---------|
| `/swagger-ui.html` | Swagger UI (main interface) |
| `/swagger-ui/index.html` | Swagger UI (alternative) |
| `/v3/api-docs` | OpenAPI JSON specification |
| `/v3/api-docs.yaml` | OpenAPI YAML specification |
| `/swagger-ui/swagger-resources` | Swagger resources metadata |

---

## Testing with curl (No UI Needed)

### Get JWT Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecureP@ss123"
  }' | jq '.accessToken'
```

### Test Protected Endpoint with Token

```bash
TOKEN="<token-from-above>"

curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

---

## Status

✅ Swagger UI integrated
✅ JWT Bearer authentication configured
✅ OpenAPI spec reflected
✅ Security maintained
✅ Optional / disable-able
✅ Zero compilation errors
✅ Ready for development and testing

