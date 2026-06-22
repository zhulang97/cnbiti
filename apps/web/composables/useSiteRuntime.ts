import { navigation as fallbackNavigation, productCategories as fallbackProductCategories, siteConfig as fallbackSiteConfig } from '@cnbjti/mock-data'
import type { NavigationItem, ProductCategory, SiteConfig } from '@cnbjti/types'

const LOCALE_PREFIX = /^\/(?:en|zh|de|es)(?=\/|$)/i

export async function useSiteRuntime() {
  const nuxtApp = useNuxtApp()
  let refreshSiteConfig = () => Promise.resolve()
  let refreshNavigation = () => Promise.resolve()
  let refreshCategories = () => Promise.resolve()

  function getHydrationCachedData<T>(key: string) {
    return (nuxtApp.isHydrating ? nuxtApp.payload.data[key] : undefined) as T | undefined
  }

  function refreshRuntime() {
    void refreshSiteConfig()
    void refreshNavigation()
    void refreshCategories()
  }

  if (import.meta.client) {
    onMounted(() => {
      refreshRuntime()

      const refreshOnVisible = () => {
        if (document.visibilityState === 'visible') refreshRuntime()
      }

      document.addEventListener('visibilitychange', refreshOnVisible)
      window.addEventListener('focus', refreshRuntime)
      onUnmounted(() => {
        document.removeEventListener('visibilitychange', refreshOnVisible)
        window.removeEventListener('focus', refreshRuntime)
      })
    })
  }

  const siteConfigAsync = useAsyncData(
    'public-site-config-runtime',
    () => publicApi<SiteConfig>('/public/site-config'),
    {
      default: () => fallbackSiteConfig,
      getCachedData: (key) => getHydrationCachedData<SiteConfig>(key),
    },
  )
  const navigationAsync = useAsyncData(
    'public-navigation-runtime',
    () => publicApi<NavigationItem[]>('/public/navigation'),
    {
      default: () => fallbackNavigation,
      getCachedData: (key) => getHydrationCachedData<NavigationItem[]>(key),
    },
  )
  const categoryAsync = useAsyncData(
    'public-navigation-categories-runtime',
    () => publicApi<ProductCategory[]>('/public/categories'),
    {
      default: () => fallbackProductCategories,
      getCachedData: (key) => getHydrationCachedData<ProductCategory[]>(key),
    },
  )

  const [
    siteConfigResult,
    navigationResult,
    categoryResult,
  ] = await Promise.all([siteConfigAsync, navigationAsync, categoryAsync])

  const { data: siteConfigData } = siteConfigResult
  const { data: navigationData } = navigationResult
  const { data: categoryData } = categoryResult
  refreshSiteConfig = siteConfigResult.refresh
  refreshNavigation = navigationResult.refresh
  refreshCategories = categoryResult.refresh

  const siteConfig = computed(() => siteConfigData.value || fallbackSiteConfig)
  const navigationItems = computed(() => ensureCoreNavigation(navigationData.value?.length ? navigationData.value : fallbackNavigation))
  const productCategories = computed(() => categoryData.value?.length ? categoryData.value : fallbackProductCategories)
  const whatsappHref = computed(() => toWhatsAppHref(siteConfig.value.whatsapp))
  const phoneHref = computed(() => toTelHref(siteConfig.value.phone))
  const mailtoHref = computed(() => `mailto:${siteConfig.value.email || fallbackSiteConfig.email}`)

  function localizedPath(href?: string | null) {
    if (!href) return '/'
    if (/^(https?:|mailto:|tel:)/i.test(href)) return href
    const [path, suffix = ''] = href.split(/(?=[?#])/)
    const normalized = (path || '/').replace(LOCALE_PREFIX, '') || '/'
    return `${normalized}${suffix}`
  }

  return {
    siteConfig,
    navigationItems,
    productCategories,
    whatsappHref,
    phoneHref,
    mailtoHref,
    localizedPath,
    refreshRuntime,
  }
}

export function toWhatsAppHref(value?: string | null) {
  const raw = String(value || '').trim()
  if (!raw) return 'https://wa.me/'
  if (/^https?:\/\//i.test(raw)) return raw
  const digits = raw.replace(/[^\d]/g, '')
  return digits ? `https://wa.me/${digits}` : 'https://wa.me/'
}

export function toTelHref(value?: string | null) {
  const raw = String(value || '').trim()
  if (!raw) return 'tel:'
  return `tel:${raw.replace(/[^\d+]/g, '')}`
}

export function stripLocalePrefix(href?: string | null) {
  if (!href) return '/'
  if (/^(https?:|mailto:|tel:)/i.test(href)) return href
  const [path, suffix = ''] = href.split(/(?=[?#])/)
  return `${(path || '/').replace(LOCALE_PREFIX, '') || '/'}${suffix}`
}

function ensureCoreNavigation(items: NavigationItem[]) {
  const result = [...items]
  const hasHref = (href: string) => result.some((item) => stripLocalePrefix(item.href) === href)
  const insertBefore = (targetHref: string, item: NavigationItem) => {
    const index = result.findIndex((entry) => stripLocalePrefix(entry.href) === targetHref)
    if (index === -1) result.push(item)
    else result.splice(index, 0, item)
  }

  if (!hasHref('/certificates')) {
    insertBefore('/processing', { label: 'Certificates', href: '/certificates' })
  }
  if (!hasHref('/factory-tour')) {
    insertBefore('/processing', { label: 'Factory Tour', href: '/factory-tour' })
  }
  return result
}
