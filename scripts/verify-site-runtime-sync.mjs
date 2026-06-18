const API = process.env.API_BASE || 'http://127.0.0.1:8080/api'
const WEB = process.env.WEB_BASE || 'http://127.0.0.1:3002'

async function api(path, options = {}) {
  const res = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    },
  })
  const json = await res.json().catch(() => ({}))
  if (!res.ok || json.code !== 'OK') {
    throw new Error(`${path}: ${json.message || res.statusText}`)
  }
  return json.data
}

async function page(path) {
  const url = `${WEB}${path}`
  const res = await fetch(url, {
    headers: {
      'Cache-Control': 'no-cache',
      Pragma: 'no-cache',
    },
  })
  const text = await res.text()
  if (!res.ok) throw new Error(`${url}: HTTP ${res.status}`)
  return text
}

function assertIncludes(html, needle, label) {
  if (!html.includes(needle)) {
    throw new Error(`${label} did not include ${needle}`)
  }
}

const login = await api('/auth/login', {
  method: 'POST',
  body: JSON.stringify({ username: 'admin', password: 'cnbjti2026' }),
})
const auth = { Authorization: `Bearer ${login.token}` }
const originalSiteConfig = await api('/admin/site-config', { headers: auth })
const originalNavigation = await api('/admin/navigation', { headers: auth })

const testSiteConfig = {
  ...originalSiteConfig,
  siteName: 'CNBJTI Runtime Sync Check',
  tagline: 'Runtime Admin Sync Tagline',
  email: 'runtime-sync@cnbjti.test',
  phone: '+86-917-0000000',
  whatsapp: '+8613888800000',
  address: 'Runtime Sync Address 88',
  city: 'Runtime City',
  country: 'Runtime Country',
  socialLinks: originalSiteConfig.socialLinks || {},
}

const testNavigation = [
  {
    label: 'Runtime Sync Products',
    href: '/products',
    children: [
      {
        label: 'Runtime Sync Fittings',
        href: '/products/titanium-fittings',
        icon: 'fitting',
      },
      {
        label: 'Runtime Sync Bar',
        href: '/products/titanium-bar',
        icon: 'bar',
      },
    ],
  },
  { label: 'Runtime Sync About', href: '/about' },
  { label: 'Runtime Sync Contact', href: '/contact' },
]

try {
  await api('/admin/site-config', {
    method: 'PUT',
    headers: auth,
    body: JSON.stringify(testSiteConfig),
  })
  await api('/admin/navigation', {
    method: 'PUT',
    headers: auth,
    body: JSON.stringify({ items: testNavigation }),
  })

  const publicSiteConfig = await api('/public/site-config')
  const publicNavigation = await api('/public/navigation')
  if (publicSiteConfig.siteName !== testSiteConfig.siteName) throw new Error('Public site config did not update')
  if (publicNavigation[0]?.label !== testNavigation[0].label) throw new Error('Public navigation did not update')

  const checks = [
    ['/', ['CNBJTI Runtime Sync Check', 'runtime-sync@cnbjti.test', 'Runtime Sync Products']],
    ['/contact', ['runtime-sync@cnbjti.test', '+8613888800000', 'CNBJTI Runtime Sync Check']],
    ['/products', ['CNBJTI Runtime Sync Check', 'Runtime Sync Products', 'runtime-sync@cnbjti.test']],
    ['/products/titanium-fittings', ['CNBJTI Runtime Sync Check', 'runtime-sync@cnbjti.test']],
    ['/privacy', ['CNBJTI Runtime Sync Check', 'Runtime Sync Address 88', 'runtime-sync@cnbjti.test']],
    ['/cookies', ['CNBJTI Runtime Sync Check', 'runtime-sync@cnbjti.test']],
  ]

  for (const [path, needles] of checks) {
    const html = await page(path)
    for (const needle of needles) {
      assertIncludes(html, needle, path)
    }
  }

  console.log(JSON.stringify({
    ok: true,
    checkedPages: checks.map(([path]) => path),
    siteName: publicSiteConfig.siteName,
    firstNavigationLabel: publicNavigation[0]?.label,
  }, null, 2))
} finally {
  await api('/admin/site-config', {
    method: 'PUT',
    headers: auth,
    body: JSON.stringify(originalSiteConfig),
  })
  await api('/admin/navigation', {
    method: 'PUT',
    headers: auth,
    body: JSON.stringify({ items: originalNavigation }),
  })
}
