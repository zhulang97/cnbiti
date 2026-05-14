<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Resources</span>
        </nav>
        <div class="section-label">Knowledge Base</div>
        <h1 class="section-title mt-3 mb-6">Titanium Resources</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          Technical guides, FAQ articles, application notes and reference tools for titanium procurement and specification work.
        </p>
      </div>
    </section>

    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Tools</div>
        <h2 class="section-title mb-8">Buyer Reference</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-16">
          <NuxtLink :to="localePath('/resources/titanium-weight-calculator')" class="card card-hover p-8 group flex items-start gap-6">
            <div class="w-12 h-12 rounded bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
              <svg class="w-6 h-6 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M8 7h8M8 11h8M8 15h5M5 3h14a2 2 0 012 2v14a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2z" />
              </svg>
            </div>
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <h3 class="text-white font-semibold text-lg group-hover:text-accent-400 transition-colors">Titanium Weight Calculator</h3>
                <span class="px-2 py-0.5 bg-accent-500/10 border border-accent-500/30 rounded text-accent-400 text-xs font-mono">Tool</span>
              </div>
              <p class="text-titanium-400 text-sm leading-relaxed">Calculate titanium bar, sheet, tube and wire weights by shape, grade and dimensions.</p>
            </div>
          </NuxtLink>

          <NuxtLink :to="localePath('/grades')" class="card card-hover p-8 group flex items-start gap-6">
            <div class="w-12 h-12 rounded bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
              <svg class="w-6 h-6 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 7h16M4 12h16M4 17h16M8 4v16M16 4v16" />
              </svg>
            </div>
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <h3 class="text-white font-semibold text-lg group-hover:text-accent-400 transition-colors">Grade Comparison</h3>
                <span class="px-2 py-0.5 bg-accent-500/10 border border-accent-500/30 rounded text-accent-400 text-xs font-mono">Guide</span>
              </div>
              <p class="text-titanium-400 text-sm leading-relaxed">Compare common titanium grades by chemistry, mechanical values and applications.</p>
            </div>
          </NuxtLink>
        </div>

        <div class="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-5 mb-8">
          <div>
            <div class="section-label mb-3">Article Library</div>
            <h2 class="section-title">Technical Guides and FAQ</h2>
          </div>
          <div class="w-full lg:w-80">
            <label for="resource-search" class="sr-only">Search resources</label>
            <input
              id="resource-search"
              v-model="searchQuery"
              type="search"
              placeholder="Search articles"
              class="w-full rounded border border-titanium-700 bg-titanium-900 px-4 py-3 text-sm text-white placeholder:text-titanium-500 focus:border-accent-400 focus:outline-none"
            />
          </div>
        </div>

        <div class="flex flex-wrap gap-2 mb-8">
          <button
            v-for="category in categories"
            :key="category"
            type="button"
            :class="[
              'px-3 py-2 rounded border text-xs font-mono transition-colors',
              selectedCategory === category
                ? 'border-accent-400 bg-accent-500/10 text-accent-300'
                : 'border-titanium-700 bg-titanium-900 text-titanium-400 hover:border-titanium-500 hover:text-white',
            ]"
            @click="selectedCategory = category"
          >
            {{ category }}
          </button>
        </div>

        <div class="text-titanium-500 text-sm mb-6">
          Showing {{ visibleArticles.length }} of {{ filteredArticles.length }} matching articles
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <NuxtLink
            v-for="article in visibleArticles"
            :key="article.slug"
            :to="localePath(`/resources/${article.slug}`)"
            class="card card-hover group overflow-hidden flex flex-col"
          >
            <div class="relative h-44 overflow-hidden bg-titanium-900">
              <img :src="articleImage(article)" :alt="article.title" class="content-image-lg" />
              <div class="absolute inset-0 bg-gradient-to-t from-titanium-950/80 to-transparent" />
              <span class="absolute top-3 left-3 px-2 py-1 bg-titanium-900/80 backdrop-blur-sm border border-titanium-700 rounded text-xs text-titanium-300">
                {{ article.category || 'Guide' }}
              </span>
            </div>
            <div class="p-6 flex flex-col flex-1">
              <h3 class="text-white font-semibold mb-3 group-hover:text-accent-400 transition-colors leading-snug">{{ article.title }}</h3>
              <p class="text-titanium-400 text-sm leading-relaxed flex-1 line-clamp-3">{{ article.excerpt }}</p>
              <div class="flex items-center gap-3 mt-4 pt-4 border-t border-titanium-800">
                <span class="text-titanium-500 text-xs">{{ article.readingTime || 4 }} min read</span>
                <span class="text-titanium-700">/</span>
                <span class="text-titanium-500 text-xs">{{ article.publishedAt }}</span>
              </div>
            </div>
          </NuxtLink>
        </div>

        <div v-if="filteredArticles.length > visibleArticles.length" class="text-center mt-10">
          <button type="button" class="btn-secondary text-sm" @click="visibleCount += 24">Show More Articles</button>
        </div>

        <div class="mt-16 grid grid-cols-1 md:grid-cols-3 gap-5">
          <NuxtLink :to="localePath('/standards')" class="card card-hover p-6 group flex items-center gap-4">
            <div class="w-10 h-10 rounded bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
              <span class="text-accent-300 font-mono text-xs">STD</span>
            </div>
            <div>
              <div class="text-white font-medium text-sm group-hover:text-accent-400 transition-colors">Standards Guide</div>
              <div class="text-titanium-500 text-xs mt-0.5">ASTM, ASME, AMS and EN references</div>
            </div>
          </NuxtLink>
          <NuxtLink :to="localePath('/industries')" class="card card-hover p-6 group flex items-center gap-4">
            <div class="w-10 h-10 rounded bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
              <span class="text-accent-300 font-mono text-xs">APP</span>
            </div>
            <div>
              <div class="text-white font-medium text-sm group-hover:text-accent-400 transition-colors">Industry Applications</div>
              <div class="text-titanium-500 text-xs mt-0.5">Grades and product forms by sector</div>
            </div>
          </NuxtLink>
          <NuxtLink :to="localePath('/faq')" class="card card-hover p-6 group flex items-center gap-4">
            <div class="w-10 h-10 rounded bg-accent-500/10 border border-accent-500/20 flex items-center justify-center flex-shrink-0">
              <span class="text-accent-300 font-mono text-xs">FAQ</span>
            </div>
            <div>
              <div class="text-white font-medium text-sm group-hover:text-accent-400 transition-colors">FAQ</div>
              <div class="text-titanium-500 text-xs mt-0.5">Common titanium buyer questions</div>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '@cnbjti/types'
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const localePath = useLocalePath()
const selectedCategory = ref('All')
const searchQuery = ref('')
const visibleCount = ref(36)

const { data: articleData } = await useAsyncData('public-resource-articles', () => publicApi<Article[]>('/public/articles'))

const articles = computed(() => articleData.value || [])
const categories = computed(() => {
  const names = [...new Set(articles.value.map((article) => article.category).filter(Boolean) as string[])]
  return ['All', ...names.sort()]
})
const filteredArticles = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  return articles.value.filter((article) => {
    const matchesCategory = selectedCategory.value === 'All' || article.category === selectedCategory.value
    const matchesQuery = !query || `${article.title} ${article.excerpt} ${(article.tags || []).join(' ')}`.toLowerCase().includes(query)
    return matchesCategory && matchesQuery
  })
})
const visibleArticles = computed(() => filteredArticles.value.slice(0, visibleCount.value))

watch([selectedCategory, searchQuery], () => {
  visibleCount.value = 36
})

function articleImage(article: Article) {
  return article.coverImage?.url || qinghangPageAssets.sheetFactory.url
}

useHead({
  title: 'Titanium Resources - Guides, FAQ and Technical Reference | CNBJTI',
  meta: [
    { name: 'description', content: 'Titanium technical resources, FAQ articles, grade guides, standards references and application notes for CNBJTI buyers.' },
  ],
})
</script>
