# Docker Deployment Guide

## Overview

This guide covers Docker configuration for identity-service.

## Files

- **docker-compose.yml** - Service orchestration
- **Dockerfile** - Application build
- **init.sql** - Database initialization

## Architecture

### Multi-Stage Build

```
Stage 1: Compile (Maven)
  └─ maven:3.9.4-eclipse-temurin-17
  └─ Compile source code
  └─ Output: identity-service-1.0.0.jar

Stage 2: Runtime (Lightweight)
  └─ eclipse-temurin:17-jre-alpine
  └─ Copy JAR from Stage 1
  └─ Run application
  └─ ~200MB image size (vs ~700MB with SDK)
```

### Services

**MySQL 8.0**
- Port: 3306
- Volume: mysql_data (persistent)
- Health check: mysqladmin ping
- Init script: init.sql (auto-runs on first start)

**Identity Service**
- Port: 8080
- Build context: services/identity-service/
- Health check: HTTP GET /actuator/health
- Depends on: MySQL (waits for service_healthy)

### Network

```
Bridge network: identity-network

┌─────────────────────────────────┐
│     identity-network (bridge)   │
│                                 │
│  ┌──────────┐    ┌───────────┐  │
│  │  MySQL   │    │  App      │  │
│  │ :3306   │───→│ :8080    │  │
│  └──────────┘    └───────────┘  │
└─────────────────────────────────┘
```

## Environment Variables

| Variable | Default | Purpose |
|----------|---------|---------|
| DB_URL | jdbc:mysql://mysql:3306/identity_service | Database connection |
| DB_USERNAME | identity_user | MySQL user |
| DB_PASSWORD | identity_password | MySQL password |
| SERVER_PORT | 8080 | Application port |
| JWT_SECRET | (dev key) | Token signing ⚠️ CHANGE IN PROD |
| JWT_EXPIRATION | 86400000 | Token lifetime (ms) |
| HIBERNATE_DDL_AUTO | update | Schema auto-update |
| SWAGGER_UI_ENABLED | true | API docs UI |
| LOG_LEVEL | INFO | Root logging |

## Health Checks

### MySQL Health
```yaml
test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
interval: 10s
timeout: 5s
retries: 5
start_period: 30s
```

### Application Health
```yaml
test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
interval: 30s
timeout: 10s
retries: 3
start_period: 60s
```

## Startup Order

```
1. MySQL container starts
   ↓
2. Health check waits for MySQL ready (30s max)
   ↓
3. init.sql runs (schema created)
   ↓
4. identity-service container starts
   ↓
5. Spring Boot application initializes
   ↓
6. Health check verifies API ready (60s max)
   ↓
7. Both services healthy ✅
```

## Data Persistence

### Volumes
```yaml
volumes:
  mysql_data:
    driver: local
    location: Docker volume (managed by Docker)
```

### Backup Data
```bash
# Backup database
docker run --rm -v identity-mysql:/data -v $(pwd):/backup \
  mysql:8.0 tar czf /backup/mysql-backup.tar.gz /data
```

### Restore Data
```bash
# Restore database
docker compose down -v
docker run --rm -v identity-mysql:/data -v $(pwd):/backup \
  mysql:8.0 tar xzf /backup/mysql-backup.tar.gz -C /
docker compose up
```

## Production Checklist

- [ ] Change JWT_SECRET to cryptographically secure value
- [ ] Use strong MySQL credentials
- [ ] Disable SWAGGER_UI_ENABLED=false
- [ ] Set LOG_LEVEL=ERROR or INFO
- [ ] Enable HTTPS/TLS (reverse proxy)
- [ ] Set up monitoring/alerting
- [ ] Configure backup strategy
- [ ] Review security settings
- [ ] Load test application
- [ ] Document runbooks

## Performance Tuning

### JVM Memory
```
Current: 256MB (-Xmx256m)
Per architecture specification
```

### MySQL Connection Pool
```yaml
hikari:
  maximum-pool-size: 10
  minimum-idle: 2
  connection-timeout: 30000
```

### Database Batch Size
```yaml
hibernate:
  jdbc:
    batch_size: 20
    fetch_size: 50
```

## Troubleshooting Deployment

### Container won't start
```bash
docker logs identity-service
docker logs identity-mysql
```

### Port conflicts
```bash
# Check port
lsof -i :8080
lsof -i :3306

# Change in docker-compose.yml
ports:
  - "8081:8080"  # Use different port
```

### Volume issues
```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect identity_mysql_data

# Remove volume (DELETE DATA)
docker volume rm identity_mysql_data
```

### Network issues
```bash
# List networks
docker network ls

# Inspect network
docker network inspect identity-network
```

## Cleanup

```bash
# Stop services
docker compose down

# Remove images
docker rmi identity-service

# Remove volumes (DELETE DATA)
docker volume rm identity_mysql_data

# Full cleanup
docker system prune -a --volumes
```

## References

- Dockerfile multi-stage builds: https://docs.docker.com/build/building/multi-stage/
- docker-compose specifications: https://docs.docker.com/compose/compose-file/
- MySQL Docker image: https://hub.docker.com/_/mysql
- Spring Boot Docker: https://spring.io/guides/gs/spring-boot-docker/

