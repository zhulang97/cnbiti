<template>
  <div class="space-y-6">
    <!-- Stat cards -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <div v-for="stat in stats" :key="stat.label" class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <div class="flex items-start justify-between mb-3">
          <div class="w-9 h-9 rounded-lg flex items-center justify-center" :class="stat.iconBg">
            <el-icon :size="18" :class="stat.iconColor"><component :is="stat.icon" /></el-icon>
          </div>
          <span class="text-xs font-medium px-2 py-0.5 rounded-full" :class="stat.trendClass">{{ stat.trend }}</span>
        </div>
        <div class="text-2xl font-bold text-admin-100 mb-0.5">{{ stat.value }}</div>
        <div class="text-admin-500 text-xs">{{ stat.label }}</div>
      </div>
    </div>

    <!-- Charts row -->
    <div class="grid lg:grid-cols-3 gap-4">
      <!-- RFQ Trend -->
      <div class="lg:col-span-2 bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <h3 class="text-admin-200 font-semibold text-sm mb-4">询盘趋势 — 近12个月</h3>
        <div ref="lineChartEl" class="h-52" />
      </div>

      <!-- RFQ by Country -->
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <h3 class="text-admin-200 font-semibold text-sm mb-4">询盘来源国家</h3>
        <div ref="pieChartEl" class="h-52" />
      </div>
    </div>

    <!-- Bottom row -->
    <div class="grid lg:grid-cols-3 gap-4">
      <!-- RFQ by Product -->
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <h3 class="text-admin-200 font-semibold text-sm mb-4">询盘产品分布</h3>
        <div ref="barChartEl" class="h-52" />
      </div>

      <!-- Recent RFQs -->
      <div class="lg:col-span-2 bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-admin-200 font-semibold text-sm">最新询盘</h3>
          <RouterLink to="/rfq" class="text-xs text-accent-400 hover:text-accent-300 transition-colors">查看全部 →</RouterLink>
        </div>
        <div class="space-y-2">
          <div
            v-for="rfq in recentRfqs"
            :key="rfq.id"
            class="flex items-center gap-3 p-3 rounded-lg hover:bg-admin-800/50 transition-colors cursor-pointer"
            @click="router.push(`/rfq/${rfq.id}`)"
          >
            <div class="w-8 h-8 rounded-full bg-admin-700 flex items-center justify-center flex-shrink-0 text-xs font-bold text-admin-300">
              {{ rfq.countryCode }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-admin-200 text-sm font-medium truncate">{{ rfq.company }}</div>
              <div class="text-admin-500 text-xs">{{ rfq.product }} · {{ rfq.grade }}</div>
            </div>
            <div class="flex flex-col items-end gap-1">
              <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="statusClass(rfq.status)">{{ statusText(rfq.status) }}</span>
              <span class="text-admin-600 text-xs">{{ rfq.createdAt }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { useMockStore } from '@/stores/mock'
import type { AdminProduct, Rfq, RfqStatus } from '@/stores/mock'

const mock = useMockStore()
const router = useRouter()

const lineChartEl = ref<HTMLElement>()
const pieChartEl = ref<HTMLElement>()
const barChartEl = ref<HTMLElement>()

let lineChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

const stats = computed(() => [
  {
    label: '今日询盘',
    value: mock.dashboard.todayRfqs,
    trend: '+2 较昨日',
    trendClass: 'bg-green-500/10 text-green-400',
    icon: 'Document',
    iconBg: 'bg-accent-500/10',
    iconColor: 'text-accent-400',
  },
  {
    label: '新询盘（未读）',
    value: mock.dashboard.newRfqs || mock.rfqs.filter((r: Rfq) => r.status === 'new').length,
    trend: '待处理',
    trendClass: 'bg-yellow-500/10 text-yellow-400',
    icon: 'Bell',
    iconBg: 'bg-yellow-500/10',
    iconColor: 'text-yellow-400',
  },
  {
    label: '跟进中',
    value: mock.dashboard.needFollowUp || mock.rfqs.filter((r: Rfq) => r.status === 'in_progress').length,
    trend: '进行中',
    trendClass: 'bg-blue-500/10 text-blue-400',
    icon: 'Loading',
    iconBg: 'bg-blue-500/10',
    iconColor: 'text-blue-400',
  },
  {
    label: '已发布产品',
    value: mock.dashboard.publishedProducts || mock.products.filter((p: AdminProduct) => p.status === 'published').length,
    trend: '线上展示',
    trendClass: 'bg-green-500/10 text-green-400',
    icon: 'Box',
    iconBg: 'bg-green-500/10',
    iconColor: 'text-green-400',
  },
])

const recentRfqs = computed<Rfq[]>(() => mock.rfqs.slice(0, 5))

function statusClass(status: RfqStatus) {
  const map: Record<RfqStatus, string> = {
    new: 'bg-accent-500/15 text-accent-400',
    in_progress: 'bg-yellow-500/15 text-yellow-400',
    quoted: 'bg-purple-500/15 text-purple-400',
    won: 'bg-green-500/15 text-green-400',
    lost: 'bg-red-500/15 text-red-400',
  }
  return map[status]
}

const statusLabel: Record<RfqStatus, string> = {
  new: '新询盘',
  in_progress: '跟进中',
  quoted: '已报价',
  won: '已成交',
  lost: '已失单',
}

function statusText(status: RfqStatus) {
  return statusLabel[status]
}

const chartTheme = {
  textColor: '#8b949e',
  lineColor: '#30363d',
  axisColor: '#484f58',
}

function initLineChart() {
  if (!lineChartEl.value) return
  lineChart = echarts.init(lineChartEl.value, 'dark')
  lineChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 10, right: 10, bottom: 30, left: 40 },
    xAxis: {
      type: 'category',
      data: ['Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Feb', 'Mar', 'Apr', 'May'],
      axisLine: { lineStyle: { color: chartTheme.lineColor } },
      axisLabel: { color: chartTheme.textColor, fontSize: 11 },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: chartTheme.lineColor } },
      axisLabel: { color: chartTheme.textColor, fontSize: 11 },
    },
    series: [{
      data: [4, 6, 3, 8, 5, 9, 7, 11, 6, 8, 12, 10],
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 5,
      lineStyle: { color: '#0ea5e9', width: 2 },
      itemStyle: { color: '#0ea5e9' },
      areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(14,165,233,0.25)' }, { offset: 1, color: 'rgba(14,165,233,0)' }] } },
    }],
    tooltip: { trigger: 'axis', backgroundColor: '#21262d', borderColor: '#30363d', textStyle: { color: '#e6edf3' } },
  })
}

function initPieChart() {
  if (!pieChartEl.value) return
  pieChart = echarts.init(pieChartEl.value, 'dark')
  pieChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', backgroundColor: '#21262d', borderColor: '#30363d', textStyle: { color: '#e6edf3' } },
    legend: { show: false },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '50%'],
      data: [
        { value: 28, name: 'United States', itemStyle: { color: '#0ea5e9' } },
        { value: 19, name: 'Germany', itemStyle: { color: '#38bdf8' } },
        { value: 15, name: 'India', itemStyle: { color: '#7dd3fc' } },
        { value: 12, name: 'South Korea', itemStyle: { color: '#0284c7' } },
        { value: 10, name: 'Japan', itemStyle: { color: '#075985' } },
        { value: 36, name: 'Others', itemStyle: { color: '#30363d' } },
      ],
      label: { show: true, color: '#8b949e', fontSize: 10, formatter: '{b}\n{d}%' },
      labelLine: { lineStyle: { color: '#484f58' } },
    }],
  })
}

function initBarChart() {
  if (!barChartEl.value) return
  barChart = echarts.init(barChartEl.value, 'dark')
  barChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 10, right: 20, bottom: 10, left: 10, containLabel: true },
    xAxis: { type: 'value', axisLabel: { color: chartTheme.textColor, fontSize: 10 }, splitLine: { lineStyle: { color: chartTheme.lineColor } } },
    yAxis: {
      type: 'category',
      data: ['Others', 'Forgings', 'CNC Parts', 'Tube', 'Sheet', 'Bar'],
      axisLabel: { color: chartTheme.textColor, fontSize: 10 },
      axisLine: { show: false },
      axisTick: { show: false },
    },
    series: [{
      type: 'bar',
      data: [8, 10, 18, 22, 28, 34],
      barMaxWidth: 16,
      itemStyle: { color: '#0ea5e9', borderRadius: [0, 4, 4, 0] },
    }],
    tooltip: { trigger: 'axis', backgroundColor: '#21262d', borderColor: '#30363d', textStyle: { color: '#e6edf3' } },
  })
}

function handleResize() {
  lineChart?.resize()
  pieChart?.resize()
  barChart?.resize()
}

onMounted(() => {
  initLineChart()
  initPieChart()
  initBarChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
  pieChart?.dispose()
  barChart?.dispose()
})
</script>
