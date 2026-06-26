<template>
  <div v-if="industry">
    <section class="relative bg-titanium-950 pt-32 pb-16 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="'/'" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <NuxtLink :to="'/industries'" class="hover:text-accent-400 transition-colors">Industries</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">{{ industry.name }}</span>
        </nav>

        <div class="grid grid-cols-1 lg:grid-cols-[1fr_480px] gap-10 items-center">
          <div>
            <div class="section-label">{{ industry.kicker }}</div>
            <h1 class="section-title mt-3 mb-6">{{ industry.name }}</h1>
            <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">{{ industry.summary }}</p>
            <div class="flex flex-wrap gap-2 mt-7">
              <span
                v-for="grade in industry.grades"
                :key="grade"
                class="px-3 py-1 bg-titanium-900 border border-titanium-700 rounded text-xs text-titanium-300 font-mono"
              >
                {{ grade }}
              </span>
            </div>
          </div>
          <div class="relative overflow-hidden rounded-lg border border-titanium-800 bg-titanium-900 aspect-[4/3]">
            <img :src="industry.image" :alt="industry.imageAlt" class="content-image-lg" />
          </div>
        </div>
      </div>
    </section>

    <section class="bg-titanium-950 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="card p-6">
            <h2 class="text-white font-semibold mb-5">Typical Applications</h2>
            <ul class="space-y-3">
              <li v-for="item in industry.applications" :key="item" class="flex gap-3 text-sm text-titanium-300">
                <span class="mt-2 h-1.5 w-1.5 rounded-full bg-accent-400 flex-shrink-0" />
                <span>{{ item }}</span>
              </li>
            </ul>
          </div>

          <div class="card p-6">
            <h2 class="text-white font-semibold mb-5">Standards and Grades</h2>
            <div class="mb-5">
              <div class="text-titanium-500 text-xs uppercase tracking-[0.18em] mb-2">Grades</div>
              <div class="flex flex-wrap gap-2">
                <span v-for="grade in industry.grades" :key="grade" class="px-2 py-1 bg-titanium-800 border border-titanium-700 rounded text-xs text-titanium-300 font-mono">{{ grade }}</span>
              </div>
            </div>
            <div>
              <div class="text-titanium-500 text-xs uppercase tracking-[0.18em] mb-2">Standards</div>
              <div class="flex flex-wrap gap-2">
                <span v-for="standard in industry.standards" :key="standard" class="px-2 py-1 bg-titanium-800 border border-titanium-700 rounded text-xs text-titanium-300 font-mono">{{ standard }}</span>
              </div>
            </div>
          </div>

          <div class="card p-6">
            <h2 class="text-white font-semibold mb-5">RFQ Notes</h2>
            <ul class="space-y-3">
              <li v-for="item in industry.requirements" :key="item" class="flex gap-3 text-sm text-titanium-300">
                <span class="mt-2 h-1.5 w-1.5 rounded-full bg-accent-400 flex-shrink-0" />
                <span>{{ item }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-[360px_1fr] gap-10">
          <div>
            <div class="section-label mb-3">Supply Route</div>
            <h2 class="section-title mb-5">Recommended Product Forms</h2>
            <p class="text-titanium-400 text-sm leading-relaxed">
              These product families are common starting points for {{ industry.name.toLowerCase() }} projects.
            </p>
            <div class="mt-6 flex flex-col gap-3">
              <NuxtLink
                v-for="product in industry.productLinks"
                :key="product.href"
                :to="product.href"
                class="card card-hover px-4 py-3 text-sm text-white flex items-center justify-between"
              >
                <span>{{ product.label }}</span>
                <span class="text-accent-400">View</span>
              </NuxtLink>
            </div>
          </div>

          <div>
            <div class="section-label mb-3">Application Guides</div>
            <h2 class="section-title mb-8">Related Engineering Articles</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
              <NuxtLink
                v-for="article in relatedArticles"
                :key="article.slug"
                :to="`/resources/${article.slug}`"
                class="card card-hover overflow-hidden group"
              >
                <div class="relative h-36 overflow-hidden bg-titanium-900">
                  <img :src="articleImage(article)" :alt="article.title" class="content-image" />
                </div>
                <div class="p-5">
                  <div class="text-titanium-500 text-xs mb-2">{{ article.category || 'Application' }}</div>
                  <h3 class="text-white font-semibold leading-snug group-hover:text-accent-400 transition-colors">{{ article.title }}</h3>
                  <p class="text-titanium-400 text-sm leading-relaxed mt-3 line-clamp-3">{{ article.excerpt }}</p>
                </div>
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Send Your {{ industry.name }} RFQ</h2>
        <p class="text-titanium-400 mb-8">Attach drawings or list grade, form, dimensions, quantity, destination and certification needs.</p>
        <NuxtLink :to="'/request-a-quote'" class="btn-primary">Request a Quote</NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article, IndustryProfile } from '@cnbjti/types'
import { defaultIndustryProfiles, findIndustryProfile, matchingIndustryArticles } from '~/utils/industryContent'
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const route = useRoute()
const slug = Array.isArray(route.params.slug) ? route.params.slug[0] : String(route.params.slug || '')
const { data: industryData } = await useAsyncData(`public-industry-${slug}`, () => publicApi<IndustryProfile>(`/public/industries/${slug}`), {
  default: () => findIndustryProfile(slug, defaultIndustryProfiles) || null,
})

const industry = computed<IndustryProfile | null>(() => industryData.value || findIndustryProfile(slug, defaultIndustryProfiles) || null)

if (!industry.value) {
  throw createError({ statusCode: 404, statusMessage: 'Industry not found' })
}

const { data: articleData } = await useAsyncData(`industry-articles-${slug}`, () => publicApi<Article[]>('/public/articles'))

const relatedArticles = computed(() => industry.value ? matchingIndustryArticles(articleData.value || [], industry.value, 4) : [])

function articleImage(article: Article) {
  return article.coverImage?.url || qinghangPageAssets.sheetFactory.url
}

useHead(() => ({
  title: `${industry.value?.name || 'Industry'} Titanium Supply | CNBJTI`,
  meta: [
    { name: 'description', content: `${industry.value?.name || 'Industry'} titanium grades, standards, product forms and RFQ guidance from CNBJTI.` },
  ],
}))
</script>
