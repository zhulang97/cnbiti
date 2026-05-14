<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-20" />
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Terms of Use</span>
        </nav>
        <h1 class="section-title mb-4">Terms of Use</h1>
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
const localePath = useLocalePath()

const sections = [
  { title: '1. Acceptance of Terms', content: 'By accessing and using the CNBJTI website, you accept and agree to be bound by these Terms of Use. If you do not agree to these terms, please do not use our website.' },
  { title: '2. Use of Website', content: 'This website is provided for informational purposes and to facilitate business inquiries. You may not use this website for any unlawful purpose or in any way that could damage, disable, or impair the website.' },
  { title: '3. Intellectual Property', content: 'All content on this website, including text, graphics, logos, and images, is the property of CNBJTI and is protected by applicable intellectual property laws. You may not reproduce or distribute content without our written permission.' },
  { title: '4. Product Information', content: 'While we strive to provide accurate product specifications and pricing information, we reserve the right to correct errors and update information without notice. All orders are subject to written confirmation.' },
  { title: '5. Limitation of Liability', content: 'CNBJTI shall not be liable for any indirect, incidental, or consequential damages arising from your use of this website or reliance on information provided herein.' },
  { title: '6. Governing Law', content: 'These Terms of Use shall be governed by the laws of the People\'s Republic of China. Any disputes shall be resolved through negotiation or, if necessary, through arbitration in Baoji, Shaanxi.' },
  { title: '7. Changes to Terms', content: 'We reserve the right to modify these Terms of Use at any time. Continued use of the website after changes constitutes acceptance of the new terms.' },
]

useHead({ title: 'Terms of Use | CNBJTI' })
</script>
