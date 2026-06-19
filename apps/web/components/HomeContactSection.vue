<template>
  <section class="bg-gradient-to-b from-titanium-50 to-white py-20">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid gap-8 lg:grid-cols-[0.9fr_1.1fr] lg:items-start">
        <div>
          <p class="section-label mb-3">Contact & RFQ</p>
          <h2 class="section-title mb-5">Send Your Titanium Requirement Directly</h2>
          <p class="section-subtitle mb-8">
            Share product form, grade, standard, size, quantity and destination country. Our sales and technical team will respond with pricing, lead time and documentation details.
          </p>

          <div class="grid gap-3 sm:grid-cols-2">
            <a :href="mailtoHref" class="group rounded-lg border border-titanium-200 bg-white p-4 shadow-sm shadow-titanium-200/60 transition-colors hover:border-accent-400">
              <div class="mb-2 text-xs font-semibold uppercase tracking-wide text-titanium-500">Email</div>
              <div class="break-all text-sm font-semibold text-titanium-950 group-hover:text-accent-600">{{ siteConfig.email }}</div>
            </a>
            <a v-if="siteConfig.phone" :href="phoneHref" class="group rounded-lg border border-titanium-200 bg-white p-4 shadow-sm shadow-titanium-200/60 transition-colors hover:border-accent-400">
              <div class="mb-2 text-xs font-semibold uppercase tracking-wide text-titanium-500">Phone</div>
              <div class="text-sm font-semibold text-titanium-950 group-hover:text-accent-600">{{ siteConfig.phone }}</div>
            </a>
            <a v-if="siteConfig.whatsapp" :href="whatsappHref" target="_blank" class="group rounded-lg border border-green-500/30 bg-white p-4 shadow-sm shadow-titanium-200/60 transition-colors hover:border-green-500/60">
              <div class="mb-2 text-xs font-semibold uppercase tracking-wide text-titanium-500">WhatsApp</div>
              <div class="text-sm font-semibold text-titanium-950 group-hover:text-green-700">{{ siteConfig.whatsapp }}</div>
            </a>
            <div class="rounded-lg border border-titanium-200 bg-white p-4 shadow-sm shadow-titanium-200/60">
              <div class="mb-2 text-xs font-semibold uppercase tracking-wide text-titanium-500">Location</div>
              <div class="text-sm font-semibold text-titanium-950">{{ locationText }}</div>
            </div>
          </div>

          <div class="mt-6 flex flex-wrap gap-3">
            <NuxtLink :to="localizedPath('/request-a-quote')" class="btn-primary">Request a Quote</NuxtLink>
            <NuxtLink :to="localizedPath('/contact')" class="btn-secondary">Contact Page</NuxtLink>
          </div>
        </div>

        <div class="grid gap-5 md:grid-cols-[0.85fr_1.15fr]">
          <div class="rounded-lg border border-titanium-200 bg-white p-5 shadow-sm shadow-titanium-200/60">
            <div class="mb-4 rounded-lg border border-titanium-200 bg-titanium-50 p-3">
              <img :src="wechatQrSrc" alt="WeChat contact QR code" class="aspect-square w-full object-contain" loading="lazy">
            </div>
            <div class="text-sm font-semibold text-titanium-950">WeChat / Contact QR</div>
            <div class="mt-1 truncate text-xs text-titanium-500">{{ wechatDisplay }}</div>
          </div>

          <div class="rounded-lg border border-titanium-200 bg-white p-5 shadow-sm shadow-titanium-200/60">
            <div class="mb-4 flex items-center justify-between gap-3 border-b border-titanium-200 pb-4">
              <div>
                <div class="text-sm font-semibold text-titanium-950">What to include in your RFQ</div>
                <div class="mt-1 text-xs text-titanium-500">A complete request helps us quote faster.</div>
              </div>
              <span class="rounded bg-accent-500/10 px-2 py-1 text-xs font-mono font-semibold text-accent-600">24H</span>
            </div>
            <div class="space-y-3">
              <div v-for="item in rfqChecklist" :key="item" class="flex items-start gap-3">
                <div class="mt-0.5 flex h-5 w-5 flex-shrink-0 items-center justify-center rounded-full bg-accent-500/10 text-accent-600">
                  <svg class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
                </div>
                <div class="text-sm text-titanium-700">{{ item }}</div>
              </div>
            </div>
            <div class="mt-5 rounded-lg bg-titanium-50 p-4 text-xs leading-relaxed text-titanium-600">
              Drawings and attachments can be uploaded from the RFQ page. PDF, DWG, DXF, STEP, XLS, JPG and ZIP files are supported.
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const { siteConfig, whatsappHref, phoneHref, mailtoHref, localizedPath } = await useSiteRuntime()

const locationText = computed(() => [siteConfig.value.city, siteConfig.value.country].filter(Boolean).join(', ') || 'Baoji, China')
const contactName = computed(() => `${siteConfig.value.siteName || 'CNBJTI'} Sales Team`)
const wechatDisplay = computed(() => siteConfig.value.socialLinks?.wechat || siteConfig.value.whatsapp || siteConfig.value.phone || siteConfig.value.email)
const contactVcard = computed(() => {
  const lines = [
    'BEGIN:VCARD',
    'VERSION:3.0',
    `FN:${contactName.value}`,
    `ORG:${siteConfig.value.siteName || 'CNBJTI'}`,
    'TITLE:Sales Manager',
  ]
  if (siteConfig.value.phone) lines.push(`TEL;TYPE=WORK,VOICE:${siteConfig.value.phone}`)
  if (siteConfig.value.email) lines.push(`EMAIL:${siteConfig.value.email}`)
  if (siteConfig.value.address || siteConfig.value.city || siteConfig.value.country) {
    lines.push(`ADR;TYPE=WORK:;;${siteConfig.value.address || ''};${siteConfig.value.city || ''};;${siteConfig.value.country || ''}`)
  }
  lines.push('END:VCARD')
  return lines.join('\n')
})
const wechatQrPayload = computed(() => siteConfig.value.socialLinks?.wechat
  ? `WeChat: ${siteConfig.value.socialLinks.wechat}\n${contactVcard.value}`
  : contactVcard.value,
)
const wechatQrSrc = computed(() => siteConfig.value.socialLinks?.wechatQrImage || `/contact-qr.svg?data=${encodeURIComponent(wechatQrPayload.value)}`)
const rfqChecklist = [
  'Product form, grade and standard',
  'Size, tolerance, quantity and surface finish',
  'Destination country, deadline and certificate requirements',
  'Drawings for CNC, forging, fittings or custom parts',
]
</script>
