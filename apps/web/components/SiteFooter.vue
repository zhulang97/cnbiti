<template>
  <footer class="bg-titanium-950 border-t border-titanium-800/50">
    <!-- Main footer -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10">
        <!-- Brand column -->
        <div class="lg:col-span-2">
          <NuxtLink to="/" class="flex items-center gap-3 group mb-5">
            <img
              src="/brand-icon.svg"
              alt=""
              aria-hidden="true"
              class="w-10 h-10 flex-shrink-0 transition-transform duration-300 group-hover:scale-105"
            >
            <div>
              <div class="font-display font-bold text-white text-xl leading-none tracking-wide">{{ siteConfig.siteName }}</div>
              <div class="text-titanium-500 text-xs leading-none tracking-widest uppercase mt-0.5">{{ siteConfig.tagline }}</div>
            </div>
          </NuxtLink>
          <p class="text-titanium-400 text-sm leading-relaxed mb-6 max-w-xs">
            {{ siteConfig.tagline }}
          </p>
          <!-- Contact info -->
          <div class="space-y-2.5">
            <a :href="mailtoHref" class="flex items-center gap-2.5 text-sm text-titanium-400 hover:text-accent-400 transition-colors group">
              <div class="w-7 h-7 rounded-lg bg-titanium-800 group-hover:bg-accent-500/20 flex items-center justify-center flex-shrink-0 transition-colors">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
              {{ siteConfig.email }}
            </a>
            <a v-if="siteConfig.phone" :href="phoneHref" class="flex items-center gap-2.5 text-sm text-titanium-400 hover:text-accent-400 transition-colors group">
              <div class="w-7 h-7 rounded-lg bg-titanium-800 group-hover:bg-accent-500/20 flex items-center justify-center flex-shrink-0 transition-colors">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 002.25-2.25v-1.372c0-.516-.351-.966-.852-1.091l-4.423-1.106a1.125 1.125 0 00-1.173.417l-.97 1.293a1.125 1.125 0 01-1.21.38 12.035 12.035 0 01-7.143-7.143 1.125 1.125 0 01.38-1.21l1.293-.97a1.125 1.125 0 00.417-1.173L6.963 3.102A1.125 1.125 0 005.872 2.25H4.5A2.25 2.25 0 002.25 4.5v2.25z" />
                </svg>
              </div>
              {{ siteConfig.phone }}
            </a>
            <a v-if="siteConfig.whatsapp" :href="whatsappHref" target="_blank" class="flex items-center gap-2.5 text-sm text-titanium-400 hover:text-[#25D366] transition-colors group">
              <div class="w-7 h-7 rounded-lg bg-titanium-800 group-hover:bg-[#25D366]/20 flex items-center justify-center flex-shrink-0 transition-colors">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
                </svg>
              </div>
              {{ siteConfig.whatsapp }}
            </a>
            <div class="flex items-start gap-2.5 text-sm text-titanium-500">
              <div class="w-7 h-7 rounded-lg bg-titanium-800 flex items-center justify-center flex-shrink-0 mt-0.5">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
              </div>
              <span>{{ siteConfig.address }}<br>{{ siteConfig.city }}, {{ siteConfig.country }}</span>
            </div>
          </div>
        </div>

        <!-- Products -->
        <div>
          <h3 class="text-white font-semibold text-sm mb-4 uppercase tracking-wider">Products</h3>
          <ul class="space-y-2">
            <li v-for="item in productLinks" :key="item.href">
              <NuxtLink :to="localizedPath(item.href)" class="text-sm text-titanium-400 hover:text-accent-400 transition-colors">
                {{ item.label }}
              </NuxtLink>
            </li>
          </ul>
        </div>

        <!-- Company -->
        <div>
          <h3 class="text-white font-semibold text-sm mb-4 uppercase tracking-wider">Company</h3>
          <ul class="space-y-2">
            <li v-for="item in companyLinks" :key="item.href">
              <NuxtLink :to="localizedPath(item.href)" class="text-sm text-titanium-400 hover:text-accent-400 transition-colors">
                {{ item.label }}
              </NuxtLink>
            </li>
          </ul>
        </div>

        <!-- Resources -->
        <div>
          <h3 class="text-white font-semibold text-sm mb-4 uppercase tracking-wider">Resources</h3>
          <ul class="space-y-2">
            <li v-for="item in resourceLinks" :key="item.href">
              <NuxtLink :to="localizedPath(item.href)" class="text-sm text-titanium-400 hover:text-accent-400 transition-colors">
                {{ item.label }}
              </NuxtLink>
            </li>
          </ul>
          <!-- Certifications -->
          <div class="mt-6">
            <h4 class="text-titanium-500 text-xs uppercase tracking-wider mb-3">Certifications</h4>
            <div class="flex flex-wrap gap-2">
              <span v-for="cert in certs" :key="cert" class="px-2 py-1 bg-titanium-800 border border-titanium-700 rounded text-xs text-titanium-300 font-mono">
                {{ cert }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom bar -->
    <div class="border-t border-titanium-800/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-5 flex flex-col sm:flex-row items-center justify-between gap-3">
        <p class="text-titanium-600 text-xs">
          &copy; {{ new Date().getFullYear() }} {{ siteConfig.siteName }}. All rights reserved.
        </p>
        <div class="flex items-center gap-4">
          <NuxtLink to="/privacy" class="text-xs text-titanium-600 hover:text-titanium-400 transition-colors">Privacy Policy</NuxtLink>
          <NuxtLink to="/terms" class="text-xs text-titanium-600 hover:text-titanium-400 transition-colors">Terms of Use</NuxtLink>
          <NuxtLink to="/cookies" class="text-xs text-titanium-600 hover:text-titanium-400 transition-colors">Cookie Policy</NuxtLink>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import type { NavigationItem } from '@cnbjti/types'

const { siteConfig, navigationItems, productCategories, whatsappHref, phoneHref, mailtoHref, localizedPath } = await useSiteRuntime()

const productLinks = computed<NavigationItem[]>(() => {
  const productNav = navigationItems.value.find(item => item.children?.length)
  if (productNav?.children?.length) return productNav.children.slice(0, 6)
  return productCategories.value.slice(0, 6).map(category => ({
    label: category.name,
    href: `/products/${category.slug}`,
  }))
})

const topLinks = computed(() => navigationItems.value.filter(item => !item.children?.length))
const companyLinks = computed(() =>
  topLinks.value.filter(item => ['About', 'Quality', 'Processing', 'Industries', 'Contact'].includes(item.label)),
)

const resourceLinks = computed<NavigationItem[]>(() => {
  const links = topLinks.value.filter(item => ['Resources', 'Grades', 'Standards'].includes(item.label))
  return [
    ...links,
    { label: 'FAQ', href: '/faq' },
    { label: 'Request a Quote', href: '/request-a-quote' },
  ]
})

const certs = ['ASTM', 'ASME', 'AMS', 'EN 10204', 'ISO 9001']
</script>
