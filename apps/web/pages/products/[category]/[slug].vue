<template>
  <div class="min-h-screen bg-titanium-950 pt-24 pb-20">
    <div v-if="product && category" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
        <NuxtLink :to="localePath('/')" class="hover:text-titanium-300 transition-colors">Home</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <NuxtLink :to="localePath('/products')" class="hover:text-titanium-300 transition-colors">Products</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <NuxtLink :to="localePath(`/products/${category.slug}`)" class="hover:text-titanium-300 transition-colors">{{ category.name }}</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <span class="text-titanium-300 truncate">{{ product.name }}</span>
      </nav>

      <section class="grid lg:grid-cols-[1.05fr_0.95fr] gap-12 items-start mb-16">
        <div class="space-y-4">
          <div class="aspect-[4/3] rounded-2xl overflow-hidden border border-titanium-700 bg-titanium-900 shadow-2xl">
            <img :src="activeImage?.url" :alt="activeImage?.alt || product.name" class="content-image-lg" />
          </div>
          <div v-if="images.length > 1" class="grid grid-cols-4 sm:grid-cols-6 gap-3">
            <button
              v-for="(image, index) in images"
              :key="image.id || image.url"
              type="button"
              class="aspect-square rounded-lg overflow-hidden border bg-titanium-900 transition-colors"
              :class="index === activeImageIndex ? 'border-accent-400' : 'border-titanium-700 hover:border-titanium-500'"
              @click="activeImageIndex = index"
            >
              <img :src="image.url" :alt="image.alt || product.name" class="content-image-sm" />
            </button>
          </div>
        </div>

        <div>
          <p class="section-label mb-3">{{ category.name }}</p>
          <h1 class="section-title mb-5">{{ product.name }}</h1>
          <p class="text-titanium-300 text-lg leading-relaxed mb-6">{{ product.shortDescription }}</p>

          <div class="flex flex-wrap gap-2 mb-8">
            <NuxtLink
              v-for="grade in product.grades"
              :key="grade.id"
              :to="localePath(`/grades/${grade.slug}`)"
              class="px-3 py-1.5 bg-accent-500/10 border border-accent-500/30 rounded-lg text-accent-300 font-mono text-sm font-semibold"
            >
              {{ grade.shortName }}
            </NuxtLink>
            <span class="px-3 py-1.5 rounded-lg text-sm font-semibold" :class="product.inStock ? 'bg-green-500/10 border border-green-500/30 text-green-300' : 'bg-titanium-800 border border-titanium-700 text-titanium-400'">
              {{ product.inStock ? 'In Stock' : 'Made to Order' }}
            </span>
          </div>

          <div class="grid sm:grid-cols-2 gap-3 mb-8">
            <div v-for="spec in product.specs" :key="spec.label" class="bg-titanium-900 border border-titanium-700 rounded-xl p-4">
              <div class="text-titanium-500 text-[11px] uppercase tracking-wide">{{ spec.label }}</div>
              <div class="text-white text-sm font-mono font-semibold mt-1">{{ spec.value }}{{ spec.unit ? ' ' + spec.unit : '' }}</div>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Get a Quote</NuxtLink>
            <a :href="mailtoHref" class="btn-secondary">Email Sales</a>
          </div>
        </div>
      </section>

      <section class="grid lg:grid-cols-[1fr_360px] gap-8 mb-16">
        <div class="space-y-8">
          <div>
            <h2 class="text-white font-bold text-xl mb-4">Product Details</h2>
            <p class="text-titanium-400 leading-relaxed whitespace-pre-line">{{ product.description || product.shortDescription }}</p>
          </div>

          <div>
            <h2 class="text-white font-bold text-xl mb-4">Applicable Standards</h2>
            <div class="flex flex-wrap gap-3">
              <NuxtLink
                v-for="standard in product.standards"
                :key="standard.id"
                :to="localePath(`/standards/${standard.slug}`)"
                class="px-4 py-2 bg-titanium-900 border border-titanium-700 rounded-xl hover:border-accent-500/40 transition-colors"
              >
                <span class="text-titanium-200 font-mono font-semibold text-sm">{{ standard.code }}</span>
                <span class="text-titanium-500 text-xs ml-2">{{ standard.name.split(' ').slice(0, 5).join(' ') }}...</span>
              </NuxtLink>
            </div>
          </div>
        </div>

        <aside class="bg-titanium-900 border border-titanium-700 rounded-2xl p-6 h-fit">
          <h2 class="text-white font-bold text-lg mb-4">Quote Checklist</h2>
          <ul class="space-y-3 mb-6">
            <li v-for="item in quoteChecklist" :key="item" class="flex items-start gap-2 text-sm text-titanium-300">
              <svg class="w-4 h-4 text-accent-400 mt-0.5 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              {{ item }}
            </li>
          </ul>
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary w-full justify-center">Request Price</NuxtLink>
        </aside>
      </section>

      <section v-if="relatedProducts.length">
        <h2 class="text-white font-bold text-xl mb-5">More {{ category.name }}</h2>
        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-5">
          <NuxtLink
            v-for="item in relatedProducts"
            :key="item.id"
            :to="localePath(`/products/${category.slug}/${item.slug}`)"
            class="group card-hover p-5"
          >
            <div class="aspect-video rounded-lg overflow-hidden bg-titanium-900 mb-4">
              <img :src="item.images?.[0]?.url || category.image?.url" :alt="item.name" class="content-image" />
            </div>
            <h3 class="text-white font-semibold text-sm group-hover:text-accent-300 transition-colors">{{ item.name }}</h3>
            <p class="text-titanium-500 text-xs mt-2">{{ item.shortDescription }}</p>
          </NuxtLink>
        </div>
      </section>
    </div>

    <div v-else class="min-h-[50vh] flex items-center justify-center px-4">
      <div class="text-center">
        <p class="text-titanium-400 mb-4">Product not found.</p>
        <NuxtLink :to="localePath('/products')" class="btn-secondary">View Products</NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { featuredProducts as mockProducts, productCategories } from '@cnbjti/mock-data'
import type { MediaAsset, Product, ProductCategory } from '@cnbjti/types'

const route = useRoute()
const localePath = useLocalePath()
const { siteConfig } = await useSiteRuntime()
const categorySlug = computed(() => String(route.params.category))
const productSlug = computed(() => String(route.params.slug))
const activeImageIndex = ref(0)

const { data: productData } = await useAsyncData(`public-product-${productSlug.value}`, () => publicApi<Product>(`/public/products/${productSlug.value}`))
const { data: productsData } = await useAsyncData('public-products-for-detail', () => publicApi<Product[]>('/public/products'))
const { data: categoriesData } = await useAsyncData('public-categories-for-product-detail', () => publicApi<ProductCategory[]>('/public/categories'))

const allProducts = computed(() => productsData.value || mockProducts)
const product = computed(() => productData.value || mockProducts.find(item => item.slug === productSlug.value && item.category?.slug === categorySlug.value))
const allCategories = computed(() => categoriesData.value || productCategories)
const category = computed(() => product.value?.category || allCategories.value.find(item => item.slug === categorySlug.value))
const images = computed<MediaAsset[]>(() => {
  if (product.value?.images?.length) return product.value.images
  return category.value?.image ? [category.value.image] : []
})
const activeImage = computed(() => images.value[activeImageIndex.value] || images.value[0])
const relatedProducts = computed(() => allProducts.value.filter(item => item.category?.slug === categorySlug.value && item.slug !== productSlug.value).slice(0, 3))
const quoteChecklist = ['Grade, standard and dimensions', 'Quantity and destination port', 'Surface finish or tolerance', 'MTR and inspection requirements']
const mailtoHref = computed(() => {
  const subject = encodeURIComponent(`RFQ - ${product.value?.name || 'Titanium Product'}`)
  return `mailto:${siteConfig.value.email}?subject=${subject}`
})

watch(productSlug, () => {
  activeImageIndex.value = 0
})

useHead(computed(() => {
  const seo = product.value?.seo
  return {
    title: seo?.title || (product.value ? `${product.value.name} — CNBJTI` : 'Product — CNBJTI'),
    meta: [
      { name: 'description', content: seo?.description || product.value?.shortDescription || '' },
      ...(seo?.ogTitle ? [{ property: 'og:title', content: seo.ogTitle }] : []),
      ...(seo?.ogDescription ? [{ property: 'og:description', content: seo.ogDescription }] : []),
      ...(seo?.ogImage ? [{ property: 'og:image', content: seo.ogImage }] : []),
      ...(seo?.noIndex ? [{ name: 'robots', content: 'noindex,nofollow' }] : []),
    ],
    link: seo?.canonical ? [{ rel: 'canonical', href: seo.canonical }] : [],
  }
}))
</script>
