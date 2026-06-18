<template>
  <div>
    <HeroSection />
    <TrustStrip />
    <ProductCategoriesSection />
    <ProcessingSection />
    <GradesSection />
    <IndustriesSection />
    <QualitySection />
    <QuickRfqSection />
    <ResourcesSection />
  </div>
</template>

<script setup lang="ts">
import { qinghangPageAssets } from '~/utils/qinghangPageAssets'

const config = useRuntimeConfig()
const siteUrl = String(config.public.siteUrl || 'https://cnbjti.com').replace(/\/$/, '')

useHead({
  title: 'CNBJTI - Baoji Titanium Materials & Custom Processing for Global Buyers',
  meta: [
    { name: 'description', content: 'Titanium bar, plate, tube, wire, forgings, fittings, anodes and CNC parts from Baoji, China. ASTM/ASME/AMS certified. MTR available. Small MOQ. Global shipping.' },
    { property: 'og:title', content: 'CNBJTI - Baoji Titanium Materials' },
    { property: 'og:description', content: 'Titanium materials and custom processing for global buyers. ASTM certified, MTR available.' },
    { property: 'og:url', content: siteUrl },
    { property: 'og:image', content: qinghangPageAssets.sheetFactory.url },
    { name: 'twitter:title', content: 'CNBJTI - Baoji Titanium Materials' },
    { name: 'twitter:description', content: 'Titanium materials and custom processing for global buyers. ASTM certified, MTR available.' },
    { name: 'twitter:image', content: qinghangPageAssets.sheetFactory.url },
  ],
  link: [
    { rel: 'canonical', href: siteUrl },
  ],
})
</script>
