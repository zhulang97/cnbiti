<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="'/'" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Industries</span>
        </nav>
        <div class="section-label">Sectors We Serve</div>
        <h1 class="section-title mt-3 mb-6">Titanium Applications by Industry</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          Select your application area to review typical titanium grades, standards, processing notes and related engineering guides.
        </p>
      </div>
    </section>

    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <NuxtLink
            v-for="industry in industryProfiles"
            :key="industry.slug"
            :to="`/industries/${industry.slug}`"
            class="group overflow-hidden rounded-lg border border-titanium-800 bg-titanium-900/40 hover:border-accent-500/50 transition-all duration-300"
          >
            <div class="relative h-56 overflow-hidden bg-titanium-900">
              <img
                :src="industry.image"
                :alt="industry.imageAlt"
                class="content-image-lg"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-titanium-950 via-titanium-950/60 to-transparent" />
              <span class="absolute top-4 left-4 px-3 py-1 rounded bg-titanium-950/70 border border-titanium-700 text-xs text-accent-300 font-mono uppercase tracking-[0.18em]">
                {{ industry.kicker }}
              </span>
            </div>
            <div class="p-6">
              <div class="flex items-start justify-between gap-4 mb-3">
                <h2 class="text-white font-semibold text-lg group-hover:text-accent-400 transition-colors">{{ industry.name }}</h2>
                <svg class="w-5 h-5 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </div>
              <p class="text-titanium-400 text-sm leading-relaxed mb-5">{{ industry.summary }}</p>
              <div class="flex flex-wrap gap-2 mb-5">
                <span
                  v-for="grade in industry.grades"
                  :key="grade"
                  class="px-2 py-1 bg-titanium-800 border border-titanium-700 rounded text-xs text-titanium-300 font-mono"
                >
                  {{ grade }}
                </span>
              </div>
              <div v-if="topArticle(industry)" class="pt-4 border-t border-titanium-800">
                <div class="text-titanium-500 text-xs uppercase tracking-[0.18em] mb-2">Related guide</div>
                <p class="text-titanium-300 text-sm line-clamp-2">{{ topArticle(industry)?.title }}</p>
              </div>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>

    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Material Advantages</div>
        <h2 class="section-title mb-10">Why Titanium Fits Critical Service</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
          <div v-for="property in properties" :key="property.title" class="card p-6 text-center">
            <div class="w-10 h-10 mx-auto mb-4 rounded bg-accent-500/10 border border-accent-500/20 flex items-center justify-center text-accent-300 font-mono text-sm">
              {{ property.code }}
            </div>
            <h3 class="text-white font-semibold mb-2 text-sm">{{ property.title }}</h3>
            <p class="text-titanium-400 text-xs leading-relaxed">{{ property.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Need a Grade Recommendation?</h2>
        <p class="text-titanium-400 mb-8">Share the medium, operating temperature, drawings and certificate requirements. CNBJTI will recommend a practical titanium supply route.</p>
        <NuxtLink :to="'/request-a-quote'" class="btn-primary">Get a Quote</NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '@cnbjti/types'
import { industryProfiles, matchingIndustryArticles, type IndustryProfile } from '~/utils/industryContent'

const { data: articleData } = await useAsyncData('public-industry-list-articles', () => publicApi<Article[]>('/public/articles'))

const articles = computed(() => articleData.value || [])

function topArticle(industry: IndustryProfile) {
  return matchingIndustryArticles(articles.value, industry, 1)[0]
}

const properties = [
  { code: 'SW', title: 'High Strength-to-Weight', desc: 'Useful where lower mass needs to be balanced with reliable mechanical strength.' },
  { code: 'CR', title: 'Corrosion Resistance', desc: 'Stable in seawater, chloride and many oxidizing chemical environments.' },
  { code: 'BC', title: 'Biocompatibility', desc: 'Suitable for dental, medical and clean-service components with the right grade.' },
  { code: 'HT', title: 'Temperature Stability', desc: 'Maintains useful strength in many elevated-temperature industrial applications.' },
]

useHead({
  title: 'Titanium Industries Served - Chemical, Marine, Medical, Aerospace | CNBJTI',
  meta: [
    { name: 'description', content: 'CNBJTI supplies titanium materials and processed parts for chemical, marine, medical, aerospace, energy, industrial machining, automotive and electronics applications.' },
  ],
})
</script>
