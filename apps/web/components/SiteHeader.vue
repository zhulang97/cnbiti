<template>
  <header
    class="fixed top-0 left-0 right-0 z-50 transition-all duration-300"
    :class="[
      scrolled
        ? 'bg-white/95 backdrop-blur-md border-b border-titanium-200 shadow-lg shadow-titanium-200/60'
        : 'bg-white/90 backdrop-blur-md border-b border-titanium-200/70',
    ]"
  >
    <!-- Top announcement bar -->
    <div class="bg-steel-900 text-white text-xs py-1.5 px-4 text-center hidden xl:block">
      <span class="opacity-90">Baoji Titanium Valley | MTR Available | Global Shipping | Small MOQ Welcome</span>
      <span class="mx-3 opacity-40">|</span>
      <a :href="mailtoHref" class="hover:underline font-medium">{{ siteConfig.email }}</a>
      <span class="mx-3 opacity-40">|</span>
      <a :href="whatsappHref" class="hover:underline font-medium">WhatsApp</a>
    </div>

    <div class="max-w-[1520px] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <NuxtLink to="/" class="flex min-w-0 shrink-0 items-center gap-3 group">
          <img
            src="/brand-icon.svg"
            alt=""
            aria-hidden="true"
            class="w-10 h-10 flex-shrink-0 transition-transform duration-300 group-hover:scale-105"
          >
          <div class="min-w-0">
            <div class="font-display font-bold text-titanium-950 text-lg leading-none tracking-wide whitespace-nowrap">{{ siteConfig.siteName }}</div>
            <div class="mt-1 hidden max-w-[170px] truncate text-[10px] font-semibold leading-none tracking-widest text-titanium-500 uppercase 2xl:block">
              {{ headerTagline }}
            </div>
          </div>
        </NuxtLink>

        <!-- Desktop nav -->
        <nav class="hidden xl:flex flex-1 items-center justify-center gap-0.5 px-4">
          <template v-for="item in topNavigationItems" :key="item.label">
            <div v-if="item.children?.length" class="group/nav">
              <NuxtLink
                :to="localizedPath(item.href)"
                class="flex items-center gap-1 rounded-lg px-2.5 py-2 text-sm font-medium whitespace-nowrap transition-colors duration-200 2xl:px-3"
                :class="item.label.toLowerCase() === 'products' ? 'text-titanium-600 hover:text-titanium-950 hover:bg-titanium-100 group-hover/nav:text-titanium-950 group-hover/nav:bg-titanium-100' : 'text-titanium-600 hover:text-titanium-950 hover:bg-titanium-100'"
              >
                {{ item.label }}
                <svg class="w-3.5 h-3.5 transition-transform duration-200 group-hover/nav:rotate-180" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                </svg>
              </NuxtLink>

              <div class="invisible absolute left-0 right-0 top-full z-50 translate-y-2 border-y border-titanium-200 bg-white opacity-0 shadow-2xl shadow-titanium-300/80 transition-all duration-200 group-hover/nav:visible group-hover/nav:translate-y-0 group-hover/nav:opacity-100 group-focus-within/nav:visible group-focus-within/nav:translate-y-0 group-focus-within/nav:opacity-100">
                <div class="mx-auto max-w-7xl px-8 py-6">
                  <div class="grid grid-cols-4 gap-3 rounded-2xl bg-gradient-to-br from-white via-white to-steel-50/70 p-2 xl:grid-cols-5">
                    <NuxtLink
                      v-for="child in dropdownChildrenFor(item)"
                      :key="`${item.label}-${child.label}`"
                      :to="localizedPath(child.href)"
                      class="group flex items-start gap-3 rounded-xl border border-transparent p-3 transition-all duration-200 hover:border-accent-500/30 hover:bg-accent-500/5"
                    >
                      <div
                        class="flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-lg border transition-all duration-200"
                        :class="child.__allProducts ? 'border-accent-500/30 bg-accent-500/10' : 'border-titanium-200 bg-steel-50 group-hover:border-accent-500/30 group-hover:bg-accent-500/10'"
                      >
                        <svg v-if="child.__allProducts" class="h-4 w-4 text-accent-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
                        </svg>
                        <ProductCategoryIcon v-else :icon="child.icon || ''" class="h-4 w-4 text-titanium-500 transition-colors group-hover:text-accent-600" />
                      </div>
                      <div>
                        <div
                          class="text-sm font-semibold leading-tight transition-colors"
                          :class="child.__allProducts ? 'text-accent-700 group-hover:text-accent-600' : 'text-titanium-800 group-hover:text-titanium-950'"
                        >
                          {{ child.label }}
                        </div>
                        <div class="mt-0.5 text-xs leading-tight text-titanium-500">
                          {{ child.badge || childBadge(item, child) }}
                        </div>
                      </div>
                    </NuxtLink>
                  </div>
                </div>
              </div>
            </div>

            <NuxtLink
              v-else
              :to="localizedPath(item.href)"
              class="rounded-lg px-2.5 py-2 text-sm font-medium whitespace-nowrap text-titanium-600 transition-colors duration-200 hover:bg-titanium-100 hover:text-titanium-950 2xl:px-3"
            >
              {{ item.label }}
            </NuxtLink>
          </template>
        </nav>

        <!-- Right actions -->
        <div class="flex shrink-0 items-center gap-2">
          <!-- WhatsApp -->
          <a
            :href="whatsappHref"
            target="_blank"
            class="hidden 2xl:flex items-center gap-1.5 px-2.5 py-2 text-sm text-titanium-600 hover:text-[#128C4A] rounded-lg hover:bg-titanium-100 transition-colors duration-200"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
              <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
            </svg>
            <span class="hidden lg:inline text-xs font-medium">WhatsApp</span>
          </a>

          <!-- Get Quote CTA -->
          <NuxtLink :to="localizedPath('/request-a-quote')" class="btn-primary whitespace-nowrap text-sm py-2.5 px-3 2xl:px-4">
            <svg class="hidden w-4 h-4 2xl:block" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Get a Quote
          </NuxtLink>

          <!-- Mobile menu button -->
          <button
            class="xl:hidden p-2 text-titanium-600 hover:text-titanium-950 rounded-lg hover:bg-titanium-100 transition-colors"
            @click="mobileOpen = !mobileOpen"
          >
            <svg v-if="!mobileOpen" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
            <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <Transition name="mobile-menu">
      <div v-if="mobileOpen" class="xl:hidden bg-white border-t border-titanium-200 shadow-xl shadow-titanium-200/60">
        <div class="max-w-7xl mx-auto px-4 py-4 space-y-1">
          <template v-for="item in topNavigationItems" :key="`mobile-${item.label}`">
            <div v-if="item.children?.length">
            <button
              class="w-full flex items-center justify-between px-4 py-3 text-sm font-medium text-titanium-700 hover:text-titanium-950 hover:bg-titanium-100 rounded-lg transition-colors"
              @click="toggleMobileGroup(item.label)"
            >
              {{ item.label }}
              <svg class="w-4 h-4 transition-transform" :class="mobileOpenGroup === item.label ? 'rotate-180' : ''" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            <div v-if="mobileOpenGroup === item.label" class="ml-4 mt-1 space-y-1">
              <NuxtLink
                v-if="item.href"
                :to="localizedPath(item.href)"
                class="block px-4 py-2 text-sm font-semibold text-accent-600 hover:text-accent-700 hover:bg-accent-500/10 rounded-lg transition-colors"
                @click="mobileOpen = false"
              >
                All {{ item.label }}
              </NuxtLink>
              <NuxtLink
                v-for="child in item.children"
                :key="`mobile-${item.label}-${child.label}`"
                :to="localizedPath(child.href)"
                class="block px-4 py-2 text-sm text-titanium-600 hover:text-titanium-950 hover:bg-titanium-100 rounded-lg transition-colors"
                @click="mobileOpen = false"
              >
                {{ child.label }}
              </NuxtLink>
            </div>
          </div>

          <NuxtLink
            v-else
            :to="localizedPath(item.href)"
            class="block px-4 py-3 text-sm font-medium text-titanium-700 hover:text-titanium-950 hover:bg-titanium-100 rounded-lg transition-colors"
            @click="mobileOpen = false"
          >
            {{ item.label }}
          </NuxtLink>
          </template>

          <div class="pt-3 border-t border-titanium-200 flex gap-2">
            <NuxtLink :to="localizedPath('/request-a-quote')" class="btn-primary flex-1 justify-center text-sm py-2.5" @click="mobileOpen = false">
              Get a Quote
            </NuxtLink>
            <a :href="whatsappHref" target="_blank" class="flex items-center justify-center w-12 h-10 bg-[#25D366]/10 border border-[#25D366]/30 text-[#25D366] rounded-lg hover:bg-[#25D366]/20 transition-colors">
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
                <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </Transition>
  </header>
</template>

<script setup lang="ts">
import type { NavigationItem } from '@cnbjti/types'

const { siteConfig, navigationItems, productCategories, whatsappHref, mailtoHref, localizedPath } = await useSiteRuntime()

type HeaderDropdownItem = NavigationItem & { __allProducts?: boolean }

const scrolled = ref(false)
const mobileOpen = ref(false)
const mobileOpenGroup = ref<string | null>(null)
const companyHrefs = new Set(['/quality', '/certificates', '/factory-tour', '/about', '/contact'])
const headerTagline = computed(() => {
  const city = siteConfig.value.city ? siteConfig.value.city.split(',')[0] : 'Baoji'
  return `${city} Titanium Supplier`
})

const productMenuFallback = computed<NavigationItem>(() => ({
  label: 'Products',
  href: '/products',
  children: productCategories.value.map(category => ({
    label: category.name,
    href: `/products/${category.slug}`,
    icon: category.icon,
  })),
}))
const topNavigationItems = computed<NavigationItem[]>(() => {
  const items = navigationItems.value.length ? navigationItems.value : [productMenuFallback.value]
  const normalized = items.map((item) => {
    if (item.label.toLowerCase() !== 'products') return item
    return {
      ...item,
      href: item.href || '/products',
      children: item.children?.length ? item.children : productMenuFallback.value.children,
    }
  })
  const primary = normalized.filter((item) => !companyHrefs.has(stripLocalePrefix(item.href)))
  const companyChildren = normalized
    .filter((item) => companyHrefs.has(stripLocalePrefix(item.href)))
    .sort((a, b) => companyOrder(a) - companyOrder(b))

  if (companyChildren.length) {
    primary.push({
      label: 'Company',
      href: '/about',
      children: companyChildren,
    })
  }

  return primary
})
function dropdownChildrenFor(item: NavigationItem): HeaderDropdownItem[] {
  if (!item) return []

  const children = (item.children || []) as HeaderDropdownItem[]
  const shouldAddAllProducts = item.label.toLowerCase() === 'products' && item.href
  if (!shouldAddAllProducts) return children

  return [
    {
      label: 'View All Products',
      href: item.href,
      badge: 'Browse the complete catalog',
      __allProducts: true,
    },
    ...children,
  ]
}

function childBadge(parent: NavigationItem, child: HeaderDropdownItem) {
  if (child.__allProducts) return 'Browse the complete catalog'
  if (parent.label.toLowerCase() === 'company') return companyBadge(child)
  return 'Product category'
}

function companyBadge(item: NavigationItem) {
  const href = stripLocalePrefix(item.href)
  if (href === '/quality') return 'Quality system and testing'
  if (href === '/certificates') return 'Certificates and documents'
  if (href === '/factory-tour') return 'Workshop and equipment gallery'
  if (href === '/contact') return 'Email, WhatsApp and address'
  return 'Company profile'
}

function stripLocalePrefix(href?: string | null) {
  if (!href) return ''
  const [path] = href.split(/(?=[?#])/)
  return (path || '').replace(/^\/(?:en|zh|de|es)(?=\/|$)/i, '') || '/'
}

function companyOrder(item: NavigationItem) {
  const order = ['/quality', '/certificates', '/factory-tour', '/about', '/contact']
  const index = order.indexOf(stripLocalePrefix(item.href))
  return index === -1 ? order.length : index
}

function toggleMobileGroup(label: string) {
  mobileOpenGroup.value = mobileOpenGroup.value === label ? null : label
}

onMounted(() => {
  const handleScroll = () => { scrolled.value = window.scrollY > 20 }
  window.addEventListener('scroll', handleScroll, { passive: true })
  onUnmounted(() => window.removeEventListener('scroll', handleScroll))
})
</script>

<style scoped>
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.25s ease;
}
.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
