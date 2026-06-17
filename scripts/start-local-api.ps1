param(
  [int]$Port = 8080,
  [string]$MinioEndpoint = $(if ($env:MINIO_ENDPOINT) { $env:MINIO_ENDPOINT } else { "http://localhost:9002" }),
  [string]$MinioPublicUrl = $(if ($env:MINIO_PUBLIC_URL) { $env:MINIO_PUBLIC_URL } else { "$MinioEndpoint/cnbjti-assets" }),
  [string]$MinioAccessKey = $(if ($env:MINIO_ACCESS_KEY) { $env:MINIO_ACCESS_KEY } else { "minioadmin" }),
  [string]$MinioSecretKey = $(if ($env:MINIO_SECRET_KEY) { $env:MINIO_SECRET_KEY } else { "minioadmin" }),
  [string]$AdminTokenSecret = $(if ($env:ADMIN_TOKEN_SECRET) { $env:ADMIN_TOKEN_SECRET } else { "local-dev-cnbjti-admin-token-secret-2026-change-me" }),
  [string]$AdminTokenTtl = $(if ($env:ADMIN_TOKEN_TTL) { $env:ADMIN_TOKEN_TTL } else { "12h" })
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent $PSScriptRoot
$TmpDir = Join-Path $RepoRoot ".tmp"
$Jar = Join-Path $RepoRoot "apps\api\target\api-0.1.0-SNAPSHOT.jar"
$OutLog = Join-Path $TmpDir "api-$Port.out.log"
$ErrLog = Join-Path $TmpDir "api-$Port.err.log"
$PidFile = Join-Path $TmpDir "api-$Port.pid"

function Test-TcpPort {
  param([int]$Port)
  $client = [System.Net.Sockets.TcpClient]::new()
  try {
    return $client.ConnectAsync("127.0.0.1", $Port).Wait(500)
  } finally {
    $client.Dispose()
  }
}

function Wait-TcpPort {
  param([int]$Port, [int]$Seconds)
  for ($i = 0; $i -lt $Seconds; $i++) {
    if (Test-TcpPort $Port) {
      Start-Sleep -Milliseconds 500
      return
    }
    Start-Sleep -Seconds 1
  }
  throw "API did not become ready on 127.0.0.1:$Port."
}

New-Item -ItemType Directory -Force $TmpDir | Out-Null

if (-not (Test-Path $Jar)) {
  throw "API jar not found: $Jar. Run `mvn -s apps/api/.mvn/settings.xml -f apps/api/pom.xml -DskipTests package` first."
}

if (Test-TcpPort $Port) {
  Write-Host "API already appears to be listening on http://127.0.0.1:$Port"
  exit 0
}

$Java = (Get-Command java -ErrorAction Stop).Source
$Launcher = Join-Path $TmpDir "api-$Port-launch.cmd"
$LauncherContent = @"
@echo off
cd /d "$RepoRoot"
"$Java" -jar "$Jar" --server.port=$Port --MINIO_ENDPOINT=$MinioEndpoint --MINIO_PUBLIC_URL=$MinioPublicUrl --MINIO_ACCESS_KEY=$MinioAccessKey --MINIO_SECRET_KEY=$MinioSecretKey --ADMIN_TOKEN_SECRET=$AdminTokenSecret --ADMIN_TOKEN_TTL=$AdminTokenTtl > "$OutLog" 2> "$ErrLog"
"@
Set-Content -LiteralPath $Launcher -Value $LauncherContent -Encoding ASCII

$StartCommand = "start `"`" /min cmd.exe /c `"$Launcher`""
cmd.exe /c $StartCommand
Wait-TcpPort $Port 90

$ProcessId = $null
$listener = netstat -ano | Select-String "127\.0\.0\.1:$Port\s+.*LISTENING\s+(\d+)" | Select-Object -First 1
if ($listener -and $listener.Line -match '\s+(\d+)$') {
  $ProcessId = [int]$Matches[1]
  Set-Content -LiteralPath $PidFile -Value $ProcessId -Encoding ASCII
}

Write-Host "API started: http://127.0.0.1:$Port"
if ($ProcessId) {
  Write-Host "PID: $ProcessId"
}
Write-Host "Logs: $OutLog"
