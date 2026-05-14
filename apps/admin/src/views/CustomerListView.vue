<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">客户管理</h2>
        <p class="text-admin-500 text-xs mt-0.5">{{ filteredCustomers.length }} 家客户</p>
      </div>
      <el-input v-model="search" placeholder="搜索客户、国家、邮箱..." size="small" clearable class="w-56">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl overflow-hidden">
      <el-table :data="filteredCustomers" style="width: 100%" row-class-name="cursor-pointer" @row-click="(row: Customer) => router.push(`/customers/${row.id}`)">
        <el-table-column label="公司" min-width="220">
          <template #default="{ row }">
            <div class="flex items-center gap-2 min-w-0">
              <span class="text-xs font-bold text-admin-500 w-7">{{ row.countryCode }}</span>
              <span class="text-admin-200 text-sm font-medium truncate">{{ row.company }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="country" label="国家" width="150">
          <template #default="{ row }">
            <span class="text-admin-400 text-sm">{{ row.country }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="220">
          <template #default="{ row }">
            <a :href="`mailto:${row.email}`" class="text-accent-400 text-sm hover:text-accent-300 transition-colors" @click.stop>{{ row.email }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="rfqCount" label="询盘数" width="90" align="center">
          <template #default="{ row }">
            <span class="text-admin-300 font-semibold text-sm">{{ row.rfqCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lastContact" label="最近联系" width="120">
          <template #default="{ row }">
            <span class="text-admin-500 text-xs">{{ row.lastContact }}</span>
          </template>
        </el-table-column>
        <el-table-column label="" width="60" align="center">
          <template #default="{ row }">
            <el-button link size="small" @click.stop="router.push(`/customers/${row.id}`)">
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMockStore } from '@/stores/mock'
import type { Customer } from '@/stores/mock'

const mock = useMockStore()
const router = useRouter()
const search = ref('')

const filteredCustomers = computed<Customer[]>(() => {
  if (!search.value) return mock.customers
  const q = search.value.toLowerCase()
  return mock.customers.filter((c: Customer) =>
    c.company.toLowerCase().includes(q) ||
    c.country.toLowerCase().includes(q) ||
    c.email.toLowerCase().includes(q),
  )
})
</script>
