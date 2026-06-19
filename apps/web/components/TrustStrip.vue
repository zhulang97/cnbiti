<template>
  <div class="bg-white border-y border-titanium-200">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
      <div class="flex flex-wrap items-center justify-center gap-6 md:gap-10">
        <div v-for="item in items" :key="item.label" class="flex items-center gap-2 text-sm">
          <div class="w-5 h-5 text-accent-400 flex-shrink-0">
            <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          </div>
          <span class="text-titanium-700 font-medium">{{ item.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
const items = [
  { label: 'ASTM / ASME / AMS Certified' },
  { label: 'MTR Available (EN 10204 3.1)' },
  { label: 'Cut-to-size & CNC Machining' },
  { label: 'Small MOQ Welcome' },
  { label: 'Global Shipping' },
  { label: '15+ Years Export Experience' },
]
</script>
