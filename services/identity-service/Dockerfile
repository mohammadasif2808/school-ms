# Multi-stage Docker build for identity-service
# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy POM and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image (lightweight)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built JAR from builder (smaller final image)
COPY --from=builder /build/target/identity-service-1.0.0.jar app.jar

# Health check configuration
# - Checks every 30 seconds
# - Waits 10 seconds before first check
# - Times out if no response in 3 seconds
# - Considers healthy after 1 successful response
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port 8080 for the API
EXPOSE 8080

# Run the application
# -Xmx256m: Limit heap to 256MB (per architecture guidelines)
# Note: Environment variables are injected at runtime via docker-compose
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]
