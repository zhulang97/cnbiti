const SITE_LOCALE_PREFIX = /^\/(?:en|zh|de|es)(?=\/|$)/i

export function withoutLocalePrefix(value?: string | null) {
  if (!value) return undefined
  return value.replace(SITE_LOCALE_PREFIX, '') || '/'
}

export function canonicalUrl(value?: string | null) {
  const normalized = withoutLocalePrefix(value)
  if (!normalized) return undefined

  try {
    const url = new URL(normalized)
    url.pathname = withoutLocalePrefix(url.pathname) || '/'
    return url.toString()
  } catch {
    return normalized
  }
}
