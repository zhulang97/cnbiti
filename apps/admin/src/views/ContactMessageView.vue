<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">留言管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">{{ filteredMessages.length }} 条联系消息</p>
      </div>
      <el-button size="small" :loading="loading" @click="void loadMessages(true)">
        <el-icon class="mr-1"><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <div class="flex flex-wrap gap-3">
      <el-input v-model="search" placeholder="搜索姓名、公司、邮箱、主题" size="small" clearable class="w-64">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="statusFilter" placeholder="全部状态" size="small" clearable class="w-36">
        <el-option label="新留言" value="new" />
        <el-option label="跟进中" value="in_progress" />
        <el-option label="已回复" value="replied" />
        <el-option label="已归档" value="archived" />
      </el-select>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table v-loading="loading" :data="paginatedMessages" style="width: 100%" row-class-name="cursor-pointer" empty-text="暂无联系消息" @row-click="openMessage">
        <el-table-column prop="id" label="编号" width="140">
          <template #default="{ row }">
            <span class="font-mono text-xs text-accent-400">{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="联系人" min-width="220">
          <template #default="{ row }">
            <div class="text-admin-200 text-sm font-medium truncate">{{ row.name }}</div>
            <div class="text-admin-500 text-xs truncate">{{ row.company || row.email }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="主题" min-width="220">
          <template #default="{ row }">
            <span class="text-admin-300 text-sm">{{ row.subject || 'General inquiry' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="" width="60" align="center">
          <template #default="{ row }">
            <el-button link size="small" @click.stop="openMessage(row)">
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
        :total="filteredMessages.length"
        layout="prev, pager, next"
        background
        small
      />
    </div>

    <el-dialog v-model="dialogVisible" title="联系消息" width="760px" destroy-on-close>
      <div v-if="selected" class="space-y-5">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <div class="text-admin-500 text-xs mb-1">联系人</div>
            <div class="text-admin-100 text-sm font-medium">{{ selected.name }}</div>
          </div>
          <div>
            <div class="text-admin-500 text-xs mb-1">邮箱</div>
            <a :href="`mailto:${selected.email}`" class="text-accent-400 text-sm hover:text-accent-300">{{ selected.email }}</a>
          </div>
          <div>
            <div class="text-admin-500 text-xs mb-1">公司</div>
            <div class="text-admin-100 text-sm">{{ selected.company || '-' }}</div>
          </div>
          <div>
            <div class="text-admin-500 text-xs mb-1">来源</div>
            <div class="text-admin-100 text-sm">{{ selected.source }}</div>
          </div>
        </div>

        <div>
          <div class="text-admin-500 text-xs mb-1">主题</div>
          <div class="text-admin-100 text-sm">{{ selected.subject || 'General inquiry' }}</div>
        </div>
        <div>
          <div class="text-admin-500 text-xs mb-1">消息内容</div>
          <div class="rounded-lg bg-admin-800/70 border border-admin-700 p-4 text-admin-200 text-sm whitespace-pre-wrap">{{ selected.message }}</div>
        </div>

        <div class="grid grid-cols-[220px_1fr] gap-4">
          <el-form-item label="状态" class="mb-0">
            <el-select v-model="selectedStatus" class="w-full">
              <el-option label="新留言" value="new" />
              <el-option label="跟进中" value="in_progress" />
              <el-option label="已回复" value="replied" />
              <el-option label="已归档" value="archived" />
            </el-select>
          </el-form-item>
          <el-form-item label="内部备注" class="mb-0">
            <el-input v-model="notes" type="textarea" :rows="3" resize="none" />
          </el-form-item>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button :loading="saving" @click="void saveNotes()">保存备注</el-button>
        <el-button type="primary" :loading="saving" @click="void saveStatus()">更新状态</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { AdminContactMessage, ContactMessageStatus } from '@/stores/mock'

const mock = useMockStore()
const loading = ref(false)
const saving = ref(false)
const search = ref('')
const statusFilter = ref<ContactMessageStatus | ''>('')
const currentPage = ref(1)
const pageSize = 10
const dialogVisible = ref(false)
const selected = ref<AdminContactMessage | null>(null)
const selectedStatus = ref<ContactMessageStatus>('new')
const notes = ref('')

const filteredMessages = computed(() => {
  let list = mock.contactMessages
  if (statusFilter.value) list = list.filter((message: AdminContactMessage) => message.status === statusFilter.value)
  if (search.value) {
    const keyword = search.value.toLowerCase()
    list = list.filter((message: AdminContactMessage) =>
      message.name.toLowerCase().includes(keyword) ||
      message.company.toLowerCase().includes(keyword) ||
      message.email.toLowerCase().includes(keyword) ||
      message.subject.toLowerCase().includes(keyword),
    )
  }
  return list
})

const paginatedMessages = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredMessages.value.slice(start, start + pageSize)
})

onMounted(() => {
  void loadMessages()
})

async function loadMessages(force = false) {
  loading.value = true
  try {
    await mock.loadContactMessages(force)
  } finally {
    loading.value = false
  }
}

async function openMessage(row: AdminContactMessage) {
  const detail = await mock.getContactMessage(row.id)
  selected.value = detail
  selectedStatus.value = detail.status
  notes.value = detail.notes || ''
  dialogVisible.value = true
}

async function saveStatus() {
  if (!selected.value) return
  saving.value = true
  try {
    selected.value = await mock.updateContactMessageStatus(selected.value.id, selectedStatus.value)
    ElMessage.success('状态已更新')
  } finally {
    saving.value = false
  }
}

async function saveNotes() {
  if (!selected.value) return
  saving.value = true
  try {
    const updated = await mock.updateContactMessageNotes(selected.value.id, notes.value)
    selected.value = updated
    notes.value = updated.notes
    ElMessage.success('备注已保存')
  } finally {
    saving.value = false
  }
}

function statusClass(status: ContactMessageStatus) {
  const map: Record<ContactMessageStatus, string> = {
    new: 'bg-accent-500/15 text-accent-400',
    in_progress: 'bg-yellow-500/15 text-yellow-400',
    replied: 'bg-green-500/15 text-green-400',
    archived: 'bg-admin-700 text-admin-400',
  }
  return map[status]
}

function statusText(status: ContactMessageStatus) {
  const map: Record<ContactMessageStatus, string> = {
    new: '新留言',
    in_progress: '跟进中',
    replied: '已回复',
    archived: '已归档',
  }
  return map[status]
}

function formatDate(value: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}
</script>
