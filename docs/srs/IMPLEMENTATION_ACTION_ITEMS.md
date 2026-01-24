# Implementation Action Items - Based on SRS v1.0 Review

## Priority 1 (CRITICAL - Blocking Other Services)

### ✋ ACTION: Fix Identity-Service Schema Definitions
**Status:** 🔴 NOT STARTED  
**Complexity:** LOW  
**Est. Time:** 30 minutes  
**Blocking:** All remaining services

**Changes Required:**
1. Update all `@Table` annotations in identity-service to include schema:
   ```java
   // BEFORE:
   @Table(name = "users")
   
   // AFTER:
   @Table(name = "users", schema = "identity_service")
   ```
   
   **Files to Update:**
   - `com/school/identity/domain/User.java`
   - `com/school/identity/domain/Role.java`
   - `com/school/identity/domain/Permission.java`
   - `com/school/identity/domain/PasswordResetToken.java`

2. Update application-local.yml to use proper schema:
   ```yaml
   # Change from:
   url: jdbc:mysql://localhost:3306/identity_service
   
   # To explicit schema approach (if using Flyway):
   spring.flyway.schemas: identity_service
   ```

**Testing:** Verify tables are created in `identity_service` schema, not root schema

---

### 🔧 ACTION: Implement Flyway Migrations for Identity-Service
**Status:** 🔴 NOT STARTED  
**Complexity:** MEDIUM  
**Est. Time:** 1-2 hours  
**Blocking:** Operational readiness for production

**Changes Required:**
1. Create `src/main/resources/db/migration/` directory
2. Create baseline migration scripts:
   - V0001__create_identity_schema.sql
   - V0002__create_users_table.sql
   - V0003__create_roles_table.sql
   - V0004__create_permissions_table.sql
   - V0005__create_user_roles_table.sql
   - V0006__create_role_permissions_table.sql
   - V0007__create_password_reset_tokens_table.sql

3. Update pom.xml (add Flyway dependency):
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-mysql</artifactId>
   </dependency>
   ```

4. Update application.yml:
   ```yaml
   spring:
     flyway:
       enabled: true
       baseline-on-migrate: true
       locations: classpath:db/migration
       schemas: identity_service
   ```

5. Update application-local.yml:
   ```yaml
   spring:
     jpa:
       hibernate:
         ddl-auto: validate  # Changed from: update
   ```

**Testing:** Run with clean database, verify all tables created via Flyway

**Reference:** See academic-core-service/src/main/resources/db/migration/ for pattern

---

### 🏗️ ACTION: Implement API Gateway
**Status:** 🔴 NOT STARTED  
**Complexity:** MEDIUM-HIGH  
**Est. Time:** 4-6 hours  
**Blocking:** Testing inter-service communication

**Requirements:**
1. Create Spring Boot application structure
   ```
   api-gateway/
   ├── src/main/
   │   ├── java/com/school/gateway/
   │   │   ├── ApiGatewayApplication.java
   │   │   ├── config/
   │   │   │   ├── SecurityConfig.java
   │   │   │   ├── WebConfig.java
   │   │   │   └── GatewayProperties.java
   │   │   ├── filter/
   │   │   │   ├── JwtAuthenticationFilter.java
   │   │   │   └── LoggingFilter.java
   │   │   ├── service/
   │   │   │   └── TokenValidationService.java
   │   │   └── exception/
   │   │       └── GlobalExceptionHandler.java
   │   └── resources/
   │       ├── application.yml
   │       ├── application-local.yml
   │       └── application-prod.yml
   ```

2. Implement Core Features:
   - JWT token validation via identity-service `/internal/validate-token` endpoint
   - HTTP routing to downstream services
   - Request logging (method, path, timestamp)
   - Response logging (status code, elapsed time)
   - Error handling with proper HTTP codes

3. Configure Spring Cloud Gateway or use RestTemplate-based approach

4. Implement circuit breaker (Resilience4j) for downstream calls

**Reference Endpoints:**
- GET `/health` - Gateway health status
- POST `/api/v1/auth/**` → route to identity-service:8080
- POST `/api/v1/students/**` → route to academic-core-service:8081
- GET `/actuator/metrics` - Prometheus metrics

---

## Priority 2 (HIGH - Before Next Services)

### 📋 ACTION: Standardize Service Template
**Status:** 🔴 NOT STARTED  
**Complexity:** LOW  
**Est. Time:** 1 hour

**Deliverable:** Create `docs/templates/SERVICE_TEMPLATE.md`
- Use academic-core-service as reference implementation
- Document:
  - pom.xml dependencies (core list)
  - application.yml structure
  - Flyway migration structure
  - Package organization
  - Entity schema annotation pattern

**Use For:**
- attendance-service
- assessment-service
- finance-service
- notification-service

---

### ✅ ACTION: Complete Academic-Core-Service API Contracts
**Status:** 🟡 PARTIAL  
**Complexity:** LOW  
**Est. Time:** 1 hour

**File:** `docs/api-contracts/academic-core-service.yaml`

**Current Status:** Skeleton only (7 lines)  
**Required:** Full OpenAPI 3.0 specification with:
- All endpoints for StudentController
- All endpoints for StaffController
- All endpoints for ClassroomController
- All endpoints for EnrollmentController
- Request/response schemas
- Error responses

**Reference:** See identity-service.yaml for format

---

## Priority 3 (MEDIUM - Best Practices)

### 📚 ACTION: Document Service Responsibility Matrix
**Status:** ✅ EXISTS  
**File:** `docs/architecture/service-responsibility-matrix.md`

**Verify:** That it matches actual implementations:
- identity-service: ✅
- academic-core-service: ✅
- api-gateway: ⚠️ (needs update)
- attendance-service: ⏳ (not started)
- assessment-service: ⏳ (not started)
- finance-service: ⏳ (not started)
- notification-service: ⏳ (not started)

---

### 🔐 ACTION: Security Audit
**Status:** 🔴 NOT STARTED  
**Complexity:** MEDIUM  
**Est. Time:** 2-3 hours

**Verify:**
- [ ] JWT secret uses strong random value (not default)
- [ ] Actuator endpoints properly restricted in production
- [ ] No sensitive data (passwords, tokens) in logs
- [ ] HTTPS enforced in production configuration
- [ ] CORS configured properly for API Gateway
- [ ] SQL injection prevention (using parameterized queries)
- [ ] Rate limiting planned for API Gateway

---

## Priority 4 (LOW - Future Enhancement)

### 📊 ACTION: Add Health Check Indicators
**Components to Monitor:**
- [ ] Database connectivity
- [ ] Service-to-service connectivity
- [ ] Downstream service availability
- [ ] Disk space
- [ ] Memory usage

**Implementation:** Custom HealthIndicator in Spring Boot Actuator

---

### 📈 ACTION: Metrics Collection Plan
**Metrics to Add:**
- [ ] API latency (p50, p95, p99)
- [ ] Error rate by endpoint
- [ ] Request throughput
- [ ] Database query performance
- [ ] Authentication failures
- [ ] Service availability

---

## Timeline Summary

### Week 1
- Priority 1.1: Fix Identity-Service Schemas (0.5 days)
- Priority 1.2: Implement Flyway for Identity-Service (1 day)
- Priority 1.3: Implement API Gateway (1-2 days)
- Priority 2.1: Create Service Template (0.5 days)

### Week 2
- Priority 2.2: Complete API Contracts (0.5 days)
- Begin: attendance-service (using template)
- Priority 3.1: Security Audit (1 day)

### Week 3+
- Remaining services: assessment, finance, notification
- Priority 3.2: Health Check Implementation
- Priority 4: Metrics and monitoring enhancement

---

## Definition of Done (DoD)

Service implementation is complete when:
- ✅ All tables have explicit `schema = "service_name"` in @Table annotations
- ✅ Flyway migrations are created and baseline applied
- ✅ Environment variables used for all DB configuration
- ✅ HikariCP pool size set to 10
- ✅ Health endpoint exposed and includes DB status
- ✅ Actuator restricted to health, info, prometheus
- ✅ Separate application-local.yml and application-prod.yml
- ✅ Constructor injection enforced throughout
- ✅ DTOs used for all API contracts
- ✅ OpenAPI contract documented
- ✅ Integration tests pass
- ✅ Docker image builds successfully

---

## Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Identity-Service not fixed early | CRITICAL | Start immediately |
| API Gateway not ready | HIGH | High priority, unblock other teams |
| Inconsistent service patterns | MEDIUM | Use service template |
| Security misconfiguration | HIGH | Conduct audit before production |
| Database schema issues in production | CRITICAL | Enforce Flyway + schema definitions |

---

## Questions for Review

1. ❓ Should identity-service schema naming be `identity_service` or `auth_service`?
2. ❓ Will API Gateway use Spring Cloud Gateway or RestTemplate?
3. ❓ Should circuit breaker be mandatory or optional for MVP?
4. ❓ What is the JWT token TTL for production? (currently 24 hours)
5. ❓ Should we implement distributed tracing (Sleuth/Zipkin)?

---

**Last Updated:** January 18, 2026  
**Next Review:** After API Gateway and first remaining service (attendance-service)
