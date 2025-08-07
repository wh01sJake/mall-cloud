import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  server: {
    proxy: {
      '/api': { //请求路径中包含了/api
        target: 'http://localhost:9000', //要更换的源,也就是后台服务的源
        changeOrigin: true, //要不要更换源
        // Removed path rewrite - gateway expects full /api path
      }
    }
  }
})
