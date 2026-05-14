import type { AdminUser } from '@cnbjti/types'

export interface ApiEnvelope<T> {
  code: string
  message: string
  data: T
  requestId?: string
}

export interface LoginResponse {
  token: string
  user: AdminUser
}

const apiBase = import.meta.env.VITE_API_BASE || '/api'

export function apiUrl(path: string) {
  return `${apiBase}${path}`
}

export function adminHeaders(headers: HeadersInit = {}) {
  const result = new Headers(headers)
  const token = localStorage.getItem('admin_token')
  if (token) result.set('Authorization', `Bearer ${token}`)
  return result
}

export async function apiRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = adminHeaders(options.headers)

  if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(apiUrl(path), {
    ...options,
    headers,
  })
  const payload = (await response.json()) as ApiEnvelope<T>

  if (!response.ok || payload.code !== 'OK') {
    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      localStorage.removeItem('admin_role')
      if (window.location.pathname !== '/login') window.location.href = '/login'
    }
    throw new Error(payload.message || 'Request failed')
  }

  return payload.data
}
