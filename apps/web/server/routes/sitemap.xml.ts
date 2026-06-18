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
  const siteUrl = String(config.public.siteUrl || 'https://cnbjti.com').replace(/\/$/, '')
  const apiBase = String(config.apiBase || 'http://127.0.0.1:8080/api').replace(/\/$/, '')

  const [categories, products, grades, standards, articles] = await Promise.all([
    fetchList<SlugItem>(apiBase, '/public/categories'),
    fetchList<SlugItem>(apiBase, '/public/products'),
    fetchList<SlugItem>(apiBase, '/public/grades'),
    fetchList<SlugItem>(apiBase, '/public/standards'),
    fetchList<SlugItem>(apiBase, '/public/articles'),
  ])

  const urls: SitemapUrl[] = [
    route(siteUrl, '/', 'weekly', '1.0'),
    route(siteUrl, '/products', 'weekly', '0.9'),
    route(siteUrl, '/grades', 'monthly', '0.7'),
    route(siteUrl, '/standards', 'monthly', '0.7'),
    route(siteUrl, '/processing', 'monthly', '0.7'),
    route(siteUrl, '/industries', 'monthly', '0.8'),
    route(siteUrl, '/resources', 'weekly', '0.8'),
    route(siteUrl, '/quality', 'monthly', '0.7'),
    route(siteUrl, '/about', 'monthly', '0.6'),
    route(siteUrl, '/contact', 'monthly', '0.6'),
    route(siteUrl, '/request-a-quote', 'monthly', '0.8'),
    route(siteUrl, '/faq', 'weekly', '0.7'),
    route(siteUrl, '/resources/titanium-grade-guide', 'monthly', '0.6'),
    route(siteUrl, '/resources/astm-b348-titanium-bar-standard', 'monthly', '0.6'),
    route(siteUrl, '/resources/titanium-corrosion-resistance', 'monthly', '0.6'),
    route(siteUrl, '/resources/titanium-weight-calculator', 'monthly', '0.7'),
    ...categories.map((item) => route(siteUrl, `/products/${item.slug}`, 'weekly', '0.8', item.updatedAt)),
    ...products
      .filter((item) => item.category?.slug)
      .map((item) => route(siteUrl, `/products/${item.category!.slug}/${item.slug}`, 'weekly', '0.8', item.updatedAt)),
    ...grades.map((item) => route(siteUrl, `/grades/${item.slug}`, 'monthly', '0.7', item.updatedAt)),
    ...standards.map((item) => route(siteUrl, `/standards/${item.slug}`, 'monthly', '0.7', item.updatedAt)),
    ...industryProfiles.map((item) => route(siteUrl, `/industries/${item.slug}`, 'monthly', '0.7')),
    ...articles.map((item) => route(siteUrl, `/resources/${item.slug}`, 'weekly', '0.7', item.publishedAt || item.updatedAt)),
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
