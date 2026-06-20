<template>
  <section class="relative overflow-hidden bg-gradient-to-b from-white via-steel-50 to-titanium-50 pt-32 pb-16">
    <div class="absolute inset-0 grid-pattern opacity-60" />
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid gap-12 lg:grid-cols-[1.02fr_0.98fr] lg:items-start">
        <div>
          <div class="mb-6 inline-flex items-center gap-2 rounded-full border border-accent-500/30 bg-accent-500/10 px-3 py-1.5">
            <span class="h-1.5 w-1.5 rounded-full bg-accent-500" />
            <span class="text-xs font-semibold uppercase tracking-wide text-accent-600">Baoji Titanium Valley | Manufacturer & Export Supplier</span>
          </div>

          <h1 class="mb-6 max-w-3xl font-display text-4xl font-bold leading-tight text-titanium-950 sm:text-5xl lg:text-6xl">
            {{ heroTitle }}
          </h1>

          <p class="mb-5 max-w-2xl text-lg leading-relaxed text-titanium-700">
            {{ introText }}
          </p>
          <p class="mb-8 max-w-2xl text-base leading-relaxed text-titanium-600">
            {{ bodyText }}
          </p>

          <div class="flex flex-wrap gap-3">
            <NuxtLink :to="localizedPath('/products')" class="btn-primary px-6 py-3.5 text-base">View Titanium Products</NuxtLink>
            <NuxtLink :to="localizedPath('/request-a-quote')" class="btn-secondary px-6 py-3.5 text-base">Send RFQ</NuxtLink>
            <a :href="whatsappHref" target="_blank" class="inline-flex items-center gap-2 rounded-lg border border-green-500/40 bg-green-500/10 px-6 py-3.5 text-base font-semibold text-green-700 transition-colors hover:bg-green-500/15">
              WhatsApp
            </a>
          </div>
        </div>

        <div class="relative lg:pt-24">
          <div class="hero-media-float aspect-[144/100] overflow-hidden rounded-2xl border border-titanium-200 bg-white shadow-2xl shadow-titanium-300/40 lg:-ml-3 lg:w-[calc(100%+1.5rem)]">
            <video
              v-if="heroVideoUrl && videoPlaying"
              :src="heroVideoUrl"
              :poster="heroPosterUrl || undefined"
              class="h-full w-full bg-white object-contain object-center"
              controls
              autoplay
              playsinline
              @ended="videoPlaying = false"
            />
            <button
              v-else-if="heroVideoUrl"
              type="button"
              class="group relative block w-full text-left"
              aria-label="Play factory introduction video"
              @click="videoPlaying = true"
            >
              <img v-if="heroPosterUrl" :src="heroPosterUrl" alt="Titanium factory and product supply" class="h-full w-full bg-white object-contain object-center" />
              <div v-else class="h-full w-full bg-gradient-to-br from-white via-steel-50 to-titanium-100" />
              <span class="absolute inset-0 flex items-center justify-center bg-titanium-950/10 transition-colors group-hover:bg-titanium-950/20">
                <span class="flex h-16 w-16 items-center justify-center rounded-full bg-accent-500 text-white shadow-2xl shadow-accent-500/30 ring-8 ring-accent-500/15 transition-transform group-hover:scale-105">
                  <svg class="ml-1 h-7 w-7" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M8 5v14l11-7z" /></svg>
                </span>
              </span>
            </button>
            <img v-else-if="heroPosterUrl" :src="heroPosterUrl" alt="Titanium factory and product supply" class="h-full w-full bg-white object-contain object-center" />
            <div v-else class="h-full w-full bg-gradient-to-br from-white via-steel-50 to-titanium-100" />

          </div>

          <div class="mt-5 grid grid-cols-2 gap-3 sm:grid-cols-4 lg:-ml-3 lg:w-[calc(100%+1.5rem)]">
            <div v-for="stat in stats" :key="stat.label" class="rounded-xl border border-titanium-200 bg-white/95 p-4 shadow-sm shadow-titanium-200/70 backdrop-blur">
              <div class="font-display text-2xl font-bold text-titanium-950">{{ stat.value }}</div>
              <div class="mt-1 text-xs leading-snug text-titanium-500">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const { siteConfig, whatsappHref, localizedPath } = await useSiteRuntime()
const videoPlaying = ref(false)

const defaultHeroTitle = 'Baoji Titanium Materials Manufacturer & Custom Processing Supplier'
const defaultHeroBody = 'We supply titanium bar, sheet, plate, tube, wire, forgings, fittings, fasteners, anodes and CNC titanium parts with MTR, export packing and drawing-based processing support.'
const defaultStats = [
  { value: '2008', label: 'Founded in Baoji' },
  { value: '50+', label: 'Countries Served' },
  { value: '100+', label: 'Specifications' },
  { value: '24h', label: 'RFQ Response' },
]
const heroVideoUrl = computed(() => siteConfig.value.socialLinks?.homeHeroVideo || '')
const heroPosterUrl = computed(() => siteConfig.value.socialLinks?.homeHeroPosterImage || '')
const heroTitle = computed(() => siteConfig.value.homePage?.heroTitle || defaultHeroTitle)
const introText = computed(() => siteConfig.value.homePage?.heroIntro
  || `CNBJTI is based in ${siteConfig.value.city || 'Baoji'}, ${siteConfig.value.country || 'China'}, focused on certified titanium materials and custom processing for global buyers.`)
const bodyText = computed(() => siteConfig.value.homePage?.heroBody || defaultHeroBody)
const stats = computed(() => siteConfig.value.homePage?.stats?.length ? siteConfig.value.homePage.stats : defaultStats)
</script>
