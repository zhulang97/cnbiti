<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">产品管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">{{ mock.products.length }} 个产品</p>
      </div>
      <el-button type="primary" size="small" @click="openCreate">
        <el-icon class="mr-1"><Plus /></el-icon>
        添加产品
      </el-button>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table :data="mock.products" style="width: 100%">
        <el-table-column prop="name" label="产品名称" min-width="220">
          <template #default="{ row }">
            <span class="text-admin-200 text-sm font-medium">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" min-width="160">
          <template #default="{ row }">
            <span class="text-admin-400 text-sm">{{ row.category }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="牌号" width="80">
          <template #default="{ row }">
            <span class="text-xs font-mono bg-admin-700 text-admin-300 px-1.5 py-0.5 rounded">{{ row.grade }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="row.status === 'published' ? 'bg-green-500/15 text-green-400' : 'bg-admin-700 text-admin-400'">
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="110">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ row.updatedAt }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-1">
              <el-button link size="small" title="编辑" @click="openEdit(row.id)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button link size="small" :title="row.status === 'published' ? '下架' : '发布'" @click="void toggleStatus(row.id)">
                <el-icon><component :is="row.status === 'published' ? 'Hide' : 'View'" /></el-icon>
              </el-button>
              <el-button link size="small" title="删除" @click="void deleteProduct(row.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingProductId ? '编辑产品' : '添加产品'" width="920px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
        <el-form-item label="产品名称" required>
          <el-input v-model="productForm.name" placeholder="Grade 2 Titanium Round Bar" @blur="fillProductSlug" />
        </el-form-item>
        <el-form-item label="路径标识" required>
          <el-input v-model="productForm.slug" placeholder="grade-2-titanium-round-bar" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="productForm.categoryId" class="w-full" filterable>
            <el-option v-for="category in mock.contentOptions.categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="productForm.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="牌号">
          <el-select v-model="productForm.gradeIds" class="w-full" multiple filterable collapse-tags collapse-tags-tooltip>
            <el-option v-for="grade in mock.contentOptions.grades" :key="grade.id" :label="grade.shortName" :value="grade.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标准">
          <el-select v-model="productForm.standardIds" class="w-full" multiple filterable collapse-tags collapse-tags-tooltip>
            <el-option v-for="standard in mock.contentOptions.standards" :key="standard.id" :label="standard.code" :value="standard.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="产品图片" class="col-span-2">
          <div class="w-full space-y-3">
            <div class="flex gap-2">
              <el-input v-model="productForm.imageUrl" placeholder="首图链接，上传后自动填入" @blur="syncImageUrlToFirstImage" />
              <el-upload :show-file-list="false" accept="image/*" multiple :before-upload="uploadProductImage">
                <el-button :loading="uploadingImage">
                  <el-icon class="mr-1"><Upload /></el-icon>
                  上传
                </el-button>
              </el-upload>
            </div>
            <div v-if="productForm.images.length" class="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div v-for="(image, index) in productForm.images" :key="image.id || image.url" class="border border-admin-600/50 bg-admin-800/60 rounded-lg p-3">
                <div class="flex gap-3">
                  <img :src="image.url" :alt="image.alt || productForm.name" class="w-24 h-20 rounded-md object-cover bg-admin-900 border border-admin-700" />
                  <div class="flex-1 min-w-0 space-y-2">
                    <div class="flex items-center justify-between gap-2">
                      <span class="text-xs text-admin-400 truncate">{{ image.filename || image.url }}</span>
                      <div class="flex items-center gap-2">
                        <el-button v-if="index > 0" link size="small" @click="setPrimaryImage(index)">设为首图</el-button>
                        <el-button link size="small" title="删除图片" @click="removeImage(index)">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </div>
                    <el-input v-model="image.alt" size="small" placeholder="图片替代文案" />
                    <el-input v-model="image.url" size="small" placeholder="图片链接" @blur="refreshImageUrl" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="短描述" class="col-span-2">
          <el-input v-model="productForm.shortDescription" type="textarea" :rows="2" resize="none" />
        </el-form-item>
        <el-form-item label="详细描述" class="col-span-2">
          <el-input v-model="productForm.description" type="textarea" :rows="4" resize="none" />
        </el-form-item>

        <el-form-item label="规格参数" class="col-span-2">
          <div class="w-full space-y-2">
            <div v-for="(spec, index) in productForm.specs" :key="index" class="grid grid-cols-[1fr_1fr_120px_36px] gap-2 items-center">
              <el-input v-model="spec.label" placeholder="Thickness" />
              <el-input v-model="spec.value" placeholder="1 - 100" />
              <el-input v-model="spec.unit" placeholder="mm" />
              <el-button link size="small" title="删除规格" @click="removeSpec(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button size="small" @click="addSpec">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加规格
            </el-button>
          </div>
        </el-form-item>

        <div class="col-span-2 border-t border-admin-700/60 pt-4 mt-1">
          <div class="grid grid-cols-2 gap-x-4">
            <el-form-item label="SEO 标题">
              <el-input v-model="productForm.seo.title" />
            </el-form-item>
            <el-form-item label="规范链接">
              <el-input v-model="productForm.seo.canonical" />
            </el-form-item>
            <el-form-item label="SEO 描述" class="col-span-2">
              <el-input v-model="productForm.seo.description" type="textarea" :rows="2" resize="none" />
            </el-form-item>
            <el-form-item label="OG 标题">
              <el-input v-model="productForm.seo.ogTitle" />
            </el-form-item>
            <el-form-item label="OG 图片">
              <el-input v-model="productForm.seo.ogImage" />
            </el-form-item>
            <el-form-item label="OG 描述" class="col-span-2">
              <el-input v-model="productForm.seo.ogDescription" type="textarea" :rows="2" resize="none" />
            </el-form-item>
          </div>
        </div>

        <div class="col-span-2 flex flex-wrap gap-6">
          <el-checkbox v-model="productForm.featured">首页推荐</el-checkbox>
          <el-checkbox v-model="productForm.inStock">现货/可供货</el-checkbox>
          <el-checkbox v-model="productForm.seo.noIndex">搜索引擎不收录</el-checkbox>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveProduct()">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRawFile } from 'element-plus'
import { apiRequest } from '@/api/client'
import { useMockStore } from '@/stores/mock'
import type { AdminContentOptions, ProductSavePayload } from '@/stores/mock'
import type { MediaAsset, ProductSpec, SeoMeta } from '@cnbjti/types'

const mock = useMockStore()
const dialogVisible = ref(false)
const editingProductId = ref('')
const saving = ref(false)
const uploadingImage = ref(false)

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '发布', value: 'published' },
]

const productForm = reactive<ProductSavePayload>(emptyProductForm())

onMounted(() => {
  void refreshProducts()
  void loadProductOptions()
})

async function refreshProducts() {
  await mock.loadProducts(true)
}

async function openCreate() {
  const options = await loadProductOptions()
  editingProductId.value = ''
  Object.assign(productForm, emptyProductForm(options.categories[0]?.id))
  dialogVisible.value = true
}

async function openEdit(id: string) {
  await loadProductOptions()
  const detail = await mock.getProductDetail(id)
  editingProductId.value = id
  Object.assign(productForm, {
    name: detail.name,
    slug: detail.slug,
    categoryId: detail.categoryId,
    gradeIds: [...detail.gradeIds],
    standardIds: [...detail.standardIds],
    status: detail.status,
    shortDescription: detail.shortDescription,
    description: detail.description,
    imageUrl: detail.imageUrl,
    images: normalizeImages(detail.images?.length ? detail.images : imageUrlToImages(detail.imageUrl, detail.name), detail.name),
    specs: normalizeSpecs(detail.specs),
    seo: detail.seo || emptySeo(),
    featured: detail.featured,
    inStock: detail.inStock,
  })
  refreshImageUrl()
  dialogVisible.value = true
}

async function loadProductOptions(force = false) {
  if (typeof mock.loadContentOptions === 'function') {
    return mock.loadContentOptions(force)
  }
  const currentOptions = mock.contentOptions || { categories: [], grades: [], standards: [] }
  if (!force && currentOptions.categories.length && currentOptions.grades.length && currentOptions.standards.length) {
    return currentOptions
  }
  const options = await apiRequest<AdminContentOptions>('/admin/content-options')
  mock.contentOptions = options
  return options
}

async function saveProduct() {
  if (!productForm.name.trim() || !productForm.slug.trim() || !productForm.categoryId) {
    ElMessage.warning('请填写产品名称、路径标识和分类')
    return
  }
  saving.value = true
  try {
    await mock.saveProduct(productPayload(), editingProductId.value || undefined)
    ElMessage.success('产品已保存')
    dialogVisible.value = false
  } finally {
    saving.value = false
  }
}

async function toggleStatus(id: string) {
  await mock.toggleProductStatus(id)
  ElMessage.success('状态已更新')
}

async function deleteProduct(id: string) {
  try {
    await ElMessageBox.confirm('删除后该产品将从前台产品列表移除，确定继续？', '删除产品', { type: 'warning' })
    await mock.deleteProduct(id)
    ElMessage.success('产品已删除')
  } catch {
    // User cancelled.
  }
}

function uploadProductImage(file: UploadRawFile) {
  void handleProductUpload(file)
  return false
}

async function handleProductUpload(file: File) {
  uploadingImage.value = true
  try {
    const asset = await mock.uploadFile(file)
    productForm.images.push(normalizeImage(asset, file.name))
    refreshImageUrl()
    ElMessage.success('图片已上传')
  } finally {
    uploadingImage.value = false
  }
}

function syncImageUrlToFirstImage() {
  const imageUrl = productForm.imageUrl.trim()
  if (!imageUrl) {
    refreshImageUrl()
    return
  }
  const image = imageUrlToImages(imageUrl, productForm.name)[0]
  if (productForm.images.length) {
    productForm.images[0] = { ...productForm.images[0], url: image.url, alt: productForm.images[0].alt || image.alt, filename: productForm.images[0].filename || image.filename }
    return
  }
  productForm.images.push(image)
}

function setPrimaryImage(index: number) {
  const [image] = productForm.images.splice(index, 1)
  if (image) productForm.images.unshift(image)
  refreshImageUrl()
}

function removeImage(index: number) {
  productForm.images.splice(index, 1)
  refreshImageUrl()
}

function refreshImageUrl() {
  productForm.imageUrl = productForm.images[0]?.url || ''
}

function addSpec() {
  productForm.specs.push({ label: '', value: '', unit: '' })
}

function removeSpec(index: number) {
  productForm.specs.splice(index, 1)
}

function fillProductSlug() {
  if (!productForm.slug && productForm.name) productForm.slug = slugify(productForm.name)
}

function productPayload(): ProductSavePayload {
  syncImageUrlToFirstImage()
  const images = normalizeImages(productForm.images)
  return {
    ...productForm,
    imageUrl: images[0]?.url || productForm.imageUrl.trim(),
    images,
    specs: normalizeSpecs(productForm.specs),
    seo: normalizeSeo(productForm.seo),
  }
}

function emptyProductForm(categoryId = ''): ProductSavePayload {
  return {
    name: '',
    slug: '',
    categoryId,
    gradeIds: [],
    standardIds: [],
    status: 'draft',
    shortDescription: '',
    description: '',
    imageUrl: '',
    images: [],
    specs: [{ label: 'Surface', value: 'Mill finish, polished', unit: '' }],
    seo: emptySeo(),
    featured: false,
    inStock: true,
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

function normalizeSeo(seo: SeoMeta): SeoMeta {
  return {
    title: seo.title?.trim() || '',
    description: seo.description?.trim() || '',
    canonical: seo.canonical?.trim() || '',
    ogTitle: seo.ogTitle?.trim() || '',
    ogDescription: seo.ogDescription?.trim() || '',
    ogImage: seo.ogImage?.trim() || '',
    noIndex: Boolean(seo.noIndex),
  }
}

function normalizeSpecs(specs: ProductSpec[] = []) {
  return specs
    .filter(spec => spec.label?.trim() && spec.value?.trim())
    .map(spec => ({
      label: spec.label.trim(),
      value: spec.value.trim(),
      unit: spec.unit?.trim() || '',
    }))
}

function normalizeImages(images: MediaAsset[] = [], productName = productForm.name) {
  return images
    .filter(image => image.url?.trim())
    .map((image, index) => normalizeImage(image, image.filename || `product-image-${index + 1}.jpg`, productName))
}

function normalizeImage(image: MediaAsset, fallbackName: string, productName = productForm.name): MediaAsset {
  const url = image.url.trim()
  return {
    id: image.id || `product-image-${Date.now()}-${Math.random().toString(16).slice(2)}`,
    url,
    thumbnailUrl: image.thumbnailUrl,
    alt: image.alt?.trim() || productName.trim() || fallbackName,
    width: image.width,
    height: image.height,
    mimeType: image.mimeType || 'image/jpeg',
    size: image.size || 0,
    filename: image.filename || filenameFromUrl(url, fallbackName),
  }
}

function imageUrlToImages(url: string, name: string): MediaAsset[] {
  const imageUrl = url.trim()
  if (!imageUrl) return []
  return [{
    id: `manual-${Date.now()}`,
    url: imageUrl,
    alt: name.trim() || filenameFromUrl(imageUrl, 'product-image.jpg'),
    mimeType: 'image/jpeg',
    size: 0,
    filename: filenameFromUrl(imageUrl, 'product-image.jpg'),
  }]
}

function filenameFromUrl(url: string, fallback: string) {
  try {
    const pathname = new URL(url).pathname
    const filename = pathname.split('/').filter(Boolean).pop()
    return filename || fallback
  } catch {
    return fallback
  }
}

function slugify(value: string) {
  return value
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
}
</script>
