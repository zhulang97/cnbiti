import { setTimeout as delay } from 'node:timers/promises'

const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'
const SOURCE_BASE = 'https://www.qinghangmetal.com'
const POSTS_API = `${SOURCE_BASE}/wp-json/wp/v2/posts`
const MEDIA_API = `${SOURCE_BASE}/wp-json/wp/v2/media`
const DRY_RUN = process.env.CNBJTI_DRY_RUN === '1'
const LIMIT = Number(process.env.CNBJTI_ARTICLE_LIMIT || 0)
const FORCE_IMAGE_MIRROR = process.env.CNBJTI_FORCE_IMAGE_MIRROR === '1'
const MIRRORED_ASSET_PATTERN = /^https?:\/\/(?:cnbjti\.com|www\.cnbjti\.com|localhost|127\.0\.0\.1)(?::(?:9000|9002))?\/cnbjti-assets\//i

const sourceHeaders = {
  'user-agent': 'Mozilla/5.0 (compatible; CNBJTI authorized content migration)',
}

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const token = login.token
  const authHeaders = { Authorization: `Bearer ${token}` }

  const existingArticles = await api('/admin/articles', { headers: authHeaders })
  const existingBySlug = new Map()
  for (const item of existingArticles) {
    const detail = await api(`/admin/articles/${encodeURIComponent(item.id)}`, { headers: authHeaders })
    existingBySlug.set(detail.slug, detail)
  }
  const posts = LIMIT > 0 ? (await fetchAllPosts()).slice(0, LIMIT) : await fetchAllPosts()
  const mediaById = await fetchMediaMap(posts.map((post) => post.featured_media).filter(Boolean))

  const results = []
  for (const [index, post] of posts.entries()) {
    const title = cleanText(post.title?.rendered)
    if (!title) continue

    const sourceUrl = post.link || `${SOURCE_BASE}/${post.slug}/`
    const localSlug = slugify(post.slug || title)
    const category = classifyArticle(title, localSlug)
    const existing = existingBySlug.get(localSlug)
    const sourceImages = unique([
      ...mediaCandidates(mediaById.get(post.featured_media)),
      findImageFromContent(post.content?.rendered, sourceUrl),
    ])
    const coverImageUrl = await resolveCoverImage({
      token,
      existing,
      sourceImages,
      slug: localSlug,
      title,
    })

    const payload = buildArticlePayload({
      post,
      title,
      slug: localSlug,
      category,
      sourceUrl,
      coverImageUrl,
    })

    if (DRY_RUN) {
      results.push({ action: existing ? 'would-update' : 'would-create', slug: localSlug, title, category, sourceImage: sourceImages[0], coverImageUrl })
    } else if (existing) {
      await api(`/admin/articles/${encodeURIComponent(existing.id)}`, {
        method: 'PUT',
        headers: authHeaders,
        body: JSON.stringify(payload),
      })
      results.push({ action: 'updated', slug: localSlug, title, category, sourceImage: sourceImages[0], coverImageUrl })
    } else {
      const created = await api('/admin/articles', {
        method: 'POST',
        headers: authHeaders,
        body: JSON.stringify(payload),
      })
      existingBySlug.set(localSlug, { ...created, slug: localSlug })
      results.push({ action: 'created', slug: localSlug, title, category, sourceImage: sourceImages[0], coverImageUrl })
    }

    console.log(`[${index + 1}/${posts.length}] ${existing ? 'updated' : 'created'} ${localSlug}`)
    await delay(120)
  }

  const publicArticles = await api('/public/articles')
  const importedSlugs = new Set(results.map((item) => item.slug))
  const imported = publicArticles.filter((article) => importedSlugs.has(article.slug))
  const missingLocalImages = imported
    .filter((article) => !MIRRORED_ASSET_PATTERN.test(article.coverImage?.url || ''))
    .map((article) => article.slug)

  console.log(JSON.stringify({
    dryRun: DRY_RUN,
    source: POSTS_API,
    discovered: posts.length,
    created: results.filter((item) => item.action === 'created').length,
    updated: results.filter((item) => item.action === 'updated').length,
    publicArticleCount: publicArticles.length,
    missingLocalImages,
    categories: countBy(results, 'category'),
  }, null, 2))
}

async function fetchAllPosts() {
  const posts = []
  for (let page = 1; page <= 20; page++) {
    const url = `${POSTS_API}?per_page=100&page=${page}&_fields=id,slug,title,excerpt,content,date,link,featured_media`
    const response = await fetch(url, { headers: sourceHeaders, signal: AbortSignal.timeout(30000) })
    if (response.status === 400 && page > 1) break
    if (!response.ok) throw new Error(`${url}: HTTP ${response.status}`)
    const batch = await response.json()
    if (!Array.isArray(batch) || batch.length === 0) break
    posts.push(...batch)
    const totalPages = Number(response.headers.get('x-wp-totalpages') || 0)
    if (totalPages && page >= totalPages) break
  }
  return posts
}

async function fetchMediaMap(ids) {
  const uniqueIds = Array.from(new Set(ids)).filter(Boolean)
  const media = new Map()
  for (let i = 0; i < uniqueIds.length; i += 100) {
    const batch = uniqueIds.slice(i, i + 100)
    const url = `${MEDIA_API}?per_page=100&include=${batch.join(',')}&_fields=id,source_url,alt_text,mime_type,media_type,media_details`
    const response = await fetch(url, { headers: sourceHeaders, signal: AbortSignal.timeout(30000) })
    if (!response.ok) continue
    for (const item of await response.json()) media.set(item.id, item)
  }
  return media
}

function buildArticlePayload({ post, title, slug, category, sourceUrl, coverImageUrl }) {
  const sourceExcerpt = cleanText(post.excerpt?.rendered)
  const contentText = cleanText(post.content?.rendered)
  const headingHints = extractHeadings(post.content?.rendered)
  const excerpt = buildExcerpt(title, category, sourceExcerpt)
  const content = buildOriginalContent({
    title,
    slug,
    category,
    sourceExcerpt,
    contentText,
    headingHints,
    sourceUrl,
  })

  return {
    title,
    slug,
    category,
    status: 'published',
    excerpt,
    content,
    coverImageUrl,
    tags: tagsFor(title, category),
    publishedAt: normalizeDate(post.date),
    readingTime: readingTime(content),
  }
}

function buildOriginalContent({ title, slug, category, sourceExcerpt, contentText, headingHints, sourceUrl }) {
  const topic = topicFromTitle(title)
  const product = productFocus(title, slug, contentText)
  const quick = quickAnswer(title, category, product)
  const buyerPoints = buyerChecklist(category, product)
  const processPoints = processChecklist(category, product)
  const relatedPoints = `<h2>Related Points to Review</h2>${list(relatedChecklist(category, product))}`

  const sourceSignal = sourceExcerpt
    ? `<p>For CNBJTI customers, this topic is best treated as a procurement and engineering check rather than a simple definition. ${rewriteSourceSignal(sourceExcerpt, product)}</p>`
    : `<p>For CNBJTI customers, this topic is best treated as a procurement and engineering check rather than a simple definition.</p>`

  return [
    `<p>${quick}</p>`,
    `<h2>Why This Topic Matters for Titanium Buyers</h2>`,
    sourceSignal,
    `<p>When a buyer sends an RFQ for ${escapeHtml(product)}, the practical questions are usually grade, size, tolerance, surface condition, operating environment and inspection documents. CNBJTI uses those details to match the material route and avoid over-specifying or under-specifying the order.</p>`,
    `<h2>Specification Checklist</h2>`,
    list(buyerPoints),
    `<h2>Processing and Quality Notes</h2>`,
    `<p>Titanium is sensitive to heat, surface contamination and tooling choices. For machined, welded or formed parts, the process plan should protect corrosion resistance while keeping dimensions stable.</p>`,
    list(processPoints),
    relatedPoints,
    `<h2>How CNBJTI Can Support the Project</h2>`,
    `<p>CNBJTI can supply ${escapeHtml(product)} as standard stock, cut-to-size material, or drawing-based components. Typical support includes material grade confirmation, dimensional inspection, MTR documents, export packaging and coordination with machining or fabrication requirements.</p>`,
  ].filter(Boolean).join('\n')
}

function buildExcerpt(title, category, sourceExcerpt) {
  const product = productFocus(title, title, sourceExcerpt)
  if (category === 'FAQ') return `A practical CNBJTI answer to "${title}" with buyer-focused notes on grade, processing, certification and RFQ details.`
  if (category === 'Applications') return `How projects using ${product} fit real industrial applications, with notes on material selection, corrosion resistance, fabrication and documentation.`
  if (category === 'Material Comparison') return `A buyer-friendly comparison for titanium selection, covering performance trade-offs, cost factors and specification checks.`
  if (category === 'Procurement') return `Procurement guidance for titanium buyers, including specification details, compliance documents, inspection and export considerations.`
  if (category === 'Processing Guide') return `A CNBJTI processing guide for ${product}, covering manufacturing route, quality controls and RFQ information buyers should prepare.`
  return `Technical notes for titanium buyers, rewritten for CNBJTI product selection, processing and quality documentation workflows.`
}

function classifyArticle(title, slug) {
  const text = `${title} ${slug}`.toLowerCase()
  if (/\b(vs|versus|difference|compare|compared|better than|stronger than)\b/.test(text)) return 'Material Comparison'
  if (/^(does|do|is|are|can|will|what|how|why|where|when|which)\b/.test(text) || title.includes('?')) return 'FAQ'
  if (/application|aerospace|medical|marine|chemical|electronics|battery|fuel cell|desalination|water treatment|ship|industrial|dental|implant|drone|sports|architecture|kitchen/.test(text)) return 'Applications'
  if (/guide|process|manufacturing|surface|finishing|machining|heat treatment|forging|polishing|coating|casting|welding|annealing|milling|cutting|sintering|prototype|procure and process/.test(text)) return 'Processing Guide'
  if (/tariff|policy|price|supplier|buyer|procurement|import|export|trade|market|cost/.test(text)) return 'Procurement'
  return 'Titanium Insights'
}

async function resolveCoverImage({ token, existing, sourceImages, slug, title }) {
  if (!FORCE_IMAGE_MIRROR && MIRRORED_ASSET_PATTERN.test(existing?.coverImageUrl || '')) return existing.coverImageUrl
  if (!FORCE_IMAGE_MIRROR && MIRRORED_ASSET_PATTERN.test(existing?.coverImage?.url || '')) return existing.coverImage.url
  if (!sourceImages.length) return existing?.coverImageUrl || existing?.coverImage?.url || ''
  try {
    const asset = await mirrorImage(token, slug, title, sourceImages)
    return asset.url
  } catch (error) {
    console.warn(`cover skipped for ${slug}: ${error.message}`)
    return existing?.coverImageUrl || existing?.coverImage?.url || ''
  }
}

async function mirrorImage(token, slug, title, imageUrls) {
  const image = await fetchImageWithFallback(imageUrls)
  const filename = `${slug}${imageExtension(image.contentType, image.url)}`
  const form = new FormData()
  form.append('file', new Blob([image.bytes], { type: image.contentType }), filename)
  if (DRY_RUN) {
    return { url: image.url }
  }
  const asset = await api('/public/uploads', {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: form,
  })
  return {
    ...asset,
    alt: title,
    filename,
    mimeType: image.contentType,
    size: image.bytes.byteLength,
  }
}

async function fetchImageWithFallback(imageUrls) {
  const urls = Array.isArray(imageUrls) ? imageUrls : [imageUrls]
  const candidates = unique(urls.flatMap((url) => [url, fullSizeImage(url)]))
  let lastError = null
  for (const url of candidates) {
    try {
      const response = await fetch(url, { headers: sourceHeaders, signal: AbortSignal.timeout(30000) })
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const contentType = normalizeContentType(response.headers.get('content-type'), url)
      if (!contentType.startsWith('image/')) throw new Error(`expected image, got ${contentType}`)
      const bytes = await response.arrayBuffer()
      if (bytes.byteLength > 950000) throw new Error(`image too large: ${bytes.byteLength} bytes`)
      return { url, contentType, bytes }
    } catch (error) {
      lastError = error
    }
  }
  throw new Error(`${urls[0]}: ${lastError?.message || 'image fetch failed'}`)
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

function quickAnswer(title, category, product) {
  const text = title.toLowerCase()
  if (/tarnish/.test(text)) return 'Titanium does not tarnish like silver or copper. It forms a stable oxide film, so buyers should focus on service environment, grade and surface finish rather than cosmetic tarnish alone.'
  if (/anodize/.test(text)) return 'Titanium can be anodized through a controlled electrochemical process. The result depends on surface preparation, voltage, electrolyte and the required cosmetic or functional finish.'
  if (/color/.test(text)) return 'Natural titanium is a silver-gray metal. Brighter colors usually come from anodizing or coating, so the finish requirement should be stated clearly in the RFQ.'
  if (/flammable|burn/.test(text)) return 'Bulk titanium is stable in normal handling, but fine powder, chips and dust require strict fire-safety controls. Machining and powder handling should be managed with proper procedures.'
  if (/radioactive/.test(text)) return 'Titanium itself is not radioactive. Buyers should still request material traceability and inspection documents when the application is regulated or safety-critical.'
  if (/ferromagnetic|magnetic/.test(text)) return 'Titanium is generally non-magnetic, which is one reason it is used in medical, electronics and precision equipment applications.'
  if (/contain nickel|nickel/.test(text)) return 'Commercially pure titanium and common titanium alloys are not nickel-based materials. The exact chemistry should still be checked against the MTR and required standard.'
  if (/melt|temperature/.test(text)) return 'Titanium has a high melting point, commonly referenced around 1668 C. For buyers, thermal exposure, joining method and heat treatment condition matter more than the number alone.'
  if (/heavy|weight/.test(text)) return 'Titanium is much lighter than steel and heavier than aluminum. Its density is about 4.51 g/cm3, which makes it attractive where strength-to-weight ratio matters.'
  if (/scratch/.test(text)) return 'Titanium can scratch, especially in polished consumer finishes. Industrial projects should specify surface finish, handling method and acceptable cosmetic criteria.'
  if (/stronger than| vs |versus|difference|compare/.test(text)) return `The practical answer depends on whether the buyer values strength, corrosion resistance, weight, price or machinability. ${product} should be compared against the service conditions, not just a single material property.`
  if (/application|used|use/.test(text) || category === 'Applications') return `${capitalizeSentence(product)} is selected when corrosion resistance, low density, heat stability or biocompatibility gives titanium an advantage over conventional metals.`
  if (/how to|process|manufacturing|make|machine|weld|polish|finish|forge|cast|coat/.test(text) || category === 'Processing Guide') return `${capitalizeSentence(product)} should be processed with controlled tooling, clean surfaces and traceable material. The best route depends on dimensions, tolerance, batch size and required documents.`
  return `${capitalizeSentence(product)} is best specified by grade, dimensions, surface condition, tolerance, standard and service environment. CNBJTI can help translate those requirements into a workable titanium supply plan.`
}

function buyerChecklist(category, product) {
  const base = [
    `Confirm the titanium grade and applicable standard for ${product}.`,
    'Define dimensions, tolerances, quantity and delivery schedule.',
    'State surface finish, edge condition, machining allowance or weld preparation.',
    'Request MTR, heat number traceability and any third-party inspection requirements.',
  ]
  if (category === 'Applications') base.push('Describe the operating medium, temperature, pressure and expected service life.')
  if (category === 'Material Comparison') base.push('Compare lifecycle cost, not only initial material price.')
  if (category === 'Procurement') base.push('Clarify Incoterms, packaging, marking and destination documentation.')
  if (category === 'FAQ') base.push('Share the end-use scenario so the answer can be tied to real service conditions.')
  return base
}

function processChecklist(category, product) {
  const points = [
    `Use clean tooling and suitable cutting parameters for ${product}.`,
    'Avoid iron contamination on surfaces intended for corrosion service.',
    'Control heat input during welding, forming or heat treatment.',
    'Inspect dimensions and surface condition before export packaging.',
  ]
  if (category === 'Processing Guide') points.push('For prototype work, confirm machining allowance and inspection points before production.')
  if (category === 'Applications') points.push('Match the process route to the expected corrosion, fatigue or wear conditions.')
  return points
}

function tagsFor(title, category) {
  const text = title.toLowerCase()
  const tags = new Set(['Titanium', category])
  if (/grade|gr\.|gr1|gr2|gr5|gr7|gr23/.test(text)) tags.add('Titanium Grades')
  if (/anode|electrode|water treatment|desalination/.test(text)) tags.add('Electrochemical')
  if (/tube|pipe|fitting|flange|elbow/.test(text)) tags.add('Piping')
  if (/bolt|screw|fastener|nut|washer/.test(text)) tags.add('Fasteners')
  if (/sheet|plate|foil|bar|rod|wire/.test(text)) tags.add('Mill Products')
  if (/machine|milling|welding|forging|casting|polishing|finish|coating/.test(text)) tags.add('Processing')
  if (/aerospace|medical|marine|chemical/.test(text)) tags.add('Applications')
  return Array.from(tags).slice(0, 6)
}

function topicFromTitle(title) {
  return title.replace(/[?!.]+$/g, '').trim()
}

function productFocus(title, slug, body = '') {
  const text = `${title} ${slug} ${body}`.toLowerCase()
  if (/anode|electrode|electrolyzer/.test(text)) return 'titanium anodes and electrochemical components'
  if (/bolt|screw|fastener|nut|washer/.test(text)) return 'titanium fasteners'
  if (/tube|pipe|exchanger|condenser/.test(text)) return 'titanium tubes and piping materials'
  if (/sheet|plate|foil|coil/.test(text)) return 'titanium sheet and plate'
  if (/bar|rod|wire/.test(text)) return 'titanium bar, rod and wire'
  if (/forg|casting|machin|milling|cnc|part/.test(text)) return 'custom titanium components'
  if (/grade|alloy|pure titanium/.test(text)) return 'titanium grades and alloys'
  return 'titanium materials'
}

function extractHeadings(html = '') {
  return Array.from(html.matchAll(/<h[2-4][^>]*>([\s\S]*?)<\/h[2-4]>/gi))
    .map((match) => cleanText(match[1]))
    .filter((value) => value && value.length <= 90)
    .filter((value, index, array) => array.indexOf(value) === index)
}

function relatedChecklist(category, product) {
  const points = [
    `Define where ${product} will be used and what medium or load it will face.`,
    'Confirm whether the project needs commercial pure titanium or an alloy grade.',
    'Match surface finish and dimensional tolerance to the fabrication route.',
    'Check whether certification, inspection or third-party testing is required.',
  ]
  if (category === 'FAQ') points.push('Turn the answer into measurable RFQ details before purchasing.')
  if (category === 'Applications') points.push('Review the expected service life and maintenance environment.')
  if (category === 'Material Comparison') points.push('Compare lifecycle value, not only material price.')
  if (category === 'Processing Guide') points.push('Agree on machining, welding or finishing notes before production starts.')
  return points
}

function rewriteSourceSignal(excerpt, product) {
  const lower = excerpt.toLowerCase()
  if (/corrosion|rust|tarnish|oxid/.test(lower)) return `The key point is to match ${product} to the corrosion environment and surface condition.`
  if (/strength|weight|light/.test(lower)) return `The key point is to balance strength, weight and manufacturing cost.`
  if (/medical|implant|biocompat/.test(lower)) return `The key point is to align grade choice with biocompatibility, cleanliness and documentation expectations.`
  if (/water|seawater|marine|chemical|acid/.test(lower)) return `The key point is to understand the medium, temperature, concentration and crevice conditions.`
  if (/machine|weld|polish|finish|process/.test(lower)) return `The key point is to choose a production route that protects both dimensions and material performance.`
  return `The key point is to connect the material property discussion back to the actual service conditions for ${product}.`
}

function findImageFromContent(html = '', baseUrl) {
  const match = html.match(/<img\b[^>]*(?:src|data-src)=["']([^"']+)["'][^>]*>/i)
  if (!match) return ''
  return new URL(decodeHtml(match[1]), baseUrl).href
}

function mediaCandidates(media) {
  if (!media) return []
  const sizes = media.media_details?.sizes || {}
  const preferred = [
    'featured',
    'entry_without_sidebar',
    'large',
    'medium_large',
    'magazine',
    'masonry',
    'portfolio',
    'medium',
  ]
  const sizedUrls = preferred
    .map((name) => sizes[name])
    .filter(Boolean)
    .filter((size) => !size.filesize || size.filesize <= 950000)
    .map((size) => size.source_url)
  return unique([...sizedUrls, media.source_url])
}

function readingTime(html) {
  const words = cleanText(html).split(/\s+/).filter(Boolean).length
  return Math.max(4, Math.min(18, Math.ceil(words / 180)))
}

function normalizeDate(value) {
  return String(value || '').slice(0, 10) || new Date().toISOString().slice(0, 10)
}

function list(items) {
  return `<ul>${items.map((item) => `<li>${escapeHtml(item)}</li>`).join('')}</ul>`
}

function cleanText(value = '') {
  return decodeHtml(String(value)
    .replace(/<script[\s\S]*?<\/script>/gi, ' ')
    .replace(/<style[\s\S]*?<\/style>/gi, ' ')
    .replace(/<[^>]+>/g, ' '))
    .replace(/\s+/g, ' ')
    .trim()
}

function decodeHtml(value = '') {
  return String(value)
    .replace(/&#038;|&amp;/g, '&')
    .replace(/&#8211;|&ndash;/g, '-')
    .replace(/&#8212;|&mdash;/g, '-')
    .replace(/&#8216;|&lsquo;/g, "'")
    .replace(/&#8217;|&rsquo;/g, "'")
    .replace(/&#8220;|&ldquo;/g, '"')
    .replace(/&#8221;|&rdquo;/g, '"')
    .replace(/&quot;/g, '"')
    .replace(/&#039;|&apos;/g, "'")
    .replace(/&nbsp;/g, ' ')
}

function escapeHtml(value = '') {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function slugify(value) {
  return String(value)
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
}

function capitalizeSentence(value) {
  const text = String(value || '').trim()
  return text ? text[0].toUpperCase() + text.slice(1) : text
}

function countBy(items, key) {
  return items.reduce((acc, item) => {
    const value = item[key] || 'unknown'
    acc[value] = (acc[value] || 0) + 1
    return acc
  }, {})
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

function unique(items) {
  return Array.from(new Set(items.filter(Boolean)))
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
