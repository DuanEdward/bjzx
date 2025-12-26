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
if [ -e "node_modules/sass" ]; then
    if [ -L "node_modules/sass" ]; then
        echo -e "${YELLOW}注意: node_modules/sass 是符号链接（由 sass-embedded 创建，这是正常的）${NC}"
        echo -e "${GREEN}前端依赖修复完成（sass 符号链接不影响构建）${NC}"
    else
        echo -e "${RED}警告: node_modules 中仍然存在 sass 包（非符号链接）${NC}"
        echo "检查是否有其他包依赖了 sass..."
        if npm ls sass 2>/dev/null | grep -q "sass@"; then
            echo -e "${YELLOW}发现其他包依赖了 sass，列出依赖关系：${NC}"
            npm ls sass
        else
            echo "没有其他包依赖 sass，尝试手动移除..."
            rm -rf node_modules/sass
            echo -e "${GREEN}已手动移除 sass 包${NC}"
        fi
    fi
else
    echo -e "${GREEN}前端依赖修复完成（没有 sass 包）${NC}"
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
if [ -e "node_modules/sass" ]; then
    if [ -L "node_modules/sass" ]; then
        echo -e "${YELLOW}注意: node_modules/sass 是符号链接（由 sass-embedded 创建，这是正常的）${NC}"
        echo -e "${GREEN}管理后台依赖修复完成（sass 符号链接不影响构建）${NC}"
    else
        echo -e "${RED}警告: node_modules 中仍然存在 sass 包（非符号链接）${NC}"
        echo "检查是否有其他包依赖了 sass..."
        if npm ls sass 2>/dev/null | grep -q "sass@"; then
            echo -e "${YELLOW}发现其他包依赖了 sass，列出依赖关系：${NC}"
            npm ls sass
        else
            echo "没有其他包依赖 sass，尝试手动移除..."
            rm -rf node_modules/sass
            echo -e "${GREEN}已手动移除 sass 包${NC}"
        fi
    fi
else
    echo -e "${GREEN}管理后台依赖修复完成（没有 sass 包）${NC}"
fi

echo ""
echo -e "${GREEN}=========================================="
echo "修复完成！"
echo "==========================================${NC}"
echo ""
echo -e "${YELLOW}重要说明：${NC}"
echo "如果 node_modules/sass 是符号链接（由 sass-embedded 创建），这是正常的，"
echo "不会影响构建，也不会导致内存问题。只有真正的 sass 包才会导致问题。"
echo ""
echo "请重新构建项目："
echo "  cd frontend && npm run build"
echo "  cd admin && npm run build"
echo ""
echo "如果构建时仍然看到 legacy-js-api 警告，但构建成功，可以忽略这些警告。"
echo "这些警告来自某些依赖包（如 element-plus）内部使用的 legacy API，"
echo "不会影响功能，也不会导致内存问题（因为主要使用现代 API）。"
echo ""

