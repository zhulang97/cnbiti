import { spawn } from 'node:child_process'
import { writeFileSync } from 'node:fs'

const child = spawn(
  'cmd.exe',
  ['/c', 'ping -n 6 127.0.0.1 >nul && echo detached-ok > D:\\codex\\cnbjti\\.tmp\\detached-proof-file.log'],
  {
    detached: true,
    stdio: 'ignore',
    windowsHide: true,
  },
)

writeFileSync('D:\\codex\\cnbjti\\.tmp\\detached-proof-file.pid', String(child.pid))
child.unref()
