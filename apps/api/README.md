# CNBJTI API

Spring Boot backend for the public CNBJTI site and admin console.

## Local Infrastructure

This project defaults to local MySQL and MinIO. From the repository root:

```bash
pnpm dev:infra
```

The script starts a local MySQL instance from the installed MySQL Server, creates the `cnbjti`
database/user, and checks whether MinIO is healthy on `127.0.0.1:9002`.
If a MySQL service is already listening on `127.0.0.1:3306`, the script uses it and only
bootstraps the database/user. Otherwise it creates a repository-local dev data directory.
If that repository-local dev directory cannot restart cleanly, it is moved to a timestamped
backup and a clean dev database is created.

Stop the local MySQL dev instance cleanly with:

```bash
pnpm stop:mysql
```

If MinIO is installed but not in `PATH`, set `MINIO_BIN` first:

```powershell
$env:MINIO_BIN = "C:\path\to\minio.exe"
pnpm dev:infra
```

Docker remains available as a fallback:

```bash
pnpm dev:infra:docker
```

Default services:

- MySQL: `127.0.0.1:3306`, database `cnbjti`, user `cnbjti`, password `cnbjti`
- MinIO API: `http://127.0.0.1:9002`
- MinIO Console: `http://127.0.0.1:9003`
- MinIO user/password: `minioadmin` / `minioadmin`

If another local tool owns port `9000`, leave it alone and run MinIO on `9002`/`9003`.
The smoke script probes MinIO health and will prefer `http://127.0.0.1:9002`.

## Run API

```bash
mvn -s apps/api/.mvn/settings.xml -f apps/api/pom.xml spring-boot:run
```

The application runs Flyway migrations on startup and seeds the database if it is empty.

Admin login:

- username: `admin`
- password: `cnbjti2026`

For production, set these environment variables before the first API startup:

- `ADMIN_BOOTSTRAP_USERNAME`
- `ADMIN_BOOTSTRAP_DISPLAY_NAME`
- `ADMIN_BOOTSTRAP_EMAIL`
- `ADMIN_BOOTSTRAP_PASSWORD`
- `ADMIN_TOKEN_SECRET` (at least 32 characters, random)
- `ADMIN_TOKEN_TTL` (for example `12h`, `8h`, or `30m`)
- `CORS_ALLOWED_ORIGIN_PATTERNS` (comma-separated, for example `https://admin.example.com,https://www.example.com`)

Bootstrap values are only used when the `admin` account does not already exist. After the first
startup, manage accounts and passwords from the admin console.

Admin tokens are stateless HMAC-signed tokens with an expiry. Each authenticated request still
reloads the admin user from MySQL, so disabling an account immediately prevents further API use
even if the browser still has an old token.

## Storage

Uploads are stored in MinIO bucket `cnbjti-assets`.
Default upload limit is 200 MB. Allowed upload types are images, videos, PDF, plain text,
CSV, Word, and Excel files. Override the size limit with `MINIO_MAX_SIZE_BYTES`.
Admin deletion refuses files that are still referenced by catalog content or RFQ attachments.

Endpoint:

```http
POST /api/public/uploads
Content-Type: multipart/form-data
```

Form field:

- `file`: uploaded file

## Verification

```bash
mvn -s apps/api/.mvn/settings.xml -f apps/api/pom.xml test
mvn -s apps/api/.mvn/settings.xml -f apps/api/pom.xml -DskipTests package
powershell -ExecutionPolicy Bypass -File scripts/smoke-local-api.ps1 -ResetMySqlData
```
