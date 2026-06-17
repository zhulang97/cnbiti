<template>
  <svg :class="$attrs.class" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
    <!-- Bar/Rod -->
    <template v-if="icon === 'bar'">
      <rect x="2" y="10" width="20" height="4" rx="2" />
      <rect x="2" y="6" width="20" height="2" rx="1" opacity="0.5" />
      <rect x="2" y="16" width="20" height="2" rx="1" opacity="0.5" />
    </template>
    <!-- Sheet/Plate -->
    <template v-else-if="icon === 'sheet'">
      <rect x="3" y="5" width="18" height="14" rx="1" />
      <line x1="3" y1="9" x2="21" y2="9" opacity="0.5" />
      <line x1="3" y1="13" x2="21" y2="13" opacity="0.5" />
    </template>
    <!-- Tube/Pipe -->
    <template v-else-if="icon === 'tube'">
      <ellipse cx="12" cy="6" rx="8" ry="3" />
      <line x1="4" y1="6" x2="4" y2="18" />
      <line x1="20" y1="6" x2="20" y2="18" />
      <ellipse cx="12" cy="18" rx="8" ry="3" />
      <ellipse cx="12" cy="18" rx="4" ry="1.5" opacity="0.5" />
    </template>
    <!-- Wire/Coil -->
    <template v-else-if="icon === 'wire'">
      <path d="M4 12 Q6 8 8 12 Q10 16 12 12 Q14 8 16 12 Q18 16 20 12" stroke-linecap="round" />
    </template>
    <!-- Forging/Hammer -->
    <template v-else-if="icon === 'forging'">
      <rect x="8" y="2" width="8" height="6" rx="1" />
      <rect x="10" y="8" width="4" height="10" rx="1" />
      <rect x="6" y="18" width="12" height="3" rx="1" />
    </template>
    <!-- Fastener/Bolt -->
    <template v-else-if="icon === 'fastener'">
      <circle cx="12" cy="8" r="4" />
      <line x1="12" y1="12" x2="12" y2="20" stroke-width="2.5" />
      <line x1="9" y1="16" x2="15" y2="16" />
      <line x1="9" y1="19" x2="15" y2="19" />
    </template>
    <!-- Fitting -->
    <template v-else-if="icon === 'fitting'">
      <path d="M4 12 L8 12 L8 8 L16 8 L16 12 L20 12" stroke-linecap="round" stroke-linejoin="round" />
      <path d="M4 12 L8 12 L8 16 L16 16 L16 12 L20 12" stroke-linecap="round" stroke-linejoin="round" />
    </template>
    <!-- Electrode/Anode -->
    <template v-else-if="icon === 'electrode'">
      <rect x="9" y="2" width="6" height="16" rx="1" />
      <line x1="6" y1="18" x2="18" y2="18" />
      <line x1="4" y1="21" x2="20" y2="21" />
      <line x1="12" y1="2" x2="12" y2="0" />
    </template>
    <!-- Filter -->
    <template v-else-if="icon === 'filter'">
      <circle cx="12" cy="12" r="8" />
      <circle cx="12" cy="12" r="4" opacity="0.5" />
      <circle cx="12" cy="12" r="1" fill="currentColor" stroke="none" />
      <circle cx="8" cy="8" r="0.8" fill="currentColor" stroke="none" opacity="0.6" />
      <circle cx="16" cy="8" r="0.8" fill="currentColor" stroke="none" opacity="0.6" />
      <circle cx="8" cy="16" r="0.8" fill="currentColor" stroke="none" opacity="0.6" />
      <circle cx="16" cy="16" r="0.8" fill="currentColor" stroke="none" opacity="0.6" />
    </template>
    <!-- CNC -->
    <template v-else-if="icon === 'cnc'">
      <circle cx="12" cy="12" r="3" />
      <path d="M12 2 L12 6 M12 18 L12 22 M2 12 L6 12 M18 12 L22 12" stroke-linecap="round" />
      <path d="M5.6 5.6 L8.5 8.5 M15.5 15.5 L18.4 18.4 M5.6 18.4 L8.5 15.5 M15.5 8.5 L18.4 5.6" stroke-linecap="round" opacity="0.6" />
    </template>
    <!-- Default -->
    <template v-else>
      <rect x="4" y="4" width="16" height="16" rx="2" />
    </template>
  </svg>
</template>

<script setup lang="ts">
defineProps<{ icon: string }>()
</script>
