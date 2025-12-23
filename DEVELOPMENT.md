# 开发指南

本文档介绍北京中兴建机职业技能鉴定中心网站项目的开发流程和环境配置。

## 项目结构

```
bjzxjj-website/
├── frontend/           # 前台网站（Vue3）
├── admin/             # 后台管理系统（Vue3）
├── backend/           # 后端服务（Spring Boot）
├── docs/              # 项目文档
└── README.md          # 项目说明
```

## 开发环境要��

### 基础环境
- Node.js 18+
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Git

### 推荐工具
- VS Code / WebStorm（前端开发）
- IntelliJ IDEA（后端开发）
- Navicat / DBeaver（数据库管理）
- Postman / Apifox（API测试）

## 快速开始

### 1. 克隆项目

```bash
git clone <项目地址>
cd hebei-cs-website
```

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE hebei_cs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 导入数据
mysql -u root -p hebei_cs < docs/database/schema.sql
mysql -u root -p hebei_cs < docs/database/init.sql
```

### 3. 启动后端服务

```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

后端服务启动在：http://localhost:8080/api

### 4. 启动前台网站

```bash
cd frontend
npm install
npm run dev
```

前台网站启动���：http://localhost:3000

### 5. 启动后台管理

```bash
cd admin
npm install
npm run dev
```

后台管理启动在：http://localhost:3001

默认账号：admin / admin123

## 开发流程

### 1. 功能开发流程

1. **需求分析**：明确功能需求和业务逻辑
2. **数据库设计**：设计数据表结构
3. **API设计**：设计前后端交互接口
4. **后端开发**：实现API接口
5. **前端开发**：实现页面和交互
6. **联调测试**：前后端联调
7. **测试验收**：功能测试

### 2. Git工作流

```bash
# 创建功能分支
git checkout -b feature/news-management

# 提交代码
git add .
git commit -m "feat: 新增新闻管理功能"

# 推送到远程
git push origin feature/news-management

# 合并到主分支
git checkout main
git merge feature/news-management
git push origin main
```

### 3. 提交信息规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 前端开发

### 技术栈
- Vue 3.4+
- Vite 5.0+
- TypeScript
- Vue Router 4
- Pinia
- Element Plus
- Axios
- SCSS

### 项目结构
```
src/
├── api/        # API接口
├── assets/     # 静态资源
├── components/ # 公共组件
├── router/     # 路由配置
├── stores/     # 状态管理
├── types/      # 类型定义
├── utils/      # 工具函数
└── views/      # 页面组件
```

### 开发规范

1. **组件命名**
   - 使用 PascalCase
   - 页面组件放在 views 目录
   - 公共组件放在 components 目录

2. **代码风格**
   - 使用 TypeScript
   - 遵循 ESLint 规则
   - 使用 Prettier 格式化

3. **状态管理**
   - 使用 Pinia
   - 模块化存储
   - 支持持久化

## 后端开发

### 技术栈
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Spring Security + JWT
- Hutool 5.8.25

### 项目结构
```
src/main/java/com/hebei/cs/
├── config/     # 配置类
├── controller/ # 控制器
├── dto/        # 数据传输对象
├── entity/     # 实体类
├── mapper/     # 数据访问层
├── service/    # 业务层
├── utils/      # 工具类
└── common/     # 公共类
```

### 开发规范

1. **分层架构**
   - Controller：控制层，处理请求
   - Service：业务层，业务逻辑
   - Mapper：数据访问层，数据库操作

2. **代码规范**
   - 遵循阿里巴巴Java开发手册
   - 使用驼峰命名法
   - 添加必要的注释

3. **API设计**
   - RESTful 风格
   - 统一响应格式
   - 完善的错误处理

## 数据库设计

### 设计原则
1. 使用 InnoDB 引擎
2. 字符集使用 utf8mb4
3. 必须有主键 id
4. 包含创建时间、更新时间、软删除字段
5. 使用下划线命名法

### 表结构规范
```sql
CREATE TABLE `example` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';
```

## 测试

### 前端测试
```bash
# 单元测试
npm run test:unit

# 端到端测试
npm run test:e2e
```

### 后端测试
```bash
# 单元测试
mvn test

# 集成测试
mvn test -Dtest=*IntegrationTest
```

## 部署

### 开发环境
- 前台：http://localhost:3000
- 后台：http://localhost:3001
- API：http://localhost:8080/api

### 生产环境
参考 `docs/Deployment.md` 部署指南。

## 常见问题

### 1. 跨域问题
- 开发环境：配置代理
- 生产环境：Nginx 反向代理

### 2. 数据库连接
- 检查 MySQL 服务是否启动
- 确认数据库配置是否正确
- 检查用户权限

### 3. 静态资源访问
- 后端：配置静态资源映射
- 前端：使用相对路径

### 4. 打包部署
- 前台：`npm run build`
- 后台：`mvn clean package`
- 数据库：导出 SQL 文件

## 联系方式

- 开发团队：contact@hsjs.org.cn
- 问题反馈：GitHub Issues