<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-24 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="absolute top-0 right-0 w-[500px] h-[500px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">About</span>
        </nav>
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <div class="section-label">{{ aboutPage.heroLabel }}</div>
            <h1 class="section-title mt-3 mb-6">{{ aboutPage.heroTitle }}</h1>
            <p v-if="aboutPage.heroIntro" class="text-titanium-300 text-lg leading-relaxed mb-6">
              {{ aboutPage.heroIntro }}
            </p>
            <p v-if="aboutPage.heroBody" class="text-titanium-400 leading-relaxed">
              {{ aboutPage.heroBody }}
            </p>
          </div>
          <!-- Stats -->
          <div v-if="aboutPage.stats.length" class="grid grid-cols-2 gap-5">
            <div v-for="stat in aboutPage.stats" :key="`${stat.value}-${stat.label}`" class="card p-6 text-center">
              <div class="text-3xl font-display font-bold gradient-text mb-2">{{ stat.value }}</div>
              <div class="text-titanium-400 text-sm">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Why Baoji -->
    <section class="bg-titanium-900/30 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">{{ aboutPage.locationLabel }}</div>
        <h2 class="section-title mb-4">{{ aboutPage.locationTitle }}</h2>
        <p class="text-titanium-400 max-w-2xl mb-12 leading-relaxed">
          {{ aboutPage.locationDescription }}
        </p>
        <div v-if="aboutPage.advantages.length" class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div v-for="item in aboutPage.advantages" :key="item.title" class="card p-6">
            <div v-if="item.icon" class="w-12 h-12 rounded-xl bg-accent-500/10 border border-accent-500/20 flex items-center justify-center mb-5">
              <span class="text-2xl">{{ item.icon }}</span>
            </div>
            <h3 class="text-white font-semibold mb-3">{{ item.title }}</h3>
            <p class="text-titanium-400 text-sm leading-relaxed">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Timeline -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">{{ aboutPage.timelineLabel }}</div>
        <h2 class="section-title mb-12">{{ aboutPage.timelineTitle }}</h2>
        <div v-if="aboutPage.timeline.length" class="relative">
          <div class="absolute left-4 top-0 bottom-0 w-px bg-titanium-800" />
          <div class="space-y-10">
            <div v-for="event in aboutPage.timeline" :key="`${event.year}-${event.title}`" class="relative pl-12">
              <div class="absolute left-0 w-8 h-8 rounded-full bg-titanium-900 border-2 border-accent-500 flex items-center justify-center">
                <div class="w-2 h-2 rounded-full bg-accent-500" />
              </div>
              <div class="text-accent-400 font-mono text-sm mb-1">{{ event.year }}</div>
              <h3 class="text-white font-semibold mb-2">{{ event.title }}</h3>
              <p class="text-titanium-400 text-sm leading-relaxed">{{ event.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Team / Values -->
    <section class="bg-titanium-900/30 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">{{ aboutPage.valuesLabel }}</div>
        <h2 class="section-title mb-12">{{ aboutPage.valuesTitle }}</h2>
        <div v-if="aboutPage.values.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="value in aboutPage.values" :key="value.title" class="card p-6 text-center">
            <div v-if="value.icon" class="text-3xl mb-4">{{ value.icon }}</div>
            <h3 class="text-white font-semibold mb-3">{{ value.title }}</h3>
            <p class="text-titanium-400 text-sm leading-relaxed">{{ value.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">{{ aboutPage.ctaTitle }}</h2>
        <p class="text-titanium-400 mb-8">{{ aboutPage.ctaDescription }}</p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Get a Quote</NuxtLink>
          <NuxtLink :to="localePath('/contact')" class="btn-secondary">Contact Us</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { siteConfig as fallbackSiteConfig } from '@cnbjti/mock-data'
import type { AboutPageConfig } from '@cnbjti/types'

const localePath = useLocalePath()
const { siteConfig } = await useSiteRuntime()

const fallbackAboutPage = fallbackSiteConfig.aboutPage as AboutPageConfig

const aboutPage = computed(() => normalizeAboutPage(siteConfig.value.aboutPage))

function normalizeAboutPage(value?: Partial<AboutPageConfig> | null): AboutPageConfig {
  return {
    heroLabel: field(value?.heroLabel, fallbackAboutPage.heroLabel),
    heroTitle: field(value?.heroTitle, fallbackAboutPage.heroTitle),
    heroIntro: field(value?.heroIntro, fallbackAboutPage.heroIntro),
    heroBody: field(value?.heroBody, fallbackAboutPage.heroBody),
    stats: Array.isArray(value?.stats) ? value.stats : fallbackAboutPage.stats,
    locationLabel: field(value?.locationLabel, fallbackAboutPage.locationLabel),
    locationTitle: field(value?.locationTitle, fallbackAboutPage.locationTitle),
    locationDescription: field(value?.locationDescription, fallbackAboutPage.locationDescription),
    advantages: Array.isArray(value?.advantages) ? value.advantages : fallbackAboutPage.advantages,
    timelineLabel: field(value?.timelineLabel, fallbackAboutPage.timelineLabel),
    timelineTitle: field(value?.timelineTitle, fallbackAboutPage.timelineTitle),
    timeline: Array.isArray(value?.timeline) ? value.timeline : fallbackAboutPage.timeline,
    valuesLabel: field(value?.valuesLabel, fallbackAboutPage.valuesLabel),
    valuesTitle: field(value?.valuesTitle, fallbackAboutPage.valuesTitle),
    values: Array.isArray(value?.values) ? value.values : fallbackAboutPage.values,
    ctaTitle: field(value?.ctaTitle, fallbackAboutPage.ctaTitle),
    ctaDescription: field(value?.ctaDescription, fallbackAboutPage.ctaDescription),
    seoTitle: field(value?.seoTitle, fallbackAboutPage.seoTitle),
    seoDescription: field(value?.seoDescription, fallbackAboutPage.seoDescription),
  }
}

function field(value: string | undefined, fallback: string) {
  return value === undefined || value === null ? fallback : value
}

useHead(() => ({
  title: aboutPage.value.seoTitle || aboutPage.value.heroTitle,
  meta: [
    { name: 'description', content: aboutPage.value.seoDescription || aboutPage.value.heroIntro },
  ],
}))
</script>
