@echo off
setlocal
cd /d "%~dp0"

where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven tidak ditemukan di PATH.
  echo Install Maven lalu pastikan perintah mvn bisa dipanggil dari terminal.
  pause
  exit /b 1
)

echo Menjalankan REST API...
echo Swagger UI: http://localhost:8080/swagger-ui.html
mvn spring-boot:run
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" (
  echo.
  echo Proses berhenti dengan exit code %EXIT_CODE%.
  pause
)
exit /b %EXIT_CODE%
