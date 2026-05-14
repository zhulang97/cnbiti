<template>
  <section class="relative min-h-screen flex items-center overflow-hidden">
    <div class="absolute inset-0 bg-titanium-950" />
    <div class="absolute inset-0 grid-pattern opacity-60" />
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-32 pb-20 w-full">
      <div class="grid lg:grid-cols-2 gap-12 items-center">
        <div>
          <div class="inline-flex items-center gap-2 px-3 py-1.5 bg-accent-500/10 border border-accent-500/30 rounded-full mb-6">
            <div class="w-1.5 h-1.5 bg-accent-400 rounded-full animate-pulse" />
            <span class="text-accent-400 text-xs font-semibold tracking-wide">Baoji Titanium Valley · Est. 2008</span>
          </div>
          <h1 class="font-display font-bold text-4xl sm:text-5xl lg:text-6xl leading-tight mb-6">
            <span class="text-white">Titanium Materials &amp; Custom Processing</span><br />
            <span class="gradient-text">for Global Buyers</span>
          </h1>
          <p class="text-titanium-400 text-lg leading-relaxed mb-8 max-w-xl">
            Titanium bar, plate, tube, wire, forgings, fittings, anodes and CNC parts supplied with export-ready documentation from Baoji, China.
          </p>
          <div class="flex flex-wrap gap-3 mb-10">
            <NuxtLink :to="localizedPath('/request-a-quote')" class="btn-primary text-base px-7 py-3.5">Get a Quote</NuxtLink>
            <a :href="whatsappHref" target="_blank" class="inline-flex items-center gap-2 px-7 py-3.5 bg-green-500/10 border border-green-500/40 text-green-400 hover:bg-green-500/20 font-semibold rounded-lg transition-all duration-200 text-base">WhatsApp</a>
            <NuxtLink :to="localizedPath('/products')" class="btn-secondary text-base px-7 py-3.5">View Products</NuxtLink>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div v-for="stat in stats" :key="stat.label" class="text-center p-3 bg-titanium-900/50 border border-titanium-800/50 rounded-xl">
              <div class="font-display font-bold text-2xl text-white">{{ stat.value }}</div>
              <div class="text-titanium-500 text-xs mt-0.5">{{ stat.label }}</div>
            </div>
          </div>
        </div>
        <div class="hidden lg:block relative">
          <div class="relative rounded-2xl overflow-hidden border border-titanium-700/50 bg-titanium-900 shadow-2xl animate-float">
            <video
              v-if="heroVideoUrl && videoPlaying"
              :src="heroVideoUrl"
              :poster="heroPosterUrl"
              class="content-image-lg h-80"
              controls
              autoplay
              playsinline
              @ended="videoPlaying = false"
            />
            <button
              v-else-if="heroVideoUrl"
              type="button"
              class="relative block w-full text-left group"
              aria-label="Play factory introduction video"
              @click="videoPlaying = true"
            >
              <img :src="heroPosterUrl" alt="Titanium factory video cover" class="content-image-lg h-80" />
              <span class="absolute inset-0 flex items-center justify-center bg-titanium-950/10 group-hover:bg-titanium-950/20 transition-colors">
                <span class="flex items-center justify-center w-16 h-16 rounded-full bg-accent-500 text-white shadow-2xl shadow-accent-500/30 ring-8 ring-accent-500/15 group-hover:scale-105 transition-transform">
                  <svg class="w-7 h-7 ml-1" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M8 5v14l11-7z" /></svg>
                </span>
              </span>
              <span class="absolute top-4 left-4 px-3 py-1.5 rounded-full bg-titanium-950/80 border border-titanium-700 text-xs font-semibold text-white backdrop-blur">
                Factory Video
              </span>
            </button>
            <img v-else :src="heroPosterUrl" alt="Titanium sheet workshop" class="content-image-lg h-80" />
            <div v-if="!videoPlaying" class="absolute inset-0 bg-gradient-to-t from-titanium-950/80 via-transparent to-transparent pointer-events-none" />
            <div v-if="!videoPlaying" class="absolute bottom-4 left-4 right-4 flex items-center justify-between">
              <div>
                <div class="text-white font-semibold text-sm">Titanium Sheet &amp; Plate Supply</div>
                <div class="text-titanium-400 text-xs">ASTM B265 &middot; Cut-to-size processing</div>
              </div>
              <NuxtLink :to="localizedPath('/request-a-quote')" class="px-3 py-1.5 bg-accent-500 text-white text-xs font-semibold rounded-lg">Quote</NuxtLink>
            </div>
          </div>
          <div class="absolute -top-4 -right-4 bg-titanium-900 border border-titanium-700 rounded-xl p-3 shadow-xl animate-float" style="animation-delay:1s">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 bg-green-500/20 rounded-lg flex items-center justify-center">
                <svg class="w-4 h-4 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              </div>
              <div><div class="text-white text-xs font-semibold">MTR Available</div><div class="text-titanium-500 text-xs">EN 10204 3.1</div></div>
            </div>
          </div>
          <div class="absolute -bottom-4 -left-4 bg-titanium-900 border border-titanium-700 rounded-xl p-3 shadow-xl animate-float" style="animation-delay:2s">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 bg-accent-500/20 rounded-lg flex items-center justify-center">
                <svg class="w-4 h-4 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064" /></svg>
              </div>
              <div><div class="text-white text-xs font-semibold">Global Shipping</div><div class="text-titanium-500 text-xs">50+ countries</div></div>
            </div>
          </div>
          <div class="absolute top-1/2 -right-6 transform -translate-y-1/2 flex flex-col gap-1.5">
            <div v-for="grade in grades" :key="grade" class="px-2.5 py-1 bg-titanium-900/90 border border-titanium-700 rounded-lg text-xs font-mono text-titanium-300">{{ grade }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="absolute bottom-8 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2 animate-bounce">
      <span class="text-titanium-600 text-xs tracking-widest uppercase">Scroll</span>
      <svg class="w-4 h-4 text-titanium-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" /></svg>
    </div>
  </section>
</template>

<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const { siteConfig, whatsappHref, localizedPath } = await useSiteRuntime()
const videoPlaying = ref(false)
const heroVideoUrl = computed(() => siteConfig.value.socialLinks?.homeHeroVideo || '')
const heroPosterUrl = computed(() => siteConfig.value.socialLinks?.homeHeroPosterImage || qinghangPageAssets.sheetFactory.url)
const stats = [
  { value: '15+', label: 'Years Export' },
  { value: '50+', label: 'Countries' },
  { value: '100+', label: 'Specifications' },
]
const grades = ['Gr.1', 'Gr.2', 'Gr.5', 'Gr.7', 'Gr.9', 'Gr.23']
</script>
