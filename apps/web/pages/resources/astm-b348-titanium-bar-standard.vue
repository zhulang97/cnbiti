<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-16 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-20" />
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <NuxtLink :to="localePath('/resources')" class="hover:text-accent-400 transition-colors">Resources</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">ASTM B348</span>
        </nav>
        <span class="inline-block px-3 py-1 bg-accent-500/10 border border-accent-500/30 rounded-full text-accent-400 text-xs font-mono mb-5">Standards · 8 min read</span>
        <h1 class="section-title mb-6">ASTM B348 Titanium Bar: What Buyers Need to Know</h1>
        <p class="text-titanium-400 text-lg leading-relaxed">A practical guide to ASTM B348 requirements, grade options, tolerances and documentation for titanium bar procurement.</p>
        <div class="flex items-center gap-4 mt-6 text-titanium-500 text-sm">
          <span>Published February 28, 2026</span>
          <span>·</span>
          <span>CNBJTI Technical Team</span>
        </div>
      </div>
    </section>

    <section class="bg-titanium-950 py-12 pb-24">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="space-y-8">
          <div v-for="section in sections" :key="section.title">
            <h2 class="text-white font-semibold text-xl mb-4">{{ section.title }}</h2>
            <p class="text-titanium-400 leading-relaxed">{{ section.content }}</p>
            <div v-if="section.table" class="mt-5 overflow-x-auto rounded-xl border border-titanium-800">
              <table class="w-full text-sm">
                <thead class="bg-titanium-900">
                  <tr>
                    <th v-for="col in section.table.headers" :key="col" class="text-left px-4 py-3 text-titanium-400 font-medium">{{ col }}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in section.table.rows" :key="row[0]" class="border-t border-titanium-800/50">
                    <td v-for="cell in row" :key="cell" class="px-4 py-3 text-titanium-300 text-xs">{{ cell }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="mt-16 card p-8 text-center">
          <h3 class="text-white font-semibold text-lg mb-3">Need ASTM B348 Certified Bar?</h3>
          <p class="text-titanium-400 text-sm mb-6">We supply titanium bar per ASTM B348 with EN 10204 3.1 MTR. All grades, cut-to-size available.</p>
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Request a Quote</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()

const sections = [
  {
    title: 'What is ASTM B348?',
    content: 'ASTM B348 is the Standard Specification for Titanium and Titanium Alloy Bar and Billet, published by ASTM International. It covers the chemical composition, mechanical properties, dimensional tolerances and testing requirements for titanium bar and billet used in general industrial applications.',
  },
  {
    title: 'Grades Covered',
    content: 'ASTM B348 covers Grades 1 through 38, including all commercially pure grades (1–4) and major alloys (5, 7, 9, 12, 23). The most commonly ordered grades are Grade 2 (CP titanium) and Grade 5 (Ti-6Al-4V).',
    table: {
      headers: ['Grade', 'Type', 'Tensile (min)', 'Yield (min)', 'Elongation'],
      rows: [
        ['Grade 1', 'CP Ti', '240 MPa', '170 MPa', '24%'],
        ['Grade 2', 'CP Ti', '345 MPa', '275 MPa', '20%'],
        ['Grade 5', 'Ti-6Al-4V', '895 MPa', '828 MPa', '10%'],
        ['Grade 7', 'CP Ti + Pd', '345 MPa', '275 MPa', '20%'],
        ['Grade 23', 'Ti-6Al-4V ELI', '860 MPa', '795 MPa', '10%'],
      ],
    },
  },
  {
    title: 'Dimensional Tolerances',
    content: 'ASTM B348 specifies diameter tolerances for round bar based on diameter range. For bar up to 25mm diameter, the tolerance is typically ±0.13mm. For larger diameters, tolerances are proportionally wider. Straightness tolerance is 3mm per 1000mm of length. Length tolerances for cut lengths are +6mm/-0mm.',
  },
  {
    title: 'Required Documentation',
    content: 'ASTM B348 requires a certificate of compliance confirming the material meets the specification. For most industrial and export applications, buyers also require an EN 10204 Type 3.1 Material Test Report (MTR) showing actual chemical composition and mechanical test results. We provide both with every order.',
  },
  {
    title: 'Ordering Information',
    content: 'When ordering ASTM B348 titanium bar, specify: (1) Grade (e.g., Grade 2 or Grade 5), (2) Diameter and length, (3) Condition (annealed is standard), (4) Surface finish (turned, ground or as-rolled), (5) Required documentation (MTR, CoC). We can supply from 3mm to 300mm diameter in standard and cut-to-size lengths.',
  },
]

useHead({
  title: 'ASTM B348 Titanium Bar Guide — Grades, Tolerances & Documentation | CNBJTI',
  meta: [
    { name: 'description', content: 'Complete guide to ASTM B348 titanium bar specification: grades covered, mechanical properties, tolerances and required documentation for procurement.' },
  ],
})
</script>
