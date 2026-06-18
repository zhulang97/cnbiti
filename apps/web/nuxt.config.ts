const getDevPort = () => {
  const portArgIndex = process.argv.findIndex((arg) => arg === '--port' || arg === '-p')
  const portArg = portArgIndex >= 0 ? process.argv[portArgIndex + 1] : undefined
  const inlinePortArg = process.argv.find((arg) => arg.startsWith('--port='))?.split('=')[1]

  return process.env.NUXT_DEV_PORT
    || process.env.NITRO_PORT
    || process.env.PORT
    || process.env.npm_config_port
    || portArg
    || inlinePortArg
}

const devPort = getDevPort()
const isDevCommand = process.argv.some((arg) => arg.includes('dev'))
const devBuildDir = `.nuxt-dev-${devPort || process.pid}`
const buildDir = process.env.NUXT_BUILD_DIR || (isDevCommand ? devBuildDir : '.nuxt')
const publicApiBase = process.env.NUXT_PUBLIC_API_BASE || '/api'
const serverApiBase = process.env.NUXT_API_BASE
  || process.env.API_BASE
  || (publicApiBase.startsWith('http') ? publicApiBase : 'http://127.0.0.1:8080/api')

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  buildDir,

  devtools: { enabled: false },

  modules: [
    '@vueuse/nuxt',
  ],

  css: ['~/assets/css/main.css'],

  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },

  app: {
    head: {
      charset: 'utf-8',
      viewport: 'width=device-width, initial-scale=1',
      titleTemplate: '%s',
      htmlAttrs: {
        lang: 'en',
      },
      meta: [
        { name: 'theme-color', content: '#020617' },
        { name: 'robots', content: 'index,follow' },
        { property: 'og:site_name', content: 'CNBJTI' },
        { property: 'og:type', content: 'website' },
        { name: 'twitter:card', content: 'summary_large_image' },
      ],
      link: [
        { rel: 'icon', type: 'image/svg+xml', href: '/brand-icon.svg' },
        { rel: 'apple-touch-icon', href: '/brand-icon.svg' },
        { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
        { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' },
        {
          rel: 'stylesheet',
          href: 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=Space+Grotesk:wght@400;500;600;700&display=swap',
        },
      ],
    },
    pageTransition: { name: 'page', mode: 'out-in' },
  },

  runtimeConfig: {
    apiBase: serverApiBase,
    public: {
      apiBase: publicApiBase,
      siteUrl: process.env.NUXT_PUBLIC_SITE_URL || 'https://www.cnbjti.com',
    },
  },

  nitro: {
    devProxy: {
      '/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true,
      },
    },
    prerender: {
      routes: ['/', '/sitemap.xml', '/robots.txt'],
    },
  },

  vite: {
    server: {
      allowedHosts: ['cnbjti.com', 'www.cnbjti.com', 'localhost', '127.0.0.1'],
    },
  },

  typescript: {
    strict: true,
  },

  compatibilityDate: '2024-11-01',
})
