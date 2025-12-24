#!/bin/bash

# 一键部署脚本 - 按顺序执行所有部署步骤

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=========================================="
echo "北京中兴建机职业技能鉴定中心网站 - 一键部署"
echo "==========================================${NC}"
echo ""
echo "部署配置："
echo "  项目目录: ${PROJECT_HOME}"
echo "  域名: ${DOMAIN}"
echo "  数据库: ${DB_NAME}"
echo ""

# 检查配置文件
if [ -z "$DB_PASSWORD" ] || [ -z "$MYSQL_ROOT_PASSWORD" ]; then
    echo -e "${RED}错误: 请先在 config.sh 中配置以下变量：${NC}"
    echo "  - MYSQL_ROOT_PASSWORD (MySQL root密码)"
    echo "  - DB_PASSWORD (数据库密码)"
    exit 1
fi

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}请使用root用户运行此脚本${NC}"
    exit 1
fi

# 步骤1: 环境准备 - 首次部署时需要，后续可跳过
# echo -e "${YELLOW}[步骤 1/5] 准备部署环境...${NC}"
# read -p "是否运行环境准备脚本？(y/n): " RUN_PREPARE
# if [ "$RUN_PREPARE" = "y" ] || [ "$RUN_PREPARE" = "Y" ]; then
#     bash ${SCRIPT_DIR}/prepare.sh
# else
#     echo "跳过环境准备"
# fi

# 步骤2: 初始化数据库 - 首次部署时需要，后续可跳过
# echo -e "${YELLOW}[步骤 2/5] 初始化数据库...${NC}"
# read -p "是否初始化数据库？(y/n): " RUN_INIT_DB
# if [ "$RUN_INIT_DB" = "y" ] || [ "$RUN_INIT_DB" = "Y" ]; then
#     bash ${SCRIPT_DIR}/init-database.sh
# else
#     echo "跳过数据库初始化"
# fi

# 步骤3: 安装系统服务 - 首次部署时需要，后续可跳过
# echo -e "${YELLOW}[步骤 3/5] 安装系统服务...${NC}"
# read -p "是否安装系统服务？(y/n): " RUN_SETUP_SERVICE
# if [ "$RUN_SETUP_SERVICE" = "y" ] || [ "$RUN_SETUP_SERVICE" = "Y" ]; then
#     bash ${SCRIPT_DIR}/setup-service.sh
# else
#     echo "跳过系统服务安装"
# fi

# 步骤4: 部署项目（主要步骤，通常需要执行）
echo -e "${YELLOW}[步骤 4/5] 部署项目...${NC}"
# read -p "是否部署项目？(y/n): " RUN_DEPLOY
# if [ "$RUN_DEPLOY" = "y" ] || [ "$RUN_DEPLOY" = "Y" ]; then
    bash ${SCRIPT_DIR}/deploy.sh
# else
#     echo "跳过项目部署"
# fi

# 步骤5: 启动服务（主要步骤，通常需要执行）
echo -e "${YELLOW}[步骤 5/5] 启动服务...${NC}"
# read -p "是否启动后端服务？(y/n): " START_SERVICE
# if [ "$START_SERVICE" = "y" ] || [ "$START_SERVICE" = "Y" ]; then
    systemctl start bjzxjj-backend 2>/dev/null || echo "后端服务未安装或已启动"
    systemctl status bjzxjj-backend 2>/dev/null || echo "无法查看服务状态"
# fi

echo -e "${GREEN}=========================================="
echo "部署流程完成！"
echo "==========================================${NC}"
echo ""
echo "访问地址："
echo "  前端: http://${DOMAIN}/"
echo "  管理后台: http://${DOMAIN}/admin/"
echo ""
echo "服务管理："
echo "  systemctl start/stop/restart/status bjzxjj-backend"
echo "  journalctl -u bjzxjj-backend -f"
echo ""

