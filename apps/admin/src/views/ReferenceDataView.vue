<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">基础资料</h2>
        <p class="text-admin-500 text-xs mt-0.5">维护产品分类、钛牌号和执行标准，供产品管理和官网页面使用。</p>
      </div>
      <el-button type="primary" size="small" @click="openCreate">
        <el-icon class="mr-1"><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-tabs v-model="activeTab" class="reference-tabs">
      <el-tab-pane label="产品分类" name="categories">
        <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
          <el-table :data="mock.categories" style="width: 100%">
            <el-table-column label="分类" min-width="240">
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <img v-if="row.imageUrl" :src="row.imageUrl" :alt="row.name" class="w-12 h-9 rounded object-cover bg-admin-800" />
                  <div>
                    <div class="text-admin-200 text-sm font-medium">{{ row.name }}</div>
                    <div class="text-admin-500 text-xs">{{ row.slug }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="icon" label="图标" width="100" />
            <el-table-column prop="productCount" label="数量" width="90" />
            <el-table-column label="首页展示" width="95">
              <template #default="{ row }">
                <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="row.showOnHome !== false ? 'bg-accent-500/15 text-accent-400' : 'bg-admin-700 text-admin-400'">
                  {{ row.showOnHome !== false ? '展示' : '隐藏' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="homeSort" label="首页排序" width="105" />
            <el-table-column prop="status" label="状态" width="105">
              <template #default="{ row }">
                <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="row.status === 'published' ? 'bg-green-500/15 text-green-400' : 'bg-admin-700 text-admin-400'">
                  {{ row.status === 'published' ? '已发布' : '草稿' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="updatedAt" label="更新日期" width="110" />
            <el-table-column label="操作" width="110" align="center">
              <template #default="{ row }">
                <el-button link size="small" title="编辑" @click="openCategory(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" title="删除" @click="void deleteCategory(row.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="钛牌号" name="grades">
        <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
          <el-table :data="mock.grades" style="width: 100%">
            <el-table-column label="牌号" min-width="220">
              <template #default="{ row }">
                <div>
                  <span class="text-accent-400 font-mono text-sm font-bold">{{ row.shortName }}</span>
                  <span class="text-admin-200 text-sm font-medium ml-2">{{ row.name }}</span>
                </div>
                <div class="text-admin-500 text-xs">{{ row.slug }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="composition" label="成分" min-width="180" />
            <el-table-column prop="density" label="密度" width="90" />
            <el-table-column prop="status" label="状态" width="105">
              <template #default="{ row }">
                <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="row.status === 'published' ? 'bg-green-500/15 text-green-400' : 'bg-admin-700 text-admin-400'">
                  {{ row.status === 'published' ? '已发布' : '草稿' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="110" align="center">
              <template #default="{ row }">
                <el-button link size="small" title="编辑" @click="openGrade(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" title="删除" @click="void deleteGrade(row.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="执行标准" name="standards">
        <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
          <el-table :data="mock.standards" style="width: 100%">
            <el-table-column label="标准" min-width="260">
              <template #default="{ row }">
                <div class="text-admin-200 text-sm font-medium">
                  <span class="text-accent-400 font-mono">{{ row.code }}</span>
                  <span class="ml-2">{{ row.name }}</span>
                </div>
                <div class="text-admin-500 text-xs">{{ row.slug }}</div>
              </template>
            </el-table-column>
            <el-table-column label="适用产品" min-width="180">
              <template #default="{ row }">
                <span class="text-admin-400 text-xs">{{ row.productTypes.join(', ') }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="105">
              <template #default="{ row }">
                <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="row.status === 'published' ? 'bg-green-500/15 text-green-400' : 'bg-admin-700 text-admin-400'">
                  {{ row.status === 'published' ? '已发布' : '草稿' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="110" align="center">
              <template #default="{ row }">
                <el-button link size="small" title="编辑" @click="openStandard(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" title="删除" @click="void deleteStandard(row.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="categoryDialogVisible" :title="editingCategoryId ? '编辑分类' : '新增分类'" width="760px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
        <el-form-item label="分类名称" required>
          <el-input v-model="categoryForm.name" @blur="fillCategorySlug" />
        </el-form-item>
        <el-form-item label="路径标识" required>
          <el-input v-model="categoryForm.slug" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="categoryForm.icon" placeholder="bar / sheet / tube" />
        </el-form-item>
        <el-form-item label="产品数量">
          <el-input-number v-model="categoryForm.productCount" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="首页展示">
          <el-switch v-model="categoryForm.showOnHome" active-text="展示" inactive-text="隐藏" />
        </el-form-item>
        <el-form-item label="首页排序">
          <el-input-number v-model="categoryForm.homeSort" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="categoryForm.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="图片链接" class="col-span-2">
          <div class="flex gap-2 w-full">
            <el-input v-model="categoryForm.imageUrl" />
            <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadCategoryImage">
              <el-button :loading="uploadingImage">
                <el-icon class="mr-1"><Upload /></el-icon>
                上传
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="描述" class="col-span-2">
          <el-input v-model="categoryForm.description" type="textarea" :rows="3" resize="none" />
        </el-form-item>
        <el-form-item label="SEO 标题">
          <el-input v-model="categoryForm.seo.title" />
        </el-form-item>
        <el-form-item label="规范链接">
          <el-input v-model="categoryForm.seo.canonical" />
        </el-form-item>
        <el-form-item label="SEO 描述" class="col-span-2">
          <el-input v-model="categoryForm.seo.description" type="textarea" :rows="2" resize="none" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveCategory()">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="gradeDialogVisible" :title="editingGradeId ? '编辑牌号' : '新增牌号'" width="760px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
        <el-form-item label="牌号名称" required>
          <el-input v-model="gradeForm.name" @blur="fillGradeSlug" />
        </el-form-item>
        <el-form-item label="短名称" required>
          <el-input v-model="gradeForm.shortName" placeholder="Gr.2" />
        </el-form-item>
        <el-form-item label="路径标识" required>
          <el-input v-model="gradeForm.slug" />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="gradeForm.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="成分" class="col-span-2">
          <el-input v-model="gradeForm.composition" />
        </el-form-item>
        <el-form-item label="抗拉强度">
          <el-input v-model="gradeForm.tensileStrength" />
        </el-form-item>
        <el-form-item label="屈服强度">
          <el-input v-model="gradeForm.yieldStrength" />
        </el-form-item>
        <el-form-item label="延伸率">
          <el-input v-model="gradeForm.elongation" />
        </el-form-item>
        <el-form-item label="密度 g/cm3">
          <el-input-number v-model="gradeForm.density" :min="0" :step="0.01" class="w-full" />
        </el-form-item>
        <el-form-item label="应用场景" class="col-span-2">
          <el-input v-model="applicationsText" placeholder="Chemical processing, Marine" />
        </el-form-item>
        <el-form-item label="描述" class="col-span-2">
          <el-input v-model="gradeForm.description" type="textarea" :rows="3" resize="none" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveGrade()">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="standardDialogVisible" :title="editingStandardId ? '编辑标准' : '新增标准'" width="760px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
        <el-form-item label="标准号" required>
          <el-input v-model="standardForm.code" placeholder="ASTM B348" @blur="fillStandardSlug" />
        </el-form-item>
        <el-form-item label="路径标识" required>
          <el-input v-model="standardForm.slug" />
        </el-form-item>
        <el-form-item label="标准名称" required class="col-span-2">
          <el-input v-model="standardForm.name" />
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="standardForm.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="适用产品">
          <el-input v-model="productTypesText" placeholder="Bar, Billet" />
        </el-form-item>
        <el-form-item label="描述" class="col-span-2">
          <el-input v-model="standardForm.description" type="textarea" :rows="3" resize="none" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="standardDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveStandard()">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRawFile } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type {
  AdminCategoryDetail,
  AdminGradeDetail,
  AdminStandardDetail,
  CategorySavePayload,
  GradeSavePayload,
  StandardSavePayload,
} from '@/stores/mock'
import type { SeoMeta } from '@cnbjti/types'

type TabName = 'categories' | 'grades' | 'standards'

const mock = useMockStore()
const activeTab = ref<TabName>('categories')
const categoryDialogVisible = ref(false)
const gradeDialogVisible = ref(false)
const standardDialogVisible = ref(false)
const editingCategoryId = ref('')
const editingGradeId = ref('')
const editingStandardId = ref('')
const applicationsText = ref('')
const productTypesText = ref('')
const saving = ref(false)
const uploadingImage = ref(false)

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '发布', value: 'published' },
]

const categoryForm = reactive<CategorySavePayload>(emptyCategoryForm())
const gradeForm = reactive<GradeSavePayload>(emptyGradeForm())
const standardForm = reactive<StandardSavePayload>(emptyStandardForm())

onMounted(() => {
  void mock.loadReferenceData()
})

function openCreate() {
  if (activeTab.value === 'categories') openCategory()
  if (activeTab.value === 'grades') openGrade()
  if (activeTab.value === 'standards') openStandard()
}

function openCategory(item?: AdminCategoryDetail) {
  editingCategoryId.value = item?.id || ''
  Object.assign(categoryForm, item ? {
    slug: item.slug,
    name: item.name,
    description: item.description,
    imageUrl: item.imageUrl,
    icon: item.icon,
    productCount: item.productCount,
    seo: item.seo || emptySeo(),
    showOnHome: item.showOnHome !== false,
    homeSort: item.homeSort ?? 0,
    status: item.status,
  } : emptyCategoryForm())
  categoryDialogVisible.value = true
}

function openGrade(item?: AdminGradeDetail) {
  editingGradeId.value = item?.id || ''
  Object.assign(gradeForm, item ? {
    slug: item.slug,
    name: item.name,
    shortName: item.shortName,
    composition: item.composition,
    description: item.description,
    tensileStrength: item.tensileStrength,
    yieldStrength: item.yieldStrength,
    elongation: item.elongation,
    density: item.density,
    applications: [...item.applications],
    status: item.status,
  } : emptyGradeForm())
  applicationsText.value = gradeForm.applications.join(', ')
  gradeDialogVisible.value = true
}

function openStandard(item?: AdminStandardDetail) {
  editingStandardId.value = item?.id || ''
  Object.assign(standardForm, item ? {
    slug: item.slug,
    code: item.code,
    name: item.name,
    description: item.description,
    productTypes: [...item.productTypes],
    status: item.status,
  } : emptyStandardForm())
  productTypesText.value = standardForm.productTypes.join(', ')
  standardDialogVisible.value = true
}

async function saveCategory() {
  if (!categoryForm.name.trim() || !categoryForm.slug.trim()) {
    ElMessage.warning('请填写分类名称和路径标识。')
    return
  }
  saving.value = true
  try {
    await mock.saveCategory({ ...categoryForm, seo: normalizeSeo(categoryForm.seo) }, editingCategoryId.value || undefined)
    await mock.loadReferenceData(true)
    await mock.loadContentOptions(true)
    categoryDialogVisible.value = false
    ElMessage.success('分类已保存。')
  } finally {
    saving.value = false
  }
}

async function saveGrade() {
  if (!gradeForm.name.trim() || !gradeForm.shortName.trim() || !gradeForm.slug.trim()) {
    ElMessage.warning('请填写牌号名称、短名称和路径标识。')
    return
  }
  saving.value = true
  try {
    await mock.saveGrade({ ...gradeForm, applications: parseList(applicationsText.value) }, editingGradeId.value || undefined)
    await mock.loadReferenceData(true)
    await mock.loadContentOptions(true)
    gradeDialogVisible.value = false
    ElMessage.success('牌号已保存。')
  } finally {
    saving.value = false
  }
}

async function saveStandard() {
  if (!standardForm.code.trim() || !standardForm.name.trim() || !standardForm.slug.trim()) {
    ElMessage.warning('请填写标准号、标准名称和路径标识。')
    return
  }
  saving.value = true
  try {
    await mock.saveStandard({ ...standardForm, productTypes: parseList(productTypesText.value) }, editingStandardId.value || undefined)
    await mock.loadReferenceData(true)
    await mock.loadContentOptions(true)
    standardDialogVisible.value = false
    ElMessage.success('标准已保存。')
  } finally {
    saving.value = false
  }
}

async function deleteCategory(id: string) {
  try {
    await ElMessageBox.confirm('删除后，该分类将不再用于官网产品选择。确定继续吗？', '删除分类', { type: 'warning' })
    await mock.deleteCategory(id)
    ElMessage.success('分类已删除。')
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  }
}

async function deleteGrade(id: string) {
  try {
    await ElMessageBox.confirm('删除后，编辑产品时将无法选择该牌号。确定继续吗？', '删除牌号', { type: 'warning' })
    await mock.deleteGrade(id)
    ElMessage.success('牌号已删除。')
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  }
}

async function deleteStandard(id: string) {
  try {
    await ElMessageBox.confirm('删除后，编辑产品时将无法选择该标准。确定继续吗？', '删除标准', { type: 'warning' })
    await mock.deleteStandard(id)
    ElMessage.success('标准已删除。')
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  }
}

function uploadCategoryImage(file: UploadRawFile) {
  void handleCategoryUpload(file)
  return false
}

async function handleCategoryUpload(file: File) {
  uploadingImage.value = true
  try {
    const asset = await mock.uploadFile(file)
    categoryForm.imageUrl = asset.url
    ElMessage.success('图片已上传。')
  } finally {
    uploadingImage.value = false
  }
}

function fillCategorySlug() {
  if (!categoryForm.slug && categoryForm.name) categoryForm.slug = slugify(categoryForm.name)
}

function fillGradeSlug() {
  if (!gradeForm.slug && gradeForm.name) gradeForm.slug = slugify(gradeForm.name)
}

function fillStandardSlug() {
  if (!standardForm.slug && standardForm.code) standardForm.slug = slugify(standardForm.code)
}

function emptyCategoryForm(): CategorySavePayload {
  return {
    slug: '',
    name: '',
    description: '',
    imageUrl: '',
    icon: '',
    productCount: 0,
    seo: emptySeo(),
    showOnHome: true,
    homeSort: 0,
    status: 'draft',
  }
}

function emptyGradeForm(): GradeSavePayload {
  return {
    slug: '',
    name: '',
    shortName: '',
    composition: '',
    description: '',
    tensileStrength: '',
    yieldStrength: '',
    elongation: '',
    density: null,
    applications: [],
    status: 'draft',
  }
}

function emptyStandardForm(): StandardSavePayload {
  return {
    slug: '',
    code: '',
    name: '',
    description: '',
    productTypes: [],
    status: 'draft',
  }
}

function emptySeo(): SeoMeta {
  return {
    title: '',
    description: '',
    canonical: '',
    ogTitle: '',
    ogDescription: '',
    ogImage: '',
    noIndex: false,
  }
}

function normalizeSeo(seo: SeoMeta | null): SeoMeta {
  return {
    title: seo?.title?.trim() || '',
    description: seo?.description?.trim() || '',
    canonical: seo?.canonical?.trim() || '',
    ogTitle: seo?.ogTitle?.trim() || '',
    ogDescription: seo?.ogDescription?.trim() || '',
    ogImage: seo?.ogImage?.trim() || '',
    noIndex: Boolean(seo?.noIndex),
  }
}

function parseList(value: string) {
  return value.split(',').map(item => item.trim()).filter(Boolean)
}

function slugify(value: string) {
  return value
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
}
</script>
