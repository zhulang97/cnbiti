<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">媒体资源</h2>
        <p class="text-admin-500 text-xs mt-0.5">{{ mock.files.length }} 个文件</p>
      </div>
      <div class="flex items-center gap-2">
        <el-button size="small" :loading="loading" @click="void loadFiles(true)">
          <el-icon class="mr-1"><Refresh /></el-icon>
          刷新
        </el-button>
        <el-upload
          :show-file-list="false"
          multiple
          accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.csv,.txt"
          :before-upload="uploadMedia"
        >
          <el-button type="primary" size="small" :loading="uploading">
            <el-icon class="mr-1"><Upload /></el-icon>
            上传文件
          </el-button>
        </el-upload>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
        <div class="text-admin-500 text-xs">文件总数</div>
        <div class="text-admin-100 text-xl font-semibold mt-2">{{ mock.files.length }}</div>
      </div>
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
        <div class="text-admin-500 text-xs">图片资源</div>
        <div class="text-admin-100 text-xl font-semibold mt-2">{{ imageCount }}</div>
      </div>
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
        <div class="text-admin-500 text-xs">占用空间</div>
        <div class="text-admin-100 text-xl font-semibold mt-2">{{ formatSize(totalSize) }}</div>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
      <div class="flex flex-col md:flex-row gap-3">
        <el-input v-model="search" clearable placeholder="搜索文件名、类型或对象路径">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-segmented v-model="typeFilter" :options="typeOptions" />
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table v-loading="loading" :data="filteredFiles" style="width: 100%" empty-text="暂无媒体资源">
        <el-table-column label="预览" width="86">
          <template #default="{ row }">
            <button
              v-if="isImage(row)"
              class="w-14 h-14 rounded-lg overflow-hidden border border-admin-600 bg-admin-800"
              title="预览图片"
              @click="openPreview(row.url)"
            >
              <img :src="row.url" :alt="row.filename" class="w-full h-full object-cover" />
            </button>
            <div v-else class="w-14 h-14 rounded-lg border border-admin-600 bg-admin-800 flex items-center justify-center text-admin-400">
              <el-icon size="22"><Document /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="文件" min-width="240">
          <template #default="{ row }">
            <div class="text-admin-200 text-sm font-medium truncate">{{ row.filename }}</div>
            <div class="text-admin-500 text-xs mt-1 truncate">{{ row.objectName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="150">
          <template #default="{ row }">
            <span class="text-admin-400 text-xs">{{ row.contentType }}</span>
          </template>
        </el-table-column>
        <el-table-column label="大小" width="110">
          <template #default="{ row }">
            <span class="text-admin-400 text-xs font-mono">{{ formatSize(row.size) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="170">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-1">
              <el-button link size="small" title="打开" @click="openFile(row.url)">
                <el-icon><TopRight /></el-icon>
              </el-button>
              <el-button link size="small" title="复制 URL" @click="void copyUrl(row.url)">
                <el-icon><CopyDocument /></el-icon>
              </el-button>
              <el-button link size="small" title="删除" @click="void deleteFile(row.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="previewVisible" title="图片预览" width="720px">
      <img :src="previewUrl" alt="" class="w-full max-h-[70vh] object-contain rounded-lg bg-admin-950" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRawFile } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { AdminStoredFile } from '@/stores/mock'

const mock = useMockStore()
const loading = ref(false)
const uploadCount = ref(0)
const search = ref('')
const typeFilter = ref('all')
const previewVisible = ref(false)
const previewUrl = ref('')

const uploading = computed(() => uploadCount.value > 0)
const imageCount = computed(() => mock.files.filter((file: AdminStoredFile) => isImage(file)).length)
const totalSize = computed(() => mock.files.reduce((sum: number, file: AdminStoredFile) => sum + file.size, 0))
const typeOptions = [
  { label: '全部', value: 'all' },
  { label: '图片', value: 'image' },
  { label: '文档', value: 'document' },
]

const filteredFiles = computed(() => {
  const keyword = search.value.trim().toLowerCase()
  return mock.files.filter((file: AdminStoredFile) => {
    const matchesType = typeFilter.value === 'all'
      || (typeFilter.value === 'image' && isImage(file))
      || (typeFilter.value === 'document' && !isImage(file))
    const haystack = `${file.filename} ${file.contentType} ${file.objectName}`.toLowerCase()
    return matchesType && (!keyword || haystack.includes(keyword))
  })
})

onMounted(() => {
  void loadFiles()
})

async function loadFiles(force = false) {
  loading.value = true
  try {
    await mock.loadFiles(force)
  } finally {
    loading.value = false
  }
}

function uploadMedia(file: UploadRawFile) {
  void handleUpload(file)
  return false
}

async function handleUpload(file: File) {
  uploadCount.value += 1
  try {
    await mock.uploadFile(file)
    ElMessage.success('文件已上传')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '上传失败')
  } finally {
    uploadCount.value -= 1
  }
}

async function deleteFile(id: string) {
  try {
    await ElMessageBox.confirm('删除后该文件的公开 URL 将不可访问，确定继续？', '删除媒体资源', { type: 'warning' })
    await mock.deleteStoredFile(id)
    ElMessage.success('文件已删除')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

async function copyUrl(url: string) {
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('URL 已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

function openFile(url: string) {
  window.open(url, '_blank', 'noopener,noreferrer')
}

function openPreview(url: string) {
  previewUrl.value = url
  previewVisible.value = true
}

function isImage(file: AdminStoredFile) {
  return file.contentType.startsWith('image/')
}

function formatSize(size: number) {
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  if (size < 1024 * 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)} MB`
  return `${(size / 1024 / 1024 / 1024).toFixed(1)} GB`
}

function formatDate(value: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}
</script>
