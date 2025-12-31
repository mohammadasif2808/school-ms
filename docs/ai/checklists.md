# Engineering Checklists
## School Management System (SMS)

These checklists ensure **discipline and consistency** when working with AI agents.

Every task MUST satisfy the relevant checklist before completion.

---

## Checklist: New Microservice

Before AI:
- [ ] Service name finalized
- [ ] Bounded context written
- [ ] Responsibilities listed
- [ ] Forbidden responsibilities listed

After AI:
- [ ] Clean package structure
- [ ] No business logic
- [ ] Dockerfile present
- [ ] application.yml uses env variables
- [ ] README.md created

Commit message:



chore: initialize <service-name>


---

## Checklist: New Feature

Planning:
- [ ] Feature mapped to exactly ONE service
- [ ] API name decided
- [ ] DTOs designed

Implementation:
- [ ] OpenAPI updated first
- [ ] DTO validation present
- [ ] Controller is thin
- [ ] Business logic in service layer
- [ ] Repository only handles persistence

Final:
- [ ] Local test passes
- [ ] No cross-service DB access
- [ ] Commit follows convention

---

## Checklist: Cross-Service Call

Before:
- [ ] Data owner identified
- [ ] Consumer confirmed as non-owner
- [ ] Data exchange is ID-based

After:
- [ ] Internal API prefixed with /internal
- [ ] No foreign keys across schemas
- [ ] Failure handling implemented
- [ ] No circular calls

---

## Checklist: Security

- [ ] JWT validated at gateway
- [ ] No session usage
- [ ] Role-based access enforced
- [ ] No sensitive data in logs

---

## Checklist: Performance

- [ ] JVM heap ≤ 256MB
- [ ] No blocking calls in controllers
- [ ] Connection pooling configured

---

## Checklist: End-of-Day Audit

- [ ] AI Guardrail Audit executed
- [ ] Critical issues fixed
- [ ] AI_PLAYBOOK.md updated if needed
- [ ] Code committed

---

## Definition of Done (DoD)

A task is DONE only if:
- All relevant checklists are satisfied
- Code compiles and runs
- Architecture rules are preserved
