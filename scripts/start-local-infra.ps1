param(
  [string]$MinioBin = $env:MINIO_BIN,
  [int]$MinioPort = $(if ($env:MINIO_PORT) { [int]$env:MINIO_PORT } else { 9002 }),
  [int]$MinioConsolePort = $(if ($env:MINIO_CONSOLE_PORT) { [int]$env:MINIO_CONSOLE_PORT } else { 9003 }),
  [string]$MinioAccessKey = $(if ($env:MINIO_ACCESS_KEY) { $env:MINIO_ACCESS_KEY } else { "minioadmin" }),
  [string]$MinioSecretKey = $(if ($env:MINIO_SECRET_KEY) { $env:MINIO_SECRET_KEY } else { "minioadmin" }),
  [switch]$SkipMySql
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent $PSScriptRoot

if (-not $SkipMySql) {
  & (Join-Path $PSScriptRoot "start-local-mysql.ps1")
}

function Test-TcpPort {
  param([int]$Port)

  $client = [System.Net.Sockets.TcpClient]::new()
  try {
    return $client.ConnectAsync("127.0.0.1", $Port).Wait(500)
  } finally {
    $client.Dispose()
  }
}

function Test-MinioHealth {
  param([string]$Endpoint)

  try {
    $response = Invoke-WebRequest -UseBasicParsing -Uri "$Endpoint/minio/health/live" -TimeoutSec 5
    return $response.StatusCode -eq 200
  } catch {
    return $false
  }
}

$minioEndpoint = "http://127.0.0.1:$MinioPort"
if (Test-MinioHealth $minioEndpoint) {
  Write-Host "MinIO is already healthy at $minioEndpoint"
  exit 0
}

if (Test-TcpPort $MinioPort) {
  Write-Warning "Port $MinioPort is listening, but it did not respond as MinIO. Set MINIO_PORT to a free MinIO API port."
  exit 0
}

if (-not $MinioBin) {
  $command = Get-Command minio.exe -ErrorAction SilentlyContinue
  if ($command) {
    $MinioBin = $command.Source
  }
}

if (-not $MinioBin -or -not (Test-Path $MinioBin)) {
  Write-Warning "MinIO is not listening and minio.exe was not found. Set MINIO_BIN to your local minio.exe path, then rerun this script."
  exit 0
}

$DataDir = Join-Path $RepoRoot ".minio-data"
$RunDir = Join-Path $RepoRoot ".minio-run"
New-Item -ItemType Directory -Force $DataDir, $RunDir | Out-Null

$env:MINIO_ROOT_USER = $MinioAccessKey
$env:MINIO_ROOT_PASSWORD = $MinioSecretKey
$OutLog = Join-Path $RunDir "minio.out.log"
$ErrLog = Join-Path $RunDir "minio.err.log"

Start-Process -FilePath $MinioBin -ArgumentList "server `"$DataDir`" --address `":$MinioPort`" --console-address `":$MinioConsolePort`"" -WindowStyle Hidden -RedirectStandardOutput $OutLog -RedirectStandardError $ErrLog | Out-Null
Write-Host "MinIO starting: http://127.0.0.1:$MinioPort, console=http://127.0.0.1:$MinioConsolePort"
