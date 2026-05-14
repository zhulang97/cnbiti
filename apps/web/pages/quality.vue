<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Quality</span>
        </nav>
        <div class="section-label">Certifications & Testing</div>
        <h1 class="section-title mt-3 mb-6">Quality & Certifications</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          Every piece of titanium we ship is backed by certified documentation. We don't cut corners on quality — your application depends on it.
        </p>
        <div class="flex flex-wrap gap-2 mt-8">
          <span v-for="cert in certBadges" :key="cert"
            class="px-3 py-1.5 bg-titanium-800 border border-titanium-700 rounded-full text-sm text-titanium-300 font-mono">
            {{ cert }}
          </span>
        </div>
      </div>
    </section>

    <!-- Certifications grid -->
    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Standards We Meet</div>
        <h2 class="section-title mb-10">Certifications</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="cert in certifications" :key="cert.code" class="card p-6">
            <div class="flex items-start gap-4 mb-4">
              <div class="w-12 h-12 rounded-xl bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
                <svg class="w-6 h-6 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
                </svg>
              </div>
              <div>
                <div class="text-accent-400 font-mono font-bold text-sm">{{ cert.code }}</div>
                <h3 class="text-white font-semibold text-sm mt-0.5">{{ cert.name }}</h3>
              </div>
            </div>
            <p class="text-titanium-400 text-sm leading-relaxed">{{ cert.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Testing capabilities -->
    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
          <div>
            <div class="section-label mb-3">In-House & Third-Party</div>
            <h2 class="section-title mb-6">Testing Capabilities</h2>
            <p class="text-titanium-400 leading-relaxed mb-8">
              We perform incoming inspection on all materials and can arrange third-party testing at accredited labs. Test reports are provided with every shipment.
            </p>
            <div class="space-y-4">
              <div v-for="test in testingCapabilities" :key="test.name" class="flex items-start gap-4 p-4 card">
                <div class="w-8 h-8 rounded-lg bg-accent-500/10 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <svg class="w-4 h-4 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <div>
                  <div class="text-white font-medium text-sm">{{ test.name }}</div>
                  <div class="text-titanium-500 text-xs mt-0.5">{{ test.desc }}</div>
                </div>
              </div>
            </div>
          </div>
          <!-- MTR checklist -->
          <div class="card p-8">
            <div class="aspect-video rounded-lg overflow-hidden bg-titanium-900 mb-6">
              <img :src="qinghangPageAssets.surfaceTreatment.url" alt="Titanium surface inspection" class="content-image-lg" />
            </div>
            <h3 class="text-white font-semibold mb-6 text-sm uppercase tracking-wider">What's in Your MTR</h3>
            <div class="space-y-4">
              <div v-for="item in mtrContents" :key="item.label" class="flex items-start gap-3 py-3 border-b border-titanium-800 last:border-0">
                <svg class="w-5 h-5 text-accent-400 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <div>
                  <div class="text-white text-sm font-medium">{{ item.label }}</div>
                  <div class="text-titanium-500 text-xs mt-0.5">{{ item.detail }}</div>
                </div>
              </div>
            </div>
            <div class="mt-6 p-4 bg-accent-500/5 border border-accent-500/20 rounded-lg">
              <p class="text-accent-400 text-xs">EN 10204 Type 3.1 — issued and signed by the manufacturer's authorized inspector</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Need Certified Titanium?</h2>
        <p class="text-titanium-400 mb-8">Specify your required standard and certification in your RFQ. We'll confirm availability and documentation before you order.</p>
        <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Request a Quote</NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const localePath = useLocalePath()

const certBadges = ['ASTM', 'ASME', 'AMS', 'EN 10204', 'ISO 9001']

const certifications = [
  { code: 'ASTM / ASME', name: 'American Society for Testing and Materials', desc: 'All standard product forms supplied per applicable ASTM specifications (B348, B265, B338, B381). ASME equivalents available.' },
  { code: 'AMS', name: 'Aerospace Material Specifications', desc: 'AMS 4928 (Ti-6Al-4V bar/billet) and other AMS grades available for aerospace applications requiring SAE certification.' },
  { code: 'EN 10204 3.1', name: 'Material Test Report', desc: 'Every shipment includes a Type 3.1 MTR issued by the mill\'s authorized inspector. Chemical and mechanical test results included.' },
  { code: 'ISO 9001', name: 'Quality Management System', desc: 'Our quality management system is certified to ISO 9001:2015. Covers procurement, inspection, storage and dispatch.' },
  { code: 'Chemical Analysis', name: 'Spectrometric Testing', desc: 'PMI (Positive Material Identification) available on request. XRF and OES spectrometry for grade verification.' },
  { code: 'Mechanical Testing', name: 'Tensile & Hardness', desc: 'Tensile testing per ASTM E8, hardness per ASTM E18. Third-party lab testing available for critical applications.' },
]

const testingCapabilities = [
  { name: 'Dimensional Inspection', desc: 'Calipers, micrometers, CMM for tight-tolerance parts' },
  { name: 'PMI / XRF Analysis', desc: 'Positive material identification on every heat' },
  { name: 'Visual & Surface Inspection', desc: 'Surface finish, defects, marking verification' },
  { name: 'Ultrasonic Testing (UT)', desc: 'Internal defect detection for bar, plate and forgings' },
  { name: 'Third-Party Lab Testing', desc: 'Accredited lab tensile, hardness and chemical analysis' },
]

const mtrContents = [
  { label: 'Chemical Composition', detail: 'All elements per grade specification (Ti, Al, V, Fe, O, N, C, H)' },
  { label: 'Mechanical Properties', detail: 'Tensile strength, yield strength, elongation, reduction of area' },
  { label: 'Heat / Lot Number', detail: 'Unique identifier traceable to original mill melt' },
  { label: 'Product Dimensions', detail: 'Actual measured dimensions and applicable tolerances' },
  { label: 'Standard Compliance', detail: 'Confirmation of ASTM / AMS / EN standard and grade' },
  { label: 'Inspector Signature', detail: 'Authorized mill inspector name, signature and date' },
]

useHead({
  title: 'Quality & Certifications — ASTM, AMS, EN 10204 MTR | CNBJTI',
  meta: [
    { name: 'description', content: 'CNBJTI supplies certified titanium with EN 10204 3.1 MTR, ASTM, ASME, AMS and ISO 9001 certification. Full material traceability on every order.' },
  ],
})
</script>
