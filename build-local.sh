#!/bin/bash

# 本地构建脚本 - 用于在本地构建前端和管理后台，然后上传到服务器

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${GREEN}=========================================="
echo "本地构建脚本"
echo "==========================================${NC}"
echo ""

# 检查 Node.js 和 npm
if ! command -v node &> /dev/null; then
    echo -e "${RED}错误: 未安装 Node.js${NC}"
    echo "请先安装 Node.js: https://nodejs.org/"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo -e "${RED}错误: 未安装 npm${NC}"
    exit 1
fi

echo "Node.js版本: $(node -v)"
echo "npm版本: $(npm -v)"
echo ""

# 1. 构建前端
echo -e "${YELLOW}[1/2] 构建前端项目...${NC}"
cd ${SCRIPT_DIR}/frontend

if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
fi

echo "开始构建前端..."
npm run build

if [ ! -d "dist" ]; then
    echo -e "${RED}前端构建失败！${NC}"
    exit 1
fi

echo -e "${GREEN}前端构建完成${NC}"
echo ""

# 2. 构建管理后台
echo -e "${YELLOW}[2/2] 构建管理后台...${NC}"
cd ${SCRIPT_DIR}/admin

if [ ! -d "node_modules" ]; then
    echo "安装管理后台依赖..."
    npm install
fi

echo "开始构建管理后台..."
npm run build

if [ ! -d "dist" ]; then
    echo -e "${RED}管理后台构建失败！${NC}"
    exit 1
fi

echo -e "${GREEN}管理后台构建完成${NC}"
echo ""

# 3. 创建构建产物压缩包
echo -e "${YELLOW}创建构建产物压缩包...${NC}"
cd ${SCRIPT_DIR}

BUILD_DIR="build-artifacts"
mkdir -p ${BUILD_DIR}

# 复制前端构建产物
echo "打包前端构建产物..."
cp -r frontend/dist ${BUILD_DIR}/frontend-dist

# 复制管理后台构建产物
echo "打包管理后台构建产物..."
cp -r admin/dist ${BUILD_DIR}/admin-dist

# 创建压缩包
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
ARCHIVE_NAME="build-artifacts-${TIMESTAMP}.tar.gz"

tar -czf ${ARCHIVE_NAME} -C ${BUILD_DIR} frontend-dist admin-dist

echo -e "${GREEN}构建产物已打包: ${ARCHIVE_NAME}${NC}"
echo ""

# 清理临时目录
rm -rf ${BUILD_DIR}

echo -e "${GREEN}=========================================="
echo "构建完成！"
echo "==========================================${NC}"
echo ""
echo "构建产物: ${ARCHIVE_NAME}"
echo ""
echo "上传到服务器后，执行以下命令解压和部署："
echo "  tar -xzf ${ARCHIVE_NAME}"
echo "  cp -r frontend-dist/* /home/bjzxjj/frontend/"
echo "  cp -r admin-dist/* /home/bjzxjj/admin/"
echo ""

