<template>
  <div class="min-h-screen bg-titanium-50 pt-24 pb-20">
    <section class="border-b border-titanium-200 bg-white">
      <div class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <nav class="mb-8 flex items-center gap-2 text-sm text-titanium-500">
          <NuxtLink to="/" class="transition-colors hover:text-titanium-800">Home</NuxtLink>
          <svg class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
          </svg>
          <span class="text-titanium-700">Factory Tour</span>
        </nav>

        <div class="max-w-3xl">
          <p class="section-label mb-3">{{ page.heroLabel }}</p>
          <h1 class="section-title mb-5">{{ page.heroTitle }}</h1>
          <p v-if="page.heroIntro" class="section-subtitle">{{ page.heroIntro }}</p>
        </div>
      </div>
    </section>

    <section class="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
      <div v-if="page.items.length" class="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
        <article v-for="item in page.items" :key="`${item.title}-${item.imageUrl}`" class="card group overflow-hidden transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-titanium-200/80">
          <div class="flex aspect-[4/3] items-center justify-center bg-white p-3">
            <img
              :src="item.imageUrl"
              :alt="item.imageAlt || item.title || 'Factory tour'"
              class="h-full w-full object-contain transition-transform duration-500 group-hover:scale-[1.03]"
              loading="lazy"
            >
          </div>
          <div class="border-t border-titanium-200 p-5">
            <h2 class="text-base font-semibold text-titanium-950">{{ item.title || 'Factory Image' }}</h2>
            <p v-if="item.desc" class="mt-2 text-sm leading-relaxed text-titanium-500">{{ item.desc }}</p>
          </div>
        </article>
      </div>

      <div v-else class="mx-auto max-w-2xl rounded-xl border border-dashed border-titanium-300 bg-white p-10 text-center">
        <p class="text-sm font-semibold text-titanium-950">Factory images are being prepared.</p>
        <p class="mt-2 text-sm leading-relaxed text-titanium-500">
          The gallery is connected to the admin panel. Upload factory, equipment or inspection photos in Site Settings to publish them here.
        </p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { siteConfig as fallbackSiteConfig } from '@cnbjti/mock-data'
import type { GalleryPageConfig } from '@cnbjti/types'

const { siteConfig } = await useSiteRuntime()
const fallbackPage = fallbackSiteConfig.factoryTourPage as GalleryPageConfig

const page = computed(() => normalizeGalleryPage(siteConfig.value.factoryTourPage, fallbackPage))

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
