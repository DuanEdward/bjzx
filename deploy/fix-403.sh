#!/bin/bash

# 快速修复403权限问题脚本

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
echo "修复403权限问题"
echo "==========================================${NC}"
echo ""

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}请使用root用户运行此脚本${NC}"
    exit 1
fi

# 检查nginx用户是否存在
if id nginx &>/dev/null; then
    NGINX_USER="nginx"
    NGINX_GROUP="nginx"
    echo -e "${GREEN}✓ 检测到nginx用户${NC}"
else
    echo -e "${YELLOW}警告: nginx用户不存在，使用root用户${NC}"
    NGINX_USER="root"
    NGINX_GROUP="root"
fi

# 确保目录存在
echo -e "${YELLOW}[1] 检查并创建目录...${NC}"
mkdir -p ${FRONTEND_DIR} ${ADMIN_DIR} ${UPLOAD_DIR} ${LOG_DIR} ${BACKEND_DIR}
echo -e "${GREEN}✓ 目录检查完成${NC}"
echo ""

# 修复前端目录权限
echo -e "${YELLOW}[2] 修复前端目录权限...${NC}"
if [ -d "${FRONTEND_DIR}" ]; then
    # 设置目录权限（755：所有者rwx，组和其他r-x）
    find ${FRONTEND_DIR} -type d -exec chmod 755 {} \; 2>/dev/null || true
    # 设置文件权限（644：所有者rw-，组和其他r--）
    find ${FRONTEND_DIR} -type f -exec chmod 644 {} \; 2>/dev/null || true
    # 设置所有者
    chown -R ${NGINX_USER}:${NGINX_GROUP} ${FRONTEND_DIR} 2>/dev/null || true
    # 确保根目录权限
    chmod 755 ${FRONTEND_DIR} 2>/dev/null || true
    
    echo -e "${GREEN}✓ 前端目录权限已修复${NC}"
    echo "  目录: ${FRONTEND_DIR}"
    echo "  所有者: $(stat -c '%U:%G' ${FRONTEND_DIR} 2>/dev/null || echo 'unknown')"
    echo "  权限: $(stat -c '%a' ${FRONTEND_DIR} 2>/dev/null || echo 'unknown')"
else
    echo -e "${RED}✗ 前端目录不存在: ${FRONTEND_DIR}${NC}"
fi
echo ""

# 修复管理后台目录权限
echo -e "${YELLOW}[3] 修复管理后台目录权限...${NC}"
if [ -d "${ADMIN_DIR}" ]; then
    # 设置目录权限
    find ${ADMIN_DIR} -type d -exec chmod 755 {} \; 2>/dev/null || true
    # 设置文件权限
    find ${ADMIN_DIR} -type f -exec chmod 644 {} \; 2>/dev/null || true
    # 设置所有者
    chown -R ${NGINX_USER}:${NGINX_GROUP} ${ADMIN_DIR} 2>/dev/null || true
    # 确保根目录权限
    chmod 755 ${ADMIN_DIR} 2>/dev/null || true
    
    echo -e "${GREEN}✓ 管理后台目录权限已修复${NC}"
    echo "  目录: ${ADMIN_DIR}"
    echo "  所有者: $(stat -c '%U:%G' ${ADMIN_DIR} 2>/dev/null || echo 'unknown')"
    echo "  权限: $(stat -c '%a' ${ADMIN_DIR} 2>/dev/null || echo 'unknown')"
else
    echo -e "${RED}✗ 管理后台目录不存在: ${ADMIN_DIR}${NC}"
fi
echo ""

# 检查SELinux状态
echo -e "${YELLOW}[4] 检查SELinux状态...${NC}"
if command -v getenforce &>/dev/null; then
    SELINUX_STATUS=$(getenforce 2>/dev/null || echo "Disabled")
    echo "  SELinux状态: ${SELINUX_STATUS}"
    
    if [ "$SELINUX_STATUS" = "Enforcing" ]; then
        echo -e "${YELLOW}  ⚠ SELinux处于强制模式，可能需要设置上下文${NC}"
        echo "  如果403问题仍然存在，可以尝试："
        echo "    chcon -R -t httpd_sys_content_t ${FRONTEND_DIR}"
        echo "    chcon -R -t httpd_sys_content_t ${ADMIN_DIR}"
        echo "  或者临时设置为Permissive模式："
        echo "    setenforce 0"
    elif [ "$SELINUX_STATUS" = "Permissive" ]; then
        echo -e "${GREEN}  ✓ SELinux处于Permissive模式，不会阻止访问${NC}"
    else
        echo -e "${GREEN}  ✓ SELinux已禁用${NC}"
    fi
else
    echo -e "${GREEN}  ✓ SELinux未安装或已禁用${NC}"
fi
echo ""

# 验证权限
echo -e "${YELLOW}[5] 验证权限设置...${NC}"
if [ -f "${FRONTEND_DIR}/index.html" ]; then
    if [ -r "${FRONTEND_DIR}/index.html" ]; then
        echo -e "${GREEN}  ✓ 前端index.html可读${NC}"
    else
        echo -e "${RED}  ✗ 前端index.html不可读${NC}"
    fi
else
    echo -e "${YELLOW}  ⚠ 前端index.html不存在${NC}"
fi

if [ -f "${ADMIN_DIR}/index.html" ]; then
    if [ -r "${ADMIN_DIR}/index.html" ]; then
        echo -e "${GREEN}  ✓ 管理后台index.html可读${NC}"
    else
        echo -e "${RED}  ✗ 管理后台index.html不可读${NC}"
    fi
else
    echo -e "${YELLOW}  ⚠ 管理后台index.html不存在${NC}"
fi
echo ""

# 重启Nginx
echo -e "${YELLOW}[6] 重启Nginx服务...${NC}"
if systemctl is-active --quiet nginx; then
    systemctl restart nginx
    echo -e "${GREEN}✓ Nginx已重启${NC}"
else
    echo -e "${YELLOW}⚠ Nginx服务未运行${NC}"
fi
echo ""

echo -e "${GREEN}=========================================="
echo "权限修复完成！"
echo "==========================================${NC}"
echo ""
echo "如果问题仍然存在，请检查："
echo "  1. Nginx错误日志: tail -50 /var/log/nginx/bjzxjj-error.log"
echo "  2. 文件权限: ls -la ${FRONTEND_DIR}"
echo "  3. SELinux状态: getenforce"
echo "  4. Nginx配置: nginx -t"
echo ""

