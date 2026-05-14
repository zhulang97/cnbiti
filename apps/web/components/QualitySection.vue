<template>
  <section class="py-24 bg-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid lg:grid-cols-2 gap-16 items-center">
        <div class="order-2 lg:order-1">
          <div class="relative h-44 rounded-xl overflow-hidden border border-titanium-800/50 bg-titanium-900 mb-4">
            <img :src="qinghangPageAssets.sheetFactory.url" alt="Titanium sheet inspection" class="content-image" />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/70 to-transparent" />
            <div class="absolute bottom-3 left-4 text-white text-xs font-medium">Material Traceability</div>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div v-for="cert in certs" :key="cert.name" class="p-4 bg-titanium-900/50 border border-titanium-800/50 rounded-xl hover:border-accent-500/30 transition-colors">
              <div class="text-2xl mb-2">{{ cert.icon }}</div>
              <div class="text-white font-semibold text-sm">{{ cert.name }}</div>
              <div class="text-titanium-500 text-xs mt-1">{{ cert.desc }}</div>
            </div>
          </div>
        </div>
        <div class="order-1 lg:order-2">
          <p class="section-label mb-3">Quality &amp; Documentation</p>
          <h2 class="section-title mb-5">Every Shipment Fully Documented</h2>
          <p class="section-subtitle mb-8">We supply titanium materials with complete material traceability. Every order includes mill test reports, chemical and mechanical test results, and export documentation.</p>
          <div class="space-y-4 mb-8">
            <div v-for="item in docItems" :key="item.title" class="flex items-start gap-3">
              <div class="w-6 h-6 bg-accent-500/20 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                <svg class="w-3.5 h-3.5 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              </div>
              <div>
                <div class="text-white text-sm font-medium">{{ item.title }}</div>
                <div class="text-titanium-500 text-xs mt-0.5">{{ item.desc }}</div>
              </div>
            </div>
          </div>
          <NuxtLink to="/en/quality" class="btn-primary">
            View Quality Standards
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
          </NuxtLink>
        </div>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const certs = [
  { icon: '📋', name: 'ASTM / ASME', desc: 'Full compliance with ASTM and ASME material standards' },
  { icon: '✈️', name: 'AMS Specs', desc: 'Aerospace Material Specifications for critical applications' },
  { icon: '📄', name: 'EN 10204 3.1', desc: 'Mill Test Reports with third-party inspection available' },
  { icon: '🔬', name: 'Chemical & Mech', desc: 'Full chemical composition and mechanical property testing' },
  { icon: '🔍', name: 'UT / PMI', desc: 'Ultrasonic testing and positive material identification' },
  { icon: '📦', name: 'Export Docs', desc: 'CO, packing list, invoice, fumigation certificate' },
]
const docItems = [
  { title: 'Mill Test Report (MTR)', desc: 'EN 10204 3.1 certified, traceable to heat number' },
  { title: 'Chemical Composition', desc: 'Full elemental analysis per ASTM/AMS requirements' },
  { title: 'Mechanical Properties', desc: 'Tensile, yield, elongation, hardness test results' },
  { title: 'Third-Party Inspection', desc: 'SGS, BV, TUV available on request' },
]
</script>
