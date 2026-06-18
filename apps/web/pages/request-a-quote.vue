<template>
  <div class="min-h-screen bg-titanium-950 pt-24 pb-20">
    <div class="mx-auto mb-12 max-w-7xl px-4 sm:px-6 lg:px-8">
      <nav class="mb-8 flex items-center gap-2 text-sm text-titanium-500">
        <NuxtLink to="/" class="transition-colors hover:text-titanium-300">Home</NuxtLink>
        <svg class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
        </svg>
        <span class="text-titanium-300">Request a Quote</span>
      </nav>
    </div>

    <QuickRfqSection />

    <div class="mx-auto mt-12 max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="grid gap-4 md:grid-cols-3">
        <div v-for="info in sideInfo" :key="info.title" class="rounded-xl border border-titanium-800/50 bg-titanium-900/50 p-5 text-center">
          <div class="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full border border-accent-500/30 bg-accent-500/10 text-sm font-bold tracking-wider text-accent-400">
            {{ info.icon }}
          </div>
          <div class="mb-1 text-sm font-semibold text-white">{{ info.title }}</div>
          <div class="text-xs text-titanium-500">{{ info.desc }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const sideInfo = [
  { icon: '24H', title: '24-Hour Response', desc: 'We review every RFQ and respond within one business day' },
  { icon: 'NDA', title: 'Confidential', desc: 'Your drawings and specifications are kept strictly confidential' },
  { icon: 'MOQ', title: 'No Minimum', desc: 'Small MOQ welcome - from prototype quantities to bulk orders' },
]

useHead({
  title: 'Request a Quote - CNBJTI',
  meta: [{ name: 'description', content: 'Request a quote for titanium materials. We respond within 24 hours.' }],
})
</script>
