<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="'/'" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Standards</span>
        </nav>
        <div class="section-label">Compliance</div>
        <h1 class="section-title mt-3 mb-6">Titanium Standards Guide</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          We supply titanium materials certified to ASTM, ASME, AMS and EN standards. Every shipment includes a Material Test Report (MTR) traceable to the mill.
        </p>
        <div class="flex flex-wrap gap-2 mt-8">
          <span v-for="std in standardList" :key="std.id"
            class="px-3 py-1.5 bg-titanium-800 border border-titanium-700 rounded-full text-sm text-titanium-300 font-mono">
            {{ std.code }}
          </span>
        </div>
      </div>
    </section>

    <!-- Standards cards -->
    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <NuxtLink
            v-for="std in standardList"
            :key="std.id"
            :to="`/standards/${std.slug}`"
            class="card card-hover p-6 group flex flex-col"
          >
            <div class="flex items-start justify-between mb-4">
              <span class="px-3 py-1.5 bg-accent-500/10 border border-accent-500/30 rounded-lg text-accent-400 font-mono font-bold text-sm">
                {{ std.code }}
              </span>
              <svg class="w-5 h-5 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </div>
            <h2 class="text-white font-semibold text-base mb-3 group-hover:text-accent-400 transition-colors leading-snug">{{ std.name }}</h2>
            <p class="text-titanium-400 text-sm leading-relaxed mb-5 flex-1">{{ std.description }}</p>
            <div class="flex flex-wrap gap-2 pt-4 border-t border-titanium-800">
              <span v-for="pt in std.productTypes" :key="pt"
                class="px-2 py-1 bg-titanium-800 rounded text-xs text-titanium-300">
                {{ pt }}
              </span>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- MTR section -->
    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div>
            <div class="section-label mb-3">Documentation</div>
            <h2 class="section-title mb-6">Material Test Reports</h2>
            <p class="text-titanium-400 leading-relaxed mb-6">
              Every order ships with a full EN 10204 3.1 Material Test Report (MTR) issued by the mill. The MTR includes chemical composition, mechanical test results, heat number and product dimensions — fully traceable to the original melt.
            </p>
            <ul class="space-y-3">
              <li v-for="item in mtrItems" :key="item" class="flex items-center gap-3 text-titanium-300 text-sm">
                <svg class="w-4 h-4 text-accent-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                {{ item }}
              </li>
            </ul>
          </div>
          <div class="card p-8">
            <h3 class="text-white font-semibold mb-6 text-sm uppercase tracking-wider">MTR Contents</h3>
            <div class="space-y-3">
              <div v-for="field in mtrFields" :key="field.label" class="flex items-start gap-3 py-3 border-b border-titanium-800 last:border-0">
                <div class="w-2 h-2 rounded-full bg-accent-500 mt-1.5 flex-shrink-0" />
                <div>
                  <div class="text-white text-sm font-medium">{{ field.label }}</div>
                  <div class="text-titanium-500 text-xs mt-0.5">{{ field.desc }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Need Certified Titanium?</h2>
        <p class="text-titanium-400 mb-8">All our products are supplied with full certification documentation. Request a quote and specify your required standard.</p>
        <NuxtLink :to="'/request-a-quote'" class="btn-primary">Request a Quote with MTR</NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { standards } from '@cnbjti/mock-data'
import type { Standard } from '@cnbjti/types'

const { data: standardsData } = await useAsyncData('public-standard-list', () => publicApi<Standard[]>('/public/standards'))
const standardList = computed(() => standardsData.value || standards)

const mtrItems = [
  'EN 10204 Type 3.1 — issued by the mill',
  'Chemical composition per ASTM / AMS specification',
  'Mechanical test results (tensile, yield, elongation)',
  'Heat number and lot traceability',
  'Dimensional inspection report',
]

const mtrFields = [
  { label: 'Chemical Composition', desc: 'Ti, Al, V, Fe, O, N, C, H content per grade spec' },
  { label: 'Mechanical Properties', desc: 'Tensile strength, yield strength, elongation, hardness' },
  { label: 'Heat / Lot Number', desc: 'Full traceability to original melt and processing batch' },
  { label: 'Product Dimensions', desc: 'Actual measured dimensions and tolerances' },
  { label: 'Standard Compliance', desc: 'Confirmation of applicable ASTM / AMS / EN standard' },
]

useHead({
  title: 'Titanium Standards Guide — ASTM, ASME, AMS, EN 10204 | CNBJTI',
  meta: [
    { name: 'description', content: 'Titanium material standards: ASTM B348, B265, B338, B381, AMS 4928. All products supplied with EN 10204 3.1 MTR. Certified titanium from Baoji.' },
  ],
})
</script>
