<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">操作日志</h2>
        <p class="text-admin-500 text-xs mt-0.5">
          共 {{ pageInfo?.total ?? 0 }} 条匹配记录，当前显示 {{ mock.auditLogs.length }} 条
        </p>
      </div>
      <el-button size="small" :loading="loading" @click="void loadLogs()">
        <el-icon class="mr-1"><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div v-for="item in summary" :key="item.label" class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
        <div class="text-admin-500 text-xs">{{ item.label }}</div>
        <div class="text-admin-100 text-xl font-semibold mt-2">{{ item.value }}</div>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
      <div class="grid grid-cols-1 lg:grid-cols-[minmax(220px,1fr)_160px_180px_160px_auto] gap-3">
        <el-input
          v-model="filters.keyword"
          clearable
          placeholder="搜索人员、对象、路径、IP"
          @clear="reloadFromFirstPage"
          @keyup.enter="reloadFromFirstPage"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filters.action" clearable placeholder="全部动作" @change="reloadFromFirstPage">
          <el-option label="新增" value="CREATE" />
          <el-option label="更新" value="UPDATE" />
          <el-option label="删除" value="DELETE" />
          <el-option label="上传" value="UPLOAD" />
        </el-select>
        <el-select v-model="filters.targetType" clearable filterable placeholder="全部对象" @change="reloadFromFirstPage">
          <el-option v-for="item in targetOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="filters.status" clearable placeholder="全部结果" @change="reloadFromFirstPage">
          <el-option label="成功" value="success" />
          <el-option label="失败" value="failed" />
        </el-select>
        <div class="flex gap-2">
          <el-button type="primary" @click="reloadFromFirstPage">
            <el-icon class="mr-1"><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table v-loading="loading" :data="mock.auditLogs" style="width: 100%" empty-text="暂无操作日志">
        <el-table-column label="时间" width="170">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="人员" width="130">
          <template #default="{ row }">
            <span class="text-admin-200 text-sm font-medium">{{ row.actorName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="动作" width="110">
          <template #default="{ row }">
            <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="actionClass(row.action)">
              {{ actionText(row.action) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="对象" width="150">
          <template #default="{ row }">
            <div class="text-admin-200 text-sm">{{ targetText(row.targetType) }}</div>
            <div class="text-admin-500 text-xs truncate">{{ row.targetId || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="路径" min-width="280">
          <template #default="{ row }">
            <div class="text-admin-400 text-xs font-mono truncate">{{ row.method }} {{ row.path }}</div>
            <div class="text-admin-600 text-xs truncate">{{ row.ipAddress || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <span class="text-xs font-mono" :class="row.statusCode >= 400 ? 'text-red-400' : 'text-green-400'">
              {{ row.statusCode }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-3 px-4 py-3 border-t border-admin-700">
        <div class="text-admin-500 text-xs">
          第 {{ pageInfo?.page ?? currentPage }} / {{ Math.max(pageInfo?.totalPages ?? 1, 1) }} 页
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          background
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          :total="pageInfo?.total ?? 0"
          @current-change="loadLogs"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useMockStore } from '@/stores/mock'
import type { AdminAuditLogPage } from '@/stores/mock'

const mock = useMockStore()
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const pageInfo = ref<AdminAuditLogPage | null>(null)
const filters = reactive({
  keyword: '',
  action: '',
  targetType: '',
  status: '',
})

const targetOptions = [
  { label: '文章', value: 'ARTICLE' },
  { label: '分类', value: 'CATEGORY' },
  { label: '客户', value: 'CUSTOMER' },
  { label: '文件', value: 'FILE' },
  { label: '牌号', value: 'GRADE' },
  { label: '导航', value: 'NAVIGATION' },
  { label: '产品', value: 'PRODUCT' },
  { label: '询盘', value: 'RFQ' },
  { label: '留言', value: 'CONTACT_MESSAGE' },
  { label: '站点设置', value: 'SITE_CONFIG' },
  { label: '标准', value: 'STANDARD' },
]

const summary = computed(() => {
  const data = pageInfo.value?.summary
  return [
    { label: '匹配记录', value: data?.total ?? 0 },
    { label: '成功操作', value: data?.success ?? 0 },
    { label: '失败操作', value: data?.failed ?? 0 },
    { label: '媒体操作', value: data?.fileActions ?? 0 },
  ]
})

onMounted(() => {
  void loadLogs()
})

async function loadLogs() {
  loading.value = true
  try {
    pageInfo.value = await mock.loadAuditLogs({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: filters.keyword,
      action: filters.action,
      targetType: filters.targetType,
      status: filters.status,
    })
  } finally {
    loading.value = false
  }
}

function reloadFromFirstPage() {
  currentPage.value = 1
  void loadLogs()
}

function handleSizeChange() {
  currentPage.value = 1
  void loadLogs()
}

function resetFilters() {
  filters.keyword = ''
  filters.action = ''
  filters.targetType = ''
  filters.status = ''
  reloadFromFirstPage()
}

function actionText(action: string) {
  const map: Record<string, string> = {
    CREATE: '新增',
    UPDATE: '更新',
    DELETE: '删除',
    UPLOAD: '上传',
  }
  return map[action] || action
}

function actionClass(action: string) {
  const map: Record<string, string> = {
    CREATE: 'bg-green-500/15 text-green-400',
    UPDATE: 'bg-blue-500/15 text-blue-400',
    DELETE: 'bg-red-500/15 text-red-400',
    UPLOAD: 'bg-accent-500/15 text-accent-400',
  }
  return map[action] || 'bg-admin-700 text-admin-300'
}

function targetText(targetType: string) {
  const match = targetOptions.find((item) => item.value === targetType)
  return match?.label || targetType
}

function formatDate(value: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}
</script>
