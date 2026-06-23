<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="absolute top-1/2 right-0 w-[500px] h-[500px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="'/'" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Processing</span>
        </nav>
        <div class="section-label">Value-Added Services</div>
        <h1 class="section-title mt-3 mb-6">Processing Capabilities</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          Beyond standard mill products, we offer a full range of processing services — from simple cut-to-size to complex CNC machining. One supplier, complete solution.
        </p>
      </div>
    </section>

    <!-- Capabilities grid -->
    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">
          <div v-for="cap in capabilities" :key="cap.title" class="card group overflow-hidden transition-all duration-300 hover:-translate-y-1 hover:shadow-lg hover:shadow-titanium-200/70">
            <div class="relative flex h-[150px] items-center justify-center overflow-hidden bg-steel-50 px-2 py-2 sm:h-[165px]">
              <img
                :src="capImage(cap)"
                :alt="cap.imageAlt || cap.title"
                class="h-[96%] w-[96%] object-contain transition-transform duration-500 group-hover:scale-[1.03]"
                loading="lazy"
              />
            </div>
            <div class="p-4">
              <div class="mb-4 flex items-start gap-3">
                <div class="flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-xl border border-accent-500/20 bg-accent-500/10">
                  <span class="text-xs font-semibold text-accent-300">{{ capCode(cap.title) }}</span>
                </div>
                <div>
                  <h2 class="mb-1 text-base font-semibold text-white">{{ cap.title }}</h2>
                  <p class="line-clamp-2 text-sm leading-relaxed text-titanium-400">{{ cap.desc }}</p>
                </div>
              </div>
              <div v-if="cap.specs.length" class="grid grid-cols-2 gap-2">
                <div v-for="spec in cap.specs" :key="spec.label" class="rounded-lg border border-titanium-200 bg-steel-50 p-3">
                  <div class="mb-1 text-[11px] leading-tight text-titanium-500">{{ spec.label }}</div>
                  <div class="text-xs font-semibold text-titanium-950">{{ spec.value }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Tolerances table -->
    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Precision</div>
        <h2 class="section-title mb-10">Standard Tolerances</h2>
        <div class="overflow-x-auto rounded-xl border border-titanium-800">
          <table class="w-full text-sm">
            <thead class="bg-titanium-900">
              <tr>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Process</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Dimensional Tolerance</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Surface Finish (Ra)</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Notes</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in tolerances" :key="row.process"
                class="border-t border-titanium-800/50 hover:bg-titanium-800/20 transition-colors">
                <td class="px-5 py-4 text-white font-medium">{{ row.process }}</td>
                <td class="px-5 py-4 text-titanium-300 font-mono text-xs">{{ row.tolerance }}</td>
                <td class="px-5 py-4 text-titanium-300 font-mono text-xs">{{ row.surface }}</td>
                <td class="px-5 py-4 text-titanium-500 text-xs">{{ row.notes }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <!-- Workflow -->
    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">How It Works</div>
        <h2 class="section-title mb-12">Order Process</h2>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div v-for="(step, i) in orderSteps" :key="step.title" class="relative">
            <div class="card p-6 text-center h-full">
              <div class="w-10 h-10 rounded-full bg-accent-500/10 border border-accent-500/30 flex items-center justify-center mx-auto mb-4">
                <span class="text-accent-400 font-mono font-bold text-sm">{{ i + 1 }}</span>
              </div>
              <h3 class="text-white font-semibold mb-3 text-sm">{{ step.title }}</h3>
              <p class="text-titanium-400 text-xs leading-relaxed">{{ step.desc }}</p>
            </div>
            <div v-if="i < 3" class="hidden md:block absolute top-1/2 -right-3 w-6 h-px bg-titanium-700 z-10" />
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="bg-titanium-900/30 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Need Custom Processing?</h2>
        <p class="text-titanium-400 mb-8">Send us your drawing or specifications. We'll confirm feasibility and provide a quote within 24 hours.</p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <NuxtLink :to="'/request-a-quote'" class="btn-primary">Request a Quote</NuxtLink>
          <NuxtLink :to="'/contact'" class="btn-secondary">Send a Drawing</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'
import type { HomeCapability } from '@cnbjti/types'

interface ProcessingSpec {
  label: string
  value: string
}

interface ProcessingCapability extends HomeCapability {
  icon?: string
  specs: ProcessingSpec[]
}

const { siteConfig } = await useSiteRuntime()

const defaultCapabilities: ProcessingCapability[] = [
  {
    icon: '✂️',
    title: 'Cut-to-Size',
    desc: 'Bar, sheet, plate and tube cut to your exact dimensions. Bandsaw, circular saw and waterjet cutting available.',
    specs: [
      { label: 'Bar diameter', value: '3 – 300 mm' },
      { label: 'Sheet thickness', value: '0.5 – 100 mm' },
      { label: 'Length tolerance', value: '+0 / -2 mm' },
      { label: 'Min order', value: '1 piece' },
    ],
  },
  {
    icon: '⚙️',
    title: 'CNC Machining',
    desc: 'Turning, milling, drilling and grinding of titanium parts from drawings. Tight tolerances for aerospace and medical.',
    specs: [
      { label: 'Turning diameter', value: 'Up to 500 mm' },
      { label: 'Milling envelope', value: '800 × 500 × 400 mm' },
      { label: 'Tolerance', value: 'IT6 / ±0.01 mm' },
      { label: 'Surface finish', value: 'Ra 0.8 – 3.2 μm' },
    ],
  },
  {
    icon: '🔧',
    title: 'Grinding & Polishing',
    desc: 'Centerless grinding for bar, surface grinding for plate. Mirror polishing and brushed finishes available.',
    specs: [
      { label: 'Bar grinding', value: 'Ø3 – Ø100 mm' },
      { label: 'Roundness', value: '< 0.01 mm' },
      { label: 'Mirror polish', value: 'Ra < 0.2 μm' },
      { label: 'Brushed finish', value: 'Ra 0.4 – 0.8 μm' },
    ],
  },
  {
    icon: '🔥',
    title: 'Welding & Fabrication',
    desc: 'TIG welding of titanium assemblies in argon-purged environment. Tube assemblies, flanged spools and custom fabrications.',
    specs: [
      { label: 'Process', value: 'GTAW / TIG' },
      { label: 'Shielding', value: 'Argon purge' },
      { label: 'Grades', value: 'Gr.1, 2, 5, 7, 9' },
      { label: 'Inspection', value: 'Visual + PT' },
    ],
  },
  {
    icon: '🧪',
    title: 'Surface Treatment',
    desc: 'Anodizing, passivation, sandblasting and chemical etching. Medical-grade anodizing for implant components.',
    specs: [
      { label: 'Anodizing', value: 'Type II / III' },
      { label: 'Passivation', value: 'Per ASTM A967' },
      { label: 'Sandblasting', value: 'Sa 2.5 / Sa 3' },
      { label: 'Etching', value: 'HF/HNO3 solution' },
    ],
  },
  {
    icon: '🔍',
    title: 'Inspection & Testing',
    desc: 'Dimensional inspection, PMI, UT and third-party lab testing. Full documentation package with every order.',
    specs: [
      { label: 'PMI / XRF', value: 'All heats' },
      { label: 'UT testing', value: 'Per ASTM E114' },
      { label: 'CMM', value: 'Complex parts' },
      { label: 'MTR', value: 'EN 10204 3.1' },
    ],
  },
]

const specsByTitle = new Map(defaultCapabilities.map((item) => [normalizeTitle(item.title), item.specs]))

const capabilities = computed<ProcessingCapability[]>(() => {
  const configured = siteConfig.value.homePage?.capabilities?.filter((item) => item?.title?.trim() || item?.imageUrl?.trim())
  if (!configured?.length) return defaultCapabilities

  return configured.map((item, index) => ({
    title: item.title || `Processing Capability ${index + 1}`,
    desc: item.desc || '',
    imageUrl: item.imageUrl || '',
    imageAlt: item.imageAlt || item.title || `Processing capability ${index + 1}`,
    specs: specsByTitle.get(normalizeTitle(item.title || '')) || [],
  }))
})

function normalizeTitle(title: string) {
  return title.trim().toLowerCase().replace(/\s+/g, ' ')
}

function capImage(cap: ProcessingCapability) {
  if (cap.imageUrl) return cap.imageUrl
  const images: Record<string, string> = {
    'cut-to-size': qinghangPageAssets.cuttingFactory.url,
    'cnc machining': qinghangPageAssets.factoryCnc.url,
    'grinding & polishing': qinghangPageAssets.surfaceTreatment.url,
    'welding & fabrication': qinghangPageAssets.weldedTubes.url,
    'surface treatment': qinghangPageAssets.heatTreatment.url,
    'inspection & testing': qinghangPageAssets.sheetFactory.url,
  }
  return images[normalizeTitle(cap.title)] || qinghangPageAssets.cncMachining.url
}

function capCode(title: string) {
  const codes: Record<string, string> = {
    'cut-to-size': 'CUT',
    'cnc machining': 'CNC',
    'grinding & polishing': 'POL',
    'welding & fabrication': 'WLD',
    'surface treatment': 'SUR',
    'inspection & testing': 'QC',
  }
  return codes[normalizeTitle(title)] || 'TI'
}

const tolerances = [
  { process: 'Bandsaw Cut', tolerance: '+0 / -2 mm', surface: 'N/A', notes: 'Standard for bar and plate' },
  { process: 'CNC Turning', tolerance: 'h6 / ±0.01 mm', surface: 'Ra 1.6 μm', notes: 'Tighter on request' },
  { process: 'CNC Milling', tolerance: '±0.02 mm', surface: 'Ra 1.6 μm', notes: 'Depends on geometry' },
  { process: 'Centerless Grinding', tolerance: 'h6 / h8', surface: 'Ra 0.4 μm', notes: 'Bar Ø3–100 mm' },
  { process: 'Surface Grinding', tolerance: '±0.01 mm', surface: 'Ra 0.8 μm', notes: 'Plate and block' },
  { process: 'Mirror Polishing', tolerance: 'N/A', surface: 'Ra < 0.2 μm', notes: 'Medical / optical grade' },
]

const orderSteps = [
  { title: 'Send RFQ', desc: 'Submit your requirements: grade, dimensions, quantity, standard and any processing needs.' },
  { title: 'Quote & DFM', desc: 'We review your requirements and send a quote with lead time. DFM feedback for machined parts.' },
  { title: 'Production', desc: 'Material sourced from approved mills. Processing performed in-house with QC at each stage.' },
  { title: 'Ship with MTR', desc: 'Packed for export, shipped with full documentation including EN 10204 3.1 MTR.' },
]

useHead({
  title: 'Titanium Processing Capabilities — Cut-to-Size, CNC, Grinding | CNBJTI',
  meta: [
    { name: 'description', content: 'CNBJTI offers titanium processing: cut-to-size, CNC machining, grinding, welding, surface treatment and inspection. Custom titanium parts from Baoji.' },
  ],
})
</script>
