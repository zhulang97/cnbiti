<template>
  <div class="pt-24 pb-20 min-h-screen bg-titanium-950">
    <div v-if="category" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
        <NuxtLink :to="localePath('/')" class="hover:text-titanium-300 transition-colors">Home</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <NuxtLink :to="localePath('/products')" class="hover:text-titanium-300 transition-colors">Products</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <span class="text-titanium-300">{{ category.name }}</span>
      </nav>

      <div class="grid lg:grid-cols-2 gap-12 items-center mb-16">
        <div>
          <p class="section-label mb-3">{{ category.name }}</p>
          <h1 class="section-title mb-5">{{ category.name }}</h1>
          <p class="section-subtitle mb-8">{{ category.description }}</p>
          <div class="flex flex-wrap gap-3">
            <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Get a Quote</NuxtLink>
            <a :href="mailtoHref" class="btn-secondary">Send Drawing</a>
          </div>
        </div>
        <div class="relative rounded-2xl overflow-hidden border border-titanium-700/50 bg-titanium-900 shadow-2xl">
          <img :src="category.image?.url" :alt="category.name" class="content-image-lg h-72" />
          <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/60 to-transparent" />
        </div>
      </div>

      <div class="mb-12">
        <h2 class="text-white font-bold text-xl mb-5">Available Grades</h2>
        <div class="flex flex-wrap gap-3">
          <div v-for="grade in grades" :key="grade.id" class="flex items-center gap-2 px-4 py-2 bg-titanium-900 border border-titanium-700 rounded-xl hover:border-accent-500/40 transition-colors cursor-pointer">
            <span class="text-accent-400 font-mono font-bold text-sm">{{ grade.shortName }}</span>
            <span class="text-titanium-400 text-sm">{{ grade.name }}</span>
          </div>
        </div>
      </div>

      <div class="mb-12">
        <h2 class="text-white font-bold text-xl mb-5">Applicable Standards</h2>
        <div class="flex flex-wrap gap-3">
          <div v-for="std in standards" :key="std.id" class="px-4 py-2 bg-titanium-900 border border-titanium-700 rounded-xl">
            <span class="text-titanium-200 font-mono font-semibold text-sm">{{ std.code }}</span>
            <span class="text-titanium-500 text-xs ml-2">{{ std.name.split(' ').slice(0, 5).join(' ') }}...</span>
          </div>
        </div>
      </div>

      <div class="mb-12">
        <h2 class="text-white font-bold text-xl mb-5">Featured Products</h2>
        <div v-if="categoryProducts.length" class="grid md:grid-cols-2 lg:grid-cols-3 gap-5">
          <NuxtLink
            v-for="product in categoryProducts"
            :key="product.id"
            :to="localePath(`/products/${category.slug}/${product.slug}`)"
            class="group card-hover p-5"
          >
            <div class="aspect-video rounded-lg overflow-hidden mb-4 bg-titanium-900">
              <img :src="productImage(product)" :alt="product.name" class="content-image" />
            </div>
            <h3 class="text-white font-semibold text-sm mb-2 group-hover:text-accent-300 transition-colors">{{ product.name }}</h3>
            <p class="text-titanium-500 text-xs mb-3">{{ product.shortDescription }}</p>
            <div class="grid grid-cols-2 gap-2">
              <div v-for="spec in productSpecs(product)" :key="spec.label" class="bg-titanium-800/50 rounded-lg p-2">
                <div class="text-titanium-500 text-[10px] uppercase tracking-wide">{{ spec.label }}</div>
                <div class="text-titanium-200 text-xs font-mono font-medium mt-0.5">{{ spec.value }}{{ spec.unit ? ' ' + spec.unit : '' }}</div>
              </div>
            </div>
          </NuxtLink>
        </div>
        <div v-else class="border border-titanium-700 bg-titanium-900/50 rounded-xl p-6 text-titanium-400 text-sm">
          Products for this category are being prepared.
        </div>
      </div>

      <div class="p-8 bg-gradient-to-r from-accent-600/10 to-steel-800/20 border border-accent-500/20 rounded-2xl">
        <div class="grid md:grid-cols-2 gap-8 items-center">
          <div>
            <h2 class="text-white font-bold text-2xl mb-3">Request a Quote for {{ category.name }}</h2>
            <p class="text-titanium-400 mb-4">Tell us your grade, standard, dimensions and quantity. We respond within 24 hours.</p>
            <ul class="space-y-2">
              <li v-for="item in rfqPoints" :key="item" class="flex items-center gap-2 text-sm text-titanium-300">
                <svg class="w-4 h-4 text-accent-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
                {{ item }}
              </li>
            </ul>
          </div>
          <div class="flex flex-col gap-3">
            <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary justify-center py-3.5">Get a Quote</NuxtLink>
            <a :href="whatsappHref" target="_blank" class="inline-flex items-center justify-center gap-2 px-6 py-3.5 bg-green-500/10 border border-green-500/40 text-green-400 hover:bg-green-500/20 font-semibold rounded-lg transition-all duration-200">
              WhatsApp Us
            </a>
            <a :href="mailtoHref" class="btn-secondary justify-center py-3">Email Drawing</a>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="max-w-7xl mx-auto px-4 py-32 text-center">
      <p class="text-titanium-400">Category not found.</p>
      <NuxtLink :to="localePath('/products')" class="btn-primary mt-4">Back to Products</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { productCategories, titaniumGrades as mockGrades, standards as mockStandards, featuredProducts as mockProducts } from '@cnbjti/mock-data'
import type { Product, ProductCategory, ProductSpec, Standard, TitaniumGrade } from '@cnbjti/types'

const route = useRoute()
const localePath = useLocalePath()
const { whatsappHref, mailtoHref } = await useSiteRuntime()
const categorySlug = computed(() => String(route.params.category))
const { data: categoryData } = await useAsyncData(`public-category-${categorySlug.value}`, () => publicApi<ProductCategory>(`/public/categories/${categorySlug.value}`))
const { data: gradesData } = await useAsyncData('public-grades', () => publicApi<TitaniumGrade[]>('/public/grades'))
const { data: standardsData } = await useAsyncData('public-standards', () => publicApi<Standard[]>('/public/standards'))
const { data: productsData } = await useAsyncData('public-products', () => publicApi<Product[]>('/public/products'))

const category = computed(() => categoryData.value || productCategories.find(c => c.slug === categorySlug.value))
const grades = computed(() => gradesData.value || mockGrades)
const standards = computed(() => standardsData.value || mockStandards)
const allProducts = computed(() => productsData.value || mockProducts)
const categoryProducts = computed(() => allProducts.value.filter(product => product.category?.slug === categorySlug.value))
const rfqPoints = ['MTR / EN 10204 3.1 available', 'Cut-to-size and custom lengths', 'Small MOQ welcome', 'Export documentation included']

function productImage(product: Product) {
  return product.images?.[0]?.url || category.value?.image?.url || ''
}

function productSpecs(product: Product): ProductSpec[] {
  return (product.specs || []).slice(0, 2)
}

useHead(computed(() => {
  const seo = category.value?.seo
  return {
    title: seo?.title || (category.value ? category.value.name + ' — CNBJTI' : 'Product — CNBJTI'),
    meta: [
      { name: 'description', content: seo?.description || category.value?.description || '' },
      ...(seo?.noIndex ? [{ name: 'robots', content: 'noindex,nofollow' }] : []),
    ],
    link: seo?.canonical ? [{ rel: 'canonical', href: seo.canonical }] : [],
  }
}))
</script>
