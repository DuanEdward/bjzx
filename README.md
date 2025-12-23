# 北京中兴建机职业技能鉴定中心网站项目

## 项目简介

北京中兴建机职业技能鉴定中心官方网站项目，包含前端展示、管理后台和后端API服务。

## 技术栈

### 前端
- Vue 3
- TypeScript
- Vite
- Element Plus
- Vue Router
- Pinia

### 管理后台
- Vue 3
- TypeScript
- Vite
- Element Plus
- ECharts

### 后端
- Spring Boot 2.7.18
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis
- Spring Security + JWT

## 项目结构

```
hebei-cs-website/
├── frontend/          # 前端项目
├── admin/             # 管理后台项目
├── backend/           # 后端项目
├── deploy/            # 部署脚本
├── docs/              # 项目文档
└── pic/               # 图片资源
```

## 快速开始

### 开发环境

1. 克隆项目
```bash
git clone <repository-url>
cd hebei-cs-website
```

2. 启动后端
```bash
cd backend
mvn spring-boot:run
```

3. 启动前端
```bash
cd frontend
npm install
npm run dev
```

4. 启动管理后台
```bash
cd admin
npm install
npm run dev
```

### 生产部署

详细部署说明请参考 [deploy/DEPLOYMENT.md](deploy/DEPLOYMENT.md)

## 功能模块

- 首页
- 新闻中心
- 通知公告
- 政策法规
- 行业动态
- 学习考试
- 证书说明
- 证书查询

## 许可证

Copyright © 2024 北京中兴建机职业技能鉴定中心
