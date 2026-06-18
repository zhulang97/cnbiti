import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  build: {
    target: 'esnext',
    minify: false,
    reportCompressedSize: false,
  },
  optimizeDeps: {
    esbuildOptions: {
      target: 'esnext',
    },
  },
  server: {
    allowedHosts: ['admin.cnbjti.com', 'localhost', '127.0.0.1'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  resolve: {
    extensions: ['.ts', '.tsx', '.mjs', '.js', '.jsx', '.json', '.vue'],
    alias: {
      '@': resolve(__dirname, 'src'),
      'vue-demi': resolve(__dirname, 'src/shims/vue-demi.ts'),
      '@popperjs/core': resolve(
        __dirname,
        '../../node_modules/.pnpm/@sxzz+popperjs-es@2.11.8/node_modules/@sxzz/popperjs-es',
      ),
    },
  },
})
