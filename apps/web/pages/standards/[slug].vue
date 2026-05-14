<template>
  <div>
    <template v-if="standard">
      <!-- Hero -->
      <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
        <div class="absolute inset-0 grid-pattern opacity-30" />
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
            <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
            <span>/</span>
            <NuxtLink :to="localePath('/standards')" class="hover:text-accent-400 transition-colors">Standards</NuxtLink>
            <span>/</span>
            <span class="text-titanium-300">{{ standard.code }}</span>
          </nav>
          <div class="flex flex-col lg:flex-row lg:items-start lg:gap-16">
            <div class="flex-1">
              <span class="inline-block px-4 py-2 bg-accent-500/10 border border-accent-500/30 rounded-xl text-accent-400 font-mono font-bold text-xl mb-5">
                {{ standard.code }}
              </span>
              <h1 class="section-title mb-6 leading-tight">{{ standard.name }}</h1>
              <p class="text-titanium-300 text-lg leading-relaxed max-w-2xl">{{ standard.description }}</p>
              <div class="flex flex-wrap gap-2 mt-6">
                <span v-for="pt in standard.productTypes" :key="pt"
                  class="px-3 py-1.5 bg-titanium-800 border border-titanium-700 rounded-full text-sm text-titanium-300">
                  {{ pt }}
                </span>
              </div>
            </div>
            <!-- Quick info panel -->
            <div class="mt-10 lg:mt-0 lg:w-72 flex-shrink-0">
              <div class="card p-6">
                <h3 class="text-white font-semibold mb-5 text-sm uppercase tracking-wider">Quick Reference</h3>
                <div class="space-y-3">
                  <div class="flex justify-between py-3 border-b border-titanium-800">
                    <span class="text-titanium-400 text-sm">Standard</span>
                    <span class="text-white font-mono text-sm">{{ standard.code }}</span>
                  </div>
                  <div class="flex justify-between py-3 border-b border-titanium-800">
                    <span class="text-titanium-400 text-sm">Body</span>
                    <span class="text-white text-sm">{{ standard.code.startsWith('AMS') ? 'SAE / AMS' : 'ASTM International' }}</span>
                  </div>
                  <div class="py-3">
                    <span class="text-titanium-400 text-sm block mb-2">Product Types</span>
                    <div class="flex flex-wrap gap-1.5">
                      <span v-for="pt in standard.productTypes" :key="pt"
                        class="px-2 py-0.5 bg-titanium-800 rounded text-xs text-titanium-300">{{ pt }}</span>
                    </div>
                  </div>
                </div>
                <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary w-full justify-center mt-5 text-sm">
                  Request Certified Material
                </NuxtLink>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Applicable grades -->
      <section class="bg-titanium-950 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="section-label mb-3">Covered Grades</div>
          <h2 class="section-title mb-10">Applicable Titanium Grades</h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            <NuxtLink v-for="grade in grades" :key="grade.id"
              :to="localePath(`/grades/${grade.slug}`)"
              class="card card-hover p-5 group flex items-center gap-4">
              <span class="px-3 py-1.5 bg-accent-500/10 border border-accent-500/30 rounded-lg text-accent-400 font-mono font-bold text-sm flex-shrink-0">
                {{ grade.shortName }}
              </span>
              <div class="flex-1 min-w-0">
                <div class="text-white text-sm font-medium group-hover:text-accent-400 transition-colors">{{ grade.name }}</div>
                <div class="text-titanium-500 text-xs mt-0.5 truncate">{{ grade.composition }}</div>
              </div>
              <svg class="w-4 h-4 text-titanium-600 group-hover:text-accent-400 transition-colors flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
              </svg>
            </NuxtLink>
          </div>
        </div>
      </section>

      <!-- MTR info -->
      <section class="bg-titanium-900/30 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div class="card p-6 text-center">
              <div class="w-12 h-12 rounded-xl bg-accent-500/10 border border-accent-500/20 flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 002.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 00-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 00.75-.75 2.25 2.25 0 00-.1-.664m-5.8 0A2.251 2.251 0 0113.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25zM6.75 12h.008v.008H6.75V12zm0 3h.008v.008H6.75V15zm0 3h.008v.008H6.75V18z" />
                </svg>
              </div>
              <h3 class="text-white font-semibold mb-2">EN 10204 3.1 MTR</h3>
              <p class="text-titanium-400 text-sm">Mill-issued test report included with every order</p>
            </div>
            <div class="card p-6 text-center">
              <div class="w-12 h-12 rounded-xl bg-accent-500/10 border border-accent-500/20 flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
                </svg>
              </div>
              <h3 class="text-white font-semibold mb-2">{{ standard.code }} Certified</h3>
              <p class="text-titanium-400 text-sm">Full compliance with specification requirements</p>
            </div>
            <div class="card p-6 text-center">
              <div class="w-12 h-12 rounded-xl bg-accent-500/10 border border-accent-500/20 flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
                </svg>
              </div>
              <h3 class="text-white font-semibold mb-2">Full Traceability</h3>
              <p class="text-titanium-400 text-sm">Heat number traceable to original mill melt</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Other standards -->
      <section class="bg-titanium-950 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 class="text-xl font-display font-bold text-white mb-8">Other Standards</h2>
          <div class="flex flex-wrap gap-3">
            <NuxtLink v-for="s in otherStandards" :key="s.id"
              :to="localePath(`/standards/${s.slug}`)"
              class="px-4 py-2 card card-hover text-sm text-titanium-300 hover:text-accent-400 transition-colors flex items-center gap-2">
              <span class="text-accent-400 font-mono text-xs">{{ s.code }}</span>
              <span class="text-titanium-500 text-xs truncate max-w-[200px]">{{ s.name }}</span>
            </NuxtLink>
          </div>
        </div>
      </section>
    </template>

    <div v-else class="min-h-screen bg-titanium-950 flex items-center justify-center">
      <div class="text-center">
        <p class="text-titanium-400 mb-4">Standard not found.</p>
        <NuxtLink :to="localePath('/standards')" class="btn-secondary">View All Standards</NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { standards, titaniumGrades } from '@cnbjti/mock-data'
import type { Standard, TitaniumGrade } from '@cnbjti/types'

const route = useRoute()
const localePath = useLocalePath()
const standardSlug = computed(() => String(route.params.slug))

const { data: standardData } = await useAsyncData(`public-standard-${standardSlug.value}`, () => publicApi<Standard>(`/public/standards/${standardSlug.value}`))
const { data: standardsData } = await useAsyncData('public-standard-list', () => publicApi<Standard[]>('/public/standards'))
const { data: gradesData } = await useAsyncData('public-grade-list', () => publicApi<TitaniumGrade[]>('/public/grades'))

const allStandards = computed(() => standardsData.value || standards)
const standard = computed(() => standardData.value || standards.find(s => s.slug === route.params.slug))
const otherStandards = computed(() => allStandards.value.filter(s => s.slug !== route.params.slug))
const grades = computed(() => gradesData.value || titaniumGrades)

useHead(computed(() => ({
  title: standard.value ? `${standard.value.code} — ${standard.value.name} | CNBJTI` : 'Standard Not Found',
  meta: [
    { name: 'description', content: standard.value ? `${standard.value.code}: ${standard.value.description} Certified titanium supply from Baoji with EN 10204 3.1 MTR.` : '' },
  ],
})))
</script>
