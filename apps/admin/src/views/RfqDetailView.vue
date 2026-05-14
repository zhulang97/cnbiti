<template>
  <div v-if="rfq" class="max-w-4xl space-y-5">
    <button @click="router.back()" class="flex items-center gap-1.5 text-admin-400 hover:text-admin-200 text-sm transition-colors">
      <el-icon><ArrowLeft /></el-icon>
      返回询盘列表
    </button>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-6">
      <div class="flex items-start justify-between gap-4 mb-5">
        <div class="min-w-0">
          <div class="flex items-center gap-3 mb-1">
            <span class="font-mono text-accent-400 text-sm">{{ rfq.id }}</span>
            <span class="text-xs px-2 py-0.5 rounded-full font-medium capitalize" :class="statusClass(rfq.status)">{{ statusText(rfq.status) }}</span>
          </div>
          <h2 class="text-admin-100 font-bold text-xl truncate">{{ rfq.company }}</h2>
          <p class="text-admin-500 text-sm mt-0.5">{{ rfq.country }} · {{ rfq.email }}</p>
        </div>
        <div class="flex-shrink-0">
          <el-select :model-value="rfq.status" size="small" @change="(v: RfqStatus) => void changeStatus(v)">
            <el-option label="新询盘" value="new" />
            <el-option label="跟进中" value="in_progress" />
            <el-option label="已报价" value="quoted" />
            <el-option label="已成交" value="won" />
            <el-option label="已失单" value="lost" />
          </el-select>
        </div>
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div v-for="field in fields" :key="field.label" class="bg-admin-800/50 rounded-lg p-3">
          <div class="text-admin-500 text-xs mb-1">{{ field.label }}</div>
          <div class="text-admin-200 text-sm font-medium break-words">{{ field.value }}</div>
        </div>
      </div>
    </div>

    <div class="grid lg:grid-cols-3 gap-5">
      <div class="lg:col-span-2 space-y-5">
        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-6">
          <h3 class="text-admin-300 font-semibold text-sm mb-3">客户留言</h3>
          <p class="text-admin-400 text-sm leading-relaxed whitespace-pre-line">{{ rfq.message }}</p>
        </div>

        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-6">
          <h3 class="text-admin-300 font-semibold text-sm mb-3">附件</h3>
          <div v-if="rfq.attachments?.length" class="space-y-2">
            <a
              v-for="asset in rfq.attachments"
              :key="asset.id"
              :href="asset.url"
              target="_blank"
              class="flex items-center justify-between gap-3 rounded-lg bg-admin-800/60 px-3 py-2 text-sm text-admin-300 hover:text-accent-400"
            >
              <span class="flex items-center gap-2 min-w-0">
                <el-icon><Paperclip /></el-icon>
                <span class="truncate">{{ asset.filename }}</span>
              </span>
              <span class="text-admin-600 text-xs">{{ formatSize(asset.size) }}</span>
            </a>
          </div>
          <div v-else class="text-admin-500 text-sm">暂无附件</div>
        </div>

        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-6">
          <h3 class="text-admin-300 font-semibold text-sm mb-3">内部备注</h3>
          <el-input
            v-model="notes"
            type="textarea"
            :rows="5"
            placeholder="添加内部备注、跟进记录、报价信息..."
            resize="none"
          />
          <div class="flex justify-end mt-3">
            <el-button type="primary" size="small" :loading="savingNotes" @click="void saveNotes()">保存备注</el-button>
          </div>
        </div>
      </div>

      <div class="space-y-5">
        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
          <h3 class="text-admin-300 font-semibold text-sm mb-4">快捷操作</h3>
          <div class="space-y-2">
            <el-button type="primary" plain class="w-full justify-start" @click="openQuoteEmail">
              <el-icon class="mr-1"><Message /></el-icon>
              发送报价邮件
            </el-button>
            <el-button plain class="!ml-0 w-full justify-start" @click="void copyEmail()">
              <el-icon class="mr-1"><CopyDocument /></el-icon>
              复制邮箱
            </el-button>
          </div>
        </div>

        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
          <h3 class="text-admin-300 font-semibold text-sm mb-4">处理时间线</h3>
          <div class="space-y-4">
            <div v-for="item in timeline" :key="item.label + item.time" class="flex gap-3">
              <div class="mt-0.5 w-7 h-7 rounded-full bg-admin-700 flex items-center justify-center text-admin-400">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <div class="min-w-0">
                <div class="text-admin-200 text-sm font-medium">{{ item.label }}</div>
                <div class="text-admin-500 text-xs mt-0.5">{{ item.time }}</div>
                <p v-if="item.detail" class="text-admin-500 text-xs mt-1 line-clamp-3 whitespace-pre-line">{{ item.detail }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="text-admin-500 text-sm">询盘不存在</div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { Rfq, RfqStatus } from '@/stores/mock'

const mock = useMockStore()
const route = useRoute()
const router = useRouter()

const rfq = computed<Rfq | undefined>(() => mock.rfqs.find((r: Rfq) => r.id === route.params.id))
const notes = ref('')
const savingNotes = ref(false)

watch(rfq, value => {
  notes.value = value?.notes || ''
}, { immediate: true })

const fields = computed<{ label: string; value: string }[]>(() => rfq.value ? [
  { label: '产品', value: rfq.value.product },
  { label: '牌号', value: rfq.value.grade },
  { label: '数量', value: rfq.value.qty },
  { label: '日期', value: rfq.value.createdAt },
  { label: '联系人邮箱', value: rfq.value.email },
  { label: '电话', value: rfq.value.phone || '—' },
  { label: '国家', value: rfq.value.country },
  { label: '附件数', value: String(rfq.value.attachments?.length || 0) },
] : [])

const timeline = computed(() => {
  if (!rfq.value) return []
  return [
    { label: '收到询盘', time: rfq.value.createdAt, icon: 'Document' },
    rfq.value.statusUpdatedAt ? { label: `状态：${statusText(rfq.value.status)}`, time: formatDateTime(rfq.value.statusUpdatedAt), icon: 'RefreshRight' } : null,
    rfq.value.notesUpdatedAt ? { label: '更新内部备注', time: formatDateTime(rfq.value.notesUpdatedAt), icon: 'EditPen', detail: rfq.value.notes } : null,
  ].filter(Boolean) as { label: string; time: string; icon: string; detail?: string }[]
})

async function changeStatus(status: RfqStatus) {
  if (!rfq.value) return
  await mock.updateRfqStatus(rfq.value.id, status)
  ElMessage.success('状态已更新')
}

async function saveNotes() {
  if (!rfq.value) return
  savingNotes.value = true
  try {
    await mock.updateRfqNotes(rfq.value.id, notes.value)
    ElMessage.success('备注已保存')
  } finally {
    savingNotes.value = false
  }
}

async function copyEmail() {
  if (!rfq.value) return
  await navigator.clipboard.writeText(rfq.value.email)
  ElMessage.success('邮箱已复制')
}

function openQuoteEmail() {
  if (!rfq.value) return
  const subject = `Quotation for ${rfq.value.id} - ${rfq.value.product}`
  const body = [
    `Dear ${rfq.value.company},`,
    '',
    `Thank you for your inquiry ${rfq.value.id}.`,
    `Product: ${rfq.value.product}`,
    `Grade: ${rfq.value.grade}`,
    `Quantity: ${rfq.value.qty}`,
    '',
    'Please find our quotation details below.',
  ].join('\n')
  window.location.href = `mailto:${rfq.value.email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`
}

function statusClass(status: RfqStatus) {
  const map: Record<RfqStatus, string> = {
    new: 'bg-accent-500/15 text-accent-400',
    in_progress: 'bg-yellow-500/15 text-yellow-400',
    quoted: 'bg-purple-500/15 text-purple-400',
    won: 'bg-green-500/15 text-green-400',
    lost: 'bg-red-500/15 text-red-400',
  }
  return map[status]
}

const statusLabel: Record<RfqStatus, string> = {
  new: '新询盘',
  in_progress: '跟进中',
  quoted: '已报价',
  won: '已成交',
  lost: '已失单',
}

function statusText(status: RfqStatus) {
  return statusLabel[status]
}

function formatDateTime(value: string) {
  return value.replace('T', ' ').slice(0, 16)
}

function formatSize(size: number) {
  if (!size) return ''
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${Math.round(size / 1024)} KB`
  return `${(size / 1024 / 1024).toFixed(1)} MB`
}
</script>
