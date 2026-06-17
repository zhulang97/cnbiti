<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-16 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <NuxtLink :to="localePath('/resources')" class="hover:text-accent-400 transition-colors">Resources</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Weight Calculator</span>
        </nav>
        <div class="section-label">Free Tool</div>
        <h1 class="section-title mt-3 mb-4">Titanium Weight Calculator</h1>
        <p class="text-titanium-300 text-lg max-w-2xl">
          Calculate the weight of titanium bar, sheet, tube or wire. Select your grade for accurate density.
        </p>
      </div>
    </section>

    <!-- Calculator -->
    <section class="bg-titanium-950 py-12 pb-24">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-5 gap-8">
          <!-- Input panel -->
          <div class="lg:col-span-3 card p-8">
            <!-- Shape selector -->
            <div class="mb-8">
              <label class="block text-titanium-400 text-sm font-medium mb-3">Shape / Form</label>
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <button v-for="shape in shapes" :key="shape.id"
                  @click="selectedShape = shape.id"
                  :class="[
                    'flex flex-col items-center gap-2 p-3 rounded-xl border text-xs font-medium transition-all',
                    selectedShape === shape.id
                      ? 'bg-accent-500/10 border-accent-500/50 text-accent-400'
                      : 'bg-titanium-900 border-titanium-700 text-titanium-400 hover:border-titanium-600'
                  ]">
                  <span class="text-xl">{{ shape.icon }}</span>
                  {{ shape.label }}
                </button>
              </div>
            </div>

            <!-- Grade selector -->
            <div class="mb-8">
              <label class="block text-titanium-400 text-sm font-medium mb-3">Titanium Grade</label>
              <select v-model="selectedGrade"
                class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors">
                <option v-for="g in grades" :key="g.id" :value="g.id">
                  {{ g.shortName }} — {{ g.name }} ({{ g.density }} g/cm³)
                </option>
              </select>
            </div>

            <!-- Dimension inputs -->
            <div class="mb-8">
              <label class="block text-titanium-400 text-sm font-medium mb-3">Dimensions (mm)</label>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <template v-if="selectedShape === 'round-bar'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Diameter (mm)</label>
                    <input v-model.number="dims.diameter" type="number" min="0" placeholder="e.g. 50"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (mm)</label>
                    <input v-model.number="dims.length" type="number" min="0" placeholder="e.g. 1000"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                </template>
                <template v-else-if="selectedShape === 'sheet'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Thickness (mm)</label>
                    <input v-model.number="dims.thickness" type="number" min="0" placeholder="e.g. 3"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Width (mm)</label>
                    <input v-model.number="dims.width" type="number" min="0" placeholder="e.g. 1000"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (mm)</label>
                    <input v-model.number="dims.length" type="number" min="0" placeholder="e.g. 2000"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                </template>
                <template v-else-if="selectedShape === 'tube'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Outer Diameter (mm)</label>
                    <input v-model.number="dims.od" type="number" min="0" placeholder="e.g. 50"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Wall Thickness (mm)</label>
                    <input v-model.number="dims.wall" type="number" min="0" placeholder="e.g. 3"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (mm)</label>
                    <input v-model.number="dims.length" type="number" min="0" placeholder="e.g. 6000"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                </template>
                <template v-else-if="selectedShape === 'wire'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Diameter (mm)</label>
                    <input v-model.number="dims.diameter" type="number" min="0" placeholder="e.g. 2"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (m)</label>
                    <input v-model.number="dims.lengthM" type="number" min="0" placeholder="e.g. 100"
                      class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
                  </div>
                </template>
              </div>
            </div>

            <!-- Quantity -->
            <div class="mb-8">
              <label class="block text-titanium-400 text-sm font-medium mb-3">Quantity (pieces)</label>
              <input v-model.number="quantity" type="number" min="1" placeholder="1"
                class="w-full bg-titanium-900 border border-titanium-700 rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-accent-500 transition-colors placeholder-titanium-700" />
            </div>

            <button @click="calculate"
              class="btn-primary w-full justify-center text-base py-3.5">
              Calculate Weight
            </button>
          </div>

          <!-- Result panel -->
          <div class="lg:col-span-2 flex flex-col gap-5">
            <!-- Result -->
            <div class="card p-8 flex-1">
              <h3 class="text-titanium-400 text-sm font-medium uppercase tracking-wider mb-6">Result</h3>
              <template v-if="result !== null">
                <div class="text-center py-4">
                  <div class="text-5xl font-display font-bold gradient-text mb-2">{{ result.kg.toFixed(3) }}</div>
                  <div class="text-titanium-400 text-lg mb-1">kg per piece</div>
                  <div class="text-titanium-500 text-sm">{{ result.lbs.toFixed(3) }} lbs per piece</div>
                  <div v-if="quantity > 1" class="mt-6 pt-6 border-t border-titanium-800">
                    <div class="text-3xl font-display font-bold text-white mb-1">{{ result.totalKg.toFixed(3) }}</div>
                    <div class="text-titanium-400 text-sm">kg total ({{ quantity }} pcs)</div>
                    <div class="text-titanium-500 text-xs mt-0.5">{{ result.totalLbs.toFixed(3) }} lbs total</div>
                  </div>
                </div>
                <div class="mt-6 p-4 bg-titanium-900 rounded-xl">
                  <div class="text-titanium-500 text-xs mb-2">Calculation</div>
                  <div class="text-titanium-300 text-xs font-mono leading-relaxed">{{ result.formula }}</div>
                </div>
              </template>
              <div v-else class="text-center py-12">
                <div class="text-titanium-700 text-sm">Enter dimensions and click Calculate</div>
              </div>
            </div>

            <!-- Grade density reference -->
            <div class="card p-6">
              <h3 class="text-titanium-400 text-xs font-medium uppercase tracking-wider mb-4">Grade Densities</h3>
              <div class="space-y-2">
                <div v-for="g in grades" :key="g.id"
                  class="flex justify-between items-center py-1.5 border-b border-titanium-800/50 last:border-0">
                  <span class="text-titanium-400 text-xs font-mono">{{ g.shortName }}</span>
                  <span class="text-white text-xs font-semibold">{{ g.density }} g/cm³</span>
                </div>
              </div>
            </div>

            <!-- CTA -->
            <NuxtLink :to="localePath('/request-a-quote')"
              class="card card-hover p-6 group flex items-center gap-4">
              <div class="flex-1">
                <div class="text-white font-medium text-sm group-hover:text-accent-400 transition-colors mb-1">Ready to Order?</div>
                <div class="text-titanium-500 text-xs">Get a quote with MTR in 24 hours</div>
              </div>
              <svg class="w-5 h-5 text-titanium-600 group-hover:text-accent-400 group-hover:translate-x-1 transition-all" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { titaniumGrades } from '@cnbjti/mock-data'

const localePath = useLocalePath()
const grades = titaniumGrades

const shapes = [
  { id: 'round-bar', label: 'Round Bar', icon: '⬤' },
  { id: 'sheet', label: 'Sheet / Plate', icon: '▬' },
  { id: 'tube', label: 'Tube / Pipe', icon: '○' },
  { id: 'wire', label: 'Wire', icon: '〰' },
]

const selectedShape = ref('round-bar')
const selectedGrade = ref('gr2')
const quantity = ref(1)
const dims = ref({ diameter: null, length: null, thickness: null, width: null, od: null, wall: null, lengthM: null })
const result = ref<null | { kg: number; lbs: number; totalKg: number; totalLbs: number; formula: string }>(null)

watch(selectedShape, () => {
  dims.value = { diameter: null, length: null, thickness: null, width: null, od: null, wall: null, lengthM: null }
  result.value = null
})

function calculate() {
  const grade = grades.find(g => g.id === selectedGrade.value)
  if (!grade) return
  const density = grade.density ?? 4.51 // g/cm³

  let volumeCm3 = 0
  let formula = ''

  if (selectedShape.value === 'round-bar') {
    const d = dims.value.diameter ?? 0
    const l = dims.value.length ?? 0
    volumeCm3 = Math.PI * Math.pow(d / 20, 2) * (l / 10)
    formula = `π × (${d}/2)² × ${l} mm ÷ 1000 = ${volumeCm3.toFixed(2)} cm³`
  } else if (selectedShape.value === 'sheet') {
    const t = dims.value.thickness ?? 0
    const w = dims.value.width ?? 0
    const l = dims.value.length ?? 0
    volumeCm3 = (t * w * l) / 1000
    formula = `${t} × ${w} × ${l} mm ÷ 1000 = ${volumeCm3.toFixed(2)} cm³`
  } else if (selectedShape.value === 'tube') {
    const od = dims.value.od ?? 0
    const wall = dims.value.wall ?? 0
    const l = dims.value.length ?? 0
    const id = od - 2 * wall
    volumeCm3 = Math.PI * (Math.pow(od / 20, 2) - Math.pow(id / 20, 2)) * (l / 10)
    formula = `π × ((${od}/2)² - (${id}/2)²) × ${l} mm ÷ 1000 = ${volumeCm3.toFixed(2)} cm³`
  } else if (selectedShape.value === 'wire') {
    const d = dims.value.diameter ?? 0
    const lm = dims.value.lengthM ?? 0
    volumeCm3 = Math.PI * Math.pow(d / 20, 2) * (lm * 100)
    formula = `π × (${d}/2)² × ${lm * 1000} mm ÷ 1000 = ${volumeCm3.toFixed(2)} cm³`
  }

  const kg = (volumeCm3 * density) / 1000
  result.value = {
    kg,
    lbs: kg * 2.20462,
    totalKg: kg * (quantity.value || 1),
    totalLbs: kg * (quantity.value || 1) * 2.20462,
    formula: `${formula}\n${volumeCm3.toFixed(2)} cm³ × ${density} g/cm³ = ${(volumeCm3 * density).toFixed(2)} g = ${kg.toFixed(3)} kg`,
  }
}

useHead({
  title: 'Titanium Weight Calculator — Bar, Sheet, Tube, Wire | CNBJTI',
  meta: [
    { name: 'description', content: 'Free titanium weight calculator. Calculate weight of titanium round bar, sheet, plate, tube and wire by entering dimensions and grade. Results in kg and lbs.' },
  ],
})
</script>
