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
    // 使用 esbuild 压缩（默认，更快且不需要额外依赖）
    // 如果需要 terser，需要先安装: npm install -D terser
    minify: 'esbuild',
    rollupOptions: {
      output: {
        // 更细粒度的代码分割，减少单次处理的内存压力
        manualChunks: (id) => {
          // echarts 单独打包，避免与其他模块一起处理
          if (id.includes('echarts') || id.includes('zrender')) {
            return 'echarts-vendor'
          }
          // element-plus 单独打包
          if (id.includes('element-plus')) {
            return 'element-plus-vendor'
          }
          // Vue 核心库
          if (id.includes('vue') && !id.includes('node_modules')) {
            return 'vue-core'
          }
          // Vue 相关库
          if (id.includes('vue-router') || id.includes('pinia')) {
            return 'vue-vendor'
          }
          // node_modules 中的其他大包
          if (id.includes('node_modules')) {
            return 'vendor'
          }
        }
      }
    }
  },
  server: {
    port: 3001,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})