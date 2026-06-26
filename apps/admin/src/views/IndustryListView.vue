<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">行业应用管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">维护官网 Industries 页面卡片、图片和行业详情内容。</p>
      </div>
      <div class="flex gap-2">
        <el-button size="small" :loading="loading" @click="void loadIndustries(true)">
          <el-icon class="mr-1"><Refresh /></el-icon>
          重新载入
        </el-button>
        <el-button type="primary" size="small" @click="openCreate">
          <el-icon class="mr-1"><Plus /></el-icon>
          新增行业
        </el-button>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table :data="mock.industries" style="width: 100%" v-loading="loading">
        <el-table-column label="行业" min-width="300">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <img
                v-if="row.image"
                :src="row.image"
                :alt="row.imageAlt || row.name"
                class="h-14 w-20 rounded-lg border border-admin-700 bg-admin-950 object-contain"
              >
              <div v-else class="h-14 w-20 rounded-lg border border-admin-700 bg-admin-950 flex items-center justify-center text-xs text-admin-500">
                无图片
              </div>
              <div class="min-w-0">
                <div class="text-admin-100 text-sm font-semibold truncate">{{ row.name }}</div>
                <div class="text-admin-500 text-xs truncate">{{ row.slug }}</div>
                <div class="text-admin-400 text-xs truncate">{{ row.kicker }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="简介" min-width="280">
          <template #default="{ row }">
            <p class="line-clamp-2 text-xs text-admin-400 leading-relaxed">{{ row.summary }}</p>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="180">
          <template #default="{ row }">
            <div class="flex flex-wrap gap-1">
              <span v-for="grade in row.grades.slice(0, 4)" :key="grade" class="rounded bg-admin-800 px-1.5 py-0.5 text-[11px] text-admin-300">{{ grade }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="row.status === 'published' ? 'bg-green-500/15 text-green-400' : 'bg-admin-700 text-admin-400'">
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新日期" width="110" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link size="small" title="编辑" @click="openEdit(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button link size="small" title="删除" @click="void deleteIndustry(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑行业应用' : '新增行业应用'" width="920px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-1 gap-x-4 md:grid-cols-2">
        <el-form-item label="行业名称" required>
          <el-input v-model="form.name" @blur="fillSlug" />
        </el-form-item>
        <el-form-item label="路径标识" required>
          <el-input v-model="form.slug" placeholder="chemical-processing" />
        </el-form-item>
        <el-form-item label="标签文案">
          <el-input v-model="form.kicker" placeholder="Reactors, piping and anodes" />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="form.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="卡片/详情图片" class="md:col-span-2">
          <div class="w-full space-y-3">
            <div class="flex gap-2">
              <el-input v-model="form.image" placeholder="上传后自动填写，也可以粘贴图片链接" />
              <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadImage">
                <el-button :loading="uploadingImage">
                  <el-icon class="mr-1"><Upload /></el-icon>
                  上传
                </el-button>
              </el-upload>
            </div>
            <div class="grid grid-cols-1 gap-3 md:grid-cols-[220px_minmax(0,1fr)]">
              <div class="h-36 rounded-xl border border-admin-700 bg-admin-950 p-2">
                <img v-if="form.image" :src="form.image" :alt="form.imageAlt || form.name" class="h-full w-full object-contain">
                <div v-else class="flex h-full items-center justify-center text-xs text-admin-500">暂无图片</div>
              </div>
              <el-input v-model="form.imageAlt" placeholder="图片说明 Alt" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="简介" class="md:col-span-2">
          <el-input v-model="form.summary" type="textarea" :rows="3" resize="none" />
        </el-form-item>
        <el-form-item label="常用钛等级，逗号分隔">
          <el-input v-model="gradesText" placeholder="Gr.1, Gr.2, Gr.7" />
        </el-form-item>
        <el-form-item label="标准，逗号分隔">
          <el-input v-model="standardsText" placeholder="ASTM B338, ASTM B265" />
        </el-form-item>
        <el-form-item label="典型应用，一行一个" class="md:col-span-2">
          <el-input v-model="applicationsText" type="textarea" :rows="4" resize="none" />
        </el-form-item>
        <el-form-item label="询盘备注/要求，一行一个" class="md:col-span-2">
          <el-input v-model="requirementsText" type="textarea" :rows="4" resize="none" />
        </el-form-item>
        <el-form-item label="相关文章关键词，逗号分隔" class="md:col-span-2">
          <el-input v-model="articleKeywordsText" placeholder="chemical, reactor, piping" />
        </el-form-item>
        <el-form-item label="推荐产品链接" class="md:col-span-2">
          <div class="w-full space-y-2">
            <div v-for="(link, index) in form.productLinks" :key="`link-${index}`" class="grid grid-cols-[1fr_1fr_auto] gap-2">
              <el-input v-model="link.label" placeholder="Titanium Tubes" />
              <el-input v-model="link.href" placeholder="/products/titanium-tube" />
              <el-button circle text type="danger" @click="removeProductLink(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button size="small" text @click="addProductLink">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加产品链接
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveIndustry()">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Plus, Refresh, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMockStore, type AdminIndustryDetail, type ContentStatus, type IndustrySavePayload } from '@/stores/mock'
import type { IndustryProductLink } from '@cnbjti/types'

const mock = useMockStore()
const loading = ref(false)
const saving = ref(false)
const uploadingImage = ref(false)
const dialogVisible = ref(false)
const editingId = ref<string | null>(null)

const statusOptions = [
  { label: '已发布', value: 'published' },
  { label: '草稿', value: 'draft' },
]

const form = reactive<IndustrySavePayload>({
  slug: '',
  name: '',
  kicker: '',
  summary: '',
  image: '',
  imageAlt: '',
  grades: [],
  standards: [],
  applications: [],
  requirements: [],
  productLinks: [],
  articleKeywords: [],
  status: 'published',
})

const gradesText = ref('')
const standardsText = ref('')
const applicationsText = ref('')
const requirementsText = ref('')
const articleKeywordsText = ref('')

onMounted(() => {
  void loadIndustries()
})

async function loadIndustries(force = false) {
  loading.value = true
  try {
    await mock.loadIndustries(force)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  fillForm()
  dialogVisible.value = true
}

async function openEdit(row: AdminIndustryDetail) {
  editingId.value = row.id
  fillForm(await mock.getIndustryDetail(row.id))
  dialogVisible.value = true
}

function fillForm(item?: AdminIndustryDetail) {
  form.slug = item?.slug || ''
  form.name = item?.name || ''
  form.kicker = item?.kicker || ''
  form.summary = item?.summary || ''
  form.image = item?.image || ''
  form.imageAlt = item?.imageAlt || ''
  form.grades = [...(item?.grades || [])]
  form.standards = [...(item?.standards || [])]
  form.applications = [...(item?.applications || [])]
  form.requirements = [...(item?.requirements || [])]
  form.productLinks = cloneLinks(item?.productLinks || [])
  form.articleKeywords = [...(item?.articleKeywords || [])]
  form.status = (item?.status || 'published') as ContentStatus
  gradesText.value = form.grades.join(', ')
  standardsText.value = form.standards.join(', ')
  applicationsText.value = form.applications.join('\n')
  requirementsText.value = form.requirements.join('\n')
  articleKeywordsText.value = form.articleKeywords.join(', ')
}

async function saveIndustry() {
  if (!form.name.trim() || !form.slug.trim()) {
    ElMessage.warning('请填写行业名称和路径标识')
    return
  }
  saving.value = true
  try {
    const payload: IndustrySavePayload = {
      ...form,
      slug: slugify(form.slug),
      name: form.name.trim(),
      kicker: form.kicker.trim(),
      summary: form.summary.trim(),
      image: form.image.trim(),
      imageAlt: form.imageAlt.trim(),
      grades: splitComma(gradesText.value),
      standards: splitComma(standardsText.value),
      applications: splitLines(applicationsText.value),
      requirements: splitLines(requirementsText.value),
      productLinks: cloneLinks(form.productLinks).filter(link => link.label && link.href),
      articleKeywords: splitComma(articleKeywordsText.value),
    }
    await mock.saveIndustry(payload, editingId.value || undefined)
    ElMessage.success('行业应用已保存')
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function deleteIndustry(row: AdminIndustryDetail) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」吗？`, '删除行业应用', { type: 'warning' })
    await mock.deleteIndustry(row.id)
    ElMessage.success('行业应用已删除')
  } catch {
    // User cancelled.
  }
}

function fillSlug() {
  if (!form.slug && form.name) form.slug = slugify(form.name)
}

function addProductLink() {
  form.productLinks.push({ label: '', href: '' })
}

function removeProductLink(index: number) {
  form.productLinks.splice(index, 1)
}

async function uploadImage(file: File) {
  uploadingImage.value = true
  try {
    const asset = await mock.uploadFile(file)
    form.image = asset.url
    if (!form.imageAlt) form.imageAlt = asset.alt || asset.filename
    ElMessage.success('图片已上传')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '图片上传失败')
  } finally {
    uploadingImage.value = false
  }
  return false
}

function cloneLinks(values: IndustryProductLink[]) {
  return values.map(link => ({ label: link.label || '', href: link.href || '' }))
}

function splitComma(value: string) {
  return value.split(',').map(item => item.trim()).filter(Boolean)
}

function splitLines(value: string) {
  return value.split(/\r?\n/).map(item => item.trim()).filter(Boolean)
}

function slugify(value: string) {
  return value.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '')
}
</script>
