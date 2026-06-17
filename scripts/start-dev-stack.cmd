@echo off
setlocal EnableExtensions
cd /d "%~dp0.."

set "ROOT=%CD%"
set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.4"
set "MYSQLD=%MYSQL_HOME%\bin\mysqld.exe"
set "MYSQL=%MYSQL_HOME%\bin\mysql.exe"
set "POWERSHELL=%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe"
set "PNPM=%ROOT%\.npm-cache\_npx\32b21065a482fe57\node_modules\.bin\pnpm.cmd"
set "TMP=%ROOT%\.tmp"
set "RUN=%ROOT%\.mysql-run"

if not exist "%TMP%" mkdir "%TMP%"
if not exist "%RUN%" mkdir "%RUN%"

if not exist "%MYSQLD%" (
  echo MySQL not found: %MYSQLD%
  exit /b 1
)
if not exist "%PNPM%" (
  echo pnpm not found: %PNPM%
  exit /b 1
)
if not exist "%POWERSHELL%" (
  echo PowerShell not found: %POWERSHELL%
  exit /b 1
)

echo Starting CNBJTI dev stack from %ROOT%
echo Logs:
echo   MySQL: %RUN%\mysqld.err
echo   API:   %TMP%\api-8080.out.log / %TMP%\api-8080.err.log
echo   Web:   %TMP%\web-3002.out.log / %TMP%\web-3002.err.log
echo   Admin: %TMP%\admin-3003.out.log / %TMP%\admin-3003.err.log
echo.

echo Starting MySQL...
"%POWERSHELL%" -NoProfile -ExecutionPolicy Bypass -File "%ROOT%\scripts\start-local-mysql.ps1" -MysqlHome "%MYSQL_HOME%"
if errorlevel 1 exit /b 1

netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if errorlevel 1 (
  start "CNBJTI API" "%ROOT%\scripts\run-api-dev.cmd"
) else (
  echo API already listening on 8080.
)

set "NUXT_DEVTOOLS=disabled"
set "NUXT_TELEMETRY_DISABLED=1"
set "VITE_CJS_IGNORE_WARNING=true"

netstat -ano | findstr /R /C:":3002 .*LISTENING" >nul
if errorlevel 1 (
  start "CNBJTI Web 3002" "%ROOT%\scripts\run-web-dev.cmd"
) else (
  echo Web already listening on 3002.
)

netstat -ano | findstr /R /C:":3003 .*LISTENING" >nul
if errorlevel 1 (
  start "CNBJTI Admin 3003" "%ROOT%\scripts\run-admin-dev.cmd"
) else (
  echo Admin already listening on 3003.
)

call :wait_port 8080 API
call :wait_http 3002 /en Web
call :wait_port 3003 Admin

echo.
echo Started stack:
echo   API:   http://127.0.0.1:8080
echo   Web:   http://127.0.0.1:3002/en
echo   Admin: http://127.0.0.1:3003
echo.
echo Service windows are running separately. Close those windows to stop services.
exit /b 0

:wait_port
set "PORT=%~1"
set "NAME=%~2"
echo Waiting for %NAME% on %PORT%...
for /l %%i in (1,1,90) do (
  netstat -ano | findstr /R /C:":%PORT% .*LISTENING" >nul
  if not errorlevel 1 (
    echo %NAME% ready on %PORT%.
    exit /b 0
  )
  ping -n 2 127.0.0.1 >nul
)
echo %NAME% did not become ready on %PORT%.
exit /b 0

:wait_http
set "PORT=%~1"
set "URL_PATH=%~2"
set "NAME=%~3"
echo Waiting for %NAME% on %PORT%%URL_PATH%...
for /l %%i in (1,1,90) do (
  "%POWERSHELL%" -NoProfile -ExecutionPolicy Bypass -Command "try { $r = Invoke-WebRequest -UseBasicParsing 'http://127.0.0.1:%PORT%%URL_PATH%' -TimeoutSec 3; if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 500) { exit 0 } } catch { }; exit 1" >nul 2>nul
  if not errorlevel 1 (
    echo %NAME% ready on %PORT%%URL_PATH%.
    exit /b 0
  )
  ping -n 2 127.0.0.1 >nul
)
echo %NAME% did not become ready on %PORT%%URL_PATH%.
exit /b 0
