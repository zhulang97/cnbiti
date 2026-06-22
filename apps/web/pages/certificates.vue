<template>
  <div class="min-h-screen bg-white pt-24 pb-20">
    <section class="bg-white">
      <div class="mx-auto max-w-7xl px-4 pt-10 sm:px-6 lg:px-8">
        <nav class="flex items-center gap-2 text-sm text-titanium-500">
          <NuxtLink to="/" class="transition-colors hover:text-titanium-800">Home</NuxtLink>
          <svg class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
          </svg>
          <span class="text-titanium-700">Certificates</span>
        </nav>
      </div>
    </section>

    <section class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
      <div v-if="certificateItems.length" class="grid grid-cols-1 gap-x-20 gap-y-20 sm:grid-cols-2 lg:grid-cols-3">
        <article v-for="item in certificateItems" :key="item.imageUrl" class="flex justify-center">
          <div class="flex h-[360px] w-full max-w-[320px] items-center justify-center bg-white">
            <img
              :src="item.imageUrl"
              alt="CNBJTI certificate"
              class="max-h-full max-w-full object-contain"
              loading="lazy"
            >
          </div>
        </article>
      </div>

      <div v-else class="mx-auto max-w-2xl rounded-xl border border-dashed border-titanium-300 bg-white p-10 text-center">
        <p class="text-sm font-semibold text-titanium-950">Certificate images are being prepared.</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { siteConfig as fallbackSiteConfig } from '@cnbjti/mock-data'
import type { GalleryPageConfig } from '@cnbjti/types'

const { siteConfig } = await useSiteRuntime()
const fallbackPage = fallbackSiteConfig.certificatesPage as GalleryPageConfig

const page = computed(() => normalizeGalleryPage(siteConfig.value.certificatesPage, fallbackPage))
const certificateItems = computed(() => page.value.items.slice(0, 6))

function normalizeGalleryPage(value: Partial<GalleryPageConfig> | null | undefined, fallback: GalleryPageConfig): GalleryPageConfig {
  return {
    heroLabel: field(value?.heroLabel, fallback.heroLabel),
    heroTitle: field(value?.heroTitle, fallback.heroTitle),
    heroIntro: field(value?.heroIntro, fallback.heroIntro),
    items: Array.isArray(value?.items) ? value.items.filter((item) => item?.imageUrl) : fallback.items,
    seoTitle: field(value?.seoTitle, fallback.seoTitle),
    seoDescription: field(value?.seoDescription, fallback.seoDescription),
  }
}

function field(value: string | undefined, fallback: string) {
  return value === undefined || value === null ? fallback : value
}

useHead(() => ({
  title: page.value.seoTitle || page.value.heroTitle,
  meta: [
    { name: 'description', content: page.value.seoDescription || page.value.heroIntro },
  ],
}))
</script>
