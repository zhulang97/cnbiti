import fs from 'node:fs/promises'

const WEB_BASE = (process.env.WEB_BASE || 'http://127.0.0.1:3002').replace(/\/$/, '')
const API_BASE = (process.env.API_BASE || 'http://127.0.0.1:8080/api').replace(/\/$/, '')
const MINIO_HEALTH = process.env.MINIO_HEALTH || 'http://127.0.0.1:9002/minio/health/live'
const REQUEST_TIMEOUT_MS = Number(process.env.SMOKE_TIMEOUT_MS || 20000)
const CONCURRENCY = Number(process.env.SMOKE_CONCURRENCY || 8)
const LOCAL_MINIO_PATTERN = /^http:\/\/(?:localhost|127\.0\.0\.1):(?:9000|9002)\/cnbjti-assets\//i

const checks = []
const warnings = []

function record(name, ok, details = '') {
  checks.push({ name, ok, details })
  const status = ok ? 'OK  ' : 'FAIL'
  console.log(`${status} ${name}${details ? ` :: ${details}` : ''}`)
}

function warn(name, details = '') {
  warnings.push({ name, details })
  console.log(`WARN ${name}${details ? ` :: ${details}` : ''}`)
}

async function withTimeout(promise, timeoutMs = REQUEST_TIMEOUT_MS) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)
  try {
    return await promise(controller.signal)
  } finally {
    clearTimeout(timer)
  }
}

async function fetchRaw(url, options = {}) {
  return withTimeout((signal) => fetch(url, { ...options, signal }))
}

async function fetchText(url, options = {}) {
  const response = await fetchRaw(url, options)
  const text = await response.text()
  return { response, text }
}

async function fetchJson(url, options = {}) {
  const { response, text } = await fetchText(url, {
    headers: { 'content-type': 'application/json', ...(options.headers || {}) },
    ...options,
  })
  let body
  try {
    body = JSON.parse(text)
  } catch {
    throw new Error(`Invalid JSON from ${url}: ${text.slice(0, 160)}`)
  }
  if (!response.ok) {
    throw new Error(`HTTP ${response.status} from ${url}: ${body.message || text.slice(0, 160)}`)
  }
  return body
}

function unwrap(envelope) {
  if (!envelope || envelope.code !== 'OK') {
    throw new Error(envelope?.message || 'API envelope was not OK')
  }
  return envelope.data
}

function asWebUrl(pathOrUrl) {
  if (/^https?:\/\//i.test(pathOrUrl)) return pathOrUrl
  const normalized = pathOrUrl.startsWith('/') ? pathOrUrl : `/${pathOrUrl}`
  return `${WEB_BASE}${normalized}`
}

function normalizeInternalHref(href) {
  if (!href || href.startsWith('#')) return null
  if (/^(mailto|tel|javascript):/i.test(href)) return null
  try {
    const url = new URL(href, WEB_BASE)
    if (url.origin !== new URL(WEB_BASE).origin) return null
    return `${url.pathname}${url.search}` || '/'
  } catch {
    return null
  }
}

function extractAttrs(html, attr) {
  const values = []
  const pattern = new RegExp(`${attr}\\s*=\\s*["']([^"']+)["']`, 'gi')
  for (const match of html.matchAll(pattern)) values.push(match[1])
  return values
}

function extractAnchors(html) {
  return extractAttrs(html, 'href')
}

function extractImages(html) {
  return extractAttrs(html, 'src')
    .filter((value) => value && !value.startsWith('data:') && !value.startsWith('blob:'))
}

function decodeXml(value) {
  return value
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&apos;/g, "'")
}

async function mapLimit(items, limit, fn) {
  const results = new Array(items.length)
  let next = 0
  const workers = Array.from({ length: Math.min(limit, items.length) }, async () => {
    while (next < items.length) {
      const current = next++
      results[current] = await fn(items[current], current)
    }
  })
  await Promise.all(workers)
  return results
}

async function checkHttpOk(name, url, options = {}) {
  try {
    const { response, text } = await fetchText(url, options)
    const type = response.headers.get('content-type') || ''
    const ok = response.ok && response.status < 400
    record(name, ok, `${response.status} ${type}`)
    return { ok, status: response.status, text, type, url }
  } catch (error) {
    record(name, false, error instanceof Error ? error.message : String(error))
    return { ok: false, status: 0, text: '', type: '', url }
  }
}

async function main() {
  console.log(`CNBJTI public web smoke`)
  console.log(`WEB_BASE=${WEB_BASE}`)
  console.log(`API_BASE=${API_BASE}`)
  console.log(`MINIO_HEALTH=${MINIO_HEALTH}`)

  await checkHttpOk('MinIO health', MINIO_HEALTH)
  await checkHttpOk('API site config', `${API_BASE}/public/site-config`)
  await checkHttpOk('Web home', `${WEB_BASE}/en`)

  const [categoryEnvelope, productEnvelope, gradeEnvelope, standardEnvelope, articleEnvelope] = await Promise.all([
    fetchJson(`${API_BASE}/public/categories`),
    fetchJson(`${API_BASE}/public/products`),
    fetchJson(`${API_BASE}/public/grades`),
    fetchJson(`${API_BASE}/public/standards`),
    fetchJson(`${API_BASE}/public/articles`),
  ])

  const categories = unwrap(categoryEnvelope)
  const products = unwrap(productEnvelope)
  const grades = unwrap(gradeEnvelope)
  const standards = unwrap(standardEnvelope)
  const articles = unwrap(articleEnvelope)
  const faqArticles = articles.filter((article) => article.category === 'FAQ')
  const minioProducts = products.filter((product) => (product.images || []).some((image) => LOCAL_MINIO_PATTERN.test(image.url || '')))

  record('Public categories loaded', categories.length >= 8, `${categories.length} categories`)
  record('Public products loaded', products.length >= 90, `${products.length} products`)
  record('Imported product images mirrored to MinIO', minioProducts.length >= 80, `${minioProducts.length} products with MinIO images`)
  record('Public articles loaded', articles.length >= 142, `${articles.length} articles`)
  record('FAQ article library loaded', faqArticles.length >= 80, `${faqArticles.length} FAQ articles`)

  const industryFile = await fs.readFile(new URL('../apps/web/utils/industryContent.ts', import.meta.url), 'utf8')
  const industrySlugs = [...industryFile.matchAll(/slug:\s*'([^']+)'/g)].map((match) => match[1])

  const staticRoutes = [
    '/en',
    '/en/products',
    '/en/grades',
    '/en/standards',
    '/en/processing',
    '/en/industries',
    '/en/resources',
    '/en/faq',
    '/en/quality',
    '/en/about',
    '/en/contact',
    '/en/request-a-quote',
    '/en/privacy',
    '/en/terms',
    '/en/cookies',
    '/en/resources/titanium-weight-calculator',
    '/en/resources/titanium-grade-guide',
    '/en/resources/astm-b348-titanium-bar-standard',
    '/en/resources/titanium-corrosion-resistance',
  ]
  const categoryRoutes = categories.map((category) => `/en/products/${category.slug}`)
  const productRoutes = products
    .filter((product) => product.category?.slug)
    .map((product) => `/en/products/${product.category.slug}/${product.slug}`)
  const gradeRoutes = grades.map((grade) => `/en/grades/${grade.slug}`)
  const standardRoutes = standards.map((standard) => `/en/standards/${standard.slug}`)
  const industryRoutes = industrySlugs.map((slug) => `/en/industries/${slug}`)
  const articleRoutes = articles.map((article) => `/en/resources/${article.slug}`)
  const seoRoutes = ['/sitemap.xml', '/robots.txt']
  const allRoutes = [...new Set([
    ...staticRoutes,
    ...categoryRoutes,
    ...productRoutes,
    ...gradeRoutes,
    ...standardRoutes,
    ...industryRoutes,
    ...articleRoutes,
    ...seoRoutes,
  ])]

  const routeResults = await mapLimit(allRoutes, CONCURRENCY, async (route) => {
    const result = await checkHttpOk(`Route ${route}`, asWebUrl(route))
    if (result.ok && /qinghangmetal\.com/i.test(result.text)) {
      record(`No external Qinghang URL in ${route}`, false, 'found qinghangmetal.com in rendered HTML')
    }
    const suspiciousText = result.text.match(/(鈥|脳|鲁|馃|ï¼|梅|路)/)
    if (result.ok && suspiciousText) {
      warn(`Possible mojibake in ${route}`, `matched ${suspiciousText[1]}`)
    }
    return { route, ...result }
  })
  const failedRoutes = routeResults.filter((result) => !result.ok)
  record('All public routes return 2xx/3xx', failedRoutes.length === 0, failedRoutes.length ? `${failedRoutes.length} failed` : `${allRoutes.length} routes`)

  const keyRoutes = [
    '/en',
    '/en/products',
    categoryRoutes[0],
    productRoutes.find((route) => route.includes('titanium-hex-nuts')) || productRoutes[0],
    '/en/resources',
    articleRoutes.find((route) => route.includes('does-titanium-tarnish')) || articleRoutes[0],
    '/en/faq',
    '/en/contact',
    '/en/request-a-quote',
  ].filter(Boolean)

  const keyHtml = new Map()
  for (const route of keyRoutes) {
    const result = routeResults.find((item) => item.route === route) || await checkHttpOk(`Route ${route}`, asWebUrl(route))
    keyHtml.set(route, result.text)
  }

  const internalLinks = new Set()
  const externalTargets = new Set()
  for (const [route, html] of keyHtml) {
    for (const href of extractAnchors(html)) {
      const internal = normalizeInternalHref(href)
      if (internal) internalLinks.add(internal)
      else if (/^https?:\/\//i.test(href) || /^mailto:/i.test(href)) externalTargets.add(href)
    }
    const hasButtonOrLink = /<button\b|<a\b/i.test(html)
    record(`Interactive controls rendered on ${route}`, hasButtonOrLink, hasButtonOrLink ? 'button/link markup found' : 'no button/link markup')
  }

  for (const href of [...externalTargets]) {
    if (/^mailto:/i.test(href)) {
      record(`External target ${href}`, /^mailto:sales@cnbjti\.com/i.test(href), 'mailto pattern')
    } else if (/wa\.me\/8613912345678/i.test(href)) {
      record(`External target ${href}`, true, 'WhatsApp pattern')
    } else if (/fonts\.googleapis\.com|fonts\.gstatic\.com|images\.unsplash\.com/i.test(href)) {
      warn(`Allowed external asset target`, href)
    } else {
      warn(`Unverified external target`, href)
    }
  }

  const linksToCheck = [...internalLinks]
    .filter((href) => !href.startsWith('/_nuxt') && !href.includes('__nuxt'))
    .slice(0, 240)
  const linkResults = await mapLimit(linksToCheck, CONCURRENCY, async (href) => {
    const result = await checkHttpOk(`Link target ${href}`, asWebUrl(href), { method: 'GET' })
    return { href, ...result }
  })
  const failedLinks = linkResults.filter((result) => !result.ok)
  record('Key page internal links are reachable', failedLinks.length === 0, failedLinks.length ? `${failedLinks.length} failed` : `${linksToCheck.length} links`)

  const imageUrls = new Set()
  for (const html of keyHtml.values()) {
    for (const src of extractImages(html)) {
      const url = new URL(src, WEB_BASE).toString()
      if (LOCAL_MINIO_PATTERN.test(url) || url.startsWith(WEB_BASE)) {
        imageUrls.add(url)
      } else if (/qinghangmetal\.com/i.test(url)) {
        record(`Image source ${url}`, false, 'external Qinghang image URL')
      } else {
        warn('External image asset not fetched', url)
      }
    }
  }
  const imageResults = await mapLimit([...imageUrls], CONCURRENCY, async (url) => {
    const result = await checkHttpOk(`Image ${url}`, url, { method: 'GET' })
    return { url, ...result }
  })
  const failedImages = imageResults.filter((result) => !result.ok)
  record('Key page MinIO/local images are reachable', failedImages.length === 0, failedImages.length ? `${failedImages.length} failed` : `${imageResults.length} checked`)

  const sitemap = await checkHttpOk('Sitemap XML', `${WEB_BASE}/sitemap.xml`)
  const robots = await checkHttpOk('Robots TXT', `${WEB_BASE}/robots.txt`)
  const sitemapLocs = [...sitemap.text.matchAll(/<loc>(.*?)<\/loc>/g)].map((match) => decodeXml(match[1]))
  const sampleProductRoute = productRoutes.find((route) => route.includes('titanium-hex-nuts')) || productRoutes[0]
  const sampleArticleRoute = articleRoutes.find((route) => route.includes('does-titanium-tarnish')) || articleRoutes[0]
  record('Sitemap contains product detail', sitemapLocs.some((loc) => loc.endsWith(sampleProductRoute)), sampleProductRoute)
  record('Sitemap contains imported article', sitemapLocs.some((loc) => loc.endsWith(sampleArticleRoute)), sampleArticleRoute)
  record('Sitemap contains calculator', sitemapLocs.some((loc) => loc.endsWith('/en/resources/titanium-weight-calculator')), 'weight calculator')
  record('Robots references sitemap', /sitemap:\s*https?:\/\/.+\/sitemap\.xml/i.test(robots.text), 'Sitemap directive')

  const productsHtml = keyHtml.get('/en/products') || ''
  record('Products page avoids empty fallback', !/Products are being prepared/i.test(productsHtml), 'live catalog visible')
  record('Products page shows live count', /\b90 items\b/i.test(productsHtml), '90 items expected')
  record('Homepage resources are dynamic', /does titanium tarnish|FAQ|Technical Guides/i.test(keyHtml.get('/en') || ''), 'resource section content')
  record('FAQ page includes imported FAQ library', /Does Titanium Tarnish|More Buyer Questions/i.test(keyHtml.get('/en/faq') || ''), 'FAQ article section')
  record('Resources page includes article filters', /Showing\s+\d+\s+of\s+\d+\s+matching articles/i.test(keyHtml.get('/en/resources') || ''), 'resource count')

  const timestamp = new Date().toISOString().replace(/[:.]/g, '-')
  const smokeEmail = `smoke-web-${Date.now()}@example.com`
  const uploadForm = new FormData()
  uploadForm.append('file', new Blob([`CNBJTI smoke upload ${timestamp}\n`], { type: 'text/plain' }), `smoke-web-${timestamp}.txt`)
  const uploadEnvelope = await fetchJson(`${API_BASE}/public/uploads`, { method: 'POST', body: uploadForm, headers: {} })
  const upload = unwrap(uploadEnvelope)
  record('Public upload endpoint accepts RFQ attachment', Boolean(upload?.id && upload?.url), upload?.filename || upload?.id || '')

  const contactEnvelope = await fetchJson(`${API_BASE}/public/contact-messages`, {
    method: 'POST',
    body: JSON.stringify({
      name: `CNBJTI Smoke Contact ${timestamp}`,
      company: 'CNBJTI Smoke Test',
      email: smokeEmail,
      subject: 'Website smoke test contact',
      message: `Smoke test contact submission ${timestamp}`,
      source: 'smoke-public-web',
    }),
  })
  const contact = unwrap(contactEnvelope)
  record('Contact form API submits', Boolean(contact?.id), contact?.id || '')

  const rfqEnvelope = await fetchJson(`${API_BASE}/public/rfqs`, {
    method: 'POST',
    body: JSON.stringify({
      productType: 'Titanium Sheet / Plate',
      grade: 'Grade 2',
      quantity: '1 smoke test piece',
      dimensions: '10 x 20 x 30 mm',
      message: `Smoke test RFQ submission ${timestamp}`,
      name: `CNBJTI Smoke RFQ ${timestamp}`,
      company: 'CNBJTI Smoke Test',
      email: smokeEmail,
      attachments: [upload],
    }),
  })
  const rfq = unwrap(rfqEnvelope)
  record('RFQ form API submits with attachment', Boolean(rfq?.rfqNo), rfq?.rfqNo || '')

  const failed = checks.filter((check) => !check.ok)
  console.log('')
  console.log(`Smoke summary: ${checks.length - failed.length}/${checks.length} checks passed, ${warnings.length} warnings.`)
  if (warnings.length) {
    console.log('Warnings:')
    for (const item of warnings.slice(0, 20)) {
      console.log(`- ${item.name}${item.details ? ` :: ${item.details}` : ''}`)
    }
    if (warnings.length > 20) console.log(`- ... ${warnings.length - 20} more warnings`)
  }
  if (failed.length) {
    console.log('Failures:')
    for (const item of failed) {
      console.log(`- ${item.name}${item.details ? ` :: ${item.details}` : ''}`)
    }
    process.exitCode = 1
  }
}

main().catch((error) => {
  console.error(error)
  process.exitCode = 1
})
