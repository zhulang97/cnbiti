<template>
  <section class="py-24 bg-white">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid lg:grid-cols-2 gap-16 items-center">
        <div class="order-2 lg:order-1">
          <div class="mb-4 overflow-hidden rounded-xl border border-titanium-200 bg-white shadow-sm shadow-titanium-200/60">
            <div class="flex h-[280px] items-center justify-center bg-gradient-to-br from-steel-50 via-white to-titanium-50/70 p-3 sm:h-[320px] lg:h-[300px]">
              <img
                :src="qinghangPageAssets.sheetFactory.url"
                alt="Titanium sheet inspection"
                class="h-full w-full object-contain object-center"
                loading="lazy"
              />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div v-for="cert in certs" :key="cert.title" class="p-4 bg-white border border-titanium-200 rounded-xl shadow-sm shadow-titanium-200/60 hover:border-accent-500/30 transition-colors">
              <div class="mb-2 inline-flex rounded-lg border border-accent-500/30 bg-accent-500/10 px-2 py-1 text-xs font-mono font-semibold text-accent-600">{{ cert.code }}</div>
              <div class="text-titanium-950 font-semibold text-sm">{{ cert.title }}</div>
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
                <div class="text-titanium-950 text-sm font-medium">{{ item.title }}</div>
                <div class="text-titanium-500 text-xs mt-0.5">{{ item.desc }}</div>
              </div>
            </div>
          </div>
          <NuxtLink :to="localizedPath('/quality')" class="btn-primary">
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

const { siteConfig, localizedPath } = await useSiteRuntime()

const defaultDocItems = [
  { code: 'MTR', title: 'Mill Test Report (MTR)', desc: 'EN 10204 3.1 certified, traceable to heat number' },
  { code: 'CHEM', title: 'Chemical Composition', desc: 'Full elemental analysis per ASTM/AMS requirements' },
  { code: 'MECH', title: 'Mechanical Properties', desc: 'Tensile, yield, elongation, hardness test results' },
  { code: 'SGS', title: 'Third-Party Inspection', desc: 'SGS, BV, TUV available on request' },
]
const defaultCerts = [
  { code: 'ASTM', title: 'ASTM / ASME', desc: 'Full compliance with ASTM and ASME material standards' },
  { code: 'AMS', title: 'AMS Specs', desc: 'Aerospace Material Specifications for critical applications' },
  { code: 'MTR', title: 'EN 10204 3.1', desc: 'Mill Test Reports with third-party inspection available' },
  { code: 'CHEM', title: 'Chemical & Mech', desc: 'Full chemical composition and mechanical property testing' },
  { code: 'PMI', title: 'UT / PMI', desc: 'Ultrasonic testing and positive material identification' },
  { code: 'DOC', title: 'Export Docs', desc: 'CO, packing list, invoice and export documents' },
]

const docItems = computed(() => siteConfig.value.homePage?.qualityItems?.length ? siteConfig.value.homePage.qualityItems : defaultDocItems)
const certs = computed(() => {
  const configured = docItems.value.map((item) => ({
    code: item.code || certCode(item.title),
    title: item.title,
    desc: item.desc,
  }))
  return configured.length >= 4 ? configured.slice(0, 6) : defaultCerts
})

function certCode(name: string) {
  if (name.includes('ASTM')) return 'ASTM'
  if (name.includes('AMS')) return 'AMS'
  if (name.includes('10204') || name.includes('MTR')) return 'MTR'
  if (name.includes('Chemical')) return 'CHEM'
  if (name.includes('PMI')) return 'PMI'
  return 'DOC'
}
</script>
