<template>
  <header
    class="fixed top-0 left-0 right-0 z-50 transition-all duration-300"
    :class="[
      scrolled
        ? 'bg-titanium-950/95 backdrop-blur-md border-b border-titanium-800/50 shadow-xl shadow-black/20'
        : 'bg-transparent',
    ]"
  >
    <!-- Top announcement bar -->
    <div class="bg-accent-600/90 text-white text-xs py-1.5 px-4 text-center hidden md:block">
      <span class="opacity-90">Baoji Titanium Valley | MTR Available | Global Shipping | Small MOQ Welcome</span>
      <span class="mx-3 opacity-40">|</span>
      <a :href="mailtoHref" class="hover:underline font-medium">{{ siteConfig.email }}</a>
      <span class="mx-3 opacity-40">|</span>
      <a :href="whatsappHref" class="hover:underline font-medium">WhatsApp</a>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <NuxtLink to="/" class="flex items-center gap-3 group">
          <img
            src="/brand-icon.svg"
            alt=""
            aria-hidden="true"
            class="w-9 h-9 flex-shrink-0 transition-transform duration-300 group-hover:scale-105"
          >
          <div>
            <div class="font-display font-bold text-white text-lg leading-none tracking-wide">{{ siteConfig.siteName }}</div>
            <div class="text-titanium-500 text-[10px] leading-none tracking-widest uppercase">{{ siteConfig.tagline }}</div>
          </div>
        </NuxtLink>

        <!-- Desktop nav -->
        <nav class="hidden lg:flex items-center gap-1">
          <template v-for="item in topNavigationItems" :key="item.label">
            <div v-if="item.children?.length" class="relative" @mouseenter="openMenu(item.label)" @mouseleave="closeMenu">
            <button
              class="flex items-center gap-1 px-3 py-2 text-sm font-medium rounded-lg transition-colors duration-200"
              :class="activeMenu === item.label ? 'text-white bg-titanium-800/50' : 'text-titanium-300 hover:text-white hover:bg-titanium-800/30'"
            >
              {{ item.label }}
              <svg class="w-3.5 h-3.5 transition-transform duration-200" :class="activeMenu === item.label ? 'rotate-180' : ''" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            </div>

            <NuxtLink
              v-else
              :to="localizedPath(item.href)"
              class="px-3 py-2 text-sm font-medium rounded-lg transition-colors duration-200 text-titanium-300 hover:text-white hover:bg-titanium-800/30"
            >
              {{ item.label }}
            </NuxtLink>
          </template>
        </nav>

        <!-- Right actions -->
        <div class="flex items-center gap-2">
          <!-- WhatsApp -->
          <a
            :href="whatsappHref"
            target="_blank"
            class="hidden md:flex items-center gap-1.5 px-3 py-2 text-sm text-titanium-400 hover:text-[#25D366] rounded-lg hover:bg-titanium-800/30 transition-colors duration-200"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
              <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
            </svg>
            <span class="hidden lg:inline text-xs font-medium">WhatsApp</span>
          </a>

          <!-- Get Quote CTA -->
          <NuxtLink :to="localizedPath('/request-a-quote')" class="btn-primary text-sm py-2 px-4">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Get a Quote
          </NuxtLink>

          <!-- Mobile menu button -->
          <button
            class="lg:hidden p-2 text-titanium-400 hover:text-white rounded-lg hover:bg-titanium-800/30 transition-colors"
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

    <!-- Mega Menu -->
    <Transition name="mega-menu">
      <div
        v-if="activeDropdownItem"
        class="hidden lg:block absolute top-full left-0 right-0 bg-titanium-950/98 backdrop-blur-xl border-b border-titanium-800/50 shadow-2xl"
        @mouseenter="openMenu(activeDropdownItem.label)"
        @mouseleave="closeMenu"
      >
        <div class="max-w-7xl mx-auto px-8 py-8">
          <div class="grid grid-cols-5 gap-3">
            <NuxtLink
              v-for="child in activeDropdownChildren"
              :key="`${activeDropdownItem.label}-${child.label}`"
              :to="localizedPath(child.href)"
              class="group flex items-start gap-3 p-3 rounded-xl hover:bg-titanium-800/50 transition-all duration-200"
              @click="closeMenu"
            >
              <div class="w-9 h-9 rounded-lg bg-titanium-800 group-hover:bg-accent-500/20 border border-titanium-700 group-hover:border-accent-500/40 flex items-center justify-center flex-shrink-0 transition-all duration-200">
                <ProductCategoryIcon :icon="child.icon || ''" class="w-4 h-4 text-titanium-400 group-hover:text-accent-400 transition-colors" />
              </div>
              <div>
                <div class="text-sm font-medium text-titanium-200 group-hover:text-white transition-colors leading-tight">{{ child.label }}</div>
                <div v-if="child.badge" class="text-xs text-accent-400 mt-0.5 leading-tight">{{ child.badge }}</div>
              </div>
            </NuxtLink>
          </div>
          <div class="mt-6 pt-5 border-t border-titanium-800/50 flex items-center justify-between">
            <span class="text-xs text-titanium-500">{{ activeDropdownChildren.length }} menu items</span>
            <NuxtLink v-if="activeDropdownItem.href" :to="localizedPath(activeDropdownItem.href)" class="text-sm text-accent-400 hover:text-accent-300 font-medium flex items-center gap-1 transition-colors" @click="closeMenu">
              View all
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
              </svg>
            </NuxtLink>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Mobile menu -->
    <Transition name="mobile-menu">
      <div v-if="mobileOpen" class="lg:hidden bg-titanium-950/98 backdrop-blur-xl border-t border-titanium-800/50">
        <div class="max-w-7xl mx-auto px-4 py-4 space-y-1">
          <template v-for="item in topNavigationItems" :key="`mobile-${item.label}`">
            <div v-if="item.children?.length">
            <button
              class="w-full flex items-center justify-between px-4 py-3 text-sm font-medium text-titanium-200 hover:text-white hover:bg-titanium-800/30 rounded-lg transition-colors"
              @click="toggleMobileGroup(item.label)"
            >
              {{ item.label }}
              <svg class="w-4 h-4 transition-transform" :class="mobileOpenGroup === item.label ? 'rotate-180' : ''" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            <div v-if="mobileOpenGroup === item.label" class="ml-4 mt-1 space-y-1">
              <NuxtLink
                v-for="child in item.children"
                :key="`mobile-${item.label}-${child.label}`"
                :to="localizedPath(child.href)"
                class="block px-4 py-2 text-sm text-titanium-400 hover:text-white hover:bg-titanium-800/30 rounded-lg transition-colors"
                @click="mobileOpen = false"
              >
                {{ child.label }}
              </NuxtLink>
            </div>
          </div>

          <NuxtLink
            v-else
            :to="localizedPath(item.href)"
            class="block px-4 py-3 text-sm font-medium text-titanium-200 hover:text-white hover:bg-titanium-800/30 rounded-lg transition-colors"
            @click="mobileOpen = false"
          >
            {{ item.label }}
          </NuxtLink>
          </template>

          <div class="pt-3 border-t border-titanium-800/50 flex gap-2">
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

const scrolled = ref(false)
const activeMenu = ref<string | null>(null)
const mobileOpen = ref(false)
const mobileOpenGroup = ref<string | null>(null)

let closeTimer: ReturnType<typeof setTimeout> | null = null

function openMenu(name: string) {
  if (closeTimer) clearTimeout(closeTimer)
  activeMenu.value = name
}

function closeMenu() {
  closeTimer = setTimeout(() => {
    activeMenu.value = null
  }, 150)
}

const productMenuFallback = computed<NavigationItem>(() => ({
  label: 'Products',
  href: '/products',
  children: productCategories.value.map(category => ({
    label: category.name,
    href: `/products/${category.slug}`,
    icon: category.icon,
  })),
}))
const topNavigationItems = computed(() => navigationItems.value.length ? navigationItems.value : [productMenuFallback.value])
const activeDropdownItem = computed(() => topNavigationItems.value.find(item => item.label === activeMenu.value && item.children?.length))
const activeDropdownChildren = computed(() => activeDropdownItem.value?.children || [])

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
.mega-menu-enter-active,
.mega-menu-leave-active {
  transition: all 0.2s ease;
}
.mega-menu-enter-from,
.mega-menu-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.15s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px) scale(0.97);
}

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
