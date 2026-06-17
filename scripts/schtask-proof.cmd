@echo off
cd /d "%~dp0.."
if not exist ".tmp" mkdir ".tmp"
echo ok > ".tmp\schtask-proof.log"
