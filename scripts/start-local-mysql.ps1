param(
  [string]$MysqlHome = $env:MYSQL_HOME,
  [string]$Database = "cnbjti",
  [string]$AppUser = "cnbjti",
  [string]$AppPassword = "cnbjti",
  [int]$Port = 3306,
  [switch]$ResetData
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent $PSScriptRoot
$DataDir = Join-Path $RepoRoot ".mysql-data"
$RunDir = Join-Path $RepoRoot ".mysql-run"

function Find-MySqlHome {
  param([string]$RequestedHome)

  if ($RequestedHome -and (Test-Path (Join-Path $RequestedHome "bin\mysqld.exe"))) {
    return $RequestedHome
  }

  $candidates = @(
    "C:\Program Files\MySQL\MySQL Server 8.4",
    "C:\Program Files\MySQL\MySQL Server 8.3",
    "C:\Program Files\MySQL\MySQL Server 8.0"
  )

  foreach ($candidate in $candidates) {
    if (Test-Path (Join-Path $candidate "bin\mysqld.exe")) {
      return $candidate
    }
  }

  $command = Get-Command mysqld.exe -ErrorAction SilentlyContinue
  if ($command) {
    return Split-Path -Parent (Split-Path -Parent $command.Source)
  }

  throw "Could not find mysqld.exe. Set MYSQL_HOME to your MySQL install directory."
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

function Wait-ForMySql {
  param(
    [string]$Mysql,
    [int]$Port
  )

  for ($i = 0; $i -lt 45; $i++) {
    if ((Test-TcpPort $Port) -and (Test-MySqlRootLogin $Mysql $Port)) {
      return
    }
    Start-Sleep -Seconds 1
  }

  throw "MySQL did not become SQL-ready on 127.0.0.1:$Port."
}

function Test-MySqlRootLogin {
  param(
    [string]$Mysql,
    [int]$Port
  )

  $previousErrorActionPreference = $ErrorActionPreference
  try {
    $ErrorActionPreference = "Continue"
    & $Mysql --protocol=tcp --host=127.0.0.1 --port=$Port --user=root -N -e "SELECT 1" *> $null
    return $LASTEXITCODE -eq 0
  } finally {
    $ErrorActionPreference = $previousErrorActionPreference
  }
}

function Test-MySqlLogin {
  param(
    [string]$Mysql,
    [int]$Port,
    [string]$User,
    [string]$Password,
    [string]$Database
  )

  $previousMysqlPwd = $env:MYSQL_PWD
  $previousErrorActionPreference = $ErrorActionPreference
  try {
    $env:MYSQL_PWD = $Password
    $ErrorActionPreference = "Continue"
    & $Mysql --protocol=tcp --host=127.0.0.1 --port=$Port --user=$User -N -e "SELECT 1" $Database *> $null
    return $LASTEXITCODE -eq 0
  } finally {
    if ($null -eq $previousMysqlPwd) {
      Remove-Item Env:\MYSQL_PWD -ErrorAction SilentlyContinue
    } else {
      $env:MYSQL_PWD = $previousMysqlPwd
    }
    $ErrorActionPreference = $previousErrorActionPreference
  }
}

function Move-UndoFiles {
  param([string]$DataDir, [string]$RunDir, [string]$Reason)

  $backupDir = Join-Path $RunDir ("initial-undo-backup-" + (Get-Date -Format "yyyyMMdd-HHmmss"))
  $moved = $false
  foreach ($name in @("undo_001", "undo_002", "undo_1_trunc.log", "undo_2_trunc.log")) {
    $path = Join-Path $DataDir $name
    if (Test-Path $path) {
      New-Item -ItemType Directory -Force $backupDir | Out-Null
      Move-Item -LiteralPath $path -Destination (Join-Path $backupDir $name) -Force
      $moved = $true
    }
  }

  if ($moved) {
    Write-Host "Moved MySQL undo files for recovery: $Reason"
  }
}

function Start-MySqlProcess {
  param(
    [string]$Mysqld,
    [string]$MysqlHome,
    [string]$DataDir,
    [string]$RunDir,
    [int]$Port
  )

  $ErrLog = Join-Path $RunDir "mysqld.err"
  $ArgLine = "--no-defaults `"--basedir=$MysqlHome`" `"--datadir=$DataDir`" --port=$Port --bind-address=127.0.0.1 `"--socket=$RunDir\mysql.sock`" `"--pid-file=$RunDir\mysqld.pid`" `"--log-error=$ErrLog`" --mysqlx=0"
  $Launcher = Join-Path $RunDir "mysqld-launch.cmd"
  $LauncherContent = @"
@echo off
"$Mysqld" $ArgLine
"@
  Set-Content -LiteralPath $Launcher -Value $LauncherContent -Encoding ASCII
  cmd.exe /c "start `"CNBJTI MySQL $Port`" cmd.exe /k `"$Launcher`""
}

function Backup-LocalMySqlDirs {
  param([string]$RepoRoot)

  $stamp = Get-Date -Format "yyyyMMdd-HHmmss"
  foreach ($name in @(".mysql-data", ".mysql-run")) {
    $path = Join-Path $RepoRoot $name
    if (Test-Path $path) {
      $resolved = (Resolve-Path $path).Path
      if (-not $resolved.StartsWith($RepoRoot)) {
        throw "Refusing to move path outside workspace: $resolved"
      }
      Move-Item -LiteralPath $resolved -Destination (Join-Path $RepoRoot "$name.bak-$stamp")
    }
  }
}

function Initialize-DataDir {
  param([string]$Mysqld, [string]$MysqlHome, [string]$DataDir, [string]$RunDir)

  if (Test-Path (Join-Path $DataDir "mysql")) {
    return $false
  }

  & $Mysqld --no-defaults --initialize-insecure "--basedir=$MysqlHome" "--datadir=$DataDir"
  if ($LASTEXITCODE -ne 0) {
    throw "MySQL data directory initialization failed."
  }
  Move-UndoFiles $DataDir $RunDir "first start after initialization"
  return $true
}

$MysqlHome = Find-MySqlHome $MysqlHome
$Mysqld = Join-Path $MysqlHome "bin\mysqld.exe"
$Mysql = Join-Path $MysqlHome "bin\mysql.exe"

$existing = Test-TcpPort $Port
if ($existing) {
  if (Test-MySqlLogin $Mysql $Port $AppUser $AppPassword $Database) {
    Write-Host "MySQL is ready: 127.0.0.1:$Port, database=$Database, user=$AppUser"
    exit 0
  }

  $Sql = @"
CREATE DATABASE IF NOT EXISTS $Database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$AppUser'@'localhost' IDENTIFIED BY '$AppPassword';
CREATE USER IF NOT EXISTS '$AppUser'@'127.0.0.1' IDENTIFIED BY '$AppPassword';
ALTER USER '$AppUser'@'localhost' IDENTIFIED BY '$AppPassword';
ALTER USER '$AppUser'@'127.0.0.1' IDENTIFIED BY '$AppPassword';
GRANT ALL PRIVILEGES ON $Database.* TO '$AppUser'@'localhost';
GRANT ALL PRIVILEGES ON $Database.* TO '$AppUser'@'127.0.0.1';
FLUSH PRIVILEGES;
"@

  $Sql | & $Mysql --protocol=tcp --host=127.0.0.1 --port=$Port --user=root
  if ($LASTEXITCODE -eq 0 -and (Test-MySqlLogin $Mysql $Port $AppUser $AppPassword $Database)) {
    Write-Host "MySQL is ready: 127.0.0.1:$Port, database=$Database, user=$AppUser"
    exit 0
  }

  throw "MySQL is already listening on 127.0.0.1:$Port, but the dev database login failed. Stop that MySQL process or fix the cnbjti user before running local recovery."
}

if ($ResetData) {
  Backup-LocalMySqlDirs $RepoRoot
}
New-Item -ItemType Directory -Force $DataDir, $RunDir | Out-Null

$freshData = Initialize-DataDir $Mysqld $MysqlHome $DataDir $RunDir

if (-not $existing) {
  Start-MySqlProcess $Mysqld $MysqlHome $DataDir $RunDir $Port
  try {
    Wait-ForMySql $Mysql $Port
  } catch {
    if ($freshData) {
      throw
    }
    Write-Warning "Existing local MySQL dev data did not start. Backing it up and recreating a clean dev database."
    Backup-LocalMySqlDirs $RepoRoot
    New-Item -ItemType Directory -Force $DataDir, $RunDir | Out-Null
    Initialize-DataDir $Mysqld $MysqlHome $DataDir $RunDir | Out-Null
    Start-MySqlProcess $Mysqld $MysqlHome $DataDir $RunDir $Port
    Wait-ForMySql $Mysql $Port
  }
} else {
  Wait-ForMySql $Mysql $Port
}

$Sql = @"
CREATE DATABASE IF NOT EXISTS $Database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$AppUser'@'localhost' IDENTIFIED BY '$AppPassword';
CREATE USER IF NOT EXISTS '$AppUser'@'127.0.0.1' IDENTIFIED BY '$AppPassword';
ALTER USER '$AppUser'@'localhost' IDENTIFIED BY '$AppPassword';
ALTER USER '$AppUser'@'127.0.0.1' IDENTIFIED BY '$AppPassword';
GRANT ALL PRIVILEGES ON $Database.* TO '$AppUser'@'localhost';
GRANT ALL PRIVILEGES ON $Database.* TO '$AppUser'@'127.0.0.1';
FLUSH PRIVILEGES;
"@

$Sql | & $Mysql --protocol=tcp --host=127.0.0.1 --port=$Port --user=root
if ($LASTEXITCODE -ne 0) {
  throw "MySQL bootstrap failed."
}

Write-Host "MySQL is ready: 127.0.0.1:$Port, database=$Database, user=$AppUser"
