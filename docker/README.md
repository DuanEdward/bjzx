# Docker 部署方案

## 概述

使用 Docker 可以解决服务器内存不足导致的构建问题。主要优势：

1. **本地构建，服务器运行**：在本地（内存充足）构建镜像，然后传输到服务器
2. **多阶段构建**：减少最终镜像大小，优化内存使用
3. **资源隔离**：更好的资源管理和隔离
4. **易于扩展**：可以轻松扩展到多服务器

## 目录结构

```
docker/
├── build-images.sh      # 在本地构建所有镜像
├── save-images.sh       # 保存镜像为 tar 文件
├── load-images.sh        # 在服务器上加载镜像
├── deploy-docker.sh      # 服务器端部署脚本
└── images/              # 镜像文件目录（由 save-images.sh 创建）
```

## 使用流程

### 方案一：本地构建 + 传输镜像（推荐）

#### 1. 在本地构建镜像

```bash
# 构建所有镜像
./docker/build-images.sh
```

#### 2. 保存镜像为文件

```bash
# 保存镜像为 tar.gz 文件
./docker/save-images.sh
```

这会创建 `docker/images/` 目录，包含所有镜像的压缩文件。

#### 3. 上传镜像到服务器

```bash
# 使用 scp 上传
scp -r docker/images/ user@server:/path/to/bjzx/docker/

# 或使用 rsync
rsync -avz docker/images/ user@server:/path/to/bjzx/docker/images/
```

#### 4. 在服务器上加载镜像

```bash
# SSH 到服务器
ssh user@server

# 进入项目目录
cd /path/to/bjzx

# 加载镜像
./docker/load-images.sh
```

#### 5. 部署服务

```bash
# 使用预构建的镜像部署
./docker/deploy-docker.sh
```

### 方案二：使用 Docker Registry

#### 1. 构建并推送镜像

```bash
# 构建镜像
./docker/build-images.sh

# 标记镜像（替换为你的 registry 地址）
docker tag bjzxjj-frontend:latest your-registry.com/bjzxjj-frontend:latest
docker tag bjzxjj-admin:latest your-registry.com/bjzxjj-admin:latest
docker tag bjzxjj-backend:latest your-registry.com/bjzxjj-backend:latest

# 推送镜像
docker push your-registry.com/bjzxjj-frontend:latest
docker push your-registry.com/bjzxjj-admin:latest
docker push your-registry.com/bjzxjj-backend:latest
```

#### 2. 在服务器上拉取镜像

```bash
docker pull your-registry.com/bjzxjj-frontend:latest
docker pull your-registry.com/bjzxjj-admin:latest
docker pull your-registry.com/bjzxjj-backend:latest

# 重新标记为本地镜像名
docker tag your-registry.com/bjzxjj-frontend:latest bjzxjj-frontend:latest
docker tag your-registry.com/bjzxjj-admin:latest bjzxjj-admin:latest
docker tag your-registry.com/bjzxjj-backend:latest bjzxjj-backend:latest
```

#### 3. 部署服务

```bash
./docker/deploy-docker.sh
```

## 直接使用 Docker Compose

### 开发环境

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 生产环境

```bash
# 使用预构建的镜像
docker-compose -f docker-compose.prod.yml up -d

# 查看日志
docker-compose -f docker-compose.prod.yml logs -f

# 停止服务
docker-compose -f docker-compose.prod.yml down
```

## 镜像说明

### 前端镜像 (bjzxjj-frontend)

- **基础镜像**: `node:18-alpine` (构建阶段) + `nginx:alpine` (运行阶段)
- **构建内存**: 默认 2GB，失败时降级到 1GB
- **最终大小**: ~50MB (只包含静态文件)

### 管理后台镜像 (bjzxjj-admin)

- **基础镜像**: `node:18-alpine` (构建阶段) + `nginx:alpine` (运行阶段)
- **构建内存**: 默认 2GB，失败时降级到 1GB
- **最终大小**: ~80MB (包含 echarts)

### 后端镜像 (bjzxjj-backend)

- **基础镜像**: `maven:3.8-openjdk-17` (构建阶段) + `openjdk:17-jre-slim` (运行阶段)
- **JVM 内存**: 512MB - 1024MB (可配置)
- **最终大小**: ~200MB

## 配置说明

### 环境变量

在 `deploy/config.sh` 中配置：

```bash
MYSQL_ROOT_PASSWORD="Root@123456"
DB_NAME="bjzxjj"
DB_USER="bjzxjj_user"
DB_PASSWORD="Bjzx@123456"
JWT_SECRET="your-jwt-secret"
BACKEND_PORT="8080"
```

### 数据持久化

- **MySQL 数据**: Docker volume `mysql_data`
- **上传文件**: `./uploads` 目录
- **日志文件**: `./logs` 目录

## 常见问题

### 1. 构建失败：内存不足

**解决方案**：
- 在本地（内存充足）构建镜像
- 使用 `docker/build-images.sh` 脚本，它会自动处理内存问题

### 2. 镜像文件太大

**解决方案**：
- 使用多阶段构建（已实现）
- 使用 `.dockerignore` 排除不必要的文件
- 使用 `docker save` 压缩镜像

### 3. 服务无法启动

**检查**：
```bash
# 查看服务状态
docker-compose -f docker-compose.prod.yml ps

# 查看日志
docker-compose -f docker-compose.prod.yml logs backend
docker-compose -f docker-compose.prod.yml logs mysql
```

### 4. 数据库连接失败

**检查**：
- MySQL 容器是否正常运行
- 环境变量配置是否正确
- 网络连接是否正常

## 优势总结

✅ **解决内存问题**：在本地构建，服务器只运行  
✅ **简化部署**：一次构建，多处使用  
✅ **环境一致**：开发、测试、生产环境一致  
✅ **易于扩展**：可以轻松扩展到多服务器  
✅ **资源隔离**：每个服务独立运行，互不干扰  

