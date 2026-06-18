<template>
  <section class="py-24 bg-gradient-to-b from-steel-950/20 to-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-14">
        <p class="section-label mb-3">Industries We Serve</p>
        <h2 class="section-title mb-4">Trusted Across Demanding Applications</h2>
        <p class="section-subtitle max-w-2xl mx-auto">
          Titanium material, machining and documentation support for chemical, marine, medical, aerospace and energy buyers.
        </p>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <NuxtLink
          v-for="industry in industryProfiles"
          :key="industry.slug"
          :to="`/industries/${industry.slug}`"
          class="group relative overflow-hidden rounded-lg border border-titanium-800/50 hover:border-titanium-600/50 transition-all duration-300"
        >
          <div class="aspect-[4/3] relative overflow-hidden bg-titanium-900">
            <img
              :src="industry.image"
              :alt="industry.imageAlt"
              class="content-image-lg"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950 via-titanium-950/50 to-transparent" />
          </div>
          <div class="absolute bottom-0 left-0 right-0 p-4">
            <div class="text-accent-300 text-xs font-mono uppercase tracking-[0.18em] mb-1">{{ industry.kicker }}</div>
            <h3 class="text-white font-semibold text-sm group-hover:text-accent-300 transition-colors">{{ industry.name }}</h3>
            <p class="text-titanium-400 text-xs mt-1 line-clamp-1">{{ industry.grades.join(', ') }}</p>
          </div>
        </NuxtLink>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { industryProfiles } from '~/utils/industryContent'

</script>
