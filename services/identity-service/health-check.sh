#!/bin/bash
# Health check script for identity-service
# Usage: ./health-check.sh

set -e

echo "🏥 Identity Service Health Check"
echo "=================================="

# Check if Docker is running
echo -n "✓ Checking Docker... "
if ! command -v docker &> /dev/null; then
    echo "❌ Docker not found. Please install Docker."
    exit 1
fi
echo "✓ Docker found"

# Check if Docker Compose is available
echo -n "✓ Checking Docker Compose... "
if ! command -v docker compose &> /dev/null && ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose not found."
    exit 1
fi
echo "✓ Docker Compose found"

# Check if services are running
echo ""
echo "Checking services..."

# Check MySQL
echo -n "  MySQL: "
if docker ps | grep -q identity-mysql; then
    echo "✓ Running"
else
    echo "❌ Not running"
    echo "    → Run: docker compose up"
    exit 1
fi

# Check identity-service
echo -n "  Identity Service: "
if docker ps | grep -q identity-service; then
    echo "✓ Running"
else
    echo "❌ Not running"
    echo "    → Run: docker compose up"
    exit 1
fi

# Test API endpoint
echo ""
echo "Testing API..."

echo -n "  Health Endpoint: "
if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo "✓ Healthy"
else
    echo "⚠️ No response (still starting)"
    echo "    → Wait 30 seconds and try again"
    exit 1
fi

# Test Swagger
echo -n "  Swagger UI: "
if curl -s http://localhost:8080/swagger-ui/index.html | grep -q "swagger"; then
    echo "✓ Available"
else
    echo "⚠️ Not available"
fi

echo ""
echo "=================================="
echo "✅ Identity Service is healthy!"
echo ""
echo "Access points:"
echo "  API:       http://localhost:8080"
echo "  Swagger:   http://localhost:8080/swagger-ui/index.html"
echo "  Health:    http://localhost:8080/actuator/health"
echo ""

