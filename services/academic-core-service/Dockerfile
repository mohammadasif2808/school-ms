# Multi-stage Dockerfile for academic-core-service
# Stage 1: Build
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copy the built artifact
COPY --from=builder /app/target/academic-core-service-*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8081

# JVM configuration for memory-constrained environment (256MB max heap)
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
