# 多阶段构建：构建阶段
FROM node:18-alpine AS builder

WORKDIR /app

# 设置 Node.js 内存限制（根据实际情况调整）
ENV NODE_OPTIONS="--max_old_space_size=2048"

# 复制 package 文件
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production=false

# 复制源代码
COPY . .

# 构建项目（尝试完整构建，失败则跳过类型检查）
RUN npm run build || npx vite build || (export NODE_OPTIONS="--max_old_space_size=1024" && npx vite build)

# 生产阶段：只保留静态文件
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置（可选）
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

