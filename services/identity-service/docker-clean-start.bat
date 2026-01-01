@echo off
REM Clean Docker setup for identity-service
REM This script removes all existing containers/volumes and starts fresh

setlocal enabledelayedexpansion

echo ==================================================
echo Clean Docker Setup for Identity Service
echo ==================================================
echo.

echo 1️⃣ Stopping and removing old containers...
docker compose down -v 2>nul || echo (no existing containers)

echo.
echo 2️⃣ Removing old images...
docker rmi identity-service-identity-service:latest 2>nul || echo (no old images)

echo.
echo 3️⃣ Building fresh image...
docker compose build --no-cache

echo.
echo 4️⃣ Starting services...
docker compose up -d

echo.
echo ==================================================
echo ✅ Setup complete!
echo ==================================================
echo.
echo 📋 Services starting (wait 2-3 minutes for full startup):
docker compose ps
echo.
echo 🔍 Check logs:
echo   docker compose logs -f identity-service
echo.
echo 📝 API will be available at:
echo   http://localhost:8080/swagger-ui.html
echo.

