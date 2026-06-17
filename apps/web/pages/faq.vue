<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">FAQ</span>
        </nav>
        <div class="section-label">Common Questions</div>
        <h1 class="section-title mt-3 mb-6">Titanium FAQ</h1>
        <p class="text-titanium-300 text-lg max-w-2xl leading-relaxed">
          Practical answers for titanium buyers, from ordering and certification to grade selection and processing.
        </p>
      </div>
    </section>

    <section class="bg-titanium-950 py-16">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="space-y-12">
          <div v-for="category in coreFaqCategories" :key="category.title">
            <h2 class="text-white font-semibold text-lg mb-6">{{ category.title }}</h2>
            <div class="space-y-3">
              <div v-for="(item, index) in category.items" :key="item.q" class="card overflow-hidden">
                <button
                  type="button"
                  class="w-full flex items-center justify-between p-5 text-left hover:bg-titanium-800/30 transition-colors"
                  @click="toggle(category.title, index)"
                >
                  <span class="text-white font-medium text-sm pr-4">{{ item.q }}</span>
                  <svg
                    :class="['w-5 h-5 text-titanium-500 flex-shrink-0 transition-transform duration-200', isOpen(category.title, index) ? 'rotate-180' : '']"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    stroke-width="2"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
                <div v-show="isOpen(category.title, index)" class="px-5 pb-5 text-titanium-400 text-sm leading-relaxed border-t border-titanium-800">
                  <p class="pt-4">{{ item.a }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="bg-titanium-900/30 py-16">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-5 mb-8">
          <div>
            <div class="section-label mb-3">FAQ Library</div>
            <h2 class="section-title">More Buyer Questions</h2>
          </div>
          <div class="w-full md:w-80">
            <label for="faq-search" class="sr-only">Search FAQ</label>
            <input
              id="faq-search"
              v-model="searchQuery"
              type="search"
              placeholder="Search titanium questions"
              class="w-full rounded border border-titanium-700 bg-titanium-950 px-4 py-3 text-sm text-white placeholder:text-titanium-500 focus:border-accent-400 focus:outline-none"
            />
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <NuxtLink
            v-for="article in visibleFaqArticles"
            :key="article.slug"
            :to="localePath(`/resources/${article.slug}`)"
            class="card card-hover p-5 group"
          >
            <div class="text-titanium-500 text-xs mb-2">{{ article.publishedAt }}</div>
            <h3 class="text-white font-semibold text-sm leading-snug group-hover:text-accent-400 transition-colors">{{ article.title }}</h3>
            <p class="text-titanium-400 text-sm leading-relaxed mt-3 line-clamp-3">{{ article.excerpt }}</p>
          </NuxtLink>
        </div>

        <div v-if="filteredFaqArticles.length > visibleFaqArticles.length" class="text-center mt-8">
          <button type="button" class="btn-secondary text-sm" @click="visibleCount += 18">Show More</button>
        </div>
      </div>
    </section>

    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Still Have Questions?</h2>
        <p class="text-titanium-400 mb-8">Our technical team can help with grade selection, certificates, export packing and drawing-based parts.</p>
        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <NuxtLink :to="localePath('/contact')" class="btn-secondary text-sm">Contact Technical Team</NuxtLink>
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary text-sm">Get a Quote</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '@cnbjti/types'

const localePath = useLocalePath()
const openItems = ref<Record<string, number[]>>({ 'Ordering and Documentation': [0] })
const searchQuery = ref('')
const visibleCount = ref(18)

const { data: articleData } = await useAsyncData('public-faq-articles', () => publicApi<Article[]>('/public/articles'))

function toggle(category: string, index: number) {
  if (!openItems.value[category]) openItems.value[category] = []
  const currentIndex = openItems.value[category].indexOf(index)
  if (currentIndex === -1) openItems.value[category].push(index)
  else openItems.value[category].splice(currentIndex, 1)
}

function isOpen(category: string, index: number) {
  return openItems.value[category]?.includes(index) ?? false
}

const faqArticles = computed(() => (articleData.value || []).filter((article) => article.category === 'FAQ'))
const filteredFaqArticles = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  if (!query) return faqArticles.value
  return faqArticles.value.filter((article) => {
    const haystack = `${article.title} ${article.excerpt} ${(article.tags || []).join(' ')}`.toLowerCase()
    return haystack.includes(query)
  })
})
const visibleFaqArticles = computed(() => filteredFaqArticles.value.slice(0, visibleCount.value))

watch(searchQuery, () => {
  visibleCount.value = 18
})

const coreFaqCategories = [
  {
    title: 'Ordering and Documentation',
    items: [
      {
        q: 'What information should I include in an RFQ?',
        a: 'Send product form, grade, standard, dimensions, tolerance, quantity, destination country, certificate requirements and drawings when available.',
      },
      {
        q: 'Do you provide material test reports?',
        a: 'Yes. Orders can ship with mill test reports showing heat number, chemical composition, mechanical values and applicable standard.',
      },
      {
        q: 'Can CNBJTI supply small quantities?',
        a: 'Standard stock, cut-to-size material and some machined parts can be quoted in small quantities. Custom production minimums depend on size and process.',
      },
    ],
  },
  {
    title: 'Grade Selection',
    items: [
      {
        q: 'When should I choose Grade 2 instead of Grade 5?',
        a: 'Grade 2 is commercially pure titanium with excellent corrosion resistance and formability. Grade 5 is stronger and better for structural or machined parts.',
      },
      {
        q: 'Which titanium grade is common for medical use?',
        a: 'Grade 23 ELI is common for load-bearing implant applications, while Grade 2 and Grade 4 are also used in medical and dental components.',
      },
      {
        q: 'Which grade is used for seawater service?',
        a: 'Grade 2 is widely used in seawater heat exchangers and piping. Grade 7 may be selected for more demanding corrosion conditions.',
      },
    ],
  },
  {
    title: 'Processing and Export',
    items: [
      {
        q: 'Can you process material before shipment?',
        a: 'Yes. CNBJTI can support cutting, CNC machining, forging, surface treatment and drawing-based custom parts depending on the product.',
      },
      {
        q: 'How is titanium packed for export?',
        a: 'Bars and tubes are bundled and protected, while sheet and plate can be interleaved and packed in export wooden cases or crates.',
      },
      {
        q: 'Can you arrange third-party inspection?',
        a: 'Third-party chemical, mechanical, dimensional or non-destructive testing can be arranged when required by the order.',
      },
    ],
  },
]

useHead({
  title: 'Titanium FAQ - Ordering, Certification and Grade Selection | CNBJTI',
  meta: [
    { name: 'description', content: 'CNBJTI titanium FAQ covering RFQ details, material test reports, titanium grade selection, processing, export packing and buyer questions.' },
  ],
})
</script>
