#!/bin/bash

# ============================================
# Docker 镜像构建脚本
# 在本地构建所有 Docker 镜像
# 解决服务器内存不足的问题
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}开始构建 Docker 镜像${NC}"
echo -e "${GREEN}===========================================${NC}"

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    echo "请先安装 Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

# 1. 构建前端镜像
echo -e "${YELLOW}[1/3] 构建前端镜像...${NC}"
cd "$PROJECT_ROOT/frontend"
docker build -t bjzxjj-frontend:latest .
echo -e "${GREEN}✓ 前端镜像构建完成${NC}"

# 2. 构建管理后台镜像
echo -e "${YELLOW}[2/3] 构建管理后台镜像...${NC}"
cd "$PROJECT_ROOT/admin"
docker build -t bjzxjj-admin:latest .
echo -e "${GREEN}✓ 管理后台镜像构建完成${NC}"

# 3. 构建后端镜像
echo -e "${YELLOW}[3/3] 构建后端镜像...${NC}"
cd "$PROJECT_ROOT/backend"
docker build -t bjzxjj-backend:latest .
echo -e "${GREEN}✓ 后端镜像构建完成${NC}"

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}所有镜像构建完成！${NC}"
echo -e "${GREEN}===========================================${NC}"
echo ""
echo -e "${YELLOW}查看镜像：${NC}"
docker images | grep bjzxjj
echo ""
echo -e "${YELLOW}下一步：${NC}"
echo "1. 保存镜像为 tar 文件: ./docker/save-images.sh"
echo "2. 或推送到 Docker Registry: ./docker/push-images.sh"
echo "3. 或直接使用 docker-compose: docker-compose up -d"

