<template>
  <div>
    <template v-if="grade">
      <!-- Hero -->
      <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
        <div class="absolute inset-0 grid-pattern opacity-30" />
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
            <NuxtLink :to="'/'" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
            <span>/</span>
            <NuxtLink :to="'/grades'" class="hover:text-accent-400 transition-colors">Grades</NuxtLink>
            <span>/</span>
            <span class="text-titanium-300">{{ grade.shortName }}</span>
          </nav>
          <div class="flex flex-col lg:flex-row lg:items-start lg:gap-16">
            <div class="flex-1">
              <span class="inline-block px-4 py-2 bg-accent-500/10 border border-accent-500/30 rounded-xl text-accent-400 font-mono font-bold text-xl mb-5">
                {{ grade.shortName }}
              </span>
              <h1 class="section-title mb-4">{{ grade.name }}</h1>
              <p class="text-titanium-500 font-mono text-sm mb-6">{{ grade.composition }}</p>
              <p class="text-titanium-300 text-lg leading-relaxed max-w-2xl">{{ grade.description }}</p>
            </div>
            <!-- Properties panel -->
            <div class="mt-10 lg:mt-0 lg:w-80 flex-shrink-0">
              <div class="card p-6">
                <h3 class="text-white font-semibold mb-5 text-sm uppercase tracking-wider">Mechanical Properties</h3>
                <div class="space-y-4">
                  <div class="flex justify-between items-center py-3 border-b border-titanium-800">
                    <span class="text-titanium-400 text-sm">Tensile Strength</span>
                    <span class="text-white font-semibold text-sm">{{ grade.tensileStrength }}</span>
                  </div>
                  <div class="flex justify-between items-center py-3 border-b border-titanium-800">
                    <span class="text-titanium-400 text-sm">Yield Strength</span>
                    <span class="text-white font-semibold text-sm">{{ grade.yieldStrength }}</span>
                  </div>
                  <div class="flex justify-between items-center py-3 border-b border-titanium-800">
                    <span class="text-titanium-400 text-sm">Elongation</span>
                    <span class="text-white font-semibold text-sm">{{ grade.elongation }}</span>
                  </div>
                  <div class="flex justify-between items-center py-3">
                    <span class="text-titanium-400 text-sm">Density</span>
                    <span class="text-white font-semibold text-sm">{{ grade.density }} g/cm³</span>
                  </div>
                </div>
                <NuxtLink :to="'/request-a-quote'" class="btn-primary w-full justify-center mt-6 text-sm">
                  Request a Quote
                </NuxtLink>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Applications -->
      <section class="bg-titanium-950 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="section-label mb-3">Use Cases</div>
          <h2 class="section-title mb-10">Applications</h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div v-for="(app, i) in grade.applications" :key="i"
              class="card p-5 flex items-center gap-4">
              <div class="w-10 h-10 rounded-lg bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
                <svg class="w-5 h-5 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <span class="text-titanium-300 text-sm">{{ app }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Available products -->
      <section class="bg-titanium-900/30 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="section-label mb-3">In Stock</div>
          <h2 class="section-title mb-10">Available in {{ grade.shortName }}</h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            <NuxtLink v-for="cat in categories" :key="cat.id"
              :to="`/products/${cat.slug}`"
              class="card card-hover p-5 flex items-center gap-4 group">
              <div class="w-12 h-12 rounded-xl bg-titanium-800 flex items-center justify-center flex-shrink-0">
                <ProductCategoryIcon :icon="cat.icon || ''" class="w-6 h-6 text-accent-400" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="text-white font-medium text-sm group-hover:text-accent-400 transition-colors">{{ cat.name }}</div>
                <div class="text-titanium-500 text-xs mt-0.5">{{ cat.productCount }} products</div>
              </div>
              <svg class="w-4 h-4 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
              </svg>
            </NuxtLink>
          </div>
        </div>
      </section>

      <!-- Other grades -->
      <section class="bg-titanium-950 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 class="text-xl font-display font-bold text-white mb-8">Other Titanium Grades</h2>
          <div class="flex flex-wrap gap-3">
            <NuxtLink v-for="g in otherGrades" :key="g.id"
              :to="`/grades/${g.slug}`"
              class="px-4 py-2 card card-hover text-sm text-titanium-300 hover:text-accent-400 transition-colors flex items-center gap-2">
              <span class="text-accent-400 font-mono text-xs">{{ g.shortName }}</span>
              {{ g.name }}
            </NuxtLink>
          </div>
        </div>
      </section>

      <!-- CTA -->
      <section class="bg-titanium-900/30 py-16">
        <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 class="text-2xl font-display font-bold text-white mb-4">Ready to Order {{ grade.shortName }}?</h2>
          <p class="text-titanium-400 mb-8">Tell us your dimensions, quantity and required standards. We'll send a quote with MTR within 24 hours.</p>
          <NuxtLink :to="'/request-a-quote'" class="btn-primary">Get a Quote</NuxtLink>
        </div>
      </section>
    </template>

    <!-- 404 state -->
    <div v-else class="min-h-screen bg-titanium-950 flex items-center justify-center">
      <div class="text-center">
        <p class="text-titanium-400 mb-4">Grade not found.</p>
        <NuxtLink :to="'/grades'" class="btn-secondary">View All Grades</NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { titaniumGrades, productCategories } from '@cnbjti/mock-data'
import type { ProductCategory, TitaniumGrade } from '@cnbjti/types'

const route = useRoute()
const gradeSlug = computed(() => String(route.params.slug))

const { data: gradeData } = await useAsyncData(`public-grade-${gradeSlug.value}`, () => publicApi<TitaniumGrade>(`/public/grades/${gradeSlug.value}`))
const { data: gradesData } = await useAsyncData('public-grade-list', () => publicApi<TitaniumGrade[]>('/public/grades'))
const { data: categoryData } = await useAsyncData('public-product-categories', () => publicApi<ProductCategory[]>('/public/categories'))

const allGrades = computed(() => gradesData.value || titaniumGrades)
const grade = computed(() => gradeData.value || titaniumGrades.find(g => g.slug === route.params.slug))
const otherGrades = computed(() => allGrades.value.filter(g => g.slug !== route.params.slug))
const categories = computed(() => categoryData.value || productCategories)

useHead(computed(() => ({
  title: grade.value ? `${grade.value.name} (${grade.value.shortName}) — Properties & Applications | CNBJTI` : 'Grade Not Found',
  meta: [
    { name: 'description', content: grade.value ? `${grade.value.name}: ${grade.value.description} Tensile ${grade.value.tensileStrength}. Supply from Baoji with ASTM certification and MTR.` : '' },
  ],
})))
</script>
