@echo off
setlocal EnableExtensions
cd /d "%~dp0.."

if not exist ".tmp" mkdir ".tmp"

java -jar "%CD%\apps\api\target\api-0.1.0-SNAPSHOT.jar" --server.port=8080 --MINIO_ENDPOINT=http://localhost:9002 --MINIO_PUBLIC_URL=http://localhost:9002/cnbjti-assets --MINIO_ACCESS_KEY=%MINIO_ACCESS_KEY% --MINIO_SECRET_KEY=%MINIO_SECRET_KEY% --ADMIN_TOKEN_SECRET=local-dev-cnbjti-admin-token-secret-2026-change-me --ADMIN_TOKEN_TTL=12h > "%CD%\.tmp\api-8080.out.log" 2> "%CD%\.tmp\api-8080.err.log"
