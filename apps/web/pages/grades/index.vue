<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[700px] h-[700px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Grades</span>
        </nav>
        <div class="section-label">Material Grades</div>
        <h1 class="section-title mt-3 mb-6">Titanium Grades Guide</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          From commercially pure Grade 1 to high-strength Ti-6Al-4V ELI, we supply all major titanium grades with full material traceability and certified test reports.
        </p>
        <div class="flex flex-wrap gap-2 mt-8">
          <span v-for="grade in grades" :key="grade.id"
            class="px-3 py-1.5 bg-titanium-800 border border-titanium-700 rounded-full text-sm text-titanium-300 font-mono">
            {{ grade.shortName }}
          </span>
        </div>
      </div>
    </section>

    <!-- Grade cards -->
    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <NuxtLink
            v-for="grade in grades"
            :key="grade.id"
            :to="localePath(`/grades/${grade.slug}`)"
            class="card card-hover p-6 group flex flex-col"
          >
            <div class="flex items-start justify-between mb-4">
              <span class="px-3 py-1 bg-accent-500/10 border border-accent-500/30 rounded-lg text-accent-400 font-mono font-bold text-sm">
                {{ grade.shortName }}
              </span>
              <svg class="w-5 h-5 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </div>
            <h2 class="text-white font-semibold text-lg mb-2 group-hover:text-accent-400 transition-colors">{{ grade.name }}</h2>
            <p class="text-titanium-500 text-xs font-mono mb-3 leading-relaxed">{{ grade.composition }}</p>
            <p class="text-titanium-400 text-sm leading-relaxed mb-5 flex-1">{{ grade.description }}</p>
            <div class="grid grid-cols-3 gap-3 pt-4 border-t border-titanium-800">
              <div>
                <div class="text-titanium-500 text-xs mb-1">Tensile</div>
                <div class="text-white text-xs font-semibold">{{ grade.tensileStrength }}</div>
              </div>
              <div>
                <div class="text-titanium-500 text-xs mb-1">Yield</div>
                <div class="text-white text-xs font-semibold">{{ grade.yieldStrength }}</div>
              </div>
              <div>
                <div class="text-titanium-500 text-xs mb-1">Density</div>
                <div class="text-white text-xs font-semibold">{{ grade.density }} g/cm³</div>
              </div>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- Comparison table -->
    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Side-by-Side</div>
        <h2 class="section-title mb-10">Grade Comparison</h2>
        <div class="overflow-x-auto rounded-xl border border-titanium-800">
          <table class="w-full text-sm">
            <thead class="bg-titanium-900">
              <tr>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Grade</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Tensile (min)</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Yield (min)</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Elongation</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Density</th>
                <th class="text-left px-5 py-4 text-titanium-400 font-medium">Primary Use</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="grade in grades" :key="grade.id"
                class="border-t border-titanium-800/50 hover:bg-titanium-800/20 transition-colors">
                <td class="px-5 py-4">
                  <NuxtLink :to="localePath(`/grades/${grade.slug}`)" class="flex items-center gap-2 group">
                    <span class="px-2 py-0.5 bg-accent-500/10 border border-accent-500/30 rounded text-accent-400 font-mono text-xs whitespace-nowrap">{{ grade.shortName }}</span>
                    <span class="text-titanium-300 group-hover:text-accent-400 transition-colors text-xs">{{ grade.name }}</span>
                  </NuxtLink>
                </td>
                <td class="px-5 py-4 text-white font-semibold">{{ grade.tensileStrength }}</td>
                <td class="px-5 py-4 text-white font-semibold">{{ grade.yieldStrength }}</td>
                <td class="px-5 py-4 text-titanium-300">{{ grade.elongation }}</td>
                <td class="px-5 py-4 text-titanium-300">{{ grade.density }} g/cm³</td>
                <td class="px-5 py-4 text-titanium-400 text-xs">{{ grade.applications?.[0] || 'General titanium applications' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Not Sure Which Grade You Need?</h2>
        <p class="text-titanium-400 mb-8 leading-relaxed">
          Our technical team can help you select the right titanium grade for your application. Send us your requirements and we'll respond within 24 hours.
        </p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Get a Quote</NuxtLink>
          <NuxtLink :to="localePath('/contact')" class="btn-secondary">Contact Technical Team</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { titaniumGrades } from '@cnbjti/mock-data'
import type { TitaniumGrade } from '@cnbjti/types'

const localePath = useLocalePath()
const { data: gradesData } = await useAsyncData('public-grade-list', () => publicApi<TitaniumGrade[]>('/public/grades'))
const grades = computed(() => gradesData.value || titaniumGrades)

useHead({
  title: 'Titanium Grades Guide — Gr.1 to Gr.23 | CNBJTI',
  meta: [
    { name: 'description', content: 'Complete guide to titanium grades: Grade 1, 2, 5 (Ti-6Al-4V), 7, 9, 23 ELI. Properties, applications and comparison table. ASTM / AMS certified supply from Baoji.' },
  ],
})
</script>
