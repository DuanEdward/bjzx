#!/bin/bash

# 修复 Sass 依赖问题的脚本
# 确保只使用 sass-embedded，移除 legacy sass 包

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=========================================="
echo "修复 Sass 依赖问题"
echo "==========================================${NC}"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_SOURCE_DIR="${SCRIPT_DIR}/.."

# 修复前端
echo -e "${YELLOW}[1/2] 修复前端 Sass 依赖...${NC}"
cd ${PROJECT_SOURCE_DIR}/frontend

# 检查 package.json 中是否还有 sass
if grep -q '"sass":' package.json; then
    echo "移除 package.json 中的 sass 依赖..."
    # 使用 sed 移除 sass 行（需要根据实际格式调整）
    sed -i '/"sass":/d' package.json
fi

# 删除 node_modules 和 package-lock.json
echo "清理旧的依赖..."
rm -rf node_modules package-lock.json

# 重新安装依赖（只会安装 sass-embedded）
echo "重新安装依赖..."
npm install

# 验证是否还有 sass 包
if [ -d "node_modules/sass" ] && [ ! -L "node_modules/sass" ]; then
    echo -e "${RED}警告: node_modules 中仍然存在 sass 包${NC}"
    echo "尝试手动移除..."
    rm -rf node_modules/sass
    # 检查是否有其他包依赖了 sass
    npm ls sass 2>/dev/null || echo "没有其他包依赖 sass"
else
    echo -e "${GREEN}前端依赖修复完成${NC}"
fi

echo ""

# 修复管理后台
echo -e "${YELLOW}[2/2] 修复管理后台 Sass 依赖...${NC}"
cd ${PROJECT_SOURCE_DIR}/admin

# 检查 package.json 中是否还有 sass
if grep -q '"sass":' package.json; then
    echo "移除 package.json 中的 sass 依赖..."
    sed -i '/"sass":/d' package.json
fi

# 删除 node_modules 和 package-lock.json
echo "清理旧的依赖..."
rm -rf node_modules package-lock.json

# 重新安装依赖
echo "重新安装依赖..."
npm install

# 验证是否还有 sass 包
if [ -d "node_modules/sass" ] && [ ! -L "node_modules/sass" ]; then
    echo -e "${RED}警告: node_modules 中仍然存在 sass 包${NC}"
    echo "尝试手动移除..."
    rm -rf node_modules/sass
    npm ls sass 2>/dev/null || echo "没有其他包依赖 sass"
else
    echo -e "${GREEN}管理后台依赖修复完成${NC}"
fi

echo ""
echo -e "${GREEN}=========================================="
echo "修复完成！"
echo "==========================================${NC}"
echo ""
echo "请重新构建项目："
echo "  cd frontend && npm run build"
echo "  cd admin && npm run build"
echo ""

