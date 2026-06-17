<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between gap-4">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">账号管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">管理后台账号、角色、状态和密码重置</p>
      </div>
      <div class="flex items-center gap-2">
        <el-button size="small" :loading="loading" @click="void loadUsers(true)">
          <el-icon class="mr-1"><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button size="small" type="primary" @click="openCreate">
          <el-icon class="mr-1"><Plus /></el-icon>
          新建账号
        </el-button>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div v-for="item in summary" :key="item.label" class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
        <div class="text-admin-500 text-xs">{{ item.label }}</div>
        <div class="text-admin-100 text-xl font-semibold mt-2">{{ item.value }}</div>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-4">
      <div class="grid grid-cols-1 md:grid-cols-[minmax(220px,1fr)_160px_160px] gap-3">
        <el-input v-model="search" clearable placeholder="搜索用户名、姓名、邮箱">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="roleFilter" clearable placeholder="全部角色">
          <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="statusFilter" clearable placeholder="全部状态">
          <el-option label="启用" value="active" />
          <el-option label="停用" value="disabled" />
        </el-select>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table v-loading="loading" :data="filteredUsers" style="width: 100%" empty-text="暂无账号">
        <el-table-column label="账号" min-width="220">
          <template #default="{ row }">
            <div class="text-admin-100 text-sm font-medium">{{ row.displayName }}</div>
            <div class="text-admin-500 text-xs font-mono">{{ row.username }}</div>
          </template>
        </el-table-column>
        <el-table-column label="邮箱" min-width="220">
          <template #default="{ row }">
            <span class="text-admin-300 text-sm">{{ row.email }}</span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="row.role === 'ADMIN' ? 'danger' : row.role === 'EDITOR' ? 'warning' : 'info'">
              {{ roleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最近登录" width="170">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ formatDate(row.lastLoginAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新" width="170">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ formatDate(row.updatedAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-button size="small" text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button size="small" text type="warning" @click="openPassword(row)">重置密码</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="userDialogVisible" :title="editingUserId ? '编辑账号' : '新建账号'" width="560px">
      <el-form label-width="92px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="letters, numbers, dots, underscores, dashes" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="userForm.displayName" placeholder="显示在后台的姓名" />
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input v-model="userForm.email" placeholder="name@example.com" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="userForm.role" class="w-full">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" required>
          <el-segmented v-model="userForm.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="头像 URL">
          <el-input v-model="userForm.avatar" clearable placeholder="可选" />
        </el-form-item>
        <el-form-item v-if="!editingUserId" label="初始密码" required>
          <el-input v-model="userForm.password" show-password placeholder="至少 8 位" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="重置密码" width="420px">
      <div class="space-y-3">
        <div class="text-admin-300 text-sm">
          为 <span class="text-admin-100 font-medium">{{ passwordUser?.displayName }}</span> 设置新密码
        </div>
        <el-input v-model="newPassword" show-password placeholder="至少 8 位" />
      </div>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingPassword" @click="resetPassword">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useMockStore } from '@/stores/mock'
import type { AdminManagedUser, AdminUserSavePayload } from '@/stores/mock'

const mock = useMockStore()
const loading = ref(false)
const saving = ref(false)
const savingPassword = ref(false)
const search = ref('')
const roleFilter = ref('')
const statusFilter = ref('')
const userDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const editingUserId = ref('')
const passwordUser = ref<AdminManagedUser | null>(null)
const newPassword = ref('')

const roleOptions = [
  { label: '管理员', value: 'ADMIN' },
  { label: '编辑', value: 'EDITOR' },
  { label: '销售', value: 'SALES' },
] as const

const statusOptions = [
  { label: '启用', value: 'active' },
  { label: '停用', value: 'disabled' },
]

const userForm = reactive<AdminUserSavePayload>({
  username: '',
  displayName: '',
  email: '',
  role: 'SALES',
  status: 'active',
  avatar: '',
  password: '',
})

const filteredUsers = computed(() => {
  const keyword = search.value.trim().toLowerCase()
  return mock.adminUsers.filter((user: AdminManagedUser) => {
    const matchesKeyword = !keyword
      || user.username.toLowerCase().includes(keyword)
      || user.displayName.toLowerCase().includes(keyword)
      || user.email.toLowerCase().includes(keyword)
    const matchesRole = !roleFilter.value || user.role === roleFilter.value
    const matchesStatus = !statusFilter.value || user.status === statusFilter.value
    return matchesKeyword && matchesRole && matchesStatus
  })
})

const summary = computed(() => [
  { label: '全部账号', value: mock.adminUsers.length },
  { label: '启用账号', value: mock.adminUsers.filter((user: AdminManagedUser) => user.status === 'active').length },
  { label: '停用账号', value: mock.adminUsers.filter((user: AdminManagedUser) => user.status === 'disabled').length },
  { label: '管理员', value: mock.adminUsers.filter((user: AdminManagedUser) => user.role === 'ADMIN').length },
])

onMounted(() => {
  void loadUsers()
})

async function loadUsers(force = false) {
  loading.value = true
  try {
    await mock.loadAdminUsers(force)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingUserId.value = ''
  Object.assign(userForm, {
    username: '',
    displayName: '',
    email: '',
    role: 'SALES',
    status: 'active',
    avatar: '',
    password: '',
  })
  userDialogVisible.value = true
}

function openEdit(user: AdminManagedUser) {
  editingUserId.value = user.id
  Object.assign(userForm, {
    username: user.username,
    displayName: user.displayName,
    email: user.email,
    role: user.role,
    status: user.status,
    avatar: user.avatar || '',
    password: undefined,
  })
  userDialogVisible.value = true
}

async function saveUser() {
  if (!editingUserId.value && (!userForm.password || userForm.password.length < 8)) {
    ElMessage.warning('初始密码至少 8 位')
    return
  }
  saving.value = true
  try {
    const payload = { ...userForm }
    if (editingUserId.value) {
      delete payload.password
      await mock.updateAdminUser(editingUserId.value, payload)
      ElMessage.success('账号已更新')
    } else {
      await mock.createAdminUser(payload)
      ElMessage.success('账号已创建')
    }
    userDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

function openPassword(user: AdminManagedUser) {
  passwordUser.value = user
  newPassword.value = ''
  passwordDialogVisible.value = true
}

async function resetPassword() {
  if (!passwordUser.value) return
  if (newPassword.value.length < 8) {
    ElMessage.warning('新密码至少 8 位')
    return
  }
  savingPassword.value = true
  try {
    await mock.resetAdminUserPassword(passwordUser.value.id, newPassword.value)
    ElMessage.success('密码已重置')
    passwordDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '重置失败')
  } finally {
    savingPassword.value = false
  }
}

function roleText(role: string) {
  const match = roleOptions.find((item) => item.value === role)
  return match?.label || role
}

function formatDate(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}
</script>
