@echo off
setlocal EnableDelayedExpansion

cd /d "%~dp0"

if not exist "bin" mkdir "bin"

for /r "src\main\java" %%f in (*.java) do (
    call set "SOURCES=!SOURCES! "%%f""
)

javac -cp "lib/*" -d bin !SOURCES!

if errorlevel 1 (
    echo.
    echo Build gagal.
    exit /b 1
)

echo.
echo Build berhasil.
exit /b 0
