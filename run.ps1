# PowerShell launcher for Snake Game
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

# 1. Locate Java JDK
$javacCmd = "javac"
$javaCmd = "java"

if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
    $javacCmd = "$env:JAVA_HOME\bin\javac.exe"
    $javaCmd = "$env:JAVA_HOME\bin\java.exe"
} elseif (Test-Path "C:\Program Files\Java\jdk-22\bin\javac.exe") {
    $javacCmd = "C:\Program Files\Java\jdk-22\bin\javac.exe"
    $javaCmd = "C:\Program Files\Java\jdk-22\bin\java.exe"
} else {
    $jdk = Get-ChildItem "C:\Program Files\Java" -Filter "jdk-*" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($jdk -and (Test-Path "$($jdk.FullName)\bin\javac.exe")) {
        $javacCmd = "$($jdk.FullName)\bin\javac.exe"
        $javaCmd = "$($jdk.FullName)\bin\java.exe"
    }
}

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "               STARTING SNAKE GAME                 " -ForegroundColor Green
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Using Java compiler: $javacCmd" -ForegroundColor Yellow

# 2. Ensure bin folder exists
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# 3. Compile sources
Write-Host "Compiling Java source files..." -ForegroundColor Gray
$sources = (Get-ChildItem -Recurse "src\main\java\com\snakegame" -Filter "*.java").FullName
& $javacCmd --release 17 -d bin $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit 1
}

# 4. Launch game
Write-Host "Launching Snake game..." -ForegroundColor Green
& $javaCmd -cp bin com.snakegame.Game
