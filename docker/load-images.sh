#!/bin/bash

# ============================================
# 在服务器上加载 Docker 镜像
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}加载 Docker 镜像${NC}"
echo -e "${GREEN}===========================================${NC}"

# 获取脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGES_DIR="$SCRIPT_DIR/images"

if [ ! -d "$IMAGES_DIR" ]; then
    echo -e "${RED}错误: 镜像目录不存在: $IMAGES_DIR${NC}"
    echo "请先运行 ./docker/save-images.sh 保存镜像"
    exit 1
fi

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    exit 1
fi

# 加载镜像
for TAR_FILE in "$IMAGES_DIR"/*.tar.gz; do
    if [ -f "$TAR_FILE" ]; then
        echo -e "${YELLOW}加载镜像: $(basename "$TAR_FILE")${NC}"
        gunzip -c "$TAR_FILE" | docker load
        echo -e "${GREEN}✓ 加载完成${NC}"
    fi
done

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}所有镜像已加载！${NC}"
echo -e "${GREEN}===========================================${NC}"
echo ""
echo -e "${YELLOW}查看镜像：${NC}"
docker images | grep bjzxjj
echo ""
echo -e "${YELLOW}下一步：${NC}"
echo "使用 docker-compose 启动服务: docker-compose -f docker-compose.prod.yml up -d"

