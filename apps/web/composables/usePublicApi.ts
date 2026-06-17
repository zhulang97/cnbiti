import type { ApiResponse } from '@cnbjti/types'

export async function publicApi<T>(path: string, options: Parameters<typeof $fetch<ApiResponse<T>>>[1] = {}): Promise<T> {
  const config = useRuntimeConfig()
  const apiBase = import.meta.server ? config.apiBase : config.public.apiBase
  const base = String(apiBase).replace(/\/$/, '')
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const method = String(options.method || 'GET').toUpperCase()
  const requestUrl = method === 'GET'
    ? withCacheBuster(`${base}${normalizedPath}`)
    : `${base}${normalizedPath}`
  const response = await $fetch<ApiResponse<T>>(requestUrl, {
    ...options,
    cache: method === 'GET' ? 'no-store' : options.cache,
    headers: {
      ...(method === 'GET' ? { 'Cache-Control': 'no-cache', Pragma: 'no-cache' } : {}),
      ...(options.headers || {}),
    },
  })

  if (response.code !== 'OK') {
    throw new Error(response.message || 'Request failed')
  }

  return response.data
}

function withCacheBuster(url: string) {
  const separator = url.includes('?') ? '&' : '?'
  return `${url}${separator}_=${Date.now()}`
}
