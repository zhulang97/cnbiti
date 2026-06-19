<template>
  <section class="py-24 bg-gradient-to-b from-white to-steel-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid lg:grid-cols-2 gap-16 items-center">
        <div>
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
        <div class="grid grid-cols-2 gap-4">
          <div v-for="(img, i) in images" :key="img.src" class="group relative overflow-hidden rounded-xl border border-titanium-200 bg-titanium-900 shadow-sm shadow-titanium-200/60" :class="i === 0 ? 'col-span-2 h-48' : 'h-36'">
            <img :src="img.src" :alt="img.alt" class="content-image-lg transition-transform duration-500 group-hover:scale-[1.04]" loading="lazy" />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/65 via-titanium-950/20 to-transparent" />
            <div class="media-caption absolute bottom-2 left-3 right-3 text-xs font-semibold text-white">{{ img.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'
import type { HomeCapability } from '@cnbjti/types'

const { siteConfig, localizedPath } = await useSiteRuntime()

const defaultCapabilities: HomeCapability[] = [
  { title: 'Cut-to-size', desc: 'Saw, waterjet, laser' },
  { title: 'CNC Machining', desc: 'Turning, milling, drilling' },
  { title: 'Grinding & Polishing', desc: 'Ra 0.4 achievable' },
  { title: 'Welding & Fabrication', desc: 'TIG, electron beam' },
  { title: 'Surface Treatment', desc: 'Anodizing, passivation' },
  { title: 'Inspection & Testing', desc: 'UT, PMI, hardness' },
]
const defaultImages = [
  { src: qinghangPageAssets.factoryCnc.url, alt: 'Titanium CNC factory', label: 'CNC Machining' },
  { src: qinghangPageAssets.waterJet.url, alt: 'Water jet cutting', label: 'Water Jet Cutting' },
  { src: qinghangPageAssets.surfaceTreatment.url, alt: 'Titanium surface treatment', label: 'Surface Treatment' },
]
const capabilities = computed(() => siteConfig.value.homePage?.capabilities?.length ? siteConfig.value.homePage.capabilities : defaultCapabilities)
const images = computed(() => {
  const customImages = capabilities.value
    .filter((item) => item.imageUrl)
    .map((item) => ({
      src: item.imageUrl || '',
      alt: item.imageAlt || item.title,
      label: item.title,
    }))
  return customImages.length ? customImages.slice(0, 3) : defaultImages
})
</script>
