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
  const [productRows, categories] = await Promise.all([
    api('/admin/products', { headers }),
    api('/public/categories'),
  ])
  const categoryImageById = new Map(categories.map((category) => [category.id, category.image?.url || '']))
  let updated = 0

  for (const row of productRows) {
    const detail = await api(`/admin/products/${encodeURIComponent(row.id)}`, { headers })
    if ((detail.images || []).some((image) => LOCAL_ASSET_PATTERN.test(image.url || ''))) continue

    const imageUrl = categoryImageById.get(detail.categoryId)
    if (!imageUrl || !LOCAL_ASSET_PATTERN.test(imageUrl)) continue

    await api(`/admin/products/${encodeURIComponent(detail.id)}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({
        name: detail.name,
        slug: detail.slug,
        categoryId: detail.categoryId,
        gradeIds: detail.gradeIds || [],
        standardIds: detail.standardIds || [],
        status: detail.status,
        shortDescription: detail.shortDescription,
        description: detail.description,
        imageUrl,
        images: [{
          id: `pm${detail.id}`,
          url: imageUrl,
          thumbnailUrl: null,
          alt: detail.name,
          width: null,
          height: null,
          mimeType: 'image/jpeg',
          size: 0,
          filename: `${detail.slug}.jpg`,
        }],
        specs: detail.specs || [],
        seo: detail.seo,
        featured: detail.featured,
        inStock: detail.inStock,
      }),
    })
    updated += 1
    console.log(`updated ${detail.slug} -> ${imageUrl}`)
  }

  console.log(JSON.stringify({ scanned: productRows.length, updated }, null, 2))
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
