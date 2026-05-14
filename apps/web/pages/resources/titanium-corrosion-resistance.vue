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
          <span class="text-titanium-300">Corrosion Resistance</span>
        </nav>
        <span class="inline-block px-3 py-1 bg-accent-500/10 border border-accent-500/30 rounded-full text-accent-400 text-xs font-mono mb-5">Technical Guide · 10 min read</span>
        <h1 class="section-title mb-6">Titanium Corrosion Resistance: Why It Outperforms Stainless Steel</h1>
        <p class="text-titanium-400 text-lg leading-relaxed">Explore the corrosion mechanisms that make titanium the material of choice for chemical processing, marine and desalination applications.</p>
        <div class="flex items-center gap-4 mt-6 text-titanium-500 text-sm">
          <span>Published February 10, 2026</span>
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
            <div v-if="section.comparison" class="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div v-for="item in section.comparison" :key="item.material" class="card p-5">
                <div class="text-white font-semibold mb-3 text-sm">{{ item.material }}</div>
                <ul class="space-y-2">
                  <li v-for="point in item.points" :key="point" class="flex items-start gap-2 text-xs text-titanium-400">
                    <span :class="item.positive ? 'text-accent-400' : 'text-red-400'" class="mt-0.5 flex-shrink-0">{{ item.positive ? '✓' : '✗' }}</span>
                    {{ point }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-16 card p-8 text-center">
          <h3 class="text-white font-semibold text-lg mb-3">Need Corrosion-Resistant Titanium?</h3>
          <p class="text-titanium-400 text-sm mb-6">We supply Grade 2 and Grade 7 titanium for the most demanding corrosive environments. MTR included.</p>
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
    title: 'The Passive Oxide Layer',
    content: 'Titanium\'s exceptional corrosion resistance comes from a thin, stable titanium dioxide (TiO₂) oxide layer that forms spontaneously on the surface when exposed to oxygen or moisture. This passive layer is only 2–7 nanometers thick but is extremely adherent and self-healing — if scratched or damaged, it reforms almost instantly in the presence of even trace amounts of oxygen or water.',
  },
  {
    title: 'Titanium vs. Stainless Steel: Key Differences',
    content: 'While stainless steel also relies on a passive chromium oxide layer, titanium\'s oxide layer is significantly more stable across a wider range of environments. The critical difference is in chloride environments — stainless steel is vulnerable to pitting and crevice corrosion in chloride solutions, while titanium is essentially immune.',
    comparison: [
      {
        material: 'Titanium (Gr.2)',
        positive: true,
        points: [
          'Immune to chloride pitting and crevice corrosion',
          'Resistant to seawater at all temperatures',
          'Stable in oxidizing and mildly reducing acids',
          'No stress corrosion cracking in chlorides',
          'Biologically inert and biocompatible',
        ],
      },
      {
        material: '316L Stainless Steel',
        positive: false,
        points: [
          'Susceptible to chloride pitting above ~50°C',
          'Crevice corrosion in stagnant seawater',
          'Stress corrosion cracking in hot chlorides',
          'Limited resistance in reducing acids',
          'Can cause galvanic corrosion with titanium',
        ],
      },
    ],
  },
  {
    title: 'Environments Where Titanium Excels',
    content: 'Titanium is the material of choice in: (1) Seawater and marine environments — immune to biofouling and crevice corrosion. (2) Chloride solutions — including bleach, hydrochloric acid (dilute, oxidizing conditions) and chlorinated process streams. (3) Oxidizing acids — nitric acid, chromic acid and wet chlorine. (4) Chemical processing — heat exchangers, reactors and piping handling corrosive process fluids. (5) Desalination — evaporator tubes and heat exchangers in MSF and MED plants.',
  },
  {
    title: 'Grade Selection for Corrosion Resistance',
    content: 'For most corrosive applications, Grade 2 (CP titanium) provides excellent performance at the lowest cost. For the most aggressive environments — particularly reducing acids, hot concentrated chlorides or crevice corrosion conditions — Grade 7 (Ti-0.15Pd) offers superior resistance due to the palladium addition, which stabilizes the passive layer in reducing environments. Grade 12 (Ti-0.3Mo-0.8Ni) is a cost-effective alternative to Grade 7 for some applications.',
  },
]

useHead({
  title: 'Titanium Corrosion Resistance vs Stainless Steel | CNBJTI',
  meta: [
    { name: 'description', content: 'Why titanium outperforms stainless steel in corrosive environments: the passive oxide layer, chloride resistance, and grade selection for chemical processing and marine applications.' },
  ],
})
</script>
