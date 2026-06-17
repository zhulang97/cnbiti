Set shell = CreateObject("WScript.Shell")
shell.Run "cmd.exe /c ping -n 6 127.0.0.1 >nul && echo wscript-ok > D:\codex\cnbjti\.tmp\wscript-proof.log", 0, False
