@echo off
setlocal EnableDelayedExpansion

cd /d "%~dp0"

if not exist "bin" mkdir "bin"
set "SOURCE_LIST=%TEMP%\crm_inline_sources_%RANDOM%.txt"

set "JAVAC_CMD=javac"
where /q javac
if errorlevel 1 (
    call :find_java_home
    if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
    )
)

if not exist "%JAVAC_CMD%" if /i not "%JAVAC_CMD%"=="javac" (
    echo.
    echo javac tidak ditemukan di "%JAVAC_CMD%".
    exit /b 1
)

if exist "%SOURCE_LIST%" del "%SOURCE_LIST%"
for /r "src\main\java" %%f in (*.java) do (
    >>"%SOURCE_LIST%" echo %%f
)

%JAVAC_CMD% -cp "lib/*" -d bin @"%SOURCE_LIST%"

if errorlevel 1 (
    if exist "%SOURCE_LIST%" del "%SOURCE_LIST%"
    echo.
    echo Build gagal.
    exit /b 1
)

if exist "%SOURCE_LIST%" del "%SOURCE_LIST%"
echo.
echo Build berhasil.
exit /b 0

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
            if exist "%%~d\%%j\bin\javac.exe" (
                set "JAVA_HOME=%%~d\%%j"
                exit /b 0
            )
        )
    )
)
exit /b 0
