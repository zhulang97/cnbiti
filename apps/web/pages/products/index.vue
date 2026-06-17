<template>
  <div class="pt-24 pb-20 min-h-screen bg-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
        <NuxtLink :to="localePath('/')" class="hover:text-titanium-300 transition-colors">Home</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <span class="text-titanium-300">Products</span>
      </nav>

      <div class="mb-12">
        <p class="section-label mb-3">Product Range</p>
        <h1 class="section-title mb-4">Titanium Products</h1>
        <p class="section-subtitle max-w-2xl">Complete titanium product forms from commercially pure CP grades to high-strength alloys. All products available with ASTM/ASME/AMS certification and MTR.</p>
      </div>

      <!-- Category grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
          <NuxtLink
          v-for="cat in categories"
          :key="cat.slug"
          :to="localePath('/products/' + cat.slug)"
          class="group card-hover overflow-hidden"
        >
          <div class="aspect-video relative overflow-hidden bg-titanium-900">
            <img :src="cat.image?.url" :alt="cat.name" class="content-image-lg" />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/80 to-transparent" />
          </div>
          <div class="p-5">
            <div class="flex items-start justify-between mb-2">
              <h2 class="text-white font-semibold text-sm group-hover:text-accent-300 transition-colors leading-tight">{{ cat.name }}</h2>
              <svg class="w-4 h-4 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all flex-shrink-0 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
            </div>
            <p class="text-titanium-500 text-xs leading-relaxed mb-3">{{ cat.description }}</p>
            <div class="flex items-center justify-between">
              <span class="text-titanium-600 text-xs">{{ cat.productCount }} products</span>
              <span class="px-2 py-0.5 bg-accent-500/10 border border-accent-500/30 rounded text-xs text-accent-400">In Stock</span>
            </div>
          </div>
        </NuxtLink>
      </div>

      <!-- Product grid -->
      <section class="mt-16">
        <div class="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-5 mb-6">
          <div>
            <p class="section-label mb-3">Live Catalog</p>
            <h2 class="text-white font-bold text-2xl mb-2">Available Products</h2>
            <p class="text-titanium-400 text-sm max-w-2xl">Browse standard and custom titanium products currently published from the admin catalog.</p>
          </div>
          <span class="text-titanium-500 text-sm">{{ filteredProducts.length }} items</span>
        </div>

        <div class="flex flex-wrap gap-2 mb-8">
          <button
            v-for="filter in filters"
            :key="filter.slug"
            type="button"
            @click="activeFilter = filter.slug"
            class="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200"
            :class="activeFilter === filter.slug ? 'bg-accent-500 text-white' : 'bg-titanium-800 text-titanium-300 hover:bg-titanium-700 hover:text-white border border-titanium-700'"
          >
            {{ filter.label }}
          </button>
        </div>

        <div v-if="filteredProducts.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          <NuxtLink
            v-for="product in filteredProducts"
            :key="product.id"
            :to="localePath(`/products/${product.category.slug}/${product.slug}`)"
            class="group card-hover p-5"
          >
            <div class="aspect-video rounded-lg overflow-hidden mb-4 bg-titanium-900">
              <img :src="productImage(product)" :alt="product.name" class="content-image" />
            </div>
            <div class="flex items-start justify-between gap-3 mb-2">
              <h3 class="text-white font-semibold text-sm group-hover:text-accent-300 transition-colors leading-tight">{{ product.name }}</h3>
              <span class="px-2 py-0.5 rounded text-[11px] whitespace-nowrap" :class="product.inStock ? 'bg-green-500/10 border border-green-500/30 text-green-300' : 'bg-titanium-800 border border-titanium-700 text-titanium-400'">
                {{ product.inStock ? 'In Stock' : 'Custom' }}
              </span>
            </div>
            <p class="text-titanium-500 text-xs leading-relaxed mb-3 line-clamp-2">{{ product.shortDescription }}</p>
            <div class="grid grid-cols-2 gap-2">
              <div v-for="spec in productSpecs(product)" :key="spec.label" class="bg-titanium-800/50 rounded-lg p-2">
                <div class="text-titanium-500 text-[10px] uppercase tracking-wide">{{ spec.label }}</div>
                <div class="text-titanium-200 text-xs font-mono font-medium mt-0.5 truncate">{{ spec.value }}{{ spec.unit ? ' ' + spec.unit : '' }}</div>
              </div>
            </div>
          </NuxtLink>
        </div>
        <div v-else class="border border-titanium-700 bg-titanium-900/50 rounded-xl p-6 text-titanium-400 text-sm">
          Products are being prepared.
        </div>
      </section>

      <!-- RFQ CTA -->
      <div class="mt-16 p-8 bg-titanium-900/50 border border-titanium-800/50 rounded-2xl text-center">
        <h2 class="text-white font-bold text-2xl mb-3">Can't find what you need?</h2>
        <p class="text-titanium-400 mb-6">We supply custom specifications, non-standard sizes and special alloys. Send us your requirements.</p>
        <div class="flex flex-wrap gap-3 justify-center">
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Get a Custom Quote</NuxtLink>
          <a :href="mailtoHref" class="btn-secondary">Email Us</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { featuredProducts, productCategories } from '@cnbjti/mock-data'
import type { Product, ProductCategory, ProductSpec } from '@cnbjti/types'

const localePath = useLocalePath()
const { mailtoHref } = await useSiteRuntime()
const { data: categoryData } = await useAsyncData('public-product-categories', () => publicApi<ProductCategory[]>('/public/categories'))
const { data: productsData } = await useAsyncData('public-product-list', () => publicApi<Product[]>('/public/products'))

const categories = computed(() => categoryData.value || productCategories)
const allProducts = computed(() => productsData.value || featuredProducts)
const filters = computed(() => [
  { label: 'All', slug: 'All' },
  ...categories.value.map(category => ({ label: category.name, slug: category.slug })),
])
const activeFilter = ref('All')
const categoryBySlug = computed(() => new Map(categories.value.map(category => [category.slug, category])))
const filteredProducts = computed(() => {
  if (activeFilter.value === 'All') return allProducts.value
  return allProducts.value.filter(product => product.category?.slug === activeFilter.value)
})

function productImage(product: Product) {
  return product.images?.[0]?.url || product.category?.image?.url || categoryBySlug.value.get(product.category?.slug || '')?.image?.url || ''
}

function productSpecs(product: Product): ProductSpec[] {
  return (product.specs || []).slice(0, 2)
}

useHead({
  title: 'Titanium Products - CNBJTI',
  meta: [
    { name: 'description', content: 'Complete titanium product range: bar, plate, tube, wire, forgings, fasteners, fittings and CNC parts. ASTM certified, MTR available.' },
  ],
})
</script>
