import { industryProfiles } from '../../utils/industryContent'

interface ApiEnvelope<T> {
  data: T
}

interface SlugItem {
  slug: string
  updatedAt?: string
  publishedAt?: string
  category?: {
    slug: string
  }
}

interface SitemapUrl {
  loc: string
  changefreq: 'daily' | 'weekly' | 'monthly'
  priority: string
  lastmod?: string
}

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const siteUrl = String(config.public.siteUrl || 'https://www.cnbjti.com').replace(/\/$/, '')
  const apiBase = String(config.apiBase || 'http://127.0.0.1:8080/api').replace(/\/$/, '')

  const [categories, products, grades, standards, articles] = await Promise.all([
    fetchList<SlugItem>(apiBase, '/public/categories'),
    fetchList<SlugItem>(apiBase, '/public/products'),
    fetchList<SlugItem>(apiBase, '/public/grades'),
    fetchList<SlugItem>(apiBase, '/public/standards'),
    fetchList<SlugItem>(apiBase, '/public/articles'),
  ])

  const urls: SitemapUrl[] = [
    route(siteUrl, '/en', 'weekly', '1.0'),
    route(siteUrl, '/en/products', 'weekly', '0.9'),
    route(siteUrl, '/en/grades', 'monthly', '0.7'),
    route(siteUrl, '/en/standards', 'monthly', '0.7'),
    route(siteUrl, '/en/processing', 'monthly', '0.7'),
    route(siteUrl, '/en/industries', 'monthly', '0.8'),
    route(siteUrl, '/en/resources', 'weekly', '0.8'),
    route(siteUrl, '/en/quality', 'monthly', '0.7'),
    route(siteUrl, '/en/about', 'monthly', '0.6'),
    route(siteUrl, '/en/contact', 'monthly', '0.6'),
    route(siteUrl, '/en/request-a-quote', 'monthly', '0.8'),
    route(siteUrl, '/en/faq', 'weekly', '0.7'),
    route(siteUrl, '/en/resources/titanium-grade-guide', 'monthly', '0.6'),
    route(siteUrl, '/en/resources/astm-b348-titanium-bar-standard', 'monthly', '0.6'),
    route(siteUrl, '/en/resources/titanium-corrosion-resistance', 'monthly', '0.6'),
    route(siteUrl, '/en/resources/titanium-weight-calculator', 'monthly', '0.7'),
    ...categories.map((item) => route(siteUrl, `/en/products/${item.slug}`, 'weekly', '0.8', item.updatedAt)),
    ...products
      .filter((item) => item.category?.slug)
      .map((item) => route(siteUrl, `/en/products/${item.category!.slug}/${item.slug}`, 'weekly', '0.8', item.updatedAt)),
    ...grades.map((item) => route(siteUrl, `/en/grades/${item.slug}`, 'monthly', '0.7', item.updatedAt)),
    ...standards.map((item) => route(siteUrl, `/en/standards/${item.slug}`, 'monthly', '0.7', item.updatedAt)),
    ...industryProfiles.map((item) => route(siteUrl, `/en/industries/${item.slug}`, 'monthly', '0.7')),
    ...articles.map((item) => route(siteUrl, `/en/resources/${item.slug}`, 'weekly', '0.7', item.publishedAt || item.updatedAt)),
  ]

  setHeader(event, 'content-type', 'application/xml; charset=utf-8')
  return `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${dedupe(urls).map(toXml).join('\n')}\n</urlset>`
})

async function fetchList<T>(apiBase: string, path: string): Promise<T[]> {
  try {
    const response = await $fetch<ApiEnvelope<T[]>>(`${apiBase}${path}`)
    return Array.isArray(response.data) ? response.data : []
  } catch {
    return []
  }
}

function route(siteUrl: string, path: string, changefreq: SitemapUrl['changefreq'], priority: string, lastmod?: string): SitemapUrl {
  return {
    loc: `${siteUrl}${path}`,
    changefreq,
    priority,
    lastmod: normalizeDate(lastmod),
  }
}

function dedupe(urls: SitemapUrl[]) {
  const seen = new Set<string>()
  return urls.filter((url) => {
    if (seen.has(url.loc)) return false
    seen.add(url.loc)
    return true
  })
}

function toXml(url: SitemapUrl) {
  return [
    '  <url>',
    `    <loc>${escapeXml(url.loc)}</loc>`,
    ...(url.lastmod ? [`    <lastmod>${url.lastmod}</lastmod>`] : []),
    `    <changefreq>${url.changefreq}</changefreq>`,
    `    <priority>${url.priority}</priority>`,
    '  </url>',
  ].join('\n')
}

function normalizeDate(value?: string) {
  if (!value) return undefined
  const time = Date.parse(value)
  return Number.isFinite(time) ? new Date(time).toISOString().slice(0, 10) : undefined
}

function escapeXml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;')
}
