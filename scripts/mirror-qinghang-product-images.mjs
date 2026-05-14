const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'
const SOURCE_BASE = 'https://www.qinghangmetal.com'
const FORCE_IMAGE_MIRROR = process.env.CNBJTI_FORCE_IMAGE_MIRROR === '1'
const LOCAL_ASSET_PATTERN = /^http:\/\/(?:localhost|127\.0\.0\.1):(?:9000|9002)\/cnbjti-assets\//i

const products = [
  ['titanium-elbows', '/titanium-elbows/'],
  ['titanium-bolts-and-nuts', '/titanium-bolts-and-nuts/'],
  ['titanium-bars', '/titanium-bars/'],
  ['titanium-flanges', '/titanium-flanges/'],
  ['titanium-wire', '/titanium-wire/'],
  ['titanium-sheets', '/titanium-sheets/'],
  ['titanium-forgings', '/titanium-forgings/'],
  ['titanium-welded-tubes', '/titanium-welded-tubes/'],
  ['titanium-standard-parts', '/titanium-standard-parts/'],
  ['titanium-pipe-cross', '/titanium-pipe-cross/'],
  ['titanium-plates', '/titanium-plate/'],
  ['titanium-tee', '/titanium-tee/'],
  ['titanium-tubes', '/titanium-tubes/'],
  ['titanium-foil', '/titanium-foil/'],
  ['titanium-coil', '/titanium-coil/'],
  ['titanium-valve', '/titanium-valve/'],
  ['titanium-spring', '/titanium-spring/'],
  ['titanium-washer', '/titanium-washer/'],
  ['titanium-channel', '/titanium-channel/'],
  ['titanium-card', '/titanium-card/'],
  ['titanium-rack', '/titanium-rack/'],
  ['titanium-nipple', '/titanium-nipple/'],
  ['titanium-disc', '/titanium-disc/'],
  ['titanium-anode', '/titanium-anode/'],
  ['titanium-powder', '/titanium-powder/'],
]

const sourceHeaders = {
  'user-agent': 'Mozilla/5.0 (compatible; CNBJTI authorized media migration)',
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

async function fetchText(url) {
  const response = await fetch(url, { headers: sourceHeaders })
  if (!response.ok) throw new Error(`${url}: HTTP ${response.status}`)
  return response.text()
}

async function fetchImage(url) {
  const response = await fetch(url, { headers: sourceHeaders })
  if (!response.ok) throw new Error(`${url}: HTTP ${response.status}`)
  const contentType = normalizeContentType(response.headers.get('content-type'), url)
  if (!contentType.startsWith('image/')) {
    throw new Error(`${url}: expected image content, got ${contentType}`)
  }
  return {
    bytes: await response.arrayBuffer(),
    contentType,
  }
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

function imageExtension(contentType, url) {
  if (contentType === 'image/png') return '.png'
  if (contentType === 'image/webp') return '.webp'
  if (contentType === 'image/gif') return '.gif'
  const pathname = new URL(url).pathname.toLowerCase()
  const extension = pathname.match(/\.(jpe?g|png|webp|gif)$/i)?.[0]
  return extension || '.jpg'
}

function findProductImage(html, pageUrl) {
  const bodyImages = Array.from(html.matchAll(/<img\b[^>]*>/gi))
    .map((match) => {
      const tag = match[0]
      const src = attr(tag, 'src') || attr(tag, 'data-src')
      return src ? {
        src: absoluteUrl(src, pageUrl),
        alt: attr(tag, 'alt'),
        title: attr(tag, 'title'),
        className: attr(tag, 'class'),
      } : null
    })
    .filter(Boolean)
    .filter((image) => isProductImage(image.src, `${image.alt} ${image.title} ${image.className}`))

  if (bodyImages.length) return fullSizeImage(bodyImages[0].src)

  const metaImages = Array.from(html.matchAll(/<meta\b[^>]*>/gi))
    .filter((match) => /\b(?:property|name)=["'](?:og:image|twitter:image)["']/i.test(match[0]))
    .map((match) => attr(match[0], 'content'))
    .filter(Boolean)
    .map((value) => absoluteUrl(value, pageUrl))
    .filter((url) => isProductImage(url))

  if (metaImages.length) return fullSizeImage(metaImages[0])
  throw new Error(`No product image found on ${pageUrl}`)
}

function attr(tag, name) {
  const match = tag.match(new RegExp(`\\s${name}=["']([^"']+)["']`, 'i'))
  return match ? decodeHtml(match[1]) : ''
}

function decodeHtml(value) {
  return value
    .replace(/&amp;/g, '&')
    .replace(/&#038;/g, '&')
    .replace(/&quot;/g, '"')
    .replace(/&#039;/g, "'")
}

function absoluteUrl(value, base) {
  return new URL(value, base).href
}

function isProductImage(url, metadata = '') {
  return /qinghangmetal\.com\/wp-content\/uploads\//i.test(url)
    && !/logo|icon|avatar|susan|wechat|whatsapp|qr-code/i.test(`${url} ${metadata}`)
}

function fullSizeImage(url) {
  const parsed = new URL(url)
  parsed.pathname = parsed.pathname.replace(/-\d+x\d+(?=\.(?:jpe?g|png|webp|gif)$)/i, '')
  return parsed.href
}

async function uploadImage(token, slug, imageUrl, productName) {
  const image = await fetchImage(imageUrl)
  const filename = `${slug}${imageExtension(image.contentType, imageUrl)}`
  const form = new FormData()
  form.append('file', new Blob([image.bytes], { type: image.contentType }), filename)
  const asset = await api('/public/uploads', {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: form,
  })
  return {
    ...asset,
    alt: productName,
    filename,
    mimeType: image.contentType,
    size: image.bytes.byteLength,
  }
}

function cleanDescription(value = '') {
  return value
    .replace(/\n\nReference product taxonomy:[\s\S]*?Content rewritten for CNBJTI product presentation\./g, '')
    .trim()
}

function cleanSpecs(specs = []) {
  return specs.filter((spec) => !/^Source reference$/i.test(spec.label || ''))
}

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const token = login.token
  const authHeaders = { Authorization: `Bearer ${token}` }
  const list = await api('/admin/products', { headers: authHeaders })
  const detailsBySlug = new Map()

  for (const item of list) {
    const detail = await api(`/admin/products/${encodeURIComponent(item.id)}`, { headers: authHeaders })
    detailsBySlug.set(detail.slug, detail)
  }

  const results = []
  for (const [slug, sourcePath] of products) {
    const detail = detailsBySlug.get(slug)
    if (!detail) {
      results.push({ slug, status: 'skipped', reason: 'missing local product' })
      continue
    }

    const sourceUrl = new URL(sourcePath, SOURCE_BASE).href
    let imageUrl = null
    let skippedUpload = false
    let asset = detail.images?.[0]
    if (!FORCE_IMAGE_MIRROR && LOCAL_ASSET_PATTERN.test(detail.imageUrl || '') && LOCAL_ASSET_PATTERN.test(asset?.url || '')) {
      skippedUpload = true
    } else {
      const html = await fetchText(sourceUrl)
      imageUrl = findProductImage(html, sourceUrl)
      asset = await uploadImage(token, slug, imageUrl, detail.name)
    }
    const images = [{
      id: asset.id,
      url: asset.url,
      thumbnailUrl: asset.thumbnailUrl || null,
      alt: detail.name,
      width: asset.width || null,
      height: asset.height || null,
      mimeType: asset.mimeType,
      size: asset.size,
      filename: asset.filename,
    }]

    await api(`/admin/products/${encodeURIComponent(detail.id)}`, {
      method: 'PUT',
      headers: authHeaders,
      body: JSON.stringify({
        ...detail,
        description: cleanDescription(detail.description),
        imageUrl: asset.url,
        images,
        specs: cleanSpecs(detail.specs),
        seo: {
          ...(detail.seo || {}),
          ogImage: asset.url,
        },
      }),
    })

    results.push({
      slug,
      status: 'updated',
      skippedUpload,
      sourceImage: imageUrl,
      minioUrl: asset.url,
    })
  }

  const updated = results.filter((item) => item.status === 'updated').length
  console.log(JSON.stringify({ updated, total: products.length, results }, null, 2))
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
