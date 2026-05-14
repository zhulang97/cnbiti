<template>
  <div class="flex h-screen bg-admin-950 overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-60 flex-shrink-0 bg-admin-900 border-r border-admin-600/40 flex flex-col">
      <!-- Logo -->
      <div class="h-16 flex items-center gap-3 px-5 border-b border-admin-600/40">
        <div class="relative w-8 h-8 flex-shrink-0">
          <div class="absolute inset-0 bg-accent-500 rounded-lg rotate-45" />
          <div class="absolute inset-1 bg-admin-900 rounded-md rotate-45" />
          <span class="absolute inset-0 flex items-center justify-center text-accent-400 font-bold text-xs">Ti</span>
        </div>
        <div>
          <div class="text-admin-100 font-bold text-sm leading-none">CNBJTI</div>
          <div class="text-admin-400 text-[10px] leading-none mt-0.5 tracking-wide">管理后台</div>
        </div>
      </div>

      <!-- Nav -->
      <nav class="flex-1 py-4 px-3 space-y-0.5 overflow-y-auto">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors duration-150"
          :class="isActive(item.to)
            ? 'bg-accent-500/15 text-accent-400 font-medium'
            : 'text-admin-400 hover:text-admin-100 hover:bg-admin-700/50'"
        >
          <el-icon class="flex-shrink-0"><component :is="item.icon" /></el-icon>
          {{ item.label }}
          <span v-if="item.badge" class="ml-auto text-xs bg-accent-500/20 text-accent-400 px-1.5 py-0.5 rounded-full font-mono">{{ item.badge }}</span>
        </RouterLink>
      </nav>

      <!-- User -->
      <div class="p-3 border-t border-admin-600/40">
        <div class="flex items-center gap-3 px-3 py-2">
          <div class="w-8 h-8 rounded-full bg-accent-500/20 border border-accent-500/30 flex items-center justify-center flex-shrink-0">
            <span class="text-accent-400 text-xs font-bold uppercase">{{ auth.username.charAt(0) }}</span>
          </div>
          <div class="flex-1 min-w-0">
            <div class="text-admin-200 text-sm font-medium truncate">{{ auth.username }}</div>
            <div class="text-admin-500 text-xs">管理员</div>
          </div>
          <button @click="handleLogout" class="text-admin-500 hover:text-red-400 transition-colors" title="Logout">
            <el-icon><SwitchButton /></el-icon>
          </button>
        </div>
      </div>
    </aside>

    <!-- Main -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <!-- Header -->
      <header class="h-16 bg-admin-900/80 backdrop-blur border-b border-admin-600/40 flex items-center justify-between px-6 flex-shrink-0">
        <div>
          <h1 class="text-admin-100 font-semibold text-base">{{ pageTitle }}</h1>
          <p class="text-admin-500 text-xs">{{ currentDate }}</p>
        </div>
        <div class="flex items-center gap-3">
          <a href="http://localhost:3002/en" target="_blank" class="flex items-center gap-1.5 text-xs text-admin-400 hover:text-accent-400 transition-colors px-3 py-1.5 rounded-lg hover:bg-admin-700/50">
            <el-icon><TopRight /></el-icon>
            查看网站
          </a>
        </div>
      </header>

      <!-- Content -->
      <main class="flex-1 overflow-y-auto p-6">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { RouterLink, RouterView, useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMockStore } from '@/stores/mock'
import type { AdminContactMessage, Rfq } from '@/stores/mock'

const auth = useAuthStore()
const mock = useMockStore()
const router = useRouter()
const route = useRoute()

const rfqList = computed<Rfq[]>(() => Array.isArray(mock.rfqs) ? mock.rfqs : [])
const contactMessageList = computed<AdminContactMessage[]>(() =>
  Array.isArray(mock.contactMessages) ? mock.contactMessages : [],
)

const newRfqCount = computed(() => rfqList.value.filter((r) => r.status === 'new').length)
const newMessageCount = computed(() => contactMessageList.value.filter((message) => message.status === 'new').length)

const navItems = computed(() => [
  { label: '数据概览', to: '/dashboard', icon: 'Grid' },
  { label: '站点设置', to: '/site-config', icon: 'Setting', roles: ['ADMIN'] },
  { label: '导航管理', to: '/navigation', icon: 'Menu', roles: ['ADMIN'] },
  { label: '询盘管理', to: '/rfq', icon: 'Document', badge: newRfqCount.value || undefined, roles: ['ADMIN', 'SALES'] },
  { label: '产品管理', to: '/products', icon: 'Box', roles: ['ADMIN', 'EDITOR'] },
  { label: '媒体资源', to: '/media', icon: 'Picture', roles: ['ADMIN', 'EDITOR'] },
  { label: '操作日志', to: '/audit-logs', icon: 'Tickets', roles: ['ADMIN'] },
  { label: '账号管理', to: '/users', icon: 'UserFilled', roles: ['ADMIN'] },
  { label: '基础资料', to: '/reference-data', icon: 'Collection', roles: ['ADMIN', 'EDITOR'] },
  { label: '文章管理', to: '/articles', icon: 'Reading', roles: ['ADMIN', 'EDITOR'] },
  { label: '留言管理', to: '/contact-messages', icon: 'Message', badge: newMessageCount.value || undefined, roles: ['ADMIN', 'SALES'] },
  { label: '客户管理', to: '/customers', icon: 'User', roles: ['ADMIN', 'SALES'] },
].filter((item) => !item.roles || item.roles.includes(auth.role)))

const pageTitles: Record<string, string> = {
  dashboard: '数据概览',
  'site-config': '站点设置',
  navigation: '导航管理',
  'rfq-list': '询盘管理',
  'rfq-detail': '询盘详情',
  products: '产品管理',
  media: '媒体资源',
  'audit-logs': '操作日志',
  users: '账号管理',
  'reference-data': '基础资料',
  articles: '文章管理',
  'contact-messages': '留言管理',
  customers: '客户管理',
  'customer-detail': '客户详情',
}

const pageTitle = computed(() => pageTitles[route.name as string] || '管理后台')

const currentDate = computed(() => new Date().toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }))

onMounted(() => {
  void mock.loadAll()
})

function isActive(to: string) {
  if (to === '/dashboard') return route.path === '/dashboard'
  return route.path.startsWith(to)
}

async function handleLogout() {
  auth.logout()
  await router.push('/login')
}
</script>
