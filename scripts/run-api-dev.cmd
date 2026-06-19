@echo off
setlocal EnableExtensions
cd /d "%~dp0.."

if not exist ".tmp" mkdir ".tmp"

if "%MINIO_ACCESS_KEY%"=="" set "MINIO_ACCESS_KEY=minioadmin"
if "%MINIO_SECRET_KEY%"=="" set "MINIO_SECRET_KEY=minioadmin"
if "%MINIO_ENDPOINT%"=="" set "MINIO_ENDPOINT=http://localhost:9002"
if "%MINIO_PUBLIC_URL%"=="" set "MINIO_PUBLIC_URL=https://cnbjti.com/cnbjti-assets"

java -jar "%CD%\apps\api\target\api-0.1.0-SNAPSHOT.jar" --server.port=8080 --MINIO_ENDPOINT=%MINIO_ENDPOINT% --MINIO_PUBLIC_URL=%MINIO_PUBLIC_URL% --MINIO_ACCESS_KEY=%MINIO_ACCESS_KEY% --MINIO_SECRET_KEY=%MINIO_SECRET_KEY% --ADMIN_TOKEN_SECRET=local-dev-cnbjti-admin-token-secret-2026-change-me --ADMIN_TOKEN_TTL=12h > "%CD%\.tmp\api-8080.out.log" 2> "%CD%\.tmp\api-8080.err.log"
