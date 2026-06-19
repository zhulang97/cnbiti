<template>
  <section class="bg-white py-20">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="mb-10 flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <p class="section-label mb-3">Titanium Products</p>
          <h2 class="section-title mb-4">All Types of Titanium Products</h2>
          <p class="section-subtitle max-w-3xl">
            Fast access to common titanium product forms for procurement teams: stock material, cut-to-size supply and drawing-based custom parts.
          </p>
        </div>
        <NuxtLink :to="localizedPath('/products')" class="btn-secondary w-fit">
          View Full Catalog
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        </NuxtLink>
      </div>

      <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        <NuxtLink
          v-for="cat in featuredCategories"
          :key="cat.slug"
          :to="localizedPath('/products/' + cat.slug)"
          class="group overflow-hidden rounded-xl border border-titanium-200 bg-white shadow-sm shadow-titanium-200/60 transition-all duration-300 hover:border-accent-400 hover:shadow-lg hover:shadow-titanium-200/80"
        >
          <div class="relative h-44 overflow-hidden bg-titanium-100">
            <img :src="categoryImage(cat)" :alt="cat.name" class="h-full w-full object-contain object-center transition-transform duration-500 group-hover:scale-[1.03]" loading="lazy" />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/75 via-titanium-950/25 to-transparent" />
            <div class="absolute left-4 top-4 rounded border border-white/20 bg-white/15 px-2.5 py-1 text-xs font-mono text-white backdrop-blur media-caption">
              {{ productCode(cat.name) }}
            </div>
            <div class="absolute bottom-4 left-4 right-4 media-caption">
              <h3 class="text-base font-semibold leading-tight text-white group-hover:text-accent-400 transition-colors">{{ cat.name }}</h3>
              <p class="mt-1 text-xs text-titanium-200">{{ cat.productCount }} published products</p>
            </div>
          </div>

          <div class="p-5">
            <p class="mb-4 min-h-[44px] text-sm leading-relaxed text-titanium-600 line-clamp-2">{{ cat.description || defaultDescription(cat.name) }}</p>
            <div class="mb-4 flex flex-wrap gap-2">
              <span v-for="grade in commonGrades" :key="`${cat.slug}-${grade}`" class="rounded bg-titanium-100 px-2 py-1 text-[11px] font-mono text-titanium-600">
                {{ grade }}
              </span>
            </div>
            <div class="flex items-center justify-between border-t border-titanium-200 pt-4">
              <span class="text-xs font-medium text-accent-600">ASTM / AMS / Custom</span>
              <span class="inline-flex items-center gap-1 text-xs font-semibold text-titanium-700 group-hover:text-accent-600">
                Details
                <svg class="w-3.5 h-3.5 transition-transform group-hover:translate-x-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
              </span>
            </div>
          </div>
        </NuxtLink>
      </div>

      <div class="mt-8 grid gap-4 rounded-xl border border-titanium-200 bg-titanium-50 p-5 md:grid-cols-3">
        <div v-for="item in buyerNotes" :key="item.title" class="flex items-start gap-3">
          <div class="mt-0.5 flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-lg border border-accent-500/20 bg-accent-500/10 text-xs font-bold text-accent-600">
            {{ item.code }}
          </div>
          <div>
            <div class="text-sm font-semibold text-titanium-950">{{ item.title }}</div>
            <div class="mt-1 text-xs leading-relaxed text-titanium-500">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { ProductCategory } from '@cnbjti/types'
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const { siteConfig, productCategories, localizedPath } = await useSiteRuntime()

const featuredCategories = computed(() => productCategories.value
  .filter((category) => category.showOnHome !== false)
  .slice()
  .sort((a, b) => (a.homeSort ?? 999) - (b.homeSort ?? 999))
  .slice(0, 8))

const commonGrades = ['Gr.2', 'Gr.5', 'Gr.7', 'Gr.9']
const defaultBuyerNotes = [
  { code: 'MTR', title: 'Certified Documentation', desc: 'EN 10204 3.1 MTR, heat number traceability and third-party inspection available.' },
  { code: 'CUT', title: 'Cut-to-size Supply', desc: 'Bar, plate, sheet and tube can be cut or processed before export packing.' },
  { code: 'OEM', title: 'Drawing-based Parts', desc: 'CNC machining, forging and special titanium components quoted from drawings.' },
]
const buyerNotes = computed(() => siteConfig.value.homePage?.buyerNotes?.length ? siteConfig.value.homePage.buyerNotes : defaultBuyerNotes)

function categoryImage(category: ProductCategory) {
  if (category.image?.url) return category.image.url
  const name = category.name.toLowerCase()
  if (name.includes('tube') || name.includes('pipe')) return qinghangPageAssets.weldedTubes.url
  if (name.includes('sheet') || name.includes('plate') || name.includes('foil')) return qinghangPageAssets.sheetFactory.url
  if (name.includes('forging')) return qinghangPageAssets.forging.url
  if (name.includes('cnc') || name.includes('machined')) return qinghangPageAssets.cncMachining.url
  if (name.includes('fitting') || name.includes('flange')) return qinghangPageAssets.pipeEquipment.url
  return qinghangPageAssets.factoryCnc.url
}

function productCode(name: string) {
  if (name.includes('&')) return name.split('&')[0].trim().slice(0, 10).toUpperCase()
  return name.split(/\s+/).slice(0, 2).join(' ').slice(0, 10).toUpperCase()
}

function defaultDescription(name: string) {
  return `${name} in commercially pure and alloy titanium grades with export documentation and custom sizing.`
}
</script>
