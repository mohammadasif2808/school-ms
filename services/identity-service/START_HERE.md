# ⚡ Get Started in 60 Seconds

## You need:
- Docker Desktop (Windows/Mac) or Docker + Docker Compose (Linux)

## You don't need:
- ❌ Java
- ❌ Maven
- ❌ MySQL
- ❌ Any setup scripts

---

## 3 Commands

```bash
# 1️⃣ Go to the service directory
cd services/identity-service

# 2️⃣ Start everything
docker compose up

# 3️⃣ Wait for this message:
# "Started IdentityServiceApplication in X.XXX seconds"
```

---

## You're done! 🎉

Access the API:
- **API:** http://localhost:8080
- **Swagger (API Docs):** http://localhost:8080/swagger-ui/index.html

---

## First Test - Sign Up

Open **Swagger** (link above) or use this command:

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

---

## Next Steps

1. **Read FRONTEND_SETUP.md** for more details
2. **Open Swagger UI** to explore all endpoints
3. **Start building** your frontend!

---

## Need Help?

```bash
# See service logs
docker compose logs identity-service

# Restart everything
docker compose down && docker compose up

# Reset all data
docker compose down -v && docker compose up
```

See **QUICK_REFERENCE.md** for more commands.

---

**Questions? Read FRONTEND_SETUP.md or QUICK_REFERENCE.md** 👈

