@echo off
REM Health check script for identity-service (Windows)
REM Usage: health-check.bat

echo.
echo 🏥 Identity Service Health Check
echo ==================================

REM Check if Docker is running
echo Checking Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker not found. Please install Docker Desktop.
    pause
    exit /b 1
)
echo ✓ Docker found

REM Check if Docker Compose is available
docker compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose not available.
    pause
    exit /b 1
)
echo ✓ Docker Compose found

echo.
echo Checking services...

REM Check MySQL
echo   MySQL:
docker ps | find "identity-mysql" >nul
if errorlevel 1 (
    echo ❌ Not running
    echo     ^→ Run: docker compose up
    pause
    exit /b 1
) else (
    echo ✓ Running
)

REM Check identity-service
echo   Identity Service:
docker ps | find "identity-service" >nul
if errorlevel 1 (
    echo ❌ Not running
    echo     ^→ Run: docker compose up
    pause
    exit /b 1
) else (
    echo ✓ Running
)

echo.
echo Testing API...

REM Test health endpoint
echo   Health Endpoint:
curl -s http://localhost:8080/actuator/health | find "UP" >nul
if errorlevel 1 (
    echo ⚠️ No response (still starting)
    echo     ^→ Wait 30 seconds and try again
    pause
    exit /b 1
) else (
    echo ✓ Healthy
)

REM Test Swagger
echo   Swagger UI:
curl -s http://localhost:8080/swagger-ui/index.html | find "swagger" >nul
if errorlevel 1 (
    echo ⚠️ Not available
) else (
    echo ✓ Available
)

echo.
echo ==================================
echo ✅ Identity Service is healthy!
echo.
echo Access points:
echo   API:       http://localhost:8080
echo   Swagger:   http://localhost:8080/swagger-ui/index.html
echo   Health:    http://localhost:8080/actuator/health
echo.
pause

