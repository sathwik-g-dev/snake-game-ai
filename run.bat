@echo off
setlocal enabledelayedexpansion

:: Change directory to script directory
cd /d "%~dp0"

:: 1. Detect Java JDK
set "JAVAC_CMD=javac"
set "JAVA_CMD=java"

:: Check if JAVA_HOME is set
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    )
)

:: Check direct JDK path first
if exist "C:\Program Files\Java\jdk-22\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Java\jdk-22\bin\javac.exe"
    set "JAVA_CMD=C:\Program Files\Java\jdk-22\bin\java.exe"
) else (
    for /d %%D in ("C:\Program Files\Java\jdk-*") do (
        if exist "%%D\bin\javac.exe" (
            set "JAVAC_CMD=%%D\bin\javac.exe"
            set "JAVA_CMD=%%D\bin\java.exe"
        )
    )
)

echo ===================================================
echo               STARTING SNAKE GAME
echo ===================================================
echo Using Java compiler: "%JAVAC_CMD%"

:: 2. Create bin directory
if not exist "bin" mkdir "bin"

:: 3. Compile sources
echo Compiling Java source files...
"%JAVAC_CMD%" --release 17 -d bin src\main\java\com\snakegame\*.java src\main\java\com\snakegame\core\*.java src\main\java\com\snakegame\ai\*.java
if errorlevel 1 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

:: 4. Launch game
echo Launching Snake game...
"%JAVA_CMD%" -cp bin com.snakegame.Game

endlocal
