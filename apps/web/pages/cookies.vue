<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-20" />
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="'/'" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Cookie Policy</span>
        </nav>
        <h1 class="section-title mb-4">Cookie Policy</h1>
        <p class="text-titanium-500 text-sm mb-12">Last updated: January 2026</p>
        <div class="space-y-8">
          <div v-for="section in sections" :key="section.title">
            <h2 class="text-white font-semibold text-lg mb-3">{{ section.title }}</h2>
            <p class="text-titanium-400 text-sm leading-relaxed">{{ section.content }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">

const { siteConfig } = await useSiteRuntime()

const sections = computed(() => [
  { title: 'What Are Cookies?', content: 'Cookies are small text files stored on your device when you visit a website. They help websites remember your preferences and improve your browsing experience.' },
  { title: 'How We Use Cookies', content: 'We use essential cookies to ensure the website functions correctly, and analytics cookies to understand how visitors use our site. We do not use advertising or tracking cookies.' },
  { title: 'Essential Cookies', content: 'These cookies are necessary for the website to function and cannot be disabled. They include session cookies for language preferences and form submissions.' },
  { title: 'Analytics Cookies', content: 'We may use analytics tools to understand website traffic and usage patterns. This data is aggregated and anonymous. You can opt out through your browser settings.' },
  { title: 'Managing Cookies', content: 'You can control and delete cookies through your browser settings. Note that disabling cookies may affect the functionality of some parts of our website.' },
  { title: 'Contact', content: `If you have questions about our use of cookies, please contact us at ${siteConfig.value.email}.` },
])

useHead({ title: 'Cookie Policy | CNBJTI' })
</script>
