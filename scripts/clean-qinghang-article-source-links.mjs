const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'

const SOURCE_NOTE_PATTERN = /\n?<p class="text-titanium-500 text-sm">Source reference reviewed for topic coverage:[\s\S]*?The article text on this site has been rewritten for CNBJTI buyers\.<\/p>/gi

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const headers = { Authorization: `Bearer ${login.token}` }
  const articles = await api('/admin/articles', { headers })
  let updated = 0

  for (const item of articles) {
    const detail = await api(`/admin/articles/${encodeURIComponent(item.id)}`, { headers })
    const content = detail.content || ''
    const cleanedContent = content.replace(SOURCE_NOTE_PATTERN, '').trim()
    if (cleanedContent === content) continue

    await api(`/admin/articles/${encodeURIComponent(item.id)}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({
        title: detail.title,
        slug: detail.slug,
        category: detail.category,
        status: detail.status,
        excerpt: detail.excerpt,
        content: cleanedContent,
        coverImageUrl: detail.coverImageUrl,
        tags: detail.tags || [],
        publishedAt: detail.publishedAt,
        readingTime: detail.readingTime,
      }),
    })
    updated += 1
    console.log(`cleaned ${detail.slug}`)
  }

  console.log(JSON.stringify({ scanned: articles.length, updated }, null, 2))
}

async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    },
  })
  const payload = await response.json()
  if (!response.ok || payload.code !== 'OK') {
    throw new Error(`${path}: ${payload.message || response.statusText}`)
  }
  return payload.data
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
