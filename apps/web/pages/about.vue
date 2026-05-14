<template>
  <div>
    <!-- Hero -->
    <section class="relative bg-titanium-950 pt-32 pb-24 overflow-hidden">
      <div class="absolute inset-0 grid-pattern opacity-30" />
      <div class="absolute top-0 right-0 w-[500px] h-[500px] bg-accent-500/5 rounded-full blur-3xl pointer-events-none" />
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <nav class="flex items-center gap-2 text-sm text-titanium-500 mb-8">
          <NuxtLink :to="localePath('/')" class="hover:text-accent-400 transition-colors">Home</NuxtLink>
          <span>/</span>
          <span class="text-titanium-300">About</span>
        </nav>
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <div class="section-label">Our Story</div>
            <h1 class="section-title mt-3 mb-6">Baoji's Titanium Specialists Since 2008</h1>
            <p class="text-titanium-300 text-lg leading-relaxed mb-6">
              CNBJTI was founded in Baoji — China's titanium capital — with a single mission: make certified titanium materials accessible to global buyers with the documentation and service they need.
            </p>
            <p class="text-titanium-400 leading-relaxed">
              Today we supply titanium bar, sheet, tube, wire, forgings and custom CNC parts to customers in over 50 countries. Every order ships with a full EN 10204 3.1 MTR and is backed by our technical team.
            </p>
          </div>
          <!-- Stats -->
          <div class="grid grid-cols-2 gap-5">
            <div v-for="stat in stats" :key="stat.label" class="card p-6 text-center">
              <div class="text-3xl font-display font-bold gradient-text mb-2">{{ stat.value }}</div>
              <div class="text-titanium-400 text-sm">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Why Baoji -->
    <section class="bg-titanium-900/30 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Location Advantage</div>
        <h2 class="section-title mb-4">Why Baoji?</h2>
        <p class="text-titanium-400 max-w-2xl mb-12 leading-relaxed">
          Baoji, Shaanxi produces over 60% of China's titanium output. Being here means direct access to the best mills, fastest lead times and most competitive pricing.
        </p>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div v-for="item in baojiAdvantages" :key="item.title" class="card p-6">
            <div class="w-12 h-12 rounded-xl bg-accent-500/10 border border-accent-500/20 flex items-center justify-center mb-5">
              <span class="text-2xl">{{ item.icon }}</span>
            </div>
            <h3 class="text-white font-semibold mb-3">{{ item.title }}</h3>
            <p class="text-titanium-400 text-sm leading-relaxed">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Timeline -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">Milestones</div>
        <h2 class="section-title mb-12">Our Journey</h2>
        <div class="relative">
          <div class="absolute left-4 top-0 bottom-0 w-px bg-titanium-800" />
          <div class="space-y-10">
            <div v-for="event in timeline" :key="event.year" class="relative pl-12">
              <div class="absolute left-0 w-8 h-8 rounded-full bg-titanium-900 border-2 border-accent-500 flex items-center justify-center">
                <div class="w-2 h-2 rounded-full bg-accent-500" />
              </div>
              <div class="text-accent-400 font-mono text-sm mb-1">{{ event.year }}</div>
              <h3 class="text-white font-semibold mb-2">{{ event.title }}</h3>
              <p class="text-titanium-400 text-sm leading-relaxed">{{ event.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Team / Values -->
    <section class="bg-titanium-900/30 py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="section-label mb-3">What We Stand For</div>
        <h2 class="section-title mb-12">Our Values</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="value in values" :key="value.title" class="card p-6 text-center">
            <div class="text-3xl mb-4">{{ value.icon }}</div>
            <h3 class="text-white font-semibold mb-3">{{ value.title }}</h3>
            <p class="text-titanium-400 text-sm leading-relaxed">{{ value.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="bg-titanium-950 py-20">
      <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-2xl font-display font-bold text-white mb-4">Ready to Work Together?</h2>
        <p class="text-titanium-400 mb-8">Send us your requirements and our team will respond within 24 hours with a quote and technical recommendations.</p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <NuxtLink :to="localePath('/request-a-quote')" class="btn-primary">Get a Quote</NuxtLink>
          <NuxtLink :to="localePath('/contact')" class="btn-secondary">Contact Us</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()

const stats = [
  { value: '2008', label: 'Founded in Baoji' },
  { value: '50+', label: 'Countries Served' },
  { value: '100+', label: 'Titanium Specifications' },
  { value: '15+', label: 'Years Export Experience' },
]

const baojiAdvantages = [
  { icon: '🏭', title: 'Direct Mill Access', desc: 'Located minutes from Baoji\'s major titanium mills. We source directly, cutting out middlemen and reducing lead times.' },
  { icon: '📋', title: 'Certified Supply Chain', desc: 'All mills are ISO 9001 certified. We audit suppliers regularly and maintain approved vendor lists for each product type.' },
  { icon: '🌍', title: 'Export Expertise', desc: 'Over 15 years exporting to North America, Europe, Southeast Asia and the Middle East. We handle all customs documentation.' },
]

const timeline = [
  { year: '2008', title: 'Founded in Baoji', desc: 'CNBJTI established as a titanium materials trading company, focusing on export to Southeast Asia.' },
  { year: '2012', title: 'ISO 9001 Certification', desc: 'Achieved ISO 9001:2008 quality management certification. Expanded product range to include forgings and CNC parts.' },
  { year: '2015', title: 'European Market Entry', desc: 'First major contracts with German and Dutch chemical processing companies. Began supplying EN 10204 3.1 MTRs as standard.' },
  { year: '2018', title: 'CNC Machining Division', desc: 'Launched in-house CNC machining capability for custom titanium components. Added aerospace-grade Ti-6Al-4V to stock.' },
  { year: '2021', title: 'North America Expansion', desc: 'Established relationships with US aerospace and medical device manufacturers. AMS 4928 supply capability added.' },
  { year: '2024', title: '50+ Countries Milestone', desc: 'Reached the milestone of supplying customers in over 50 countries. Launched multilingual website and 24-hour RFQ response commitment.' },
]

const values = [
  { icon: '🔬', title: 'Technical Accuracy', desc: 'We know titanium. Our team can advise on grade selection, processing and certification requirements.' },
  { icon: '📄', title: 'Documentation First', desc: 'Every order ships with complete MTR and certification. No exceptions, no shortcuts.' },
  { icon: '⚡', title: 'Fast Response', desc: '24-hour quote turnaround. We respect your time and your project deadlines.' },
  { icon: '🤝', title: 'Long-term Partnership', desc: 'We build relationships, not just transactions. Many customers have been with us for over 10 years.' },
]

useHead({
  title: 'About CNBJTI — Baoji Titanium Materials Supplier Since 2008',
  meta: [
    { name: 'description', content: 'CNBJTI is a Baoji-based titanium materials supplier with 15+ years of export experience. We supply certified titanium to 50+ countries with EN 10204 3.1 MTR.' },
  ],
})
</script>
