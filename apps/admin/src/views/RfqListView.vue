<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">全部询盘</h2>
        <p class="text-admin-500 text-xs mt-0.5">{{ filteredRfqs.length }} 条记录</p>
      </div>
      <el-button type="primary" plain size="small" :loading="exporting" @click="void exportCsv()">
        <el-icon class="mr-1"><Download /></el-icon>
        导出 CSV
      </el-button>
    </div>

    <div class="flex flex-wrap gap-3">
      <el-input
        v-model="search"
        placeholder="搜索公司、产品、邮箱..."
        size="small"
        clearable
        class="w-56"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="statusFilter" placeholder="全部状态" size="small" clearable class="w-36">
        <el-option label="新询盘" value="new" />
        <el-option label="跟进中" value="in_progress" />
        <el-option label="已报价" value="quoted" />
        <el-option label="已成交" value="won" />
        <el-option label="已失单" value="lost" />
      </el-select>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table :data="paginatedRfqs" style="width: 100%" row-class-name="cursor-pointer" @row-click="(row: Rfq) => router.push(`/rfq/${row.id}`)">
        <el-table-column prop="id" label="编号" width="140">
          <template #default="{ row }">
            <span class="font-mono text-xs text-accent-400">{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="company" label="公司" min-width="180">
          <template #default="{ row }">
            <div class="min-w-0">
              <div class="flex items-center gap-2">
                <span class="text-xs font-bold text-admin-500 w-7">{{ row.countryCode }}</span>
                <span class="text-admin-200 text-sm truncate">{{ row.company }}</span>
              </div>
              <div class="text-admin-500 text-xs truncate mt-0.5">{{ row.email }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="product" label="产品" width="170">
          <template #default="{ row }">
            <span class="text-admin-300 text-sm">{{ row.product }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="牌号" width="90">
          <template #default="{ row }">
            <span class="text-xs font-mono bg-admin-700 text-admin-300 px-1.5 py-0.5 rounded">{{ row.grade }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="qty" label="数量" width="110">
          <template #default="{ row }">
            <span class="text-admin-400 text-xs">{{ row.qty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="attachments" label="附件" width="70" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.attachments?.length" class="text-accent-400"><Paperclip /></el-icon>
            <span v-else class="text-admin-600 text-xs">—</span>
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
        <el-table-column label="" width="60" align="center">
          <template #default="{ row }">
            <el-button link size="small" @click.stop="router.push(`/rfq/${row.id}`)">
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="flex justify-end">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="filteredRfqs.length"
        layout="prev, pager, next"
        background
        small
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useMockStore } from '@/stores/mock'
import type { Rfq, RfqStatus } from '@/stores/mock'

const mock = useMockStore()
const router = useRouter()

const search = ref('')
const statusFilter = ref<RfqStatus | ''>('')
const currentPage = ref(1)
const exporting = ref(false)
const pageSize = 10

const filteredRfqs = computed<Rfq[]>(() => {
  let list: Rfq[] = mock.rfqs
  if (statusFilter.value) list = list.filter((r: Rfq) => r.status === statusFilter.value)
  if (search.value) {
    const q = search.value.toLowerCase()
    list = list.filter((r: Rfq) =>
      r.company.toLowerCase().includes(q) ||
      r.product.toLowerCase().includes(q) ||
      r.email.toLowerCase().includes(q) ||
      r.id.toLowerCase().includes(q),
    )
  }
  return list
})

const paginatedRfqs = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredRfqs.value.slice(start, start + pageSize)
})

async function exportCsv() {
  exporting.value = true
  try {
    const blob = await mock.exportRfqCsv()
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `rfqs-${new Date().toISOString().slice(0, 10)}.csv`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('询盘 CSV 已导出')
  } finally {
    exporting.value = false
  }
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
</script>
