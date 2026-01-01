# Troubleshooting Guide

## Common Issues & Solutions

### Port 8080 Already in Use

**Symptom:** Error when running `docker compose up`
```
Error response from daemon: Ports are not available
```

**Solutions:**

1. **Find what's using port 8080:**
   ```bash
   # Mac/Linux
   lsof -i :8080
   
   # Windows (PowerShell)
   netstat -ano | findstr :8080
   ```

2. **Stop the other service:**
   ```bash
   docker compose down
   ```

3. **Or change the port in docker-compose.yml:**
   ```yaml
   ports:
     - "8081:8080"  # Now access at http://localhost:8081
   ```

---

### MySQL Won't Initialize

**Symptom:** MySQL container crashes or won't start
```
docker compose logs mysql
```

**Solutions:**

1. **Wait longer** - MySQL takes 30+ seconds first run
   ```bash
   # Check MySQL logs
   docker compose logs mysql
   ```

2. **Restart MySQL:**
   ```bash
   docker compose restart mysql
   docker compose up
   ```

3. **Reset MySQL volume:**
   ```bash
   docker compose down -v
   docker compose up
   ```

---

### Application Won't Start

**Symptom:** identity-service container crashes immediately

**Debug:**
```bash
docker compose logs identity-service
```

**Common Causes & Fixes:**

1. **MySQL not ready yet:**
   - Wait 30+ seconds for MySQL to initialize
   - Check: `docker compose logs mysql`

2. **Database connection failed:**
   ```bash
   # Restart both
   docker compose down
   docker compose up
   ```

3. **Port 8080 in use:**
   - See section above
   - Change port in docker-compose.yml

4. **Corrupted volumes:**
   ```bash
   # Nuclear option: delete everything
   docker compose down -v
   docker compose up
   ```

---

### Data Not Persisting

**Symptom:** Data disappears after `docker compose down`

**Wrong:**
```bash
docker kill identity-mysql  # ❌ This loses data
```

**Correct:**
```bash
docker compose down  # ✅ This preserves data
```

**To delete data (intentionally):**
```bash
docker compose down -v  # -v removes volumes
```

---

### Swagger UI Not Loading

**Symptom:** http://localhost:8080/swagger-ui/index.html shows error

**Solutions:**

1. **Check if app is running:**
   ```bash
   curl http://localhost:8080/actuator/health
   # Should return: {"status":"UP"}
   ```

2. **If not running, start it:**
   ```bash
   docker compose up
   ```

3. **Wait for full startup** - Swagger loads after app fully starts

4. **Check logs:**
   ```bash
   docker compose logs identity-service
   ```

---

### Can't Connect to Database Directly

**Symptom:** MySQL client can't connect

**Debug:**
```bash
# Check if MySQL is running
docker ps | grep identity-mysql

# View MySQL logs
docker compose logs mysql

# Check port
docker compose logs mysql | grep "port"
```

**Connect to MySQL:**
```bash
docker exec -it identity-mysql mysql -u identity_user -pidentity_password identity_service
```

Then query:
```sql
SELECT * FROM users;
SELECT * FROM roles;
SHOW TABLES;
```

---

### Sign Up Fails with 400 Bad Request

**Symptom:** POST /api/v1/auth/signup returns 400

**Common Causes:**

1. **Invalid password** - Must meet requirements:
   - ✅ Min 8 characters
   - ✅ 1 uppercase (A-Z)
   - ✅ 1 lowercase (a-z)
   - ✅ 1 digit (0-9)
   - ✅ 1 special (@$!%*?&)

   Valid: `Test@Pass123!`  
   Invalid: `password`

2. **Missing required fields:**
   ```json
   {
     "username": "required",
     "email": "required",
     "password": "required",
     "first_name": "required",
     "last_name": "required",
     "phone": "required"
   }
   ```

3. **Duplicate username/email:**
   - User already exists
   - Use different username/email

4. **Invalid email format:**
   - Make sure it looks like: `user@example.com`

---

### Sign In Returns 401 Unauthorized

**Symptom:** POST /api/v1/auth/signin returns 401

**Causes & Fixes:**

1. **Wrong username or password:**
   - Check spelling
   - Passwords are case-sensitive

2. **User doesn't exist:**
   - Sign up first
   - Check username is correct

3. **User is blocked:**
   - Check user status in database
   - Contact admin

---

### JWT Token Expired

**Symptom:** GET /api/v1/auth/me returns 401 with "TOKEN_EXPIRED"

**Solution:**
```bash
# Get a new token
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "Pass@123!"}'
```

Token is valid for 24 hours by default.

---

### Health Check Script Fails

**Symptom:** `./health-check.sh` shows services not running

**Solutions:**

1. **Verify Docker is running:**
   ```bash
   docker ps
   ```

2. **Check if services are up:**
   ```bash
   docker ps | grep identity
   ```

3. **Start services:**
   ```bash
   docker compose up
   ```

4. **Wait for startup** - Takes 2-3 minutes first run

---

### Docker Compose Command Not Found

**Symptom:** `docker compose: command not found`

**On Mac/Linux:**
- Ensure Docker Desktop is installed
- Or install docker-compose separately:
  ```bash
  brew install docker-compose  # Mac
  sudo apt install docker-compose  # Ubuntu
  ```

**On Windows:**
- Install Docker Desktop
- Or use: `docker-compose` (with hyphen)

---

### Everything Broken? Reset

**Nuclear Option:**
```bash
# Stop everything
docker compose down

# Delete all data
docker system prune -f

# Remove volume
docker volume rm identity_mysql_data

# Start fresh
docker compose up
```

---

## Still Not Working?

1. **Check logs:**
   ```bash
   docker compose logs identity-service
   docker compose logs mysql
   ```

2. **Verify health:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

3. **Read full documentation:**
   - docs/frontend/SETUP.md
   - ../deployment/DOCKER_HANDOFF.md

4. **Check API docs:**
   - http://localhost:8080/swagger-ui/index.html

