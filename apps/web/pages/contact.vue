<template>
  <div class="pt-32 pb-20 min-h-screen bg-titanium-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-10">
        <NuxtLink :to="'/'" class="hover:text-titanium-800 transition-colors">Home</NuxtLink>
        <span>/</span>
        <span class="text-titanium-700">Contact</span>
      </nav>
      <div class="grid lg:grid-cols-2 gap-16">
        <div>
          <p class="section-label mb-3">Contact Us</p>
          <h1 class="section-title mb-5">Get in Touch</h1>
          <p class="section-subtitle mb-8">Have a question about titanium materials, specifications or pricing? Our technical team is ready to help.</p>
          <div class="space-y-4">
            <a :href="mailtoHref" class="flex items-center gap-4 p-4 bg-white border border-titanium-200 rounded-xl shadow-sm shadow-titanium-200/60 hover:border-accent-500/40 transition-colors group">
              <div class="w-10 h-10 bg-accent-500/10 border border-accent-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                <svg class="w-5 h-5 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
              </div>
              <div>
                <div class="text-titanium-400 text-xs uppercase tracking-wide mb-0.5">Email</div>
                <div class="text-titanium-950 font-medium group-hover:text-accent-600 transition-colors">{{ siteConfig.email }}</div>
              </div>
            </a>
            <a v-if="siteConfig.phone" :href="phoneHref" class="flex items-center gap-4 p-4 bg-white border border-titanium-200 rounded-xl shadow-sm shadow-titanium-200/60 hover:border-accent-500/40 transition-colors group">
              <div class="w-10 h-10 bg-accent-500/10 border border-accent-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                <svg class="w-5 h-5 text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 002.25-2.25v-1.372c0-.516-.351-.966-.852-1.091l-4.423-1.106a1.125 1.125 0 00-1.173.417l-.97 1.293a1.125 1.125 0 01-1.21.38 12.035 12.035 0 01-7.143-7.143 1.125 1.125 0 01.38-1.21l1.293-.97a1.125 1.125 0 00.417-1.173L6.963 3.102A1.125 1.125 0 005.872 2.25H4.5A2.25 2.25 0 002.25 4.5v2.25z" /></svg>
              </div>
              <div>
                <div class="text-titanium-400 text-xs uppercase tracking-wide mb-0.5">Phone</div>
                <div class="text-titanium-950 font-medium group-hover:text-accent-600 transition-colors">{{ siteConfig.phone }}</div>
              </div>
            </a>
            <a v-if="siteConfig.whatsapp" :href="whatsappHref" target="_blank" class="flex items-center gap-4 p-4 bg-white border border-titanium-200 rounded-xl shadow-sm shadow-titanium-200/60 hover:border-green-500/40 transition-colors group">
              <div class="w-10 h-10 bg-green-500/10 border border-green-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                <svg class="w-5 h-5 text-green-400" viewBox="0 0 24 24" fill="currentColor"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/></svg>
              </div>
              <div>
                <div class="text-titanium-400 text-xs uppercase tracking-wide mb-0.5">WhatsApp</div>
                <div class="text-titanium-950 font-medium group-hover:text-green-700 transition-colors">{{ siteConfig.whatsapp }}</div>
              </div>
            </a>
          </div>
        </div>
        <div class="card p-8">
          <h2 class="text-titanium-950 font-bold text-xl mb-6">Send a Message</h2>
          <div v-if="!sent" class="space-y-4">
            <div>
              <label class="form-label">Name *</label>
              <input v-model="form.name" type="text" placeholder="Your name" class="form-field" />
            </div>
            <div>
              <label class="form-label">Email *</label>
              <input v-model="form.email" type="email" placeholder="you@company.com" class="form-field" />
            </div>
            <div class="grid md:grid-cols-2 gap-4">
              <div>
                <label class="form-label">Company</label>
                <input v-model="form.company" type="text" placeholder="Company name" class="form-field" />
              </div>
              <div>
                <label class="form-label">Subject</label>
                <input v-model="form.subject" type="text" placeholder="Titanium sheet inquiry" class="form-field" />
              </div>
            </div>
            <div>
              <label class="form-label">Message *</label>
              <textarea v-model="form.message" rows="5" placeholder="Tell us about your requirements..." class="form-field resize-none" />
            </div>
            <div v-if="error" class="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm">{{ error }}</div>
            <button @click="send" :disabled="loading" class="btn-primary w-full justify-center py-3">{{ loading ? "Sending..." : "Send Message" }}</button>
          </div>
          <div v-else class="text-center py-8">
            <div class="w-14 h-14 bg-green-500/20 border border-green-500/30 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-7 h-7 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </div>
            <h3 class="text-titanium-950 font-bold text-lg mb-2">Message Sent</h3>
            <p class="text-accent-400 text-sm font-mono mb-2">{{ messageId }}</p>
            <p class="text-titanium-400 text-sm">We will get back to you within 24 hours.</p>
          </div>
        </div>
      </div>

      <section class="mt-20">
        <div class="text-center mb-8">
          <p class="section-label mb-3">Instant Contact</p>
          <h2 class="text-2xl md:text-3xl font-display font-semibold text-titanium-950">Do you want to know more about our products?</h2>
        </div>

        <div class="grid gap-6 lg:grid-cols-[0.9fr_1fr_1fr]">
          <div class="bg-white border border-titanium-200 rounded-xl p-5 shadow-sm shadow-titanium-200/60">
            <div class="aspect-[4/3] rounded-lg border border-titanium-200 bg-titanium-50 flex items-center justify-center overflow-hidden mb-6">
              <img v-if="contactCardImage" :src="contactCardImage" alt="Contact card" class="w-full h-full object-contain" loading="lazy">
              <div v-else class="w-24 h-24 rounded-full bg-accent-500/15 border border-accent-500/30 flex items-center justify-center">
                <span class="font-display text-3xl font-bold text-accent-500">Ti</span>
              </div>
            </div>
            <p class="text-titanium-950 text-lg font-semibold mb-6">Contact Me</p>
            <div class="space-y-4 text-sm">
              <p class="text-titanium-600"><span class="text-titanium-950">{{ contactName }}</span><span class="text-titanium-500"> | </span>Sales Manager</p>
              <p class="text-titanium-600">E-mail: <a :href="mailtoHref" class="text-accent-600 hover:text-accent-500">{{ siteConfig.email }}</a></p>
              <p v-if="siteConfig.phone" class="text-titanium-600">Phone: <a :href="phoneHref" class="text-titanium-950 hover:text-accent-600">{{ siteConfig.phone }}</a><span class="text-titanium-500"> (WhatsApp/WeChat)</span></p>
            </div>
          </div>

          <div class="bg-white border border-accent-500/30 rounded-xl p-5 shadow-sm shadow-titanium-200/60">
            <div class="aspect-square rounded-lg bg-white p-4 flex items-center justify-center">
              <img :src="wechatQrSrc" alt="WeChat contact QR code" class="w-full h-full object-contain" loading="lazy">
            </div>
            <div class="mt-5 flex items-center justify-between gap-4">
              <div>
                <div class="text-titanium-950 text-lg font-medium">WeChat</div>
                <div class="text-titanium-500 text-xs mt-1 truncate">{{ wechatDisplay }}</div>
              </div>
              <span class="px-3 py-1 rounded-full bg-accent-500/10 border border-accent-500/30 text-accent-600 text-xs">QR</span>
            </div>
          </div>

          <div class="bg-white border border-green-500/30 rounded-xl p-5 shadow-sm shadow-titanium-200/60">
            <div class="aspect-square rounded-lg bg-white p-4 flex items-center justify-center">
              <img :src="whatsappQrSrc" alt="WhatsApp contact QR code" class="w-full h-full object-contain" loading="lazy">
            </div>
            <div class="mt-5 flex items-center justify-between gap-4">
              <div>
                <div class="text-titanium-950 text-lg font-medium">WhatsApp</div>
                <div class="text-titanium-500 text-xs mt-1 truncate">{{ siteConfig.whatsapp || siteConfig.phone }}</div>
              </div>
              <span class="px-3 py-1 rounded-full bg-green-500/10 border border-green-500/30 text-green-300 text-xs">QR</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>
<script setup lang="ts">
const { siteConfig, whatsappHref, phoneHref, mailtoHref } = await useSiteRuntime()
interface ContactResponse {
  id: string
  message: string
  estimatedResponseTime: string
}

const form = reactive({ name: "", company: "", email: "", subject: "", message: "" })
const loading = ref(false)
const sent = ref(false)
const error = ref("")
const messageId = ref("")
const contactName = computed(() => `${siteConfig.value.siteName || 'CNBJTI'} Sales Team`)
const contactCardImage = computed(() => siteConfig.value.socialLinks?.contactCardImage || '')
const wechatDisplay = computed(() => siteConfig.value.socialLinks?.wechat || siteConfig.value.phone || siteConfig.value.email)
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
const whatsappQrPayload = computed(() => siteConfig.value.whatsapp ? whatsappHref.value : contactVcard.value)
const wechatQrSrc = computed(() => siteConfig.value.socialLinks?.wechatQrImage || qrSrc(wechatQrPayload.value))
const whatsappQrSrc = computed(() => siteConfig.value.socialLinks?.whatsappQrImage || qrSrc(whatsappQrPayload.value))

function qrSrc(data: string) {
  return `/contact-qr.svg?data=${encodeURIComponent(data)}`
}
async function send() {
  error.value = ""
  if (!form.name || !form.email || !form.message) { error.value = "Please fill in all required fields."; return }
  if (!/^[^@]+@[^@]+.[^@]+$/.test(form.email)) { error.value = "Please enter a valid email address."; return }
  loading.value = true
  try {
    const result = await publicApi<ContactResponse>('/public/contact-messages', {
      method: 'POST',
      body: {
        name: form.name,
        company: form.company,
        email: form.email,
        subject: form.subject,
        message: form.message,
        source: 'contact-page',
      },
    })
    messageId.value = result.id
    sent.value = true
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Unable to send message. Please try again."
  } finally {
    loading.value = false
  }
}
useHead({ title: "Contact Us — CNBJTI", meta: [{ name: "description", content: "Contact CNBJTI for titanium materials, pricing and technical support." }] })
</script>
