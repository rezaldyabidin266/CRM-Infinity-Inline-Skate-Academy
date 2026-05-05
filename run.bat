@echo off
setlocal

cd /d "%~dp0"

call build.bat
if errorlevel 1 exit /b 1

set "JAVA_CMD=java"
where /q java
if errorlevel 1 (
    if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    ) else (
        call :find_java_home
        if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
            set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        )
    )
)

%JAVA_CMD% -cp "bin;lib/*" com.tugasbesar.app.MainApp
exit /b %errorlevel%

:find_java_home
for %%d in (
    "%ProgramFiles%\Eclipse Adoptium"
    "%ProgramFiles%\AdoptOpenJDK"
    "%ProgramFiles%\Java"
    "%ProgramFiles(x86)%\Eclipse Adoptium"
    "%ProgramFiles(x86)%\AdoptOpenJDK"
    "%ProgramFiles(x86)%\Java"
) do (
    if exist %%~d (
        for /f "delims=" %%j in ('dir /b /ad /o-n "%%~d" 2^>nul') do (
            if exist "%%~d\%%j\bin\java.exe" (
                set "JAVA_HOME=%%~d\%%j"
                exit /b 0
            )
        )
    )
)
exit /b 0
