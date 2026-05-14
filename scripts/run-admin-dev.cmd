@echo off
setlocal EnableExtensions
cd /d "%~dp0.."

set "PNPM=%CD%\.npm-cache\_npx\32b21065a482fe57\node_modules\.bin\pnpm.cmd"

if not exist ".tmp" mkdir ".tmp"

"%PNPM%" --filter admin dev --host 127.0.0.1 --port 3003 > "%CD%\.tmp\admin-3003.out.log" 2> "%CD%\.tmp\admin-3003.err.log"
