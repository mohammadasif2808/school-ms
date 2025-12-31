# Swagger/OpenAPI - Integration & Testing Guide

## Complete Setup

### 1. Dependency Added to pom.xml

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

### 2. Configuration Class Created

**File:** `OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Identity Service API")
                .version("1.0.0")
                .description("..."))
            
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Bearer token...")));
    }
}
```

### 3. Application Configuration

**Updated application.yml:**

```yaml
springdoc:
  swagger-ui:
    enabled: ${SWAGGER_UI_ENABLED:true}
    show-models: true
    persist-authorization: true
    doc-expansion: list
    show-extensions: true
    url: /v3/api-docs
  
  api-docs:
    enabled: ${OPENAPI_DOCS_ENABLED:true}
    path: /v3/api-docs
  
  show-actuator: false
```

---

## Step-by-Step Usage Guide

### Step 1: Start the Application

```bash
cd services/identity-service
mvn clean install
mvn spring-boot:run
```

**Application started at:** `http://localhost:8080`

### Step 2: Open Swagger UI

```
http://localhost:8080/swagger-ui.html
```

**You should see:**
- All identity-service endpoints documented
- Request/response schemas
- Error codes and descriptions
- Green "Authorize" button (top right)

### Step 3: Sign In and Get JWT Token

1. **Scroll down** to find `POST /api/v1/auth/signin`
2. **Click** to expand the endpoint
3. **Click** "Try it out" button
4. **Enter request body:**
   ```json
   {
     "username": "admin",
     "password": "Admin@12345"
   }
   ```
5. **Click** "Execute" button
6. **View response:**
   ```json
   {
     "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
     "user": {
       "id": "550e8400-e29b-41d4-a716-446655440000",
       "username": "admin",
       "email": "admin@example.com"
     }
   }
   ```
7. **Copy** the `accessToken` value

### Step 4: Authorize Swagger UI with Token

1. **Scroll to top** of page
2. **Click** green "Authorize" button
3. **In popup dialog:**
   - **Locate** "Value" field under "BearerAuth"
   - **Paste** the token (example: `eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...`)
   - **Note:** Paste ONLY the token, NOT the "Bearer " prefix
4. **Click** "Authorize" button
5. **Click** "Close" button

**Result:** Token is now stored and will be automatically included in all subsequent requests.

### Step 5: Test Protected Endpoint

1. **Scroll down** to find `GET /api/v1/auth/me`
2. **Click** to expand
3. **Click** "Try it out" button
4. **Click** "Execute" button
5. **View response** - Should return current user profile with permissions

**Response example:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "admin",
  "email": "admin@example.com",
  "first_name": "Admin",
  "last_name": "User",
  "status": "ACTIVE",
  "role": "ADMIN",
  "permissions": ["ROLE_MANAGE", "PERMISSION_MANAGE"],
  "created_at": "2026-01-01T10:00:00"
}
```

### Step 6: Test Admin Endpoints

1. **Find** `POST /api/v1/admin/roles`
2. **Click** "Try it out"
3. **Enter request body:**
   ```json
   {
     "name": "Teacher",
     "description": "Teacher role with classroom management"
   }
   ```
4. **Click** "Execute"
5. **View response** - New role created with 201 status

---

## Authorization Flow Visualization

```
User Opens Swagger UI
    ↓
http://localhost:8080/swagger-ui.html

User Clicks "Authorize"
    ↓
Dialog: Enter JWT token
    ↓
User Pastes Token from /signin response
    ↓
Swagger UI Stores Token in Browser Memory
    ↓
User Clicks "Authorize"
    ↓
Swagger UI Loads Token into Session
    ↓
User Calls Protected Endpoint
    ↓
Swagger UI Adds Authorization Header
    Authorization: Bearer <token>
    ↓
API Server Validates Token
    ↓
API Server Returns Success Response
```

---

## Token Lifecycle in Swagger UI

### When Token is Set

1. User clicks "Authorize" button
2. Pastes token in "Value" field
3. Clicks "Authorize"
4. **Token is stored** in browser's Swagger UI session
5. All subsequent requests include token in Authorization header

### Token Persistence

**persist-authorization: true**
- Token persists across page reloads
- Token persists across browser tabs (same domain)
- Token cleared when:
  - User explicitly clicks "Logout" in Authorize dialog
  - Browser cookies/session storage cleared
  - User closes all tabs for the domain

### Token Expiration

- Token expires based on JWT configuration (`jwt.expiration`)
- Default: 24 hours (86400000 ms)
- When expired:
  - Swagger UI still sends token
  - API server rejects with 401 Unauthorized
  - User needs to sign in again and get new token

---

## Complete Example: Workflow

### 1. Create Role (Admin-only)

**Endpoint:** `POST /api/v1/admin/roles`

**Request:**
```json
{
  "name": "Teacher",
  "description": "Teacher role"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Teacher",
  "status": "ACTIVE",
  "permissions": [],
  "createdAt": "2026-01-01T10:05:00"
}
```

### 2. Create Permission (Admin-only)

**Endpoint:** `POST /api/v1/admin/permissions`

**Request:**
```json
{
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data"
}
```

**Response (201 Created):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "code": "STUDENT_VIEW",
  "module": "STUDENT",
  "description": "View student data",
  "createdAt": "2026-01-01T10:06:00"
}
```

### 3. Assign Permission to Role (Admin-only)

**Endpoint:** `POST /api/v1/admin/roles/{roleId}/permissions`

**URL:** `POST /api/v1/admin/roles/550e8400-e29b-41d4-a716-446655440001/permissions`

**Request:**
```json
{
  "permissionIds": ["660e8400-e29b-41d4-a716-446655440001"]
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Teacher",
  "permissions": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "code": "STUDENT_VIEW",
      "module": "STUDENT"
    }
  ]
}
```

### 4. Create User (Teacher)

**Endpoint:** `POST /api/v1/auth/signup`

**Request:**
```json
{
  "username": "jane_teacher",
  "email": "jane@school.edu",
  "password": "TeacherP@ss123",
  "first_name": "Jane",
  "last_name": "Teacher",
  "phone": "+1234567890"
}
```

**Response (201 Created):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440001",
  "username": "jane_teacher",
  "email": "jane@school.edu",
  "status": "ACTIVE"
}
```

### 5. Assign Role to User

**Endpoint:** `POST /api/v1/admin/users/{userId}/roles`

**URL:** `POST /api/v1/admin/users/770e8400-e29b-41d4-a716-446655440001/roles`

**Request:**
```json
{
  "roleIds": ["550e8400-e29b-41d4-a716-446655440001"]
}
```

**Response (200 OK):**
```json
{
  "message": "Roles assigned successfully"
}
```

### 6. Sign In as Teacher

**Endpoint:** `POST /api/v1/auth/signin`

**Request:**
```json
{
  "username": "jane_teacher",
  "password": "TeacherP@ss123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "770e8400-e29b-41d4-a716-446655440001",
    "username": "jane_teacher"
  }
}
```

### 7. Click Authorize and Paste New Token

1. Click "Authorize" button
2. Clear old admin token
3. Paste teacher token
4. Click "Authorize"

### 8. Test Teacher Permissions

**Now test endpoints that require teacher permissions:**
- Should succeed if teacher has required permission
- Should fail (403) if teacher lacks permission

---

## Error Scenarios in Swagger UI

### Scenario 1: Invalid Credentials

**Request:**
```json
{
  "username": "invalid_user",
  "password": "wrong_password"
}
```

**Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "INVALID_CREDENTIALS",
  "message": "Invalid username or password",
  "timestamp": "2026-01-01T10:10:00"
}
```

### Scenario 2: Missing Token on Protected Endpoint

**Setup:** Don't click "Authorize" (no token set)

**Endpoint:** `GET /api/v1/auth/me`

**Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Authentication failed",
  "timestamp": "2026-01-01T10:11:00"
}
```

### Scenario 3: Insufficient Permissions

**Setup:** User with STUDENT role trying to create role

**Endpoint:** `POST /api/v1/admin/roles`

**Response (403 Forbidden):**
```json
{
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Access denied - insufficient permissions",
  "timestamp": "2026-01-01T10:12:00"
}
```

### Scenario 4: Validation Error

**Request to /api/v1/auth/signup (missing email):**
```json
{
  "username": "john_doe",
  "password": "SecureP@ss123",
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+1234567890"
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "details": {
    "email": "Email is required"
  },
  "timestamp": "2026-01-01T10:13:00"
}
```

---

## Disabling Swagger UI (Production)

### Option 1: Configuration File

**application.yml:**
```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

### Option 2: Environment Variable

```bash
export SWAGGER_UI_ENABLED=false
java -jar identity-service.jar
```

### Option 3: Docker Environment

```dockerfile
FROM openjdk:17-alpine
ENV SWAGGER_UI_ENABLED=false
CMD ["java", "-jar", "identity-service.jar"]
```

---

## Swagger UI Customization

### Change Documentation

**OpenApiConfig.java:**
```java
.info(new Info()
    .title("Custom API Title")
    .version("2.0.0")
    .description("Custom description")
    .contact(new Contact()
        .name("Your Team")
        .email("support@yourcompany.com")))
```

### Change UI Appearance

**application.yml:**
```yaml
springdoc:
  swagger-ui:
    doc-expansion: full          # Show all operations expanded
    default-models-expand-depth: 2  # Expand models to depth 2
    show-extensions: false       # Hide extensions
    filter: true                 # Show filter/search field
```

---

## Testing Without Swagger UI (programmatic)

### Using curl

```bash
# 1. Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"SecureP@ss123"}' \
  | jq -r '.accessToken')

echo "Token: $TOKEN"

# 2. Use token
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

### Using Postman

1. **Sign In endpoint:** POST /api/v1/auth/signin
2. **Copy token** from response
3. **Set Authorization:** Bearer <token>
4. **Test protected endpoints**

### Using Java HttpClient

```java
HttpClient client = HttpClient.newHttpClient();

// 1. Sign in
HttpRequest signInRequest = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8080/api/v1/auth/signin"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(
        "{\"username\":\"john_doe\",\"password\":\"SecureP@ss123\"}"))
    .build();

HttpResponse<String> response = client.send(signInRequest, 
    HttpResponse.BodyHandlers.ofString());
// Extract token from response...

// 2. Use token
HttpRequest meRequest = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8080/api/v1/auth/me"))
    .header("Authorization", "Bearer " + token)
    .GET()
    .build();

client.send(meRequest, HttpResponse.BodyHandlers.ofString());
```

---

## Status

✅ Swagger UI integrated
✅ JWT authentication configured
✅ OpenAPI spec auto-generated
✅ Ready for development and testing
✅ Can be disabled in production
✅ No code changes required

