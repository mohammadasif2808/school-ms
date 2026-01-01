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

# Copy wait-for-mysql script
COPY wait-for-mysql.sh /app/wait-for-mysql.sh
RUN chmod +x /app/wait-for-mysql.sh

# Health check configuration
HEALTHCHECK --interval=30s --timeout=3s --start-period=120s --retries=5 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port 8080 for the API
EXPOSE 8080

# Create entrypoint wrapper script
RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
    echo 'set -e' >> /app/entrypoint.sh && \
    echo 'echo "🚀 Starting identity-service..."' >> /app/entrypoint.sh && \
    echo 'echo "⏳ Waiting for MySQL to be ready..."' >> /app/entrypoint.sh && \
    echo './wait-for-mysql.sh mysql 3306 identity_user identity_password 120 || exit 1' >> /app/entrypoint.sh && \
    echo 'echo "✅ MySQL is ready, starting Spring Boot..."' >> /app/entrypoint.sh && \
    echo 'exec java -Xmx256m -jar app.jar' >> /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
