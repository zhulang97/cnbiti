<template>
  <div>
    <section class="relative bg-titanium-950 pt-32 pb-16 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-20" />
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <NuxtLink :to="localePath('/resources')" class="hover:text-accent-400 transition-colors">Resources</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">Grade Guide</span>
        </nav>
        <span class="inline-block px-3 py-1 bg-accent-500/10 border border-accent-500/30 rounded-full text-accent-400 text-xs font-mono mb-5">Technical Guide · 12 min read</span>
        <h1 class="section-title mb-6">Complete Guide to Titanium Grades: Gr.1 to Gr.23</h1>
        <p class="text-titanium-400 text-lg leading-relaxed">Understand the differences between commercially pure and alloyed titanium grades, their properties and best applications.</p>
        <div class="flex items-center gap-4 mt-6 text-titanium-500 text-sm">
          <span>Published March 15, 2026</span>
          <span>·</span>
          <span>CNBJTI Technical Team</span>
        </div>
      </div>
    </section>

    <section class="bg-titanium-950 py-12 pb-24">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="prose-titanium space-y-8">
          <div v-for="section in sections" :key="section.title">
            <h2 class="text-white font-semibold text-xl mb-4">{{ section.title }}</h2>
            <p class="text-titanium-400 leading-relaxed">{{ section.content }}</p>
            <div v-if="section.grades" class="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div v-for="g in section.grades" :key="g.name" class="card p-4">
                <div class="flex items-center gap-3 mb-2">
                  <span class="px-2 py-0.5 bg-accent-500/10 border border-accent-500/30 rounded text-accent-400 font-mono text-xs">{{ g.shortName }}</span>
                  <span class="text-white font-medium text-sm">{{ g.name }}</span>
                </div>
                <p class="text-titanium-500 text-xs leading-relaxed">{{ g.note }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-16 card p-8 text-center">
          <h3 class="text-white font-semibold text-lg mb-3">Need Help Selecting a Grade?</h3>
          <p class="text-titanium-400 text-sm mb-6">Our technical team can recommend the right grade for your application and provide a quote with MTR.</p>
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Request a Quote</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()

const sections = [
  {
    title: 'What Are Titanium Grades?',
    content: 'Titanium grades are standardized compositions defined by ASTM, AMS and other bodies. They range from commercially pure (CP) titanium (Grades 1–4) to alloys with aluminum, vanadium, palladium and other elements. Each grade offers a different balance of strength, corrosion resistance, formability and cost.',
  },
  {
    title: 'Commercially Pure (CP) Titanium Grades',
    content: 'CP titanium grades (1–4) are unalloyed titanium with varying oxygen content. Higher oxygen content increases strength but reduces ductility. These grades offer the best corrosion resistance and are easiest to form and weld.',
    grades: [
      { shortName: 'Gr.1', name: 'Grade 1', note: 'Lowest oxygen, highest purity. Best formability and corrosion resistance. Used in chemical processing and desalination.' },
      { shortName: 'Gr.2', name: 'Grade 2', note: 'Most widely used CP grade. Excellent balance of strength, formability and corrosion resistance. The "workhorse" of CP titanium.' },
      { shortName: 'Gr.7', name: 'Grade 7', note: 'CP Ti + 0.15% Pd. Best corrosion resistance of all grades. Ideal for reducing acid environments.' },
    ],
  },
  {
    title: 'Titanium Alloys',
    content: 'Titanium alloys add elements like aluminum, vanadium, molybdenum and palladium to achieve higher strength, better high-temperature performance or improved corrosion resistance. The most important alloy is Ti-6Al-4V (Grade 5).',
    grades: [
      { shortName: 'Gr.5', name: 'Ti-6Al-4V', note: 'The most widely used titanium alloy. Highest strength-to-weight ratio. Aerospace, medical and industrial applications.' },
      { shortName: 'Gr.9', name: 'Ti-3Al-2.5V', note: 'Higher strength than CP grades with good formability. Standard for aerospace hydraulic tubing.' },
      { shortName: 'Gr.23', name: 'Ti-6Al-4V ELI', note: 'Medical-grade Ti-6Al-4V with extra low interstitials. Superior fracture toughness for implants.' },
    ],
  },
  {
    title: 'How to Choose the Right Grade',
    content: 'Grade selection depends on your application requirements: (1) If corrosion resistance is the priority, choose CP grades (Gr.1, Gr.2) or Gr.7 for the most aggressive environments. (2) If strength-to-weight ratio is critical, choose Gr.5 (Ti-6Al-4V). (3) For medical implants, choose Gr.23 (Ti-6Al-4V ELI). (4) For aerospace tubing, choose Gr.9. When in doubt, contact our technical team — we can advise based on your specific application, environment and required certifications.',
  },
]

useHead({
  title: 'Complete Guide to Titanium Grades: Gr.1 to Gr.23 | CNBJTI',
  meta: [
    { name: 'description', content: 'Complete guide to titanium grades from Grade 1 to Grade 23. Properties, applications and selection guide for CP titanium and titanium alloys.' },
  ],
})
</script>
