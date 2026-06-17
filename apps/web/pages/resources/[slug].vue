<template>
  <article v-if="article" class="pt-24 pb-20 min-h-screen bg-titanium-950">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
        <NuxtLink :to="localePath('/')" class="hover:text-titanium-300 transition-colors">Home</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <NuxtLink :to="localePath('/resources')" class="hover:text-titanium-300 transition-colors">Resources</NuxtLink>
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg>
        <span class="text-titanium-300 line-clamp-1">{{ article.title }}</span>
      </nav>

      <header class="mb-10">
        <div class="flex flex-wrap items-center gap-3 mb-5">
          <span class="px-3 py-1 bg-accent-500/10 border border-accent-500/30 rounded text-accent-300 text-xs font-medium">
            {{ article.category || 'Titanium Resources' }}
          </span>
          <span class="text-titanium-500 text-sm">{{ article.readingTime || 5 }} min read</span>
          <span class="text-titanium-700">&middot;</span>
          <span class="text-titanium-500 text-sm">{{ article.publishedAt }}</span>
        </div>
        <h1 class="text-white font-bold text-3xl md:text-5xl leading-tight mb-5">{{ article.title }}</h1>
        <p class="text-titanium-300 text-lg leading-relaxed">{{ article.excerpt }}</p>
      </header>

      <div v-if="articleImage" class="aspect-[16/9] rounded-xl overflow-hidden bg-titanium-900 border border-titanium-800 mb-10">
        <img :src="articleImage" :alt="article.title" class="content-image-lg" />
      </div>

      <div class="article-body" v-html="articleHtml" />

      <footer class="mt-12 pt-8 border-t border-titanium-800">
        <div class="flex flex-wrap gap-2 mb-8">
          <span
            v-for="tag in article.tags || []"
            :key="tag"
            class="px-2.5 py-1 bg-titanium-900 border border-titanium-800 rounded text-titanium-400 text-xs"
          >
            {{ tag }}
          </span>
        </div>
        <NuxtLink :to="localePath('/resources')" class="btn-secondary text-sm">Back to Resources</NuxtLink>
      </footer>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { Article } from '@cnbjti/types'

const route = useRoute()
const localePath = useLocalePath()
const slug = computed(() => String(route.params.slug || ''))
const { data: articleData, error } = await useAsyncData(`public-resource-${slug.value}`, () => publicApi<Article>(`/public/articles/${slug.value}`))

if (error.value || !articleData.value) {
  throw createError({ statusCode: 404, statusMessage: 'Article not found' })
}

const article = computed(() => articleData.value)
const articleImage = computed(() => article.value?.coverImage?.url || '')
const articleHtml = computed(() => renderArticleContent(article.value?.content || article.value?.excerpt || ''))

function renderArticleContent(content: string) {
  const trimmed = content.trim()
  if (!trimmed) return ''
  if (/<[a-z][\s\S]*>/i.test(trimmed)) return trimmed
  return markdownToHtml(trimmed)
}

function markdownToHtml(markdown: string) {
  const blocks = markdown.split(/\n{2,}/).map(block => block.trim()).filter(Boolean)
  return blocks.map(block => {
    if (block.startsWith('### ')) return `<h3>${escapeHtml(block.slice(4).trim())}</h3>`
    if (block.startsWith('## ')) return `<h2>${escapeHtml(block.slice(3).trim())}</h2>`
    if (block.startsWith('# ')) return `<h2>${escapeHtml(block.slice(2).trim())}</h2>`
    if (/^[-*]\s+/m.test(block)) {
      const items = block
        .split('\n')
        .filter(line => /^[-*]\s+/.test(line.trim()))
        .map(line => `<li>${escapeHtml(line.trim().replace(/^[-*]\s+/, ''))}</li>`)
        .join('')
      return `<ul>${items}</ul>`
    }
    return `<p>${escapeHtml(block).replace(/\n/g, '<br>')}</p>`
  }).join('')
}

function escapeHtml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

useHead(() => ({
  title: `${article.value?.title || 'Titanium Resource'} | CNBJTI`,
  meta: [
    { name: 'description', content: article.value?.excerpt || 'Titanium technical resource from CNBJTI.' },
    { property: 'og:title', content: article.value?.title || 'Titanium Resource' },
    { property: 'og:description', content: article.value?.excerpt || 'Titanium technical resource from CNBJTI.' },
    ...(articleImage.value ? [{ property: 'og:image', content: articleImage.value }] : []),
  ],
}))
</script>

<style scoped>
.article-body {
  color: rgb(203 213 225);
  font-size: 1rem;
  line-height: 1.8;
}

.article-body :deep(p) {
  margin: 1rem 0;
}

.article-body :deep(h2) {
  color: white;
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1.25;
  margin: 2.25rem 0 0.75rem;
}

.article-body :deep(h3) {
  color: white;
  font-size: 1.25rem;
  font-weight: 700;
  line-height: 1.3;
  margin: 1.75rem 0 0.65rem;
}

.article-body :deep(ul) {
  list-style: disc;
  padding-left: 1.25rem;
  margin: 1rem 0;
}

.article-body :deep(li) {
  margin: 0.5rem 0;
}

.article-body :deep(a) {
  color: rgb(56 189 248);
}
</style>
