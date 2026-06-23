<template>
  <section class="py-24 bg-gradient-to-b from-white to-steel-50">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="grid items-center gap-10 lg:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)] xl:gap-12">
        <div class="lg:max-w-[720px]">
          <p class="section-label mb-3">Processing Capabilities</p>
          <h2 class="section-title mb-5">In-House Machining &amp; Custom Processing</h2>
          <p class="section-subtitle mb-8">From raw titanium stock to finished components, we handle cutting, machining, forming and surface treatment with tighter quality control.</p>
          <div class="grid grid-cols-2 gap-3 mb-8">
            <div v-for="cap in capabilities" :key="cap.title" class="flex items-start gap-3 p-3 bg-white border border-titanium-200 rounded-xl shadow-sm shadow-titanium-200/60">
              <div class="w-8 h-8 bg-accent-500/10 border border-accent-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              </div>
              <div>
                <div class="text-titanium-950 text-sm font-medium">{{ cap.title }}</div>
                <div class="text-titanium-500 text-xs mt-0.5">{{ cap.desc }}</div>
              </div>
            </div>
          </div>
          <NuxtLink :to="localizedPath('/processing')" class="btn-primary">
            View All Capabilities
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
          </NuxtLink>
        </div>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:justify-self-end lg:max-w-[640px] xl:max-w-[660px]">
          <div
            v-for="img in showcaseImages"
            :key="img.src"
            class="group overflow-hidden rounded-xl border border-titanium-200 bg-white shadow-sm shadow-titanium-200/60 transition-all duration-300 hover:-translate-y-1 hover:border-accent-500/30 hover:shadow-xl hover:shadow-titanium-200/80"
          >
            <div class="relative aspect-[4/3] overflow-hidden bg-gradient-to-br from-white via-steel-50 to-titanium-100">
              <img
                :src="img.src"
                :alt="img.alt"
                class="absolute inset-0 h-full w-full scale-110 object-cover object-center opacity-16 blur-xl transition-opacity duration-500 group-hover:opacity-22"
                loading="lazy"
                aria-hidden="true"
              />
              <div class="absolute inset-0 bg-white/50" />
              <img
                :src="img.src"
                :alt="img.alt"
                class="relative h-full w-full object-contain object-center transition-transform duration-500 group-hover:scale-[1.015]"
                loading="lazy"
              />
            </div>
            <div class="min-h-[58px] border-t border-titanium-100 bg-white px-4 py-3">
              <div class="truncate text-sm font-semibold leading-tight text-titanium-950">{{ img.label }}</div>
              <div v-if="img.subtitle" class="mt-1 truncate text-xs font-medium leading-tight text-titanium-500">{{ img.subtitle }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import { siteConfig as fallbackSiteConfig } from '@cnbjti/mock-data'
import type { GalleryPageConfig, HomeCapability } from '@cnbjti/types'

const { siteConfig, localizedPath } = await useSiteRuntime()

const defaultCapabilities: HomeCapability[] = [
  { title: 'Cut-to-size', desc: 'Saw, waterjet, laser' },
  { title: 'CNC Machining', desc: 'Turning, milling, drilling' },
  { title: 'Grinding & Polishing', desc: 'Ra 0.4 achievable' },
  { title: 'Welding & Fabrication', desc: 'TIG, electron beam' },
  { title: 'Surface Treatment', desc: 'Anodizing, passivation' },
  { title: 'Inspection & Testing', desc: 'UT, PMI, hardness' },
]
const fallbackFactoryTourPage = fallbackSiteConfig.factoryTourPage as GalleryPageConfig
const capabilities = computed(() => siteConfig.value.homePage?.capabilities?.length ? siteConfig.value.homePage.capabilities : defaultCapabilities)

const capabilityImages = computed(() => capabilities.value
  .filter((item) => item?.imageUrl)
  .slice(0, 4)
  .map((item) => ({
    src: item.imageUrl as string,
    alt: item.imageAlt || item.title || 'Processing capability image',
    label: item.title || 'Processing Capability',
    subtitle: item.desc || '',
  })))

const factoryImages = computed(() => {
  const configuredItems = siteConfig.value.factoryTourPage?.items
  const sourceItems = configuredItems?.some((item) => item?.imageUrl) ? configuredItems : fallbackFactoryTourPage.items

  return sourceItems
    .filter((item) => item?.imageUrl)
    .slice(0, 4)
    .map((item) => ({
      src: item.imageUrl,
      alt: item.imageAlt || item.title || 'Factory tour image',
      label: item.title || 'Factory Image',
      subtitle: item.desc || '',
    }))
})

const showcaseImages = computed(() => capabilityImages.value.length ? capabilityImages.value : factoryImages.value)
</script>
