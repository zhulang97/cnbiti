@echo off
setlocal EnableExtensions
cd /d "%~dp0.."

set "ROOT=%CD%"
set "WEB_DIR=%ROOT%\apps\web"
set "NUXT=%WEB_DIR%\node_modules\.bin\nuxt.CMD"
set "TMP_DIR=%ROOT%\.tmp"
set "HOME_DIR=%TMP_DIR%\web-home"
set "APPDATA_DIR=%TMP_DIR%\web-appdata"
set "LOCALAPPDATA_DIR=%TMP_DIR%\web-localappdata"

set "NUXT_BUILD_DIR=.nuxt-dev-3002-current"
set "NUXT_DEV_PORT=3002"
set "NUXT_DEVTOOLS=false"
set "NUXT_TELEMETRY_DISABLED=1"
set "VITE_CJS_IGNORE_WARNING=true"
set "INIT_CWD=%WEB_DIR%"
set "HOME=%HOME_DIR%"
set "USERPROFILE=%HOME_DIR%"
set "APPDATA=%APPDATA_DIR%"
set "LOCALAPPDATA=%LOCALAPPDATA_DIR%"

if not exist "%TMP_DIR%" mkdir "%TMP_DIR%"
if not exist "%HOME_DIR%" mkdir "%HOME_DIR%"
if not exist "%APPDATA_DIR%" mkdir "%APPDATA_DIR%"
if not exist "%LOCALAPPDATA_DIR%" mkdir "%LOCALAPPDATA_DIR%"

if not exist "%NUXT%" (
  echo Nuxt not found: %NUXT% > "%TMP_DIR%\web-3002.out.log"
  exit /b 1
)

cd /d "%WEB_DIR%"
"%NUXT%" dev --host 127.0.0.1 --port 3002 > "%TMP_DIR%\web-3002.out.log" 2> "%TMP_DIR%\web-3002.err.log"
