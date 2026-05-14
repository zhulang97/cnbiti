<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">文章管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">
          共 {{ mock.articles.length }} 篇文章，当前显示 {{ filteredArticles.length }} 篇
        </p>
      </div>
      <div class="flex items-center gap-2">
        <el-button size="small" :loading="loadingArticles" @click="void refreshArticles()">
          刷新
        </el-button>
        <el-button type="primary" size="small" @click="openCreate">
          <el-icon class="mr-1"><Plus /></el-icon>
          新建文章
        </el-button>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-[1fr_180px_140px] gap-3">
      <el-input v-model="keyword" clearable placeholder="搜索标题、分类或发布时间" />
      <el-select v-model="categoryFilter" clearable placeholder="全部分类">
        <el-option v-for="category in articleCategories" :key="category" :label="category" :value="category" />
      </el-select>
      <el-select v-model="statusFilter" clearable placeholder="全部状态">
        <el-option label="已发布" value="published" />
        <el-option label="草稿" value="draft" />
      </el-select>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table
        v-loading="loadingArticles"
        :data="pagedArticles"
        style="width: 100%"
        empty-text="暂无文章"
      >
        <el-table-column prop="title" label="标题" min-width="360">
          <template #default="{ row }">
            <span class="text-admin-200 text-sm font-medium">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="180">
          <template #default="{ row }">
            <span class="text-xs px-2 py-0.5 rounded-full bg-admin-700 text-admin-400">{{ row.category || '未分类' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <span
              class="text-xs px-2 py-0.5 rounded-full font-medium"
              :class="row.status === 'published' ? 'bg-green-500/15 text-green-400' : 'bg-admin-700 text-admin-400'"
            >
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="130">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ row.publishedAt || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-1">
              <el-button link size="small" title="编辑" @click="openEdit(row.id)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button link size="small" :title="row.status === 'published' ? '下架' : '发布'" @click="void toggleStatus(row.id)">
                <el-icon><component :is="row.status === 'published' ? 'Hide' : 'View'" /></el-icon>
              </el-button>
              <el-button link size="small" title="删除" @click="void deleteArticle(row.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex items-center justify-between px-4 py-3 border-t border-admin-600/40">
        <span class="text-admin-500 text-xs">每页 {{ pageSize }} 篇</span>
        <el-pagination
          v-model:current-page="currentPage"
          small
          background
          layout="prev, pager, next"
          :page-size="pageSize"
          :total="filteredArticles.length"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingArticleId ? '编辑文章' : '新建文章'" width="800px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
        <el-form-item label="标题" required>
          <el-input v-model="articleForm.title" placeholder="Complete Guide to Titanium Grades" @blur="fillArticleSlug" />
        </el-form-item>
        <el-form-item label="Slug" required>
          <el-input v-model="articleForm.slug" placeholder="complete-guide-to-titanium-grades" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="articleForm.category" class="w-full" filterable allow-create default-first-option>
            <el-option v-for="category in articleCategories" :key="category" :label="category" :value="category" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="articleForm.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="articleForm.publishedAt" class="w-full" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="阅读时间（分钟）">
          <el-input-number v-model="articleForm.readingTime" :min="1" :max="60" class="w-full" />
        </el-form-item>
        <el-form-item label="封面图 URL" class="col-span-2">
          <div class="flex gap-2 w-full">
            <el-input v-model="articleForm.coverImageUrl" placeholder="上传后自动填入，或粘贴图片 URL" />
            <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadCoverImage">
              <el-button :loading="uploadingImage">
                <el-icon class="mr-1"><Upload /></el-icon>
                上传
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="标签" class="col-span-2">
          <el-input v-model="tagsText" placeholder="Titanium, ASTM, Procurement" />
        </el-form-item>
        <el-form-item label="摘要" class="col-span-2">
          <el-input v-model="articleForm.excerpt" type="textarea" :rows="2" resize="none" />
        </el-form-item>
        <el-form-item label="正文" class="col-span-2">
          <el-input v-model="articleForm.content" type="textarea" :rows="8" resize="none" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveArticle()">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRawFile } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { AdminArticle, ArticleSavePayload, ContentStatus } from '@/stores/mock'

const mock = useMockStore()
const dialogVisible = ref(false)
const editingArticleId = ref('')
const saving = ref(false)
const uploadingImage = ref(false)
const loadingArticles = ref(false)
const tagsText = ref('')
const keyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref<ContentStatus | ''>('')
const currentPage = ref(1)
const pageSize = 20

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '发布', value: 'published' },
]

const articleCategories = computed(() => {
  const categories = mock.articles.map((article: AdminArticle) => article.category).filter(Boolean)
  return Array.from(new Set(['Technical Guide', 'Standards', 'Procurement', 'Industry', 'FAQ', 'Applications', 'Material Comparison', 'Processing Guide', 'Titanium Insights', ...categories])).sort()
})

const filteredArticles = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  return mock.articles.filter((article: AdminArticle) => {
    const matchesKeyword = !query || `${article.title} ${article.category} ${article.publishedAt}`.toLowerCase().includes(query)
    const matchesCategory = !categoryFilter.value || article.category === categoryFilter.value
    const matchesStatus = !statusFilter.value || article.status === statusFilter.value
    return matchesKeyword && matchesCategory && matchesStatus
  })
})

const pagedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredArticles.value.slice(start, start + pageSize)
})

const articleForm = reactive<ArticleSavePayload>(emptyArticleForm())

onMounted(() => {
  void refreshArticles()
})

watch([keyword, categoryFilter, statusFilter], () => {
  currentPage.value = 1
})

async function refreshArticles() {
  loadingArticles.value = true
  try {
    await mock.loadArticles(true)
  } finally {
    loadingArticles.value = false
  }
}

function openCreate() {
  editingArticleId.value = ''
  Object.assign(articleForm, emptyArticleForm())
  tagsText.value = ''
  dialogVisible.value = true
}

async function openEdit(id: string) {
  const detail = await mock.getArticleDetail(id)
  editingArticleId.value = id
  Object.assign(articleForm, {
    title: detail.title,
    slug: detail.slug,
    category: detail.category,
    status: detail.status,
    excerpt: detail.excerpt,
    content: detail.content,
    coverImageUrl: detail.coverImageUrl,
    tags: [...detail.tags],
    publishedAt: detail.publishedAt,
    readingTime: detail.readingTime || 5,
  })
  tagsText.value = detail.tags.join(', ')
  dialogVisible.value = true
}

async function saveArticle() {
  if (!articleForm.title.trim() || !articleForm.slug.trim() || !articleForm.category.trim()) {
    ElMessage.warning('请填写标题、Slug 和分类')
    return
  }
  saving.value = true
  try {
    await mock.saveArticle({ ...articleForm, tags: parseTags(tagsText.value) }, editingArticleId.value || undefined)
    ElMessage.success('文章已保存')
    dialogVisible.value = false
    await refreshArticles()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(id: string) {
  await mock.toggleArticleStatus(id)
  ElMessage.success('状态已更新')
}

async function deleteArticle(id: string) {
  try {
    await ElMessageBox.confirm('删除后该文章将从前台知识库移除，确定继续？', '删除文章', { type: 'warning' })
    await mock.deleteArticle(id)
    ElMessage.success('文章已删除')
  } catch {
    // User cancelled.
  }
}

function uploadCoverImage(file: UploadRawFile) {
  void handleCoverUpload(file)
  return false
}

async function handleCoverUpload(file: File) {
  uploadingImage.value = true
  try {
    const asset = await mock.uploadFile(file)
    articleForm.coverImageUrl = asset.url
    ElMessage.success('封面已上传')
  } finally {
    uploadingImage.value = false
  }
}

function fillArticleSlug() {
  if (!articleForm.slug && articleForm.title) articleForm.slug = slugify(articleForm.title)
}

function emptyArticleForm(): ArticleSavePayload {
  return {
    title: '',
    slug: '',
    category: 'Technical Guide',
    status: 'draft',
    excerpt: '',
    content: '',
    coverImageUrl: '',
    tags: [],
    publishedAt: '',
    readingTime: 5,
  }
}

function parseTags(value: string) {
  return value.split(',').map(tag => tag.trim()).filter(Boolean)
}

function slugify(value: string) {
  return value
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
}
</script>
