#!/bin/bash

# ============================================
# Docker 部署脚本（服务器端）
# 使用预构建的镜像部署
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 加载配置
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CONFIG_FILE="$PROJECT_ROOT/deploy/config.sh"

if [ -f "$CONFIG_FILE" ]; then
    source "$CONFIG_FILE"
else
    echo -e "${YELLOW}警告: 未找到配置文件，使用默认配置${NC}"
    export MYSQL_ROOT_PASSWORD="Root@123456"
    export DB_NAME="bjzxjj"
    export DB_USER="bjzxjj_user"
    export DB_PASSWORD="Bjzx@123456"
    export JWT_SECRET="bjzxjj-website-jwt-secret-key-2024-prod"
    export BACKEND_PORT="8080"
fi

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}Docker 部署${NC}"
echo -e "${GREEN}===========================================${NC}"

cd "$PROJECT_ROOT"

# 检查 Docker 和 Docker Compose
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}错误: Docker Compose 未安装${NC}"
    exit 1
fi

# 检查镜像是否存在
echo -e "${YELLOW}检查 Docker 镜像...${NC}"
MISSING_IMAGES=()
for IMAGE in "bjzxjj-frontend:latest" "bjzxjj-admin:latest" "bjzxjj-backend:latest"; do
    if ! docker images "$IMAGE" | grep -q "$IMAGE"; then
        MISSING_IMAGES+=("$IMAGE")
    fi
done

if [ ${#MISSING_IMAGES[@]} -gt 0 ]; then
    echo -e "${RED}错误: 以下镜像不存在：${NC}"
    for IMAGE in "${MISSING_IMAGES[@]}"; do
        echo "  - $IMAGE"
    done
    echo ""
    echo -e "${YELLOW}请先：${NC}"
    echo "1. 在本地运行: ./docker/build-images.sh"
    echo "2. 运行: ./docker/save-images.sh"
    echo "3. 上传镜像文件到服务器"
    echo "4. 在服务器上运行: ./docker/load-images.sh"
    exit 1
fi

# 创建必要的目录
echo -e "${YELLOW}创建必要的目录...${NC}"
mkdir -p uploads logs

# 停止现有服务
echo -e "${YELLOW}停止现有服务...${NC}"
docker-compose -f docker-compose.prod.yml down 2>/dev/null || true

# 启动服务
echo -e "${YELLOW}启动服务...${NC}"
docker-compose -f docker-compose.prod.yml up -d

# 等待服务启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 10

# 检查服务状态
echo -e "${YELLOW}检查服务状态...${NC}"
docker-compose -f docker-compose.prod.yml ps

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}部署完成！${NC}"
echo -e "${GREEN}===========================================${NC}"
echo ""
echo -e "${YELLOW}服务状态：${NC}"
docker-compose -f docker-compose.prod.yml ps
echo ""
echo -e "${YELLOW}查看日志：${NC}"
echo "  docker-compose -f docker-compose.prod.yml logs -f"
echo ""
echo -e "${YELLOW}停止服务：${NC}"
echo "  docker-compose -f docker-compose.prod.yml down"

