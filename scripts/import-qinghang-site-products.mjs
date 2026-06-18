import { setTimeout as delay } from 'node:timers/promises'

const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'
const SOURCE_BASE = 'https://www.qinghangmetal.com'
const SITEMAP_URL = `${SOURCE_BASE}/page-sitemap.xml`
const DRY_RUN = process.env.CNBJTI_DRY_RUN === '1'
const FORCE_IMAGE_MIRROR = process.env.CNBJTI_FORCE_IMAGE_MIRROR === '1'
const LOCAL_ASSET_PATTERN = /^http:\/\/(?:localhost|127\.0\.0\.1):(?:9000|9002)\/cnbjti-assets\//i

const sourceHeaders = {
  'user-agent': 'Mozilla/5.0 (compatible; CNBJTI authorized product migration)',
}

const excludedPaths = new Set([
  '/',
  '/products/',
  '/about-us/',
  '/factory-tour/',
  '/certificates/',
  '/inquiry/',
  '/contact-us/',
  '/privacy-policy/',
  '/full-list-of-titanium-manufacturers-in-usa/',
  '/blog/',
])

const sourceSlugOverrides = new Map([
  ['titanium-plate', 'titanium-plates'],
  ['180titanium-elbow', 'titanium-180-degree-elbow'],
  ['45-titanium-elbow', 'titanium-45-degree-elbow'],
  ['90-titanium-elbow', 'titanium-90-degree-elbow'],
  ['titanium-fastener-2', 'titanium-fastener'],
])

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const token = login.token
  const authHeaders = { Authorization: `Bearer ${token}` }

  const options = await api('/admin/content-options', { headers: authHeaders })
  const categories = bySlug(options.categories)
  const grades = bySlug(options.grades)
  const standards = bySlug(options.standards)
  const adminProducts = await api('/admin/products', { headers: authHeaders })
  const existingBySlug = new Map()
  for (const item of adminProducts) {
    const detail = await api(`/admin/products/${encodeURIComponent(item.id)}`, { headers: authHeaders })
    existingBySlug.set(detail.slug, detail)
  }

  const sourcePages = await discoverProductPages()
  const results = []

  for (const [index, sourceUrl] of sourcePages.entries()) {
    const sourceSlug = slugFromUrl(sourceUrl)
    const slug = normalizeLocalSlug(sourceSlug)
    const existing = existingBySlug.get(slug)
    const html = await fetchText(sourceUrl)
    const name = productName(html, sourceUrl)
    const categorySlug = inferCategorySlug(name, slug)
    const category = categories.get(categorySlug)
    if (!category) throw new Error(`Missing local category: ${categorySlug}`)

    const gradeIds = inferGradeIds(name, slug).filter((id) => grades.has(id))
    const standardIds = inferStandardIds(name, slug, categorySlug).filter((id) => standards.has(id))
    const product = buildProductPayload({
      name,
      slug,
      category,
      categorySlug,
      gradeIds,
      standardIds,
      sourceUrl,
      html,
      existing,
    })

    let sourceImage = null
    let asset = existing?.images?.[0] || null
    if (!FORCE_IMAGE_MIRROR && LOCAL_ASSET_PATTERN.test(existing?.imageUrl || '') && LOCAL_ASSET_PATTERN.test(asset?.url || '')) {
      product.imageUrl = asset.url
      product.images = [normalizeAsset(asset, name)]
      product.seo.ogImage = asset.url
    } else {
      sourceImage = findProductImage(html, sourceUrl)
      asset = await mirrorImage(token, slug, name, sourceImage)
      product.imageUrl = asset.url
      product.images = [asset]
      product.seo.ogImage = asset.url
    }

    if (DRY_RUN) {
      results.push({ action: existing ? 'would-update' : 'would-create', slug, name, categorySlug, sourceUrl, sourceImage, minioUrl: product.imageUrl })
    } else if (existing) {
      await api(`/admin/products/${encodeURIComponent(existing.id)}`, {
        method: 'PUT',
        headers: authHeaders,
        body: JSON.stringify(product),
      })
      results.push({ action: 'updated', slug, name, categorySlug, sourceUrl, sourceImage, minioUrl: product.imageUrl })
    } else {
      const created = await api('/admin/products', {
        method: 'POST',
        headers: authHeaders,
        body: JSON.stringify(product),
      })
      existingBySlug.set(slug, { ...product, id: created.id })
      results.push({ action: 'created', slug, name, categorySlug, sourceUrl, sourceImage, minioUrl: product.imageUrl })
    }

    console.log(`[${index + 1}/${sourcePages.length}] ${existing ? 'updated' : 'created'} ${slug}`)
    await delay(120)
  }

  const categoryCounts = await refreshCategoryCounts(authHeaders, results, categories)
  const publicProducts = await api('/public/products')
  const imported = publicProducts.filter((product) => results.some((item) => item.slug === product.slug))
  const missingLocalImages = imported.filter((product) => !LOCAL_ASSET_PATTERN.test(product.images?.[0]?.url || product.imageUrl || ''))

  console.log(JSON.stringify({
    dryRun: DRY_RUN,
    source: SITEMAP_URL,
    discovered: sourcePages.length,
    created: results.filter((item) => item.action === 'created').length,
    updated: results.filter((item) => item.action === 'updated').length,
    publicProductCount: publicProducts.length,
    missingLocalImages: missingLocalImages.map((item) => item.slug),
    categoryCounts,
    results,
  }, null, 2))
}

async function discoverProductPages() {
  const xml = await fetchText(SITEMAP_URL)
  const urls = Array.from(xml.matchAll(/<loc>(.*?)<\/loc>/g))
    .map((match) => decodeHtml(match[1]))
    .filter((url) => {
      const pathname = new URL(url).pathname
      return !excludedPaths.has(pathname)
    })

  const reachable = []
  for (const url of urls) {
    const response = await fetch(url, { headers: sourceHeaders })
    if (response.ok) reachable.push(url)
  }
  return reachable
}

async function refreshCategoryCounts(authHeaders, importedResults, categories) {
  const products = await api('/public/products')
  const counts = new Map()
  for (const product of products) {
    counts.set(product.category.slug, (counts.get(product.category.slug) || 0) + 1)
  }

  const categoryDetails = await api('/admin/categories', { headers: authHeaders })
  const updated = []
  for (const detail of categoryDetails) {
    const count = counts.get(detail.slug) || 0
    const touched = importedResults.some((item) => item.categorySlug === detail.slug)
    if (!touched && detail.productCount === count) continue
    if (!DRY_RUN) {
      await api(`/admin/categories/${encodeURIComponent(detail.id)}`, {
        method: 'PUT',
        headers: authHeaders,
        body: JSON.stringify({
          slug: detail.slug,
          name: detail.name,
          description: detail.description,
          imageUrl: detail.imageUrl,
          icon: detail.icon,
          productCount: count,
          seo: detail.seo,
          status: detail.status,
        }),
      })
    }
    updated.push({ slug: detail.slug, count })
  }
  return updated
}

function buildProductPayload({ name, slug, category, categorySlug, gradeIds, standardIds, sourceUrl, html, existing }) {
  const shortDescription = rewriteShortDescription(name, categorySlug)
  const categoryPath = `/products/${category.slug}/${slug}`
  return {
    name,
    slug,
    categoryId: category.id,
    gradeIds,
    standardIds,
    status: 'published',
    shortDescription,
    description: rewriteDescription(name, categorySlug, sourceUrl, html),
    imageUrl: existing?.imageUrl || category.image?.url || '',
    images: existing?.images?.length ? existing.images.map((asset) => normalizeAsset(asset, name)) : [],
    specs: inferSpecs(name, slug, categorySlug),
    seo: {
      title: `${name} Supplier | CNBJTI Baoji Titanium`,
      description: shortDescription,
      canonical: categoryPath,
      ogTitle: `${name} - CNBJTI`,
      ogDescription: shortDescription,
      ogImage: existing?.seo?.ogImage || existing?.imageUrl || null,
      noIndex: false,
    },
    featured: existing?.featured || false,
    inStock: true,
  }
}

function rewriteShortDescription(name, categorySlug) {
  const lower = name.toLowerCase()
  if (categorySlug === 'titanium-fasteners') return `${name} for lightweight, corrosion-resistant fastening assemblies.`
  if (categorySlug === 'titanium-fittings') return `${name} for chemical, marine and industrial titanium piping systems.`
  if (categorySlug === 'titanium-tube') return `${name} for heat-exchanger, process equipment and precision tube projects.`
  if (categorySlug === 'titanium-sheet') return `${name} supplied for fabrication, machining, forming and corrosion-service applications.`
  if (categorySlug === 'titanium-forgings') return `${name} produced as traceable titanium blanks or near-net components.`
  if (categorySlug === 'titanium-bar') return `${name} supplied in cut-to-size titanium forms for machining and fabrication.`
  if (/anode|electrode|electrolyzer/.test(lower)) return `${name} for electrochemical, water-treatment and industrial process systems.`
  return `${name} supplied by CNBJTI for custom titanium industrial applications.`
}

function rewriteDescription(name, categorySlug, sourceUrl, html) {
  const hints = metaDescription(html)
  const base = rewriteShortDescription(name, categorySlug)
  const sourceNote = hints ? ` The source page identifies this product as ${plainSentence(hints)}` : ''
  return `${base} CNBJTI can support grade selection, custom dimensions, inspection, MTR documentation and export packaging for this product family.${sourceNote}`
}

function inferSpecs(name, slug, categorySlug) {
  const text = `${name} ${slug}`.toLowerCase()
  const specs = []
  if (/45/.test(text) && /elbow/.test(text)) specs.push(spec('Angle', '45 degree'))
  if (/90/.test(text) && /elbow/.test(text)) specs.push(spec('Angle', '90 degree'))
  if (/180/.test(text) && /elbow/.test(text)) specs.push(spec('Angle', '180 degree'))
  if (/gr23|grade 23/.test(text)) specs.push(spec('Primary grade', 'Grade 23 / Ti-6Al-4V ELI'))
  if (/gr5|grade 5/.test(text)) specs.push(spec('Primary grade', 'Grade 5 / Ti-6Al-4V'))
  if (/gr2|grade 2/.test(text)) specs.push(spec('Primary grade', 'Grade 2 titanium'))

  if (categorySlug === 'titanium-fasteners') {
    specs.push(spec('Thread range', 'M3 - M64'), spec('Production', 'Standard and drawing-based'))
  } else if (categorySlug === 'titanium-fittings') {
    specs.push(spec('Size range', 'DN6 - DN600'), spec('Connection', 'Welded, threaded, flanged or custom'))
  } else if (categorySlug === 'titanium-tube') {
    specs.push(spec('OD', '6 - 219', 'mm'), spec('Supply', 'Seamless, welded or custom cut'))
  } else if (categorySlug === 'titanium-sheet') {
    specs.push(spec('Thickness', '0.01 - 100', 'mm'), spec('Supply form', 'Sheet, plate, foil, coil or custom blank'))
  } else if (categorySlug === 'titanium-forgings') {
    specs.push(spec('Forms', 'Ring, shaft, disc, block or custom blank'), spec('Testing', 'UT, mechanical test, MTR'))
  } else if (categorySlug === 'titanium-bar') {
    specs.push(spec('Forms', 'Round, square, hex, flat or custom profile'), spec('Surface', 'Mill finish, pickled or polished'))
  } else {
    specs.push(spec('Input', 'Drawing, sample or technical requirement'), spec('Process', 'Machining, welding, fabrication or assembly'))
  }
  specs.push(spec('Documents', 'MTR, certificate, inspection report'))
  return dedupeSpecs(specs)
}

function inferCategorySlug(name, slug) {
  const text = `${name} ${slug}`.toLowerCase()
  if (/anode|electrode|electrolyzer|reactor|pump|heat exchanger|casting|dental|powder|rack/.test(text)) return 'cnc-parts'
  if (/bolt|nut|washer|fastener|spring|u-bolt|cap bolt|slotted|axle adjuster|standard parts/.test(text)) return 'titanium-fasteners'
  if (/flange|elbow|tee|cross|nipple|reducer|pipe fitting|stub end|connector|valve/.test(text)) return 'titanium-fittings'
  if (/tube|pipe|exhaust/.test(text)) return 'titanium-tube'
  if (/wire|welding rod|ribbon/.test(text)) return 'titanium-wire'
  if (/sheet|plate|foil|coil|card|cutting board|substrate|sputtering target|mesh/.test(text)) return 'titanium-sheet'
  if (/forg|ring|shaft|axle|block|cube|disc/.test(text) && !/dental/.test(text)) return 'titanium-forgings'
  if (/bar|rod|channel/.test(text)) return 'titanium-bar'
  return 'cnc-parts'
}

function inferGradeIds(name, slug) {
  const text = `${name} ${slug}`.toLowerCase()
  const grades = new Set(['gr2'])
  if (/gr1|grade 1|foil|anode|electrode|electrolyzer|mesh/.test(text)) grades.add('gr1')
  if (/gr5|grade 5|alloy|bolt|nut|fastener|casting|rod|bar|plate|forg|spring|axle/.test(text)) grades.add('gr5')
  if (/gr7|flange|elbow|tee|cross|nipple|reducer|reactor|heat exchanger|chemical/.test(text)) grades.add('gr7')
  if (/gr23|grade 23|dental|medical/.test(text)) grades.add('gr23')
  return Array.from(grades)
}

function inferStandardIds(name, slug, categorySlug) {
  const text = `${name} ${slug}`.toLowerCase()
  const standards = new Set()
  if (categorySlug === 'titanium-sheet') standards.add('s2')
  if (categorySlug === 'titanium-tube' || categorySlug === 'titanium-fittings') standards.add('s3')
  if (categorySlug === 'titanium-forgings') standards.add('s4')
  if (categorySlug === 'titanium-bar' || categorySlug === 'titanium-fasteners' || categorySlug === 'cnc-parts') standards.add('s1')
  if (/gr5|grade 5|aerospace|forg|ring|shaft/.test(text)) standards.add('s5')
  return Array.from(standards)
}

async function mirrorImage(token, slug, name, imageUrl) {
  const image = await fetchImageWithFallback(imageUrl)
  const filename = `${slug}${imageExtension(image.contentType, image.url)}`
  const form = new FormData()
  form.append('file', new Blob([image.bytes], { type: image.contentType }), filename)
  if (DRY_RUN) {
    return {
      id: `dry-${slug}`,
      url: image.url,
      thumbnailUrl: null,
      alt: name,
      width: null,
      height: null,
      mimeType: image.contentType,
      size: image.bytes.byteLength,
      filename,
    }
  }
  const asset = await api('/public/uploads', {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: form,
  })
  return normalizeAsset({
    ...asset,
    alt: name,
    filename,
    mimeType: image.contentType,
    size: image.bytes.byteLength,
  }, name)
}

async function fetchImageWithFallback(imageUrl) {
  const candidates = unique([fullSizeImage(imageUrl), imageUrl])
  let lastError = null
  for (const url of candidates) {
    try {
      const response = await fetch(url, { headers: sourceHeaders, signal: AbortSignal.timeout(30000) })
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const contentType = normalizeContentType(response.headers.get('content-type'), url)
      if (!contentType.startsWith('image/')) throw new Error(`expected image, got ${contentType}`)
      return { url, contentType, bytes: await response.arrayBuffer() }
    } catch (error) {
      lastError = error
    }
  }
  throw new Error(`${imageUrl}: ${lastError?.message || 'image fetch failed'}`)
}

function findProductImage(html, pageUrl) {
  const bodyImages = Array.from(html.matchAll(/<img\b[^>]*>/gi))
    .map((match) => {
      const tag = match[0]
      const src = attr(tag, 'src') || attr(tag, 'data-src') || firstSrcsetUrl(attr(tag, 'srcset') || attr(tag, 'data-srcset'))
      return src ? {
        src: absoluteUrl(decodeHtml(src), pageUrl),
        alt: attr(tag, 'alt'),
        title: attr(tag, 'title'),
        className: attr(tag, 'class'),
      } : null
    })
    .filter(Boolean)
    .filter((image) => isProductImage(image.src, `${image.alt} ${image.title} ${image.className}`))

  if (bodyImages.length) return bodyImages[0].src

  const metaImages = Array.from(html.matchAll(/<meta\b[^>]*>/gi))
    .filter((match) => /\b(?:property|name)=["'](?:og:image|twitter:image)["']/i.test(match[0]))
    .map((match) => attr(match[0], 'content'))
    .filter(Boolean)
    .map((value) => absoluteUrl(value, pageUrl))
    .filter((url) => isProductImage(url))

  if (metaImages.length) return metaImages[0]
  throw new Error(`No product image found on ${pageUrl}`)
}

function productName(html, sourceUrl) {
  const h1 = textFromMatch(html.match(/<h1[^>]*>([\s\S]*?)<\/h1>/i)?.[1])
  if (h1) return normalizeProductName(h1)
  const title = textFromMatch(html.match(/<title>([\s\S]*?)<\/title>/i)?.[1])
  if (title) return normalizeProductName(title.replace(/\s*[-|]\s*Qinghang.*$/i, ''))
  return titleFromSlug(slugFromUrl(sourceUrl))
}

function normalizeProductName(value) {
  return decodeHtml(value)
    .replace(/\s+/g, ' ')
    .replace(/\bGr(\d+)\b/g, 'GR$1')
    .trim()
}

function metaDescription(html) {
  const tag = Array.from(html.matchAll(/<meta\b[^>]*>/gi))
    .map((match) => match[0])
    .find((value) => /\bname=["']description["']/i.test(value))
  return tag ? decodeHtml(attr(tag, 'content')).replace(/\s+/g, ' ').trim() : ''
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
  const response = await fetch(url, { headers: sourceHeaders, signal: AbortSignal.timeout(30000) })
  if (!response.ok) throw new Error(`${url}: HTTP ${response.status}`)
  return response.text()
}

function bySlug(items) {
  return new Map(items.map((item) => [item.slug, item]))
}

function slugFromUrl(url) {
  return new URL(url).pathname.split('/').filter(Boolean).at(-1)
}

function normalizeLocalSlug(sourceSlug) {
  return sourceSlugOverrides.get(sourceSlug) || sourceSlug
}

function spec(label, value, unit = null) {
  return { label, value, unit }
}

function dedupeSpecs(specs) {
  const seen = new Set()
  return specs.filter((item) => {
    const key = item.label.toLowerCase()
    if (seen.has(key)) return false
    seen.add(key)
    return true
  })
}

function normalizeAsset(asset, name) {
  return {
    id: asset.id,
    url: asset.url,
    thumbnailUrl: asset.thumbnailUrl || null,
    alt: asset.alt || name,
    width: asset.width || null,
    height: asset.height || null,
    mimeType: asset.mimeType || 'image/jpeg',
    size: asset.size || 0,
    filename: asset.filename || `${slugify(name)}.jpg`,
  }
}

function isProductImage(url, metadata = '') {
  return /qinghangmetal\.com\/wp-content\/uploads\//i.test(url)
    && !/logo|icon|avatar|susan|wechat|whatsapp|qr-code|favicon/i.test(`${url} ${metadata}`)
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

function imageExtension(contentType, url) {
  if (contentType === 'image/png') return '.png'
  if (contentType === 'image/webp') return '.webp'
  if (contentType === 'image/gif') return '.gif'
  const extension = new URL(url).pathname.match(/\.(jpe?g|png|webp|gif)$/i)?.[0]
  return extension || '.jpg'
}

function firstSrcsetUrl(value) {
  return value?.split(',')[0]?.trim()?.split(/\s+/)[0] || ''
}

function attr(tag, name) {
  const match = tag.match(new RegExp(`\\s${name}=["']([^"']+)["']`, 'i'))
  return match ? decodeHtml(match[1]) : ''
}

function textFromMatch(value = '') {
  return decodeHtml(value.replace(/<[^>]+>/g, ' ')).replace(/\s+/g, ' ').trim()
}

function decodeHtml(value = '') {
  return value
    .replace(/&amp;/g, '&')
    .replace(/&#038;/g, '&')
    .replace(/&quot;/g, '"')
    .replace(/&#039;/g, "'")
    .replace(/&nbsp;/g, ' ')
}

function absoluteUrl(value, base) {
  return new URL(value, base).href
}

function titleFromSlug(slug) {
  return slug.split('-').map((part) => part ? part[0].toUpperCase() + part.slice(1) : '').join(' ')
}

function slugify(value) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '')
}

function plainSentence(value) {
  const cleaned = value.replace(/\s+/g, ' ').trim()
  return cleaned.endsWith('.') ? cleaned : `${cleaned}.`
}

function unique(items) {
  return Array.from(new Set(items.filter(Boolean)))
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
