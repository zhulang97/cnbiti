import { spawn, spawnSync } from 'node:child_process'
import { readFile } from 'node:fs/promises'
import { dirname, join } from 'node:path'
import { setTimeout as sleep } from 'node:timers/promises'
import { fileURLToPath } from 'node:url'

const rootPath = dirname(dirname(fileURLToPath(import.meta.url)))
const script = join(rootPath, 'scripts', 'run-web-dev.cmd')
const outLog = join(rootPath, '.tmp', 'web-3002.out.log')
const errLog = join(rootPath, '.tmp', 'web-3002.err.log')

const child = spawn('cmd.exe', ['/c', script], {
  cwd: rootPath,
  detached: false,
  stdio: 'ignore',
  windowsHide: true,
})

let status = null
let length = 0
let error = null

try {
  for (let attempt = 0; attempt < 80; attempt += 1) {
    await sleep(500)
    try {
      const response = await fetch('http://127.0.0.1:3002/')
      status = response.status
      const text = await response.text()
      length = text.length
      if (response.ok && text.includes('CNBJTI')) {
        break
      }
    } catch (err) {
      error = err
    }
  }
} finally {
  spawnSync('taskkill.exe', ['/PID', String(child.pid), '/T', '/F'], {
    stdio: 'ignore',
    windowsHide: true,
  })
  child.unref()
}

console.log(`WEB_STATUS=${status ?? 'NO_RESPONSE'}`)
console.log(`WEB_LENGTH=${length}`)
if (error && status === null) {
  console.log(`WEB_ERROR=${error.message}`)
}

for (const [label, path] of [['OUT', outLog], ['ERR', errLog]]) {
  try {
    const content = await readFile(path, 'utf8')
    console.log(`---${label}---`)
    console.log(content.slice(-4000))
  } catch {
    console.log(`---${label}: missing---`)
  }
}
