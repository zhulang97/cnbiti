<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">导航管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">维护前台主导航和下拉菜单</p>
      </div>
      <div class="flex items-center gap-2">
        <el-button size="small" :loading="loading" @click="void loadNavigation(true)">
          <el-icon class="mr-1"><Refresh /></el-icon>
          重新载入
        </el-button>
        <el-button size="small" @click="openTopItem()">
          <el-icon class="mr-1"><Plus /></el-icon>
          新增一级菜单
        </el-button>
        <el-button type="primary" size="small" :loading="saving" @click="void saveNavigation()">
          <el-icon class="mr-1"><Check /></el-icon>
          保存
        </el-button>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table :data="items" style="width: 100%" row-key="label">
        <el-table-column type="expand" width="44">
          <template #default="{ row, $index: parentIndex }">
            <div class="pl-8 pr-4 py-3">
              <div class="flex items-center justify-between mb-3">
                <span class="text-admin-400 text-xs">子菜单</span>
                <el-button size="small" link @click="openChildItem(parentIndex)">
                  <el-icon class="mr-1"><Plus /></el-icon>
                  新增子菜单
                </el-button>
              </div>
              <el-table v-if="row.children?.length" :data="row.children" size="small" style="width: 100%">
                <el-table-column label="名称" min-width="180">
                  <template #default="{ row: child }">
                    <span class="text-admin-200 text-sm">{{ child.label }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="href" label="链接" min-width="220" />
                <el-table-column prop="icon" label="图标" width="100" />
                <el-table-column label="操作" width="160" align="center">
                  <template #default="{ $index: childIndex }">
                    <el-button link size="small" title="编辑" @click="openChildItem(parentIndex, childIndex)">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button link size="small" title="上移" :disabled="childIndex === 0 || saving" @click="void moveChild(parentIndex, childIndex, -1)">
                      <el-icon><ArrowUp /></el-icon>
                    </el-button>
                    <el-button link size="small" title="下移" :disabled="childIndex === row.children.length - 1 || saving" @click="void moveChild(parentIndex, childIndex, 1)">
                      <el-icon><ArrowDown /></el-icon>
                    </el-button>
                    <el-button link size="small" title="删除" :disabled="saving" @click="void deleteChild(parentIndex, childIndex)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div v-else class="text-admin-500 text-xs py-3">暂无子菜单</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="名称" min-width="180">
          <template #default="{ row }">
            <div class="text-admin-200 text-sm font-medium">{{ row.label }}</div>
            <div v-if="row.badge" class="text-accent-400 text-xs mt-0.5">{{ row.badge }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="href" label="链接" min-width="220">
          <template #default="{ row }">
            <span class="text-admin-400 text-xs">{{ row.href || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <span class="text-admin-400 text-xs">{{ row.icon || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="子菜单" width="90">
          <template #default="{ row }">
            <span class="text-admin-400 text-xs">{{ row.children?.length || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190" align="center">
          <template #default="{ $index }">
            <el-button link size="small" title="编辑" @click="openTopItem($index)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button link size="small" title="添加子菜单" @click="openChildItem($index)">
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button link size="small" title="上移" :disabled="$index === 0 || saving" @click="void moveTop($index, -1)">
              <el-icon><ArrowUp /></el-icon>
            </el-button>
            <el-button link size="small" title="下移" :disabled="$index === items.length - 1 || saving" @click="void moveTop($index, 1)">
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <el-button link size="small" title="删除" :disabled="saving" @click="void deleteTop($index)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
        <el-form-item label="名称" required>
          <el-input v-model="itemForm.label" />
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="itemForm.href" placeholder="/en/contact" />
        </el-form-item>
        <el-form-item label="角标">
          <el-input v-model="itemForm.badge" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="itemForm.icon" placeholder="bar / sheet / user" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="void saveItem()">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { NavigationItem } from '@cnbjti/types'

const mock = useMockStore()
const items = ref<NavigationItem[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingTopIndex = ref<number | null>(null)
const editingChildIndex = ref<number | null>(null)
const itemMode = ref<'create-top' | 'edit-top' | 'create-child' | 'edit-child'>('create-top')
const itemForm = reactive({
  label: '',
  href: '',
  badge: '',
  icon: '',
})

const dialogTitle = computed(() => {
  if (itemMode.value === 'edit-top') return '编辑一级菜单'
  if (itemMode.value === 'create-child') return '新增子菜单'
  if (itemMode.value === 'edit-child') return '编辑子菜单'
  return '新增一级菜单'
})

onMounted(() => {
  void loadNavigation()
})

async function loadNavigation(force = false) {
  loading.value = true
  try {
    items.value = cloneItems(await mock.loadNavigation(force))
  } finally {
    loading.value = false
  }
}

async function saveNavigation() {
  try {
    await persistNavigation('导航已保存')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '导航保存失败')
  }
}

async function persistNavigation(successMessage: string) {
  const payload = normalizeItems(items.value)
  if (!payload.length) {
    ElMessage.warning('请至少保留一个导航项')
    return false
  }

  saving.value = true
  try {
    items.value = cloneItems(await mock.saveNavigation(payload))
    ElMessage.success(successMessage)
    return true
  } finally {
    saving.value = false
  }
}

function openTopItem(index?: number) {
  editingTopIndex.value = index ?? null
  editingChildIndex.value = null
  itemMode.value = index === undefined ? 'create-top' : 'edit-top'
  const item = index === undefined ? null : items.value[index]
  fillForm(item)
  dialogVisible.value = true
}

function openChildItem(parentIndex: number, childIndex?: number) {
  editingTopIndex.value = parentIndex
  editingChildIndex.value = childIndex ?? null
  itemMode.value = childIndex === undefined ? 'create-child' : 'edit-child'
  const child = childIndex === undefined ? null : items.value[parentIndex]?.children?.[childIndex]
  fillForm(child)
  dialogVisible.value = true
}

async function saveItem() {
  if (!itemForm.label.trim()) {
    ElMessage.warning('请填写名称')
    return
  }

  const saved = await commitNavigationChange(() => {
    const item = normalizeItem({ ...itemForm })
    if (itemMode.value === 'create-top') {
      items.value.push(item)
    } else if (itemMode.value === 'edit-top' && editingTopIndex.value !== null) {
      const previous = items.value[editingTopIndex.value]
      items.value[editingTopIndex.value] = { ...item, children: previous.children }
    } else if (itemMode.value === 'create-child') {
      const parent = ensureChildren(editingTopIndex.value)
      parent.children?.push(item)
    } else if (editingChildIndex.value !== null) {
      const parent = ensureChildren(editingTopIndex.value)
      if (parent.children) parent.children[editingChildIndex.value] = item
    }
  }, '导航项已保存')
  if (saved) dialogVisible.value = false
}

async function deleteTop(index: number) {
  try {
    await ElMessageBox.confirm('删除后该导航项和它的子菜单都会移除，确定继续？', '删除导航', { type: 'warning' })
    await commitNavigationChange(() => {
      items.value.splice(index, 1)
    }, '导航项已删除')
  } catch {
    // User cancelled.
  }
}

async function deleteChild(parentIndex: number, childIndex: number) {
  await commitNavigationChange(() => {
    const parent = ensureChildren(parentIndex)
    parent.children?.splice(childIndex, 1)
    if (!parent.children?.length) parent.children = undefined
  }, '导航项已删除')
}

async function moveTop(index: number, offset: number) {
  await commitNavigationChange(() => move(items.value, index, index + offset), '导航排序已保存')
}

async function moveChild(parentIndex: number, childIndex: number, offset: number) {
  await commitNavigationChange(() => {
    const parent = ensureChildren(parentIndex)
    return parent.children ? move(parent.children, childIndex, childIndex + offset) : false
  }, '导航排序已保存')
}

function ensureChildren(parentIndex: number | null) {
  if (parentIndex === null) throw new Error('Parent navigation item is required')
  const parent = items.value[parentIndex]
  if (!parent.children) parent.children = []
  return parent
}

function fillForm(item?: NavigationItem | null) {
  itemForm.label = item?.label || ''
  itemForm.href = item?.href || ''
  itemForm.badge = item?.badge || ''
  itemForm.icon = item?.icon || ''
}

function normalizeItems(values: NavigationItem[]) {
  return values.map(normalizeItem).filter(item => item.label)
}

function normalizeItem(item: NavigationItem): NavigationItem {
  const children = item.children ? normalizeItems(item.children) : []
  return {
    label: item.label.trim(),
    href: item.href?.trim() || undefined,
    children: children.length ? children : undefined,
    badge: item.badge?.trim() || undefined,
    icon: item.icon?.trim() || undefined,
  }
}

function cloneItems(values: NavigationItem[]): NavigationItem[] {
  return values.map(item => ({
    ...item,
    children: item.children ? cloneItems(item.children) : undefined,
  }))
}

async function commitNavigationChange(change: () => boolean | void, successMessage: string) {
  const previous = cloneItems(items.value)
  try {
    const changed = change()
    if (changed === false) return false
    const saved = await persistNavigation(successMessage)
    if (!saved) items.value = previous
    return saved
  } catch (error) {
    items.value = previous
    ElMessage.error(error instanceof Error ? error.message : '导航保存失败')
    return false
  }
}

function move<T>(values: T[], from: number, to: number) {
  if (to < 0 || to >= values.length) return false
  const [item] = values.splice(from, 1)
  values.splice(to, 0, item)
  return true
}
</script>
