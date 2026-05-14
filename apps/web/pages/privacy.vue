<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-20 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-20" />
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Privacy Policy</span>
        </nav>
        <h1 class="section-title mb-4">Privacy Policy</h1>
        <p class="text-titanium-500 text-sm mb-12">Last updated: January 2026</p>
        <div class="prose-titanium space-y-8">
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

const { siteConfig } = await useSiteRuntime()

const sections = computed(() => [
  { title: '1. Information We Collect', content: 'We collect information you provide directly to us, such as when you submit a request for quotation (RFQ), contact us, or subscribe to our newsletter. This includes your name, company name, email address, phone number, and the details of your inquiry.' },
  { title: '2. How We Use Your Information', content: 'We use the information we collect to respond to your inquiries, provide quotations, process orders, send transactional communications, and improve our services. We do not sell your personal information to third parties.' },
  { title: '3. Information Sharing', content: 'We may share your information with trusted third-party service providers who assist us in operating our website and conducting our business, subject to confidentiality agreements. We may also disclose information when required by law.' },
  { title: '4. Data Security', content: 'We implement appropriate technical and organizational measures to protect your personal information against unauthorized access, alteration, disclosure, or destruction. However, no method of transmission over the Internet is 100% secure.' },
  { title: '5. Cookies', content: 'Our website uses cookies to enhance your browsing experience. You can control cookie settings through your browser. See our Cookie Policy for more details.' },
  { title: '6. Your Rights', content: `You have the right to access, correct, or delete your personal information. To exercise these rights, please contact us at ${siteConfig.value.email}.` },
  { title: '7. Contact Us', content: `If you have questions about this Privacy Policy, please contact us at: ${siteConfig.value.siteName}, ${siteConfig.value.address}, ${siteConfig.value.city}, ${siteConfig.value.country}. Email: ${siteConfig.value.email}` },
])

useHead({ title: 'Privacy Policy | CNBJTI' })
</script>
