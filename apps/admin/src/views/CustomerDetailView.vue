<template>
  <div v-if="customer" class="max-w-5xl space-y-5">
    <button @click="router.back()" class="flex items-center gap-1.5 text-admin-400 hover:text-admin-200 text-sm transition-colors">
      <el-icon><ArrowLeft /></el-icon>
      返回客户列表
    </button>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-6">
      <div class="flex items-start justify-between gap-4">
        <div class="min-w-0">
          <div class="flex items-center gap-3 mb-1">
            <span class="text-xs font-bold text-admin-500">{{ customer.countryCode }}</span>
            <span class="text-xs px-2 py-0.5 rounded-full bg-admin-700 text-admin-400">{{ customer.country }}</span>
          </div>
          <h2 class="text-admin-100 font-bold text-xl truncate">{{ customer.company }}</h2>
          <p class="text-admin-500 text-sm mt-0.5">{{ customer.email }}</p>
        </div>
        <div class="flex gap-2">
          <el-button plain size="small" @click="void copyEmail()">
            <el-icon class="mr-1"><CopyDocument /></el-icon>
            复制邮箱
          </el-button>
          <el-button type="primary" plain size="small" @click="openEmail">
            <el-icon class="mr-1"><Message /></el-icon>
            发邮件
          </el-button>
        </div>
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-5">
        <div v-for="field in fields" :key="field.label" class="bg-admin-800/50 rounded-lg p-3">
          <div class="text-admin-500 text-xs mb-1">{{ field.label }}</div>
          <div class="text-admin-200 text-sm font-medium break-words">{{ field.value }}</div>
        </div>
      </div>
    </div>

    <div class="grid lg:grid-cols-3 gap-5">
      <div class="lg:col-span-2 bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
        <div class="px-5 py-4 border-b border-admin-600/40 flex items-center justify-between">
          <h3 class="text-admin-200 font-semibold text-sm">历史询盘</h3>
          <span class="text-admin-500 text-xs">{{ customer.rfqs.length }} 条</span>
        </div>
        <el-table :data="customer.rfqs" style="width: 100%" row-class-name="cursor-pointer" @row-click="(row: Rfq) => router.push(`/rfq/${row.id}`)">
          <el-table-column prop="id" label="编号" width="140">
            <template #default="{ row }">
              <span class="font-mono text-xs text-accent-400">{{ row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="product" label="产品" min-width="160">
            <template #default="{ row }">
              <div class="text-admin-300 text-sm">{{ row.product }}</div>
              <div class="text-admin-500 text-xs mt-0.5">{{ row.grade }} · {{ row.qty }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="110">
            <template #default="{ row }">
              <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="日期" width="110">
            <template #default="{ row }">
              <span class="text-admin-500 text-xs">{{ row.createdAt }}</span>
            </template>
          </el-table-column>
          <el-table-column label="" width="50" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click.stop="router.push(`/rfq/${row.id}`)">
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="space-y-5">
        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
          <h3 class="text-admin-200 font-semibold text-sm mb-4">客户跟进</h3>
          <el-form label-position="top">
            <el-form-item label="最近联系">
              <el-date-picker v-model="form.lastContact" class="w-full" type="date" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item label="内部备注">
              <el-input
                v-model="form.notes"
                type="textarea"
                :rows="7"
                placeholder="客户偏好、报价策略、付款/交期关注点..."
                resize="none"
              />
            </el-form-item>
          </el-form>
          <el-button type="primary" class="w-full" :loading="saving" @click="void saveCustomer()">保存客户备注</el-button>
          <p v-if="customer.notesUpdatedAt" class="text-admin-600 text-xs mt-3">备注更新于 {{ formatDateTime(customer.notesUpdatedAt) }}</p>
        </div>

        <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
          <h3 class="text-admin-200 font-semibold text-sm mb-4">客户摘要</h3>
          <div class="space-y-3 text-sm">
            <div class="flex justify-between gap-4">
              <span class="text-admin-500">询盘总数</span>
              <span class="text-admin-200 font-medium">{{ customer.rfqCount }}</span>
            </div>
            <div class="flex justify-between gap-4">
              <span class="text-admin-500">最新产品</span>
              <span class="text-admin-200 font-medium text-right">{{ customer.rfqs[0]?.product || '—' }}</span>
            </div>
            <div class="flex justify-between gap-4">
              <span class="text-admin-500">最新状态</span>
              <span class="text-admin-200 font-medium">{{ customer.rfqs[0] ? statusText(customer.rfqs[0].status) : '—' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else-if="loading" class="text-admin-500 text-sm">正在加载客户...</div>
  <div v-else class="text-admin-500 text-sm">客户不存在</div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { CustomerDetail, Rfq, RfqStatus } from '@/stores/mock'

const mock = useMockStore()
const route = useRoute()
const router = useRouter()

const customer = ref<CustomerDetail | null>(null)
const loading = ref(true)
const saving = ref(false)
const form = reactive({ notes: '', lastContact: '' })

const fields = computed(() => customer.value ? [
  { label: '国家', value: customer.value.country },
  { label: '邮箱', value: customer.value.email },
  { label: '询盘数', value: String(customer.value.rfqCount) },
  { label: '最近联系', value: customer.value.lastContact },
] : [])

onMounted(() => {
  void loadCustomer()
})

async function loadCustomer() {
  loading.value = true
  try {
    const id = String(route.params.id)
    const detail = await mock.getCustomerDetail(id)
    customer.value = detail
    form.notes = detail.notes
    form.lastContact = detail.lastContact
  } finally {
    loading.value = false
  }
}

async function saveCustomer() {
  if (!customer.value) return
  saving.value = true
  try {
    const updated = await mock.updateCustomer(customer.value.id, { notes: form.notes, lastContact: form.lastContact })
    customer.value = updated
    form.notes = updated.notes
    form.lastContact = updated.lastContact
    ElMessage.success('客户备注已保存')
  } finally {
    saving.value = false
  }
}

async function copyEmail() {
  if (!customer.value) return
  await navigator.clipboard.writeText(customer.value.email)
  ElMessage.success('邮箱已复制')
}

function openEmail() {
  if (!customer.value) return
  const subject = `Follow up from CNBJTI`
  window.location.href = `mailto:${customer.value.email}?subject=${encodeURIComponent(subject)}`
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
</script>
