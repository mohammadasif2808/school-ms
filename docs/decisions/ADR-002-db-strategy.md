# ADR-002: Database Strategy

## Status
Accepted

## Decision
Single database instance with schema-per-service.

## Rationale
- Cost efficiency
- Logical isolation
- Easy future extraction

## Forbidden
- Cross-schema joins
- Shared tables
