# 前台网站 - 河北省计算机学会

## 项目简介

河北省计算机学会官方网站前台项目，基于 Vue3 + Vite + TypeScript 构建。

## 技术栈

- **框架**: Vue 3.4+
- **构建工具**: Vite 5.0+
- **类型系统**: TypeScript
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **UI组件**: Element Plus
- **HTTP请求**: Axios
- **样式**: SCSS

## 目录结构

```
frontend/
├��─ public/              # 静态资源
├── src/
│   ├── api/            # API接口
│   │   ├── index.ts    # 请求封装
│   │   └── modules/    # 各模块接口
│   ├── assets/         # 资源文件
│   │   ├── images/     # 图片
│   │   └── styles/     # 样式文件
│   ├── components/     # 公共组件
│   │   └── layout/     # 布局组件
│   ├── router/         # 路由配置
│   ├── stores/         # 状态管理
│   ├── types/          # 类型定义
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── index.html          # HTML模板
├── package.json        # 项目配置
├── tsconfig.json       # TypeScript配置
└── vite.config.ts      # Vite配置
```

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

访问 http://localhost:3000

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

### 组件命名

- 使用 PascalCase 命名组件文件
- 使用 kebab-case 引用组件

### 代码风格

- 使用 TypeScript 进行类型检查
- 遵循 ESLint 规则
- 使用 Prettier 格式化代码

### Git 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 环境变量配置

创建 `.env.local` 文件：

```bash
# API基础地址
VITE_API_BASE_URL=http://localhost:8080/api

# 其他环境变量
VITE_APP_TITLE=河北省计算机学会
```

## 部署说明

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name www.hsjs.org.cn;
    root /path/to/dist;
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

## 常见问题

### 1. 跨域问题

开发环境已在 `vite.config.ts` 中配置代理，生产环境需要通过 Nginx 代理或后端配置 CORS。

### 2. 路由模式

项目使用 History 路由模式，需要服务器配置路由回退到 index.html。

### 3. 图片资源

- 小图片可以使用 base64 内联
- 大图片建议使用 CDN
- 图片需要优化压缩

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 更新日志

### v1.0.0 (2024-01-10)

- 初始版本发布
- 完成基础功能开发
- 包含新闻、活动、会员、资源等模块