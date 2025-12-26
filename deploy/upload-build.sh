#!/bin/bash

# 上传构建产物到服务器的脚本
# 使用方法: ./upload-build.sh build-artifacts-YYYYMMDD_HHMMSS.tar.gz

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

if [ -z "$1" ]; then
    echo -e "${RED}错误: 请指定构建产物文件${NC}"
    echo "使用方法: $0 build-artifacts-YYYYMMDD_HHMMSS.tar.gz"
    exit 1
fi

ARCHIVE_FILE="$1"

if [ ! -f "$ARCHIVE_FILE" ]; then
    echo -e "${RED}错误: 文件不存在: ${ARCHIVE_FILE}${NC}"
    exit 1
fi

echo -e "${GREEN}=========================================="
echo "上传构建产物到服务器"
echo "==========================================${NC}"
echo ""
echo "构建产物文件: ${ARCHIVE_FILE}"
echo "目标服务器: ${PROJECT_HOME}"
echo ""

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo -e "${YELLOW}警告: 建议使用root用户运行此脚本${NC}"
fi

# 解压构建产物
echo -e "${YELLOW}解压构建产物...${NC}"
TEMP_DIR=$(mktemp -d)
tar -xzf "${ARCHIVE_FILE}" -C "${TEMP_DIR}"

# 备份现有文件（如果存在）
if [ -d "${FRONTEND_DIR}" ] && [ "$(ls -A ${FRONTEND_DIR})" ]; then
    echo "备份现有前端文件..."
    BACKUP_DIR="${FRONTEND_DIR}.backup.$(date +%Y%m%d_%H%M%S)"
    mv "${FRONTEND_DIR}" "${BACKUP_DIR}"
    mkdir -p "${FRONTEND_DIR}"
fi

if [ -d "${ADMIN_DIR}" ] && [ "$(ls -A ${ADMIN_DIR})" ]; then
    echo "备份现有管理后台文件..."
    BACKUP_DIR="${ADMIN_DIR}.backup.$(date +%Y%m%d_%H%M%S)"
    mv "${ADMIN_DIR}" "${BACKUP_DIR}"
    mkdir -p "${ADMIN_DIR}"
fi

# 复制前端构建产物
echo -e "${YELLOW}部署前端构建产物...${NC}"
mkdir -p "${FRONTEND_DIR}"
cp -r "${TEMP_DIR}/frontend-dist"/* "${FRONTEND_DIR}/"
chown -R nginx:nginx "${FRONTEND_DIR}" || true

# 复制管理后台构建产物
echo -e "${YELLOW}部署管理后台构建产物...${NC}"
mkdir -p "${ADMIN_DIR}"
cp -r "${TEMP_DIR}/admin-dist"/* "${ADMIN_DIR}/"
chown -R nginx:nginx "${ADMIN_DIR}" || true

# 清理临时文件
rm -rf "${TEMP_DIR}"

# 重启 Nginx（如果需要）
if systemctl is-active --quiet nginx 2>/dev/null; then
    echo -e "${YELLOW}重启 Nginx...${NC}"
    systemctl reload nginx
fi

echo -e "${GREEN}=========================================="
echo "部署完成！"
echo "==========================================${NC}"
echo ""
echo "前端访问: http://${DOMAIN}/"
echo "管理后台访问: http://${DOMAIN}/admin/"
echo ""

