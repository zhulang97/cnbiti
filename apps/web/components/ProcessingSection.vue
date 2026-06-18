<template>
  <section class="py-24 bg-gradient-to-b from-titanium-950 to-steel-950/30">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid lg:grid-cols-2 gap-16 items-center">
        <div>
          <p class="section-label mb-3">Processing Capabilities</p>
          <h2 class="section-title mb-5">In-House Machining &amp; Custom Processing</h2>
          <p class="section-subtitle mb-8">From raw titanium stock to finished components, we handle cutting, machining, forming and surface treatment with tighter quality control.</p>
          <div class="grid grid-cols-2 gap-3 mb-8">
            <div v-for="cap in capabilities" :key="cap.name" class="flex items-start gap-3 p-3 bg-titanium-900/50 border border-titanium-800/50 rounded-xl">
              <div class="w-8 h-8 bg-accent-500/10 border border-accent-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              </div>
              <div>
                <div class="text-white text-sm font-medium">{{ cap.name }}</div>
                <div class="text-titanium-500 text-xs mt-0.5">{{ cap.desc }}</div>
              </div>
            </div>
          </div>
          <NuxtLink to="/processing" class="btn-primary">
            View All Capabilities
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
          </NuxtLink>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div v-for="(img, i) in images" :key="i" class="relative rounded-xl overflow-hidden bg-titanium-900" :class="i === 0 ? 'col-span-2 h-48' : 'h-36'">
            <img :src="img.src" :alt="img.alt" class="content-image-lg" />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/60 to-transparent" />
            <div class="absolute bottom-2 left-3 text-white text-xs font-medium">{{ img.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const capabilities = [
  { name: 'Cut-to-size', desc: 'Saw, waterjet, laser' },
  { name: 'CNC Machining', desc: 'Turning, milling, drilling' },
  { name: 'Grinding & Polishing', desc: 'Ra 0.4 achievable' },
  { name: 'Welding & Fabrication', desc: 'TIG, electron beam' },
  { name: 'Surface Treatment', desc: 'Anodizing, passivation' },
  { name: 'Inspection & Testing', desc: 'UT, PMI, hardness' },
]
const images = [
  { src: qinghangPageAssets.factoryCnc.url, alt: 'Titanium CNC factory', label: 'CNC Machining' },
  { src: qinghangPageAssets.waterJet.url, alt: 'Water jet cutting', label: 'Water Jet Cutting' },
  { src: qinghangPageAssets.surfaceTreatment.url, alt: 'Titanium surface treatment', label: 'Surface Treatment' },
]
</script>
