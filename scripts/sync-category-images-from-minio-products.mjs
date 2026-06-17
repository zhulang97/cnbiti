const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'
const LOCAL_ASSET_PATTERN = /^http:\/\/(?:localhost|127\.0\.0\.1):(?:9000|9002)\/cnbjti-assets\//i

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const headers = { Authorization: `Bearer ${login.token}` }
  const [categories, products] = await Promise.all([
    api('/admin/categories', { headers }),
    api('/public/products'),
  ])

  let updated = 0
  for (const item of categories) {
    const detail = await api(`/admin/categories/${encodeURIComponent(item.id)}`, { headers })
    const candidate = products.find((product) =>
      product.category?.slug === detail.slug
      && (product.images || []).some((image) => LOCAL_ASSET_PATTERN.test(image.url || '')),
    )
    const imageUrl = candidate?.images?.find((image) => LOCAL_ASSET_PATTERN.test(image.url || ''))?.url
    if (!imageUrl || detail.imageUrl === imageUrl) continue

    await api(`/admin/categories/${encodeURIComponent(detail.id)}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({
        slug: detail.slug,
        name: detail.name,
        description: detail.description,
        imageUrl,
        icon: detail.icon,
        productCount: detail.productCount,
        seo: detail.seo,
        status: detail.status,
      }),
    })
    updated += 1
    console.log(`updated ${detail.slug} -> ${imageUrl}`)
  }

  console.log(JSON.stringify({ scanned: categories.length, updated }, null, 2))
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
