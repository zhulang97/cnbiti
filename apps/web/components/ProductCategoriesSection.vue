<template>
  <section class="py-24 bg-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-14">
        <p class="section-label mb-3">Product Range</p>
        <h2 class="section-title mb-4">Complete Titanium Product Forms</h2>
        <p class="section-subtitle max-w-2xl mx-auto">From commercially pure CP grades to high-strength alloys — bar, plate, tube, wire, forgings, fittings and custom machined parts.</p>
      </div>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <NuxtLink
          v-for="cat in categories"
          :key="cat.slug"
          :to="localePath('/products/' + cat.slug)"
          class="group card-hover p-5 flex flex-col gap-3"
        >
          <div class="w-10 h-10 rounded-xl bg-titanium-800 group-hover:bg-accent-500/20 border border-titanium-700 group-hover:border-accent-500/40 flex items-center justify-center transition-all duration-300">
            <ProductCategoryIcon :icon="cat.icon || ''" class="w-5 h-5 text-titanium-400 group-hover:text-accent-400 transition-colors" />
          </div>
          <div>
            <h3 class="text-white font-semibold text-sm group-hover:text-accent-300 transition-colors leading-tight">{{ cat.name }}</h3>
            <p class="text-titanium-500 text-xs mt-1 leading-relaxed line-clamp-2">{{ cat.description }}</p>
          </div>
          <div class="mt-auto flex items-center justify-between">
            <span class="text-titanium-600 text-xs">{{ cat.productCount }} products</span>
            <svg class="w-4 h-4 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
          </div>
        </NuxtLink>
      </div>
      <div class="mt-8 text-center">
        <NuxtLink :to="localePath('/products')" class="btn-secondary">
          View All Products
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        </NuxtLink>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import { productCategories as mockCategories } from '@cnbjti/mock-data'
import type { ProductCategory } from '@cnbjti/types'

const localePath = useLocalePath()
const { data: categoryData } = await useAsyncData('homepage-product-categories', () => publicApi<ProductCategory[]>('/public/categories'))
const categories = computed(() => categoryData.value || mockCategories)
</script>
