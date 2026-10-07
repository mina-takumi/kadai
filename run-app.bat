@echo off
cd /d "%~dp0"
set "JDK_BIN=%~dp0java-portable_20260715\jdk-25.0.3_gui_min\bin"

if not exist build mkdir build
"%JDK_BIN%\javac.exe" -encoding UTF-8 -d build src\*.java
if errorlevel 1 (
    echo [ERROR] Compile failed.
    pause
    exit /b 1
)

"%JDK_BIN%\java.exe" -cp build OmikujiApp
pause
