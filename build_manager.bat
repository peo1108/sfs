@echo off
setlocal enabledelayedexpansion
title KernelSU Manager - Local APK Builder
color 0A

echo ============================================
echo   KernelSU Manager - Local APK Builder
echo   Liquid Glass iOS 26 Edition
echo ============================================
echo.

:: Configuration
set "PROJECT_DIR=%~dp0"
set "MANAGER_DIR=%PROJECT_DIR%manager"
set "OUTPUT_DIR=%PROJECT_DIR%Release"

:: Check Java
echo [1/5] Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found! Please install JDK 21+
    echo Download: https://adoptium.net/
    pause
    exit /b 1
)
echo       Java OK

:: Check Android SDK
echo [2/5] Checking Android SDK...
if not defined ANDROID_HOME (
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
    ) else if exist "%USERPROFILE%\AppData\Local\Android\Sdk" (
        set "ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk"
    ) else (
        echo [ERROR] Android SDK not found!
        echo Set ANDROID_HOME or install Android Studio
        pause
        exit /b 1
    )
)
echo       Android SDK: %ANDROID_HOME%

:: Write local.properties
echo [3/5] Configuring project...
echo sdk.dir=%ANDROID_HOME:\=/% > "%MANAGER_DIR%\local.properties"

:: Create jniLibs stub (if ksud not present)
if not exist "%MANAGER_DIR%\app\src\main\jniLibs\arm64-v8a\libksud.so" (
    echo [INFO] Creating ksud stubs ^(required for build^)...
    mkdir "%MANAGER_DIR%\app\src\main\jniLibs\arm64-v8a" 2>nul
    mkdir "%MANAGER_DIR%\app\src\main\jniLibs\x86_64" 2>nul
    echo. > "%MANAGER_DIR%\app\src\main\jniLibs\arm64-v8a\libksud.so"
    echo. > "%MANAGER_DIR%\app\src\main\jniLibs\x86_64\libksud.so"
    echo       Stubs created ^(APK will need real ksud for full functionality^)
) else (
    echo       jniLibs OK
)

:: Build
echo [4/5] Building Release APK...
echo       This may take several minutes on first run...
echo.
pushd "%MANAGER_DIR%"
call gradlew.bat clean assembleRelease 2>&1
set BUILD_RESULT=%errorlevel%
popd

if %BUILD_RESULT% neq 0 (
    echo.
    echo [ERROR] Build failed! Check errors above.
    pause
    exit /b 1
)

:: Copy APK to Release folder
echo [5/5] Collecting APK...
mkdir "%OUTPUT_DIR%" 2>nul

set "APK_FOUND=0"
for /r "%MANAGER_DIR%\app\build\outputs\apk\release" %%f in (*.apk) do (
    copy /y "%%f" "%OUTPUT_DIR%\" >nul
    echo       Output: %OUTPUT_DIR%\%%~nxf
    set "APK_FOUND=1"
)

if "%APK_FOUND%"=="0" (
    echo [WARNING] No APK found in build output!
) else (
    echo.
    echo ============================================
    echo   BUILD SUCCESSFUL!
    echo   APK location: %OUTPUT_DIR%
    echo ============================================
)

echo.
echo Press any key to open Release folder...
pause >nul
explorer "%OUTPUT_DIR%"
