<template>
  <section class="py-24 bg-gradient-to-b from-titanium-950 via-steel-950/20 to-titanium-950">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="max-w-3xl mx-auto">
        <div class="text-center mb-10">
          <p class="section-label mb-3">Request a Quote</p>
          <h2 class="section-title mb-4">Tell Us What You Need</h2>
          <p class="section-subtitle">Fill in your requirements and we will respond within 24 hours with pricing and availability.</p>
        </div>
        <div class="card p-8">
          <div v-if="!submitted">
            <div class="grid md:grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Product Type *</label>
                <select v-model="form.productType" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors">
                  <option value="">Select product type</option>
                  <option v-for="p in productTypes" :key="p" :value="p">{{ p }}</option>
                </select>
              </div>
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Grade</label>
                <select v-model="form.grade" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors">
                  <option value="">Select grade</option>
                  <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
                </select>
              </div>
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Quantity *</label>
                <input v-model="form.quantity" type="text" placeholder="e.g. 500 kg or 100 pcs" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm placeholder-titanium-600 focus:outline-none focus:border-accent-500 transition-colors" />
              </div>
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Dimensions / Spec</label>
                <input v-model="form.dimensions" type="text" placeholder="e.g. Dia 20mm x 1000mm" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm placeholder-titanium-600 focus:outline-none focus:border-accent-500 transition-colors" />
              </div>
            </div>
            <div class="mb-4">
              <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Additional Requirements</label>
              <textarea v-model="form.message" rows="3" placeholder="Surface finish, tolerance, certificate requirements, delivery time..." class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm placeholder-titanium-600 focus:outline-none focus:border-accent-500 transition-colors resize-none" />
            </div>
            <div class="mb-4">
              <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Drawings / Attachments</label>
              <div class="flex flex-wrap items-center gap-3">
                <button
                  type="button"
                  class="inline-flex items-center gap-2 rounded-lg bg-accent-500 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-accent-600"
                  @click="openAttachmentPicker"
                >
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 16.5V19a2 2 0 002 2h12a2 2 0 002-2v-2.5M12 4v11m0-11l-4 4m4-4 4 4" />
                  </svg>
                  Choose Files
                </button>
                <span class="text-xs text-titanium-500">
                  {{ form.attachments.length ? `${form.attachments.length} file${form.attachments.length > 1 ? 's' : ''} uploaded` : 'No files uploaded yet' }}
                </span>
              </div>
              <input ref="attachmentInput" type="file" multiple accept=".pdf,.dwg,.dxf,.step,.stp,.xls,.xlsx,.jpg,.jpeg,.png,.zip" class="sr-only" @change="uploadAttachments" />
              <p class="mt-2 text-xs text-titanium-500">PDF, DWG, DXF, STEP, STP, XLS, XLSX, JPG, PNG, ZIP. Max 30MB per file.</p>
              <div v-if="uploadingAttachments" class="mt-2 text-xs text-accent-400">Uploading files...</div>
              <div v-if="form.attachments.length" class="mt-3 flex flex-wrap gap-2">
                <span v-for="asset in form.attachments" :key="asset.id" class="inline-flex items-center gap-2 rounded-full bg-titanium-800 px-3 py-1 text-xs text-titanium-300">
                  {{ asset.filename }}
                  <button type="button" class="text-titanium-500 hover:text-red-400" aria-label="Remove file" @click="removeAttachment(asset.id)">x</button>
                </span>
              </div>
            </div>
            <div class="grid md:grid-cols-3 gap-4 mb-6">
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Your Name *</label>
                <input v-model="form.name" type="text" placeholder="John Smith" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm placeholder-titanium-600 focus:outline-none focus:border-accent-500 transition-colors" />
              </div>
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Company</label>
                <input v-model="form.company" type="text" placeholder="Company name" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm placeholder-titanium-600 focus:outline-none focus:border-accent-500 transition-colors" />
              </div>
              <div>
                <label class="block text-titanium-400 text-xs font-medium mb-1.5 uppercase tracking-wide">Email *</label>
                <input v-model="form.email" type="email" placeholder="you@company.com" class="w-full bg-titanium-800 border border-titanium-700 rounded-lg px-3 py-2.5 text-white text-sm placeholder-titanium-600 focus:outline-none focus:border-accent-500 transition-colors" />
              </div>
            </div>
            <div v-if="error" class="mb-4 p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm">{{ error }}</div>
            <button @click="submit" :disabled="loading" class="btn-primary w-full justify-center py-3.5 text-base">
              {{ loading ? 'Submitting...' : 'Submit RFQ' }}
            </button>
            <p class="text-titanium-600 text-xs text-center mt-3">We respond within 24 hours. Your information is kept confidential.</p>
          </div>
          <div v-else class="text-center py-8">
            <div class="w-16 h-16 bg-green-500/20 border border-green-500/30 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </div>
            <h3 class="text-white font-bold text-xl mb-2">RFQ Submitted Successfully</h3>
            <p class="text-titanium-400 mb-2">Your RFQ <span class="text-accent-400 font-mono font-semibold">{{ rfqNo }}</span> has been received.</p>
            <p class="text-titanium-500 text-sm">We will review your request and respond within 24 hours.</p>
            <button @click="reset" class="mt-6 btn-secondary text-sm">Submit Another RFQ</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import type { ApiResponse, MediaAsset, RfqResponse } from '@cnbjti/types'

const config = useRuntimeConfig()
const form = reactive<{ productType: string; grade: string; quantity: string; dimensions: string; message: string; name: string; company: string; email: string; attachments: MediaAsset[] }>({
  productType: '',
  grade: '',
  quantity: '',
  dimensions: '',
  message: '',
  name: '',
  company: '',
  email: '',
  attachments: [],
})
const loading = ref(false)
const uploadingAttachments = ref(false)
const submitted = ref(false)
const error = ref('')
const rfqNo = ref('')
const attachmentInput = ref<HTMLInputElement | null>(null)
const productTypes = ['Titanium Bar & Rod', 'Titanium Sheet / Plate', 'Titanium Tube & Pipe', 'Titanium Wire', 'Titanium Forgings', 'Titanium Fasteners', 'CNC Titanium Parts', 'Other']
const gradeOptions = ['Grade 1', 'Grade 2', 'Grade 3', 'Grade 4', 'Grade 5 (Ti-6Al-4V)', 'Grade 7', 'Grade 9', 'Grade 12', 'Grade 23 (ELI)', 'Not sure']

function openAttachmentPicker() {
  attachmentInput.value?.click()
}

async function submit() {
  error.value = ''
  if (!form.productType || !form.quantity || !form.name || !form.email) { error.value = 'Please fill in all required fields.'; return }
  if (!/^[^@]+@[^@]+.[^@]+$/.test(form.email)) { error.value = 'Please enter a valid email address.'; return }
  loading.value = true
  try {
    const result = await publicApi<RfqResponse>('/public/rfqs', {
      method: 'POST',
      body: {
        productType: form.productType,
        grade: form.grade,
        quantity: form.quantity,
        dimensions: form.dimensions,
        message: form.message,
        name: form.name,
        company: form.company,
        email: form.email,
        attachments: form.attachments,
      },
    })
    rfqNo.value = result.rfqNo
    submitted.value = true
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Unable to submit RFQ. Please try again.'
  } finally {
    loading.value = false
  }
}
async function uploadAttachments(event: Event) {
  const input = event.target as HTMLInputElement
  const files = Array.from(input.files || [])
  if (!files.length) return
  uploadingAttachments.value = true
  error.value = ''
  try {
    for (const file of files) {
      const body = new FormData()
      body.append('file', file)
      const response = await $fetch<ApiResponse<MediaAsset>>(`${config.public.apiBase}/public/uploads`, {
        method: 'POST',
        body,
      })
      if (response.code !== 'OK') throw new Error(response.message || 'Unable to upload attachment')
      form.attachments.push(response.data)
    }
    input.value = ''
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Unable to upload attachment.'
  } finally {
    uploadingAttachments.value = false
  }
}
function removeAttachment(id: string) {
  form.attachments = form.attachments.filter(asset => asset.id !== id)
}
function reset() {
  Object.assign(form, { productType: '', grade: '', quantity: '', dimensions: '', message: '', name: '', company: '', email: '', attachments: [] })
  submitted.value = false; rfqNo.value = ''
}
</script>
