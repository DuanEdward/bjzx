import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 强制使用 sass-embedded 的现代 API，避免 legacy API 导致的内存问题
        // silenceDeprecations 只能抑制警告，但不能解决内存问题
        // 需要确保 node_modules 中只有 sass-embedded，没有 legacy sass
        api: 'modern-compiler',
        silenceDeprecations: ['legacy-js-api']
      }
    }
  },
  build: {
    // 优化构建性能，减少内存使用
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks(id) {
          // 将 element-plus 单独打包
          if (id.includes('element-plus')) {
            return 'element-plus'
          }
          // 将 Vue 相关库打包在一起
          if (id.includes('vue') || id.includes('vue-router') || id.includes('pinia')) {
            return 'vue-vendor'
          }
          // 将 node_modules 中的其他依赖打包
          if (id.includes('node_modules')) {
            return 'vendor'
          }
        }
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})