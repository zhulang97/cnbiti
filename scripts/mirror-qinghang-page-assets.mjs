const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'

const sourceHeaders = {
  'user-agent': 'Mozilla/5.0 (compatible; CNBJTI authorized page asset migration)',
}

const assets = [
  {
    key: 'factoryCnc',
    label: 'Titanium CNC Factory',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/03/titanium-CNC-factory_%E5%89%AF%E6%9C%AC1-300x300.jpg',
  },
  {
    key: 'heatTreatment',
    label: 'Titanium Heat Treatment Furnace',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/03/Titanium-Heat-treatment-furnace-1-300x300.jpg',
  },
  {
    key: 'surfaceTreatment',
    label: 'Titanium Sheet Surface Treatment',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/03/Titanium-sheets-Surface-treatment-300x300.jpg',
  },
  {
    key: 'weldedTubes',
    label: 'Titanium Welded Tube Production',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/03/titanium-Welded-tubes-Welded_%E5%89%AF%E6%9C%AC1-300x300.jpg',
  },
  {
    key: 'cuttingFactory',
    label: 'Titanium Cutting Factory',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/03/titanium-cut-factory1_%E5%89%AF%E6%9C%AC-300x300.jpg',
  },
  {
    key: 'sheetFactory',
    label: 'Titanium Sheet Factory',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/03/Titanium-sheets-factory1_%E5%89%AF%E6%9C%AC1-300x300.jpg',
  },
  {
    key: 'cncMachining',
    label: 'Titanium CNC Machining',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Titanium-CNC-300x300.jpg',
  },
  {
    key: 'forging',
    label: 'Titanium Forging',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Titanium-Forging-300x300.jpg',
  },
  {
    key: 'waterJet',
    label: 'Water Jet Cutting',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Water-Jet-Cutting-300x300.jpg',
  },
  {
    key: 'pipeEquipment',
    label: 'Titanium Pipe Making Equipment',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Titanium-Pipe-making-equipment-300x300.jpg',
  },
  {
    key: 'chemicalIndustry',
    label: 'Titanium Chemical Industry Application',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Titanium-Chemical-industry-300x300.jpg',
  },
  {
    key: 'medicalApplication',
    label: 'Titanium Medical Application',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Titanium-medical-300x300.jpg',
  },
  {
    key: 'shipbuilding',
    label: 'Shipbuilding and Desalination Application',
    url: 'https://www.qinghangmetal.com/wp-content/uploads/2023/02/Shipbuilding-and-desalination-industries-300x300.jpg',
  },
]

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const token = login.token
  const uploaded = {}

  for (const asset of assets) {
    const image = await fetchImage(asset.url)
    const filename = `${asset.key}${extensionFor(image.contentType, asset.url)}`
    const form = new FormData()
    form.append('file', new Blob([image.bytes], { type: image.contentType }), filename)
    const result = await api('/public/uploads', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: form,
    })
    uploaded[asset.key] = {
      label: asset.label,
      source: asset.url,
      url: result.url,
      filename,
      mimeType: image.contentType,
      size: image.bytes.byteLength,
    }
    console.log(`${asset.key} -> ${result.url}`)
  }

  console.log(JSON.stringify(uploaded, null, 2))
}

async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      ...(options.body && !(options.body instanceof FormData) ? { 'Content-Type': 'application/json' } : {}),
    },
  })
  const payload = await response.json()
  if (!response.ok || payload.code !== 'OK') {
    throw new Error(`${path}: ${payload.message || response.statusText}`)
  }
  return payload.data
}

async function fetchImage(url) {
  const candidates = [fullSizeImage(url), url]
  let lastError = null
  for (const candidate of candidates) {
    try {
      const response = await fetch(candidate, { headers: sourceHeaders, signal: AbortSignal.timeout(30000) })
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const contentType = normalizeContentType(response.headers.get('content-type'), candidate)
      if (!contentType.startsWith('image/')) throw new Error(`expected image, got ${contentType}`)
      return { url: candidate, contentType, bytes: await response.arrayBuffer() }
    } catch (error) {
      lastError = error
    }
  }
  throw new Error(`${url}: ${lastError?.message || 'image fetch failed'}`)
}

function fullSizeImage(url) {
  const parsed = new URL(url)
  parsed.pathname = parsed.pathname.replace(/-\d+x\d+(?=\.(?:jpe?g|png|webp|gif)$)/i, '')
  return parsed.href
}

function normalizeContentType(value, url) {
  const raw = value?.split(';')[0]?.trim().toLowerCase()
  if (raw) return raw
  const pathname = new URL(url).pathname.toLowerCase()
  if (pathname.endsWith('.png')) return 'image/png'
  if (pathname.endsWith('.webp')) return 'image/webp'
  if (pathname.endsWith('.gif')) return 'image/gif'
  return 'image/jpeg'
}

function extensionFor(contentType, url) {
  if (contentType === 'image/png') return '.png'
  if (contentType === 'image/webp') return '.webp'
  if (contentType === 'image/gif') return '.gif'
  return new URL(url).pathname.match(/\.(jpe?g|png|webp|gif)$/i)?.[0] || '.jpg'
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
