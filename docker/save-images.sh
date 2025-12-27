#!/bin/bash

# ============================================
# 保存 Docker 镜像为 tar 文件
# 用于传输到服务器
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}保存 Docker 镜像${NC}"
echo -e "${GREEN}===========================================${NC}"

# 创建输出目录
OUTPUT_DIR="./docker/images"
mkdir -p "$OUTPUT_DIR"

# 保存镜像
IMAGES=("bjzxjj-frontend:latest" "bjzxjj-admin:latest" "bjzxjj-backend:latest")

for IMAGE in "${IMAGES[@]}"; do
    echo -e "${YELLOW}保存镜像: ${IMAGE}${NC}"
    FILENAME=$(echo "$IMAGE" | tr '/:' '_')
    docker save "$IMAGE" | gzip > "$OUTPUT_DIR/${FILENAME}.tar.gz"
    echo -e "${GREEN}✓ 已保存: ${OUTPUT_DIR}/${FILENAME}.tar.gz${NC}"
done

echo -e "${GREEN}===========================================${NC}"
echo -e "${GREEN}所有镜像已保存！${NC}"
echo -e "${GREEN}===========================================${NC}"
echo ""
echo -e "${YELLOW}文件大小：${NC}"
du -h "$OUTPUT_DIR"/*.tar.gz
echo ""
echo -e "${YELLOW}下一步：${NC}"
echo "1. 将 $OUTPUT_DIR 目录上传到服务器"
echo "2. 在服务器上运行: ./docker/load-images.sh"

