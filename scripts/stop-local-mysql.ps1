param(
  [string]$MysqlHome = $env:MYSQL_HOME,
  [int]$Port = 3306
)

$ErrorActionPreference = "Stop"

function Find-MySqlHome {
  param([string]$RequestedHome)

  if ($RequestedHome -and (Test-Path (Join-Path $RequestedHome "bin\mysqladmin.exe"))) {
    return $RequestedHome
  }

  $candidates = @(
    "C:\Program Files\MySQL\MySQL Server 8.4",
    "C:\Program Files\MySQL\MySQL Server 8.3",
    "C:\Program Files\MySQL\MySQL Server 8.0"
  )

  foreach ($candidate in $candidates) {
    if (Test-Path (Join-Path $candidate "bin\mysqladmin.exe")) {
      return $candidate
    }
  }

  $command = Get-Command mysqladmin.exe -ErrorAction SilentlyContinue
  if ($command) {
    return Split-Path -Parent (Split-Path -Parent $command.Source)
  }

  throw "Could not find mysqladmin.exe. Set MYSQL_HOME to your MySQL install directory."
}

$MysqlHome = Find-MySqlHome $MysqlHome
$MysqlAdmin = Join-Path $MysqlHome "bin\mysqladmin.exe"

& $MysqlAdmin --protocol=tcp --host=127.0.0.1 --port=$Port --user=root shutdown
if ($LASTEXITCODE -ne 0) {
  throw "MySQL shutdown failed."
}

Write-Host "MySQL stopped: 127.0.0.1:$Port"
