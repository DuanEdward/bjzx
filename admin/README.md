# 管理后台 - 河北省计算机学会

## 项目简介

河北省计算机学会网站管理后台系统，基于 Vue3 + Element Plus + TypeScript 构建。

## 技术栈

- **框架**: Vue 3.4+
- **构建工具**: Vite 5.0+
- **类型系统**: TypeScript
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **UI组件**: Element Plus 2.4+
- **HTTP请求**: Axios
- **图表**: ECharts + Vue-ECharts
- **样式**: SCSS

## 目录结构

```
admin/
├── public/              # 静态资源
├── src/
│   ├── api/            # API接口
│   │   ├── index.ts    # 请求封装
│   │   └── modules/    # 各模块接口
│   ├── assets/         # 资源文件
│   │   └── styles/     # 样式文件
│   ├── components/     # 公共组件
│   │   └── layout/     # 布局组件
│   ├── router/         # 路由配置
│   ├── stores/         # 状态管理
│   ├── types/          # 类型定义
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   │   ├── content/    # 内容管理
│   │   ├── member/     # 会员管理
│   │   ├── system/     # 系统管理
│   │   ├── Dashboard.vue
│   │   └── Login.vue
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── index.html          # HTML模板
├── package.json        # 项目配置
├── tsconfig.json       # TypeScript配置
└── vite.config.ts      # Vite配置
```

## 功能模块

### 1. 首页仪表板
- 数据统计展示
- 图表分析
- 最新动态

### 2. 内容管理
- Banner管理
- 新闻管理（增删改查）
- 活动管理（增删改查）
- 资源管理（文件上传）

### 3. 会员管理
- 会员列表
- 入会申请审核

### 4. 系统管理
- 用户管理
- 角色管理
- 系统设置

## 快速开始

### 环境要求

- Node.js 18+
- npm 9+ 或 yarn 3+

### 安装依赖

```bash
# 使用npm
npm install

# 或使用yarn
yarn install
```

### 开发环境

```bash
# 启动开发服务器
npm run dev

# 或
yarn dev
```

访问 http://localhost:3001

### 登录信息

默认管理员账号：
- 用户名：admin
- 密码：admin123

### 构建生产环境

```bash
# 构建生产版本
npm run build

# 或
yarn build
```

构建产物在 `dist` 目录下。

### 预览生产环境

```bash
# 预览构建结果
npm run preview

# 或
yarn preview
```

### 代码检查

```bash
# ESLint检查
npm run lint

# 或
yarn lint
```

## 开发规范

### 1. 组件开发

- 页面组件放在 `views` 目录
- 公共组件放在 `components` 目录
- 使用 TypeScript 定义 Props

### 2. API 接口

- 在 `api/modules` 下按模块创建接口文件
- 统一使用 `request` 实例发起请求
- 需要定义接口响应类型

### 3. 路由配置

- 静态路由在 `router/index.ts` 中配置
- 动态路由根据用户权限动态添加
- 使用路由元信息控制权限和显示

### 4. 样式规范

- 使用 SCSS 预处理器
- 遵循 BEM 命名规范
- 使用 CSS 变量定义主题色

## 环境变量配置

创建 `.env.local` 文件：

```bash
# API基础地址
VITE_API_BASE_URL=http://localhost:8080/api

# 其他环境变量
VITE_APP_TITLE=河北省计算机学会管理后台
```

## 部署说明

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name admin.hsjs.org.cn;
    root /path/to/admin/dist;
    index index.html;

    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # 前端路由history模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 代理API请求
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

## 权限控制

### 1. 页面级权限

- 通过路由守卫控制页面访问
- 根据 token 判断是否登录

### 2. 操作级权限

- 通过角色控制按钮显示/隐藏
- API 接口需要验证权限

### 3. 数据级权限

- 普通用户只能查看自己创建的内容
- 管理员可以查看所有内容

## 富文本编辑器

项目支持富文本内容编辑，建议使用：

1. **TinyMCE**
   - 功能强大
   - 插件丰富
   - 需要授权

2. **Quill**
   - 轻量级
   - 免费
   - 基础功能够用

3. **WangEditor**
   - 国产
   - 中文文档完善
   - 轻量易用

## 文件上传

- 图片上传：支持 JPG、PNG、GIF
- 文件上传：支持 PDF、DOC、DOCX、XLS、XLSX、PPT、PPTX
- 大小限制：单个文件不超过 10MB
- 建议使用对象存储服务（如阿里云OSS）

## 常见问题

### 1. 权限问题

确保 token 正确设置在请求头中，后端需要验证 token 有效性。

### 2. 菜单不显示

检查用户角色和菜单配置，确保路由的 `hidden` 属性设置正确。

### 3. 表格分页

使用 Element Plus 的分页组件，配合后端分页接口。

## 性能优化

1. **路由懒加载**
   ```typescript
   const routes = [
     {
       path: '/news',
       component: () => import('@/views/content/News.vue')
     }
   ]
   ```

2. **组件异步加载**
   ```vue
   <template>
     <AsyncComponent />
   </template>

   <script setup>
   const AsyncComponent = defineAsyncComponent(() => import('./AsyncComponent.vue'))
   </script>
   ```

3. **图片懒加载**
   - 使用 `loading="lazy"` 属性
   - 使用 Intersection Observer API

## 更新日志

### v1.0.0 (2024-01-10)

- 初始版本发布
- 完成基础管理功能
- 实现用户权限控制
- 集成 ECharts 图表