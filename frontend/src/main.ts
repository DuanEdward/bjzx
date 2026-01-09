// 先导入样式，避免样式加载问题
import 'element-plus/dist/index.css'
import '@/assets/styles/index.scss'

// 然后导入 Vue 核心
import { createApp } from 'vue'
import { createPinia } from 'pinia'

// 导入 Element Plus（必须在 Vue 之后）
import ElementPlus from 'element-plus'

// 导入应用组件和路由
import App from './App.vue'
import router from './router'

// 创建应用实例
const app = createApp(App)

// 按顺序注册插件
app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 挂载应用
app.mount('#app')