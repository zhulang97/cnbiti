<template>
  <div class="min-h-screen bg-admin-950 flex items-center justify-center p-4">
    <div class="w-full max-w-sm">
      <!-- Logo -->
      <div class="flex items-center justify-center gap-3 mb-8">
        <div class="relative w-10 h-10">
          <div class="absolute inset-0 bg-accent-500 rounded-xl rotate-45" />
          <div class="absolute inset-1.5 bg-admin-950 rounded-lg rotate-45" />
          <span class="absolute inset-0 flex items-center justify-center text-accent-400 font-bold text-sm">Ti</span>
        </div>
        <div>
          <div class="text-admin-100 font-bold text-xl leading-none">CNBJTI</div>
          <div class="text-admin-500 text-xs leading-none mt-1 tracking-widest uppercase">管理后台</div>
        </div>
      </div>

      <!-- Card -->
      <div class="bg-admin-900 border border-admin-600/40 rounded-2xl p-8">
        <h1 class="text-admin-100 font-semibold text-lg mb-1">登录</h1>
        <p class="text-admin-500 text-sm mb-6">请输入账号密码继续</p>

        <div class="space-y-4">
          <div>
            <label class="block text-admin-400 text-xs font-medium mb-1.5 uppercase tracking-wide">用户名</label>
            <el-input
              v-model="form.username"
              placeholder="admin"
              size="large"
              @keyup.enter="handleLogin"
            />
          </div>
          <div>
            <label class="block text-admin-400 text-xs font-medium mb-1.5 uppercase tracking-wide">密码</label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="••••••••"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>

          <div v-if="error" class="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm">
            {{ error }}
          </div>

          <el-button
            type="primary"
            size="large"
            class="w-full !mt-6"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </div>

        <!-- Demo hint -->
        <div class="mt-6 p-3 bg-admin-800/50 border border-admin-600/30 rounded-lg">
          <p class="text-admin-500 text-xs text-center">
            演示账号：<span class="text-admin-300 font-mono">admin</span> / <span class="text-admin-300 font-mono">cnbjti2026</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  error.value = ''
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  const ok = await auth.login(form.username, form.password)
  loading.value = false
  if (ok) {
    await router.push('/dashboard')
  } else {
    error.value = '用户名或密码错误'
  }
}
</script>
