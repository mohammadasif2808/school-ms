# Identity Service Documentation

Complete documentation for identity-service is organized by role and topic.

## Quick Navigation

### For Frontend Developers

**Just starting?**
1. Read [../START_HERE.md](../START_HERE.md) (5 minutes)
2. Follow [frontend/SETUP.md](frontend/SETUP.md) (15 minutes)
3. Keep [frontend/QUICK_REFERENCE.md](frontend/QUICK_REFERENCE.md) handy

**Getting stuck?**
→ See [frontend/TROUBLESHOOTING.md](frontend/TROUBLESHOOTING.md)

**Need API docs?**
→ Open Swagger UI at http://localhost:8080/swagger-ui/index.html

### For Backend Developers

**Understanding the system?**
1. Read [../README.md](../README.md) (overview)
2. Review [architecture/](architecture/) (design decisions)
3. Check [implementation/](implementation/) (how things work)

**Implementing features?**
→ See [implementation/](implementation/) guides

### For DevOps / Deployment

**First time deploying?**
1. Read [deployment/DOCKER_GUIDE.md](deployment/DOCKER_GUIDE.md)
2. Use [deployment/CHECKLIST.md](deployment/CHECKLIST.md)

**Need to debug Docker?**
→ See [deployment/DOCKER_GUIDE.md](deployment/DOCKER_GUIDE.md) troubleshooting section

---

## Documentation Structure

```
identity-service/
├── START_HERE.md                    ← All developers start here
├── README.md                        ← Project overview
├── DOCS_ORGANIZATION.md             ← This structure explained
├── docker-compose.yml               ← Docker setup
├── Dockerfile                       ← Build definition
├── init.sql                         ← Database schema
│
└── docs/
    ├── frontend/                    ← For frontend team
    │   ├── SETUP.md                 ← How to start backend
    │   ├── QUICK_REFERENCE.md       ← Common commands & APIs
    │   └── TROUBLESHOOTING.md       ← Common issues & fixes
    │
    ├── deployment/                  ← For DevOps team
    │   ├── DOCKER_GUIDE.md          ← Docker architecture & config
    │   └── CHECKLIST.md             ← Pre-deployment verification
    │
    ├── architecture/                ← Design & decisions
    │   ├── README.md
    │   └── *.md files
    │
    ├── implementation/              ← Implementation guides
    │   ├── README.md
    │   ├── CONTROLLER_*.md
    │   └── ...
    │
    └── guides/                      ← General guides
        └── ...
```

---

## Common Tasks

### "I want to start the backend"
→ [START_HERE.md](../START_HERE.md)

### "I need to make an API call"
→ [frontend/QUICK_REFERENCE.md](frontend/QUICK_REFERENCE.md)

### "My backend won't start"
→ [frontend/TROUBLESHOOTING.md](frontend/TROUBLESHOOTING.md)

### "I need to deploy this"
→ [deployment/DOCKER_GUIDE.md](deployment/DOCKER_GUIDE.md)

### "I need to verify before deployment"
→ [deployment/CHECKLIST.md](deployment/CHECKLIST.md)

### "I need to understand the architecture"
→ [architecture/](architecture/)

### "I need to implement a feature"
→ [implementation/](implementation/)

---

## Files at Root Level

These files are at `services/identity-service/` (root):

- **START_HERE.md** - Quick start guide (all developers)
- **README.md** - Project overview
- **docker-compose.yml** - Docker orchestration
- **Dockerfile** - Application build
- **init.sql** - Database initialization
- **.env.example** - Environment variables template
- **health-check.sh / health-check.bat** - Service verification
- **DOCS_ORGANIZATION.md** - How documentation is organized

---

## Key Directories

### docs/frontend/
- **SETUP.md** - How to start and use the backend
- **QUICK_REFERENCE.md** - Commands and API endpoints
- **TROUBLESHOOTING.md** - Common issues and solutions

### docs/deployment/
- **DOCKER_GUIDE.md** - Docker architecture and configuration
- **CHECKLIST.md** - Pre-deployment verification

### docs/architecture/
- Decision records
- Security configuration
- System design

### docs/implementation/
- Controller guides
- Service implementation
- Database schema
- API contract

---

## Access Points

Once backend is running:

| Resource | URL |
|----------|-----|
| API Base | http://localhost:8080 |
| Swagger UI (API Docs) | http://localhost:8080/swagger-ui/index.html |
| Health Check | http://localhost:8080/actuator/health |

---

## Quick Commands

```bash
# Start backend (from identity-service directory)
docker compose up

# View logs
docker compose logs -f identity-service

# Stop backend (data persists)
docker compose down

# Reset database (delete all data)
docker compose down -v

# Verify health
./health-check.sh  (or health-check.bat on Windows)
```

---

## Support Flowchart

```
Need help?
    ↓
What's your role?
    ├─ Frontend Developer → START_HERE.md → docs/frontend/
    ├─ Backend Developer → README.md → docs/implementation/
    ├─ DevOps → docs/deployment/DOCKER_GUIDE.md
    └─ Architect → docs/architecture/
```

---

## Recent Changes

All documentation has been reorganized into role-based folders:

- ✅ Frontend docs moved to `docs/frontend/`
- ✅ Deployment docs moved to `docs/deployment/`
- ✅ Navigation simplified with README pointers
- ✅ Quick references consolidated
- ✅ Troubleshooting guides organized

See [../DOCS_ORGANIZATION.md](../DOCS_ORGANIZATION.md) for details.

---

**Start here:** [../START_HERE.md](../START_HERE.md)

