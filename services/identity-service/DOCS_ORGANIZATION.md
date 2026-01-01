# Documentation Organization

## Root Level (Essential)
- **START_HERE.md** - Quick start for new developers
- **README.md** - Project overview
- **docker-compose.yml** - Docker setup
- **Dockerfile** - Build definition
- **init.sql** - Database initialization

## For Frontend Developers

See: `docs/frontend/`

- **docs/frontend/SETUP.md** - Complete setup guide
- **docs/frontend/QUICK_REFERENCE.md** - Command cheatsheet
- **docs/frontend/TROUBLESHOOTING.md** - Common issues

## For DevOps/Deployment

See: `docs/deployment/`

- **docs/deployment/DOCKER_HANDOFF.md** - Docker documentation
- **docs/deployment/CHECKLIST.md** - Pre-deployment verification

## Project Documentation

See: `docs/`

- **docs/README.md** - Documentation index
- **docs/architecture/** - Architecture decisions
- **docs/implementation/** - Implementation guides
- **docs/features/** - Feature documentation
- **docs/guides/** - General guides

## Quick Navigation

| Role | Start Here | Next |
|------|-----------|------|
| **Frontend Dev** | START_HERE.md | docs/frontend/SETUP.md |
| **Backend Dev** | README.md | docs/implementation/ |
| **DevOps** | docs/deployment/DOCKER_HANDOFF.md | docs/deployment/CHECKLIST.md |
| **Architect** | docs/architecture/ | docs/decisions/ |

## Files to Keep at Root

Only these should be in `services/identity-service/`:

```
START_HERE.md             ← Quick entry point
README.md                 ← Project overview
docker-compose.yml        ← Docker orchestration
Dockerfile                ← Build definition
init.sql                  ← Database schema
.env.example              ← Env template
.dockerignore             ← Docker optimization
health-check.sh           ← Verification script
health-check.bat          ← Windows verification
```

## All Other Docs

Move to `docs/` subdirectories as follows:

```
docs/
├── frontend/
│   ├── SETUP.md                  (was FRONTEND_SETUP.md)
│   ├── QUICK_REFERENCE.md        (was QUICK_REFERENCE.md)
│   └── TROUBLESHOOTING.md        (new - consolidated)
│
├── deployment/
│   ├── DOCKER_HANDOFF.md         (was DOCKER_HANDOFF.md)
│   ├── CHECKLIST.md              (was HANDOFF_CHECKLIST.md)
│   └── VERIFICATION.md           (was VERIFICATION_COMPLETE.md)
│
├── architecture/
│   ├── README.md
│   └── SECURITY_*.md
│
├── implementation/
│   ├── README.md
│   ├── CONTROLLER_*.md
│   └── ...existing files...
│
└── guides/
    ├── README.md
    └── ...
```

## Consolidation

Delete these (content merged into organized docs):
- HANDOFF_COMPLETE.md (summary only)
- PACKAGE_CONTENTS.md (inventory only)
- DOCKER_HANDOFF.md (moved to docs/deployment/)
- HANDOFF_CHECKLIST.md (moved to docs/deployment/)
- INDEX.md (replaced by this file)
- VERIFICATION_COMPLETE.md (moved to docs/deployment/)

Keep this file as the single source of truth for documentation organization.

