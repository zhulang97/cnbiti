<template>
  <section class="py-24 bg-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-14">
        <p class="section-label mb-3">Titanium Grades</p>
        <h2 class="section-title mb-4">All Major Grades In Stock</h2>
        <p class="section-subtitle max-w-2xl mx-auto">From commercially pure CP grades to aerospace alloys — we stock and process all major titanium grades with full material traceability.</p>
      </div>
      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
        <NuxtLink
          v-for="grade in gradeList"
          :key="grade.id"
          :to="'/grades/' + grade.slug"
          class="group card-hover p-5"
        >
          <div class="flex items-start justify-between mb-3">
            <div class="px-2.5 py-1 bg-accent-500/10 border border-accent-500/30 rounded-lg">
              <span class="text-accent-400 font-mono font-bold text-sm">{{ grade.shortName }}</span>
            </div>
            <svg class="w-4 h-4 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
          </div>
          <h3 class="text-white font-semibold text-sm mb-1 group-hover:text-accent-300 transition-colors">{{ grade.name }}</h3>
          <p class="text-titanium-500 text-xs leading-relaxed mb-3">{{ grade.description }}</p>
          <div class="grid grid-cols-3 gap-2 pt-3 border-t border-titanium-800/50">
            <div class="text-center">
              <div class="text-titanium-300 text-xs font-mono font-medium">{{ grade.tensileStrength?.split(' ')[0] }}</div>
              <div class="text-titanium-600 text-[10px] mt-0.5">Tensile</div>
            </div>
            <div class="text-center">
              <div class="text-titanium-300 text-xs font-mono font-medium">{{ grade.yieldStrength?.split(' ')[0] }}</div>
              <div class="text-titanium-600 text-[10px] mt-0.5">Yield</div>
            </div>
            <div class="text-center">
              <div class="text-titanium-300 text-xs font-mono font-medium">{{ grade.density }}</div>
              <div class="text-titanium-600 text-[10px] mt-0.5">g/cm³</div>
            </div>
          </div>
        </NuxtLink>
      </div>
      <div class="mt-8 text-center">
        <NuxtLink to="/grades" class="btn-secondary">
          View All Grades
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        </NuxtLink>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import { titaniumGrades as gradeList } from '@cnbjti/mock-data'
</script>
