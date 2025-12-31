# AI PLAYBOOK

Purpose: Train AI to behave predictably.

## Always Enforce
- One service = one domain
- IDs across services, never objects
- Internal APIs prefixed with /internal

## Common AI Mistakes
- Creating god services
- Mixing domains
- Putting logic in controllers

## Rejected Ideas
- Kubernetes
- Event sourcing
- Shared schemas

## Preferred Patterns
- DTO validation
- Service-level transactions
- Thin controllers
