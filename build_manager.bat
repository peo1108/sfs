@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

:: ============================================================
::  KernelSU Manager - Local Release Build Tool
::  Build Manager APK giong nhu GitHub Actions Release
:: ============================================================

set "ROOT_DIR=%~dp0"
set "MANAGER_DIR=%ROOT_DIR%manager"
set "RELEASE_DIR=%ROOT_DIR%Release"
set "GRADLE_PROPS=%MANAGER_DIR%\gradle.properties"

:: ---------- Signing Config ----------
set "KS_PASSWORD=A123@abc"
set "KS_ALIAS=cam"
set "KS_KEY_PASSWORD=A123@abc"
set "KS_FILE=dummy.keystore"

echo.
echo ============================================================
echo   KernelSU Manager - Local Release Build Tool
echo ============================================================
echo.

:: ============================================================
:: STEP 1: Kiem tra Java
:: ============================================================
echo [1/8] Kiem tra Java...
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo    [ERROR] Khong tim thay Java!
    echo    Hay cai dat JDK 21+ va them vao PATH.
    echo    Download: https://adoptium.net/
    goto :fail
)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo    Java version: %%~i
)

:: ============================================================
:: STEP 2: Auto-detect JAVA_HOME
:: ============================================================
echo [2/8] Kiem tra JAVA_HOME...
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo    JAVA_HOME: %JAVA_HOME%
        goto :java_ok
    ) else (
        echo    [WARN] JAVA_HOME khong hop le: %JAVA_HOME%
        echo    Dang tu dong tim lai...
        set "JAVA_HOME="
    )
)
:: Tim JDK trong cac thu muc pho bien
for /d %%d in ("C:\Program Files\Java\jdk-*") do set "JAVA_HOME=%%d"
for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk-*") do set "JAVA_HOME=%%d"
for /d %%d in ("C:\Program Files\Microsoft\jdk-*") do set "JAVA_HOME=%%d"
for /d %%d in ("C:\Program Files\Zulu\zulu-*") do set "JAVA_HOME=%%d"
if not defined JAVA_HOME (
    for /f "tokens=*" %%p in ('where java 2^>nul') do (
        for %%q in ("%%~dpp..") do set "JAVA_HOME=%%~fq"
    )
)
if defined JAVA_HOME (
    echo    JAVA_HOME duoc set thanh: %JAVA_HOME%
) else (
    echo    [WARN] Khong the tu dong xac dinh JAVA_HOME.
    echo    Build co the that bai. Hay set JAVA_HOME thu cong.
)
:java_ok

:: ============================================================
:: STEP 3: Kiem tra Git
:: ============================================================
echo [3/8] Kiem tra Git...
where git >nul 2>&1
if %ERRORLEVEL% neq 0 (
    :: Tim Git trong cac thu muc pho bien
    set "GIT_FOUND="
    for %%p in (
        "C:\Program Files\Git\cmd\git.exe"
        "C:\Program Files (x86)\Git\cmd\git.exe"
        "%LOCALAPPDATA%\Programs\Git\cmd\git.exe"
    ) do (
        if exist %%p (
            for %%q in (%%~dp.) do set "PATH=%%~fq;!PATH!"
            set "GIT_FOUND=1"
        )
    )
    if defined GIT_FOUND (
        echo    Tim thay Git va da them vao PATH.
    ) else (
        echo    [WARN] Khong tim thay Git. Version se la "local-build".
        echo    Neu muon co version dung, hay cai Git: https://git-scm.com/
    )
) else (
    for /f "tokens=3" %%v in ('git --version') do echo    Git version: %%v
)

:: ============================================================
:: STEP 4: Kiem tra Android SDK
:: ============================================================
echo [4/8] Kiem tra Android SDK...
if not defined ANDROID_HOME (
    :: Tim Android SDK trong cac thu muc pho bien
    for %%s in (
        "%LOCALAPPDATA%\Android\Sdk"
        "C:\Android\Sdk"
        "%USERPROFILE%\Android\Sdk"
        "%USERPROFILE%\AppData\Local\Android\Sdk"
    ) do (
        if exist "%%~s\platform-tools" (
            set "ANDROID_HOME=%%~s"
        )
    )
)
if defined ANDROID_HOME (
    echo    ANDROID_HOME: %ANDROID_HOME%
    :: Tao local.properties de Gradle tim thay SDK
    echo sdk.dir=%ANDROID_HOME:\=/%> "%MANAGER_DIR%\local.properties"
    echo    Da tao local.properties.
) else (
    echo    [ERROR] Khong tim thay Android SDK!
    echo    Hay cai Android Studio hoac set ANDROID_HOME thu cong.
    echo    Download: https://developer.android.com/studio
    goto :fail
)

:: ============================================================
:: STEP 5: Tao Keystore neu chua co
:: ============================================================
echo [5/8] Kiem tra Keystore...
if not exist "%MANAGER_DIR%\%KS_FILE%" (
    echo    Khong tim thay %KS_FILE%, dang tao moi...
    where keytool >nul 2>&1
    if %ERRORLEVEL% neq 0 (
        echo    [ERROR] Khong tim thay keytool! Hay cai JDK day du.
        goto :fail
    )
    keytool -genkeypair ^
        -alias %KS_ALIAS% ^
        -keyalg RSA -keysize 2048 ^
        -validity 36500 ^
        -storepass "%KS_PASSWORD%" ^
        -keypass "%KS_KEY_PASSWORD%" ^
        -dname "CN=KernelSU, OU=KernelSU, O=KernelSU, L=Unknown, ST=Unknown, C=US" ^
        -storetype JKS ^
        -keystore "%MANAGER_DIR%\%KS_FILE%"
    if %ERRORLEVEL% neq 0 (
        echo    [ERROR] Tao keystore that bai!
        goto :fail
    )
    echo    Da tao %KS_FILE% thanh cong.
) else (
    echo    Tim thay %KS_FILE%.
)

:: ============================================================
:: STEP 6: Ghi Signing Config vao gradle.properties
:: ============================================================
echo [6/8] Cau hinh Signing Key...
findstr /C:"KEYSTORE_PASSWORD" "%GRADLE_PROPS%" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo.>> "%GRADLE_PROPS%"
    echo KEYSTORE_PASSWORD=%KS_PASSWORD%>> "%GRADLE_PROPS%"
    echo KEY_ALIAS=%KS_ALIAS%>> "%GRADLE_PROPS%"
    echo KEY_PASSWORD=%KS_KEY_PASSWORD%>> "%GRADLE_PROPS%"
    echo KEYSTORE_FILE=%KS_FILE%>> "%GRADLE_PROPS%"
    echo    Da ghi signing config vao gradle.properties.
) else (
    echo    Signing config da ton tai.
)

:: ============================================================
:: STEP 7: Build Release APK
:: ============================================================
echo [7/8] Bat dau build Release APK...
echo    Qua trinh nay co the mat 5-15 phut tuy cau hinh may.
echo.

pushd "%MANAGER_DIR%"
call gradlew.bat clean assembleRelease
set BUILD_RESULT=%ERRORLEVEL%
popd

if %BUILD_RESULT% neq 0 (
    echo.
    echo [ERROR] Build that bai! Xem log o tren de biet chi tiet.
    goto :fail
)

:: ============================================================
:: STEP 8: Copy output vao thu muc Release (giong GitHub Release)
:: ============================================================
echo.
echo [8/8] Tao thu muc Release...

:: Xoa Release cu neu co
if exist "%RELEASE_DIR%" rmdir /S /Q "%RELEASE_DIR%"

:: Tao cau truc thu muc giong GitHub Release
mkdir "%RELEASE_DIR%\manager" 2>nul
mkdir "%RELEASE_DIR%\mappings" 2>nul

:: Copy APK
set "APK_COUNT=0"
for %%f in ("%MANAGER_DIR%\app\build\outputs\apk\release\*.apk") do (
    copy /Y "%%f" "%RELEASE_DIR%\manager\" >nul
    echo    APK: %%~nxf
    set /a APK_COUNT+=1
)

if %APK_COUNT%==0 (
    echo    [ERROR] Khong tim thay file APK nao!
    goto :fail
)

:: Copy Mappings (dung de debug crash report)
set "MAP_DIR=%MANAGER_DIR%\app\build\outputs\mapping\release"
if exist "%MAP_DIR%" (
    xcopy /Y /E /Q "%MAP_DIR%\*" "%RELEASE_DIR%\mappings\" >nul 2>&1
    echo    Mappings: da copy.
)

:: ============================================================
:: HOAN TAT
:: ============================================================
echo.
echo ============================================================
echo   BUILD THANH CONG!
echo ============================================================
echo.
echo   Thu muc Release:
echo   %RELEASE_DIR%
echo.
echo   Cau truc output:
echo     Release\
echo       manager\        - Manager APK
echo       mappings\       - ProGuard mapping files
echo.
echo ============================================================

:: Mo thu muc Release
explorer "%RELEASE_DIR%"
goto :end

:fail
echo.
echo ============================================================
echo   BUILD THAT BAI! Xem log o tren de biet chi tiet loi.
echo ============================================================
:end
echo.
pause
exit /b %BUILD_RESULT%
