<template>
  <div>
    <section class="relative bg-gradient-to-b from-white via-steel-50 to-titanium-50 pt-32 pb-16 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="'/'" class="hover:text-titanium-800 transition-colors">Home</NuxtLink>
          <span>/</span>
          <NuxtLink :to="'/resources'" class="hover:text-titanium-800 transition-colors">Resources</NuxtLink>
          <span>/</span>
          <span class="text-titanium-700">Weight Calculator</span>
        </nav>
        <div class="section-label">Free Tool</div>
        <h1 class="section-title mt-3 mb-4">Titanium Weight Calculator</h1>
        <p class="text-titanium-600 text-lg max-w-2xl">
          Calculate the weight of titanium bar, sheet, tube or wire. Select your grade for accurate density.
        </p>
      </div>
    </section>

    <section class="bg-titanium-50 py-12 pb-24">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-5 gap-8">
          <div class="lg:col-span-3 card p-8">
            <div class="mb-8">
              <label class="block text-titanium-500 text-sm font-medium mb-3">Shape / Form</label>
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <button
                  v-for="shape in shapes"
                  :key="shape.id"
                  type="button"
                  :class="[
                    'flex flex-col items-center gap-2 p-3 rounded-xl border text-xs font-medium transition-all',
                    selectedShape === shape.id
                      ? 'bg-accent-500/10 border-accent-500/50 text-accent-600'
                      : 'bg-white border-titanium-200 text-titanium-600 hover:border-accent-400 hover:text-titanium-950',
                  ]"
                  @click="selectedShape = shape.id"
                >
                  <span class="font-mono text-[11px] tracking-wide">{{ shape.code }}</span>
                  {{ shape.label }}
                </button>
              </div>
            </div>

            <div class="mb-8">
              <label class="block text-titanium-500 text-sm font-medium mb-3">Titanium Grade</label>
              <select v-model="selectedGrade" class="form-field rounded-xl px-4 py-3">
                <option v-for="grade in grades" :key="grade.id" :value="grade.id">
                  {{ grade.shortName }} - {{ grade.name }} ({{ grade.density }} g/cm3)
                </option>
              </select>
            </div>

            <div class="mb-8">
              <label class="block text-titanium-500 text-sm font-medium mb-3">Dimensions (mm)</label>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <template v-if="selectedShape === 'round-bar'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Diameter (mm)</label>
                    <input v-model.number="dims.diameter" type="number" min="0" placeholder="e.g. 50" class="form-field rounded-xl px-4 py-3" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (mm)</label>
                    <input v-model.number="dims.length" type="number" min="0" placeholder="e.g. 1000" class="form-field rounded-xl px-4 py-3" />
                  </div>
                </template>

                <template v-else-if="selectedShape === 'sheet'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Thickness (mm)</label>
                    <input v-model.number="dims.thickness" type="number" min="0" placeholder="e.g. 3" class="form-field rounded-xl px-4 py-3" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Width (mm)</label>
                    <input v-model.number="dims.width" type="number" min="0" placeholder="e.g. 1000" class="form-field rounded-xl px-4 py-3" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (mm)</label>
                    <input v-model.number="dims.length" type="number" min="0" placeholder="e.g. 2000" class="form-field rounded-xl px-4 py-3" />
                  </div>
                </template>

                <template v-else-if="selectedShape === 'tube'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Outer Diameter (mm)</label>
                    <input v-model.number="dims.od" type="number" min="0" placeholder="e.g. 50" class="form-field rounded-xl px-4 py-3" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Wall Thickness (mm)</label>
                    <input v-model.number="dims.wall" type="number" min="0" placeholder="e.g. 3" class="form-field rounded-xl px-4 py-3" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (mm)</label>
                    <input v-model.number="dims.length" type="number" min="0" placeholder="e.g. 6000" class="form-field rounded-xl px-4 py-3" />
                  </div>
                </template>

                <template v-else-if="selectedShape === 'wire'">
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Diameter (mm)</label>
                    <input v-model.number="dims.diameter" type="number" min="0" placeholder="e.g. 2" class="form-field rounded-xl px-4 py-3" />
                  </div>
                  <div>
                    <label class="block text-titanium-500 text-xs mb-1.5">Length (m)</label>
                    <input v-model.number="dims.lengthM" type="number" min="0" placeholder="e.g. 100" class="form-field rounded-xl px-4 py-3" />
                  </div>
                </template>
              </div>
            </div>

            <div class="mb-8">
              <label class="block text-titanium-500 text-sm font-medium mb-3">Quantity (pieces)</label>
              <input v-model.number="quantity" type="number" min="1" placeholder="1" class="form-field rounded-xl px-4 py-3" />
            </div>

            <button type="button" class="btn-primary w-full justify-center text-base py-3.5" @click="calculate">
              Calculate Weight
            </button>
          </div>

          <div class="lg:col-span-2 flex flex-col gap-5">
            <div class="card p-8 flex-1">
              <h3 class="text-titanium-500 text-sm font-medium uppercase tracking-wider mb-6">Result</h3>
              <template v-if="result !== null">
                <div class="text-center py-4">
                  <div class="text-5xl font-display font-bold gradient-text mb-2">{{ result.kg.toFixed(3) }}</div>
                  <div class="text-titanium-600 text-lg mb-1">kg per piece</div>
                  <div class="text-titanium-500 text-sm">{{ result.lbs.toFixed(3) }} lbs per piece</div>
                  <div v-if="quantity > 1" class="mt-6 pt-6 border-t border-titanium-200">
                    <div class="text-3xl font-display font-bold text-titanium-950 mb-1">{{ result.totalKg.toFixed(3) }}</div>
                    <div class="text-titanium-600 text-sm">kg total ({{ quantity }} pcs)</div>
                    <div class="text-titanium-500 text-xs mt-0.5">{{ result.totalLbs.toFixed(3) }} lbs total</div>
                  </div>
                </div>
                <div class="mt-6 p-4 bg-titanium-100 rounded-xl">
                  <div class="text-titanium-500 text-xs mb-2">Calculation</div>
                  <div class="text-titanium-700 text-xs font-mono leading-relaxed whitespace-pre-line">{{ result.formula }}</div>
                </div>
              </template>
              <div v-else class="text-center py-12">
                <div class="text-titanium-500 text-sm">Enter dimensions and click Calculate</div>
              </div>
            </div>

            <div class="card p-6">
              <h3 class="text-titanium-500 text-xs font-medium uppercase tracking-wider mb-4">Grade Densities</h3>
              <div class="space-y-2">
                <div
                  v-for="grade in grades"
                  :key="grade.id"
                  class="flex justify-between items-center py-1.5 border-b border-titanium-200 last:border-0"
                >
                  <span class="text-titanium-500 text-xs font-mono">{{ grade.shortName }}</span>
                  <span class="text-titanium-950 text-xs font-semibold">{{ grade.density }} g/cm3</span>
                </div>
              </div>
            </div>

            <NuxtLink :to="'/request-a-quote'" class="card card-hover p-6 group flex items-center gap-4">
              <div class="flex-1">
                <div class="text-titanium-950 font-medium text-sm group-hover:text-accent-600 transition-colors mb-1">Ready to Order?</div>
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

type ShapeId = 'round-bar' | 'sheet' | 'tube' | 'wire'

interface Dimensions {
  diameter: number | null
  length: number | null
  thickness: number | null
  width: number | null
  od: number | null
  wall: number | null
  lengthM: number | null
}

const grades = titaniumGrades
const shapes: { id: ShapeId; label: string; code: string }[] = [
  { id: 'round-bar', label: 'Round Bar', code: 'BAR' },
  { id: 'sheet', label: 'Sheet / Plate', code: 'PLATE' },
  { id: 'tube', label: 'Tube / Pipe', code: 'TUBE' },
  { id: 'wire', label: 'Wire', code: 'WIRE' },
]

const selectedShape = ref<ShapeId>('round-bar')
const selectedGrade = ref('gr2')
const quantity = ref(1)
const dims = ref<Dimensions>(emptyDimensions())
const result = ref<null | { kg: number; lbs: number; totalKg: number; totalLbs: number; formula: string }>(null)

watch(selectedShape, () => {
  dims.value = emptyDimensions()
  result.value = null
})

function emptyDimensions(): Dimensions {
  return { diameter: null, length: null, thickness: null, width: null, od: null, wall: null, lengthM: null }
}

function calculate() {
  const grade = grades.find(grade => grade.id === selectedGrade.value)
  if (!grade) return
  const density = grade.density ?? 4.51

  let volumeCm3 = 0
  let formula = ''

  if (selectedShape.value === 'round-bar') {
    const d = dims.value.diameter ?? 0
    const l = dims.value.length ?? 0
    volumeCm3 = Math.PI * (d / 20) ** 2 * (l / 10)
    formula = `pi x (${d}/2)^2 x ${l} mm / 1000 = ${volumeCm3.toFixed(2)} cm3`
  } else if (selectedShape.value === 'sheet') {
    const t = dims.value.thickness ?? 0
    const w = dims.value.width ?? 0
    const l = dims.value.length ?? 0
    volumeCm3 = (t * w * l) / 1000
    formula = `${t} x ${w} x ${l} mm / 1000 = ${volumeCm3.toFixed(2)} cm3`
  } else if (selectedShape.value === 'tube') {
    const od = dims.value.od ?? 0
    const wall = dims.value.wall ?? 0
    const l = dims.value.length ?? 0
    const id = Math.max(od - 2 * wall, 0)
    volumeCm3 = Math.PI * ((od / 20) ** 2 - (id / 20) ** 2) * (l / 10)
    formula = `pi x ((${od}/2)^2 - (${id}/2)^2) x ${l} mm / 1000 = ${volumeCm3.toFixed(2)} cm3`
  } else if (selectedShape.value === 'wire') {
    const d = dims.value.diameter ?? 0
    const lm = dims.value.lengthM ?? 0
    volumeCm3 = Math.PI * (d / 20) ** 2 * (lm * 100)
    formula = `pi x (${d}/2)^2 x ${lm * 1000} mm / 1000 = ${volumeCm3.toFixed(2)} cm3`
  }

  const kg = (volumeCm3 * density) / 1000
  result.value = {
    kg,
    lbs: kg * 2.20462,
    totalKg: kg * (quantity.value || 1),
    totalLbs: kg * (quantity.value || 1) * 2.20462,
    formula: `${formula}\n${volumeCm3.toFixed(2)} cm3 x ${density} g/cm3 = ${(volumeCm3 * density).toFixed(2)} g = ${kg.toFixed(3)} kg`,
  }
}

useHead({
  title: 'Titanium Weight Calculator - Bar, Sheet, Tube, Wire | CNBJTI',
  meta: [
    { name: 'description', content: 'Free titanium weight calculator. Calculate weight of titanium round bar, sheet, plate, tube and wire by entering dimensions and grade. Results in kg and lbs.' },
  ],
})
</script>
