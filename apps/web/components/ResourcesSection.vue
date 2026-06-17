<template>
  <section class="py-24 bg-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-5 mb-12">
        <div>
          <p class="section-label mb-3">Technical Resources</p>
          <h2 class="section-title mb-4">Guides and Tools for Titanium Buyers</h2>
          <p class="section-subtitle max-w-2xl">
            Buyer-focused titanium guides, FAQ answers and application notes pulled from the live resource library.
          </p>
        </div>
        <NuxtLink :to="localePath('/resources')" class="btn-secondary text-sm w-fit">View All Resources</NuxtLink>
      </div>

      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-5">
        <NuxtLink
          :to="localePath('/resources/titanium-weight-calculator')"
          class="group card-hover p-6 flex flex-col"
        >
          <div class="w-10 h-10 rounded-lg bg-titanium-800 group-hover:bg-accent-500/20 border border-titanium-700 group-hover:border-accent-500/40 flex items-center justify-center mb-4 transition-all duration-300">
            <svg class="w-5 h-5 text-accent-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.7">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8 7h8M8 11h8M8 15h5M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" />
            </svg>
          </div>
          <div class="flex items-center gap-2 mb-2">
            <span class="px-2 py-0.5 bg-accent-500/10 border border-accent-500/30 rounded text-xs text-accent-400 font-medium">Tool</span>
            <span class="px-2 py-0.5 bg-titanium-800 rounded text-xs text-titanium-400 font-medium">Calculator</span>
          </div>
          <h3 class="text-white font-semibold text-sm mb-2 group-hover:text-accent-300 transition-colors">Titanium Weight Calculator</h3>
          <p class="text-titanium-500 text-xs leading-relaxed flex-1">Calculate titanium bar, plate, tube and wire weights by dimensions and shape.</p>
          <div class="mt-4 flex items-center gap-1 text-accent-400 text-xs font-medium">
            Open Tool
            <svg class="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
          </div>
        </NuxtLink>

        <NuxtLink
          v-for="article in resourceCards"
          :key="article.slug"
          :to="localePath(`/resources/${article.slug}`)"
          class="group card-hover overflow-hidden flex flex-col"
        >
          <div class="relative h-36 overflow-hidden bg-titanium-900">
            <img :src="articleImage(article)" :alt="article.title" class="content-image" />
            <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/70 to-transparent" />
            <span class="absolute top-3 left-3 px-2 py-0.5 bg-titanium-950/75 border border-titanium-700 rounded text-xs text-titanium-300">
              {{ article.category || 'Guide' }}
            </span>
          </div>
          <div class="p-6 flex flex-col flex-1">
            <h3 class="text-white font-semibold text-sm mb-2 group-hover:text-accent-300 transition-colors leading-snug">{{ article.title }}</h3>
            <p class="text-titanium-500 text-xs leading-relaxed flex-1 line-clamp-3">{{ article.excerpt }}</p>
            <div class="mt-4 flex items-center gap-3 text-xs text-titanium-500">
              <span>{{ article.readingTime || 5 }} min read</span>
              <span>/</span>
              <span>{{ article.publishedAt || 'Updated' }}</span>
            </div>
          </div>
        </NuxtLink>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { Article } from '@cnbjti/types'
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const localePath = useLocalePath()
const { data: articleData } = await useAsyncData('home-resource-articles', () => publicApi<Article[]>('/public/articles'))

const preferredCategories = ['Processing Guide', 'Applications', 'Material Comparison', 'FAQ', 'Titanium Insights']
const resourceCards = computed(() => {
  const articles = [...(articleData.value || [])]
    .filter((article) => article.category && article.title && article.excerpt)
    .sort((a, b) => {
      const categoryDelta = categoryRank(a.category) - categoryRank(b.category)
      if (categoryDelta !== 0) return categoryDelta
      return dateRank(b.publishedAt) - dateRank(a.publishedAt)
    })

  const selected: Article[] = []
  for (const category of preferredCategories) {
    const match = articles.find((article) => article.category === category && !selected.some((item) => item.slug === article.slug))
    if (match) selected.push(match)
  }

  for (const article of articles) {
    if (selected.length >= 5) break
    if (!selected.some((item) => item.slug === article.slug)) selected.push(article)
  }

  return selected.slice(0, 5)
})

function categoryRank(category?: string) {
  const index = preferredCategories.indexOf(category || '')
  return index === -1 ? preferredCategories.length : index
}

function dateRank(value?: string) {
  const time = Date.parse(value || '')
  return Number.isFinite(time) ? time : 0
}

function articleImage(article: Article) {
  return article.coverImage?.url || qinghangPageAssets.sheetFactory.url
}
</script>
