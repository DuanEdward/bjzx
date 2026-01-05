#!/bin/bash

# 部署检查脚本 - 用于诊断前端资源404问题

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
echo "部署检查脚本"
echo "==========================================${NC}"
echo ""

# 1. 检查目录是否存在
echo -e "${YELLOW}[1] 检查部署目录...${NC}"
if [ -d "${FRONTEND_DIR}" ]; then
    echo -e "${GREEN}✓ 前端目录存在: ${FRONTEND_DIR}${NC}"
    echo "  文件数量: $(find ${FRONTEND_DIR} -type f | wc -l)"
    echo "  目录大小: $(du -sh ${FRONTEND_DIR} 2>/dev/null | cut -f1)"
    if [ -f "${FRONTEND_DIR}/index.html" ]; then
        echo -e "${GREEN}  ✓ index.html 存在${NC}"
    else
        echo -e "${RED}  ✗ index.html 不存在${NC}"
    fi
else
    echo -e "${RED}✗ 前端目录不存在: ${FRONTEND_DIR}${NC}"
fi

if [ -d "${ADMIN_DIR}" ]; then
    echo -e "${GREEN}✓ 管理后台目录存在: ${ADMIN_DIR}${NC}"
    echo "  文件数量: $(find ${ADMIN_DIR} -type f | wc -l)"
    echo "  目录大小: $(du -sh ${ADMIN_DIR} 2>/dev/null | cut -f1)"
    if [ -f "${ADMIN_DIR}/index.html" ]; then
        echo -e "${GREEN}  ✓ index.html 存在${NC}"
    else
        echo -e "${RED}  ✗ index.html 不存在${NC}"
    fi
else
    echo -e "${RED}✗ 管理后台目录不存在: ${ADMIN_DIR}${NC}"
fi

echo ""

# 2. 检查文件权限
echo -e "${YELLOW}[2] 检查文件权限...${NC}"

# 检查nginx用户
if id nginx &>/dev/null; then
    NGINX_USER="nginx"
    echo -e "${GREEN}  ✓ nginx用户存在${NC}"
else
    NGINX_USER="root"
    echo -e "${YELLOW}  ⚠ nginx用户不存在，使用root${NC}"
fi

if [ -d "${FRONTEND_DIR}" ]; then
    FRONTEND_PERM=$(stat -c "%a" ${FRONTEND_DIR} 2>/dev/null || stat -f "%OLp" ${FRONTEND_DIR} 2>/dev/null || echo "unknown")
    FRONTEND_OWNER=$(stat -c "%U:%G" ${FRONTEND_DIR} 2>/dev/null || stat -f "%Su:%Sg" ${FRONTEND_DIR} 2>/dev/null || echo "unknown")
    echo "  前端目录权限: ${FRONTEND_PERM} (所有者: ${FRONTEND_OWNER})"
    
    # 检查权限是否足够（目录应该是755）
    if [ "$FRONTEND_PERM" != "755" ] && [ "$FRONTEND_PERM" != "unknown" ]; then
        echo -e "${YELLOW}    ⚠ 目录权限建议为755${NC}"
    fi
    
    if [ -f "${FRONTEND_DIR}/index.html" ]; then
        FILE_PERM=$(stat -c "%a" ${FRONTEND_DIR}/index.html 2>/dev/null || stat -f "%OLp" ${FRONTEND_DIR}/index.html 2>/dev/null || echo "unknown")
        FILE_OWNER=$(stat -c "%U:%G" ${FRONTEND_DIR}/index.html 2>/dev/null || stat -f "%Su:%Sg" ${FRONTEND_DIR}/index.html 2>/dev/null || echo "unknown")
        echo "  index.html 权限: ${FILE_PERM} (所有者: ${FILE_OWNER})"
        
        # 检查文件是否可读
        if [ -r "${FRONTEND_DIR}/index.html" ]; then
            echo -e "${GREEN}    ✓ 文件可读${NC}"
        else
            echo -e "${RED}    ✗ 文件不可读（可能导致403错误）${NC}"
        fi
    fi
fi

if [ -d "${ADMIN_DIR}" ]; then
    ADMIN_PERM=$(stat -c "%a" ${ADMIN_DIR} 2>/dev/null || stat -f "%OLp" ${ADMIN_DIR} 2>/dev/null || echo "unknown")
    ADMIN_OWNER=$(stat -c "%U:%G" ${ADMIN_DIR} 2>/dev/null || stat -f "%Su:%Sg" ${ADMIN_DIR} 2>/dev/null || echo "unknown")
    echo "  管理后台目录权限: ${ADMIN_PERM} (所有者: ${ADMIN_OWNER})"
    
    # 检查权限是否足够
    if [ "$ADMIN_PERM" != "755" ] && [ "$ADMIN_PERM" != "unknown" ]; then
        echo -e "${YELLOW}    ⚠ 目录权限建议为755${NC}"
    fi
    
    if [ -f "${ADMIN_DIR}/index.html" ]; then
        FILE_PERM=$(stat -c "%a" ${ADMIN_DIR}/index.html 2>/dev/null || stat -f "%OLp" ${ADMIN_DIR}/index.html 2>/dev/null || echo "unknown")
        FILE_OWNER=$(stat -c "%U:%G" ${ADMIN_DIR}/index.html 2>/dev/null || stat -f "%Su:%Sg" ${ADMIN_DIR}/index.html 2>/dev/null || echo "unknown")
        echo "  index.html 权限: ${FILE_PERM} (所有者: ${FILE_OWNER})"
        
        # 检查文件是否可读
        if [ -r "${ADMIN_DIR}/index.html" ]; then
            echo -e "${GREEN}    ✓ 文件可读${NC}"
        else
            echo -e "${RED}    ✗ 文件不可读（可能导致403错误）${NC}"
        fi
    fi
fi

echo ""

# 3. 检查静态资源文件
echo -e "${YELLOW}[3] 检查静态资源文件...${NC}"
if [ -d "${FRONTEND_DIR}" ]; then
    JS_COUNT=$(find ${FRONTEND_DIR} -name "*.js" | wc -l)
    CSS_COUNT=$(find ${FRONTEND_DIR} -name "*.css" | wc -l)
    echo "  前端 JS 文件: ${JS_COUNT}"
    echo "  前端 CSS 文件: ${CSS_COUNT}"
    if [ "$JS_COUNT" -eq 0 ]; then
        echo -e "${RED}  ✗ 未找到 JS 文件${NC}"
    fi
    if [ "$CSS_COUNT" -eq 0 ]; then
        echo -e "${RED}  ✗ 未找到 CSS 文件${NC}"
    fi
fi

if [ -d "${ADMIN_DIR}" ]; then
    JS_COUNT=$(find ${ADMIN_DIR} -name "*.js" | wc -l)
    CSS_COUNT=$(find ${ADMIN_DIR} -name "*.css" | wc -l)
    echo "  管理后台 JS 文件: ${JS_COUNT}"
    echo "  管理后台 CSS 文件: ${CSS_COUNT}"
    if [ "$JS_COUNT" -eq 0 ]; then
        echo -e "${RED}  ✗ 未找到 JS 文件${NC}"
    fi
    if [ "$CSS_COUNT" -eq 0 ]; then
        echo -e "${RED}  ✗ 未找到 CSS 文件${NC}"
    fi
fi

echo ""

# 4. 检查 Nginx 配置
echo -e "${YELLOW}[4] 检查 Nginx 配置...${NC}"
if [ -f "/etc/nginx/conf.d/bjzxjj.conf" ]; then
    echo -e "${GREEN}✓ Nginx 配置文件存在${NC}"
    
    # 检查配置中的路径
    CONFIG_FRONTEND=$(grep -A 2 "location / {" /etc/nginx/conf.d/bjzxjj.conf | grep "root" | awk '{print $2}' | tr -d ';' || echo "")
    CONFIG_ADMIN=$(grep -A 2 "location /admin" /etc/nginx/conf.d/bjzxjj.conf | grep "alias" | awk '{print $2}' | tr -d ';' || echo "")
    
    echo "  配置中的前端路径: ${CONFIG_FRONTEND:-未找到}"
    echo "  配置中的管理后台路径: ${CONFIG_ADMIN:-未找到}"
    
    if [ -n "$CONFIG_FRONTEND" ] && [ "$CONFIG_FRONTEND" != "${FRONTEND_DIR}" ]; then
        echo -e "${YELLOW}  ⚠ 前端路径不匹配${NC}"
    fi
    if [ -n "$CONFIG_ADMIN" ] && [ "$CONFIG_ADMIN" != "${ADMIN_DIR}/" ]; then
        echo -e "${YELLOW}  ⚠ 管理后台路径不匹配${NC}"
    fi
    
    # 测试 Nginx 配置
    if nginx -t 2>&1 | grep -q "successful"; then
        echo -e "${GREEN}  ✓ Nginx 配置语法正确${NC}"
    else
        echo -e "${RED}  ✗ Nginx 配置语法错误${NC}"
        nginx -t
    fi
else
    echo -e "${RED}✗ Nginx 配置文件不存在${NC}"
fi

echo ""

# 5. 检查 index.html 中的资源路径
echo -e "${YELLOW}[5] 检查 index.html 中的资源路径...${NC}"
if [ -f "${FRONTEND_DIR}/index.html" ]; then
    echo "  前端 index.html 中的资源引用:"
    grep -oE 'src="[^"]*"|href="[^"]*"' ${FRONTEND_DIR}/index.html | head -5 | sed 's/^/    /'
fi

if [ -f "${ADMIN_DIR}/index.html" ]; then
    echo "  管理后台 index.html 中的资源引用:"
    grep -oE 'src="[^"]*"|href="[^"]*"' ${ADMIN_DIR}/index.html | head -5 | sed 's/^/    /'
    
    # 检查是否有 /admin/ 前缀
    if grep -q 'href="/admin/' ${ADMIN_DIR}/index.html || grep -q 'src="/admin/' ${ADMIN_DIR}/index.html; then
        echo -e "${GREEN}  ✓ 资源路径包含 /admin/ 前缀${NC}"
    else
        echo -e "${YELLOW}  ⚠ 资源路径可能缺少 /admin/ 前缀${NC}"
        echo "    提示: 需要重新构建管理后台（已设置 base: '/admin/'）"
    fi
fi

echo ""

# 6. 检查 Nginx 服务状态
echo -e "${YELLOW}[6] 检查 Nginx 服务状态...${NC}"
if systemctl is-active --quiet nginx; then
    echo -e "${GREEN}✓ Nginx 服务正在运行${NC}"
else
    echo -e "${RED}✗ Nginx 服务未运行${NC}"
fi

echo ""

# 7. 检查SELinux（可能导致403）
echo -e "${YELLOW}[7] 检查SELinux状态...${NC}"
if command -v getenforce &>/dev/null; then
    SELINUX_STATUS=$(getenforce 2>/dev/null || echo "Disabled")
    echo "  SELinux状态: ${SELINUX_STATUS}"
    if [ "$SELINUX_STATUS" = "Enforcing" ]; then
        echo -e "${YELLOW}  ⚠ SELinux处于强制模式，可能阻止访问${NC}"
        echo "    如果出现403错误，可以尝试："
        echo "      chcon -R -t httpd_sys_content_t ${FRONTEND_DIR}"
        echo "      chcon -R -t httpd_sys_content_t ${ADMIN_DIR}"
    fi
else
    echo -e "${GREEN}  ✓ SELinux未安装或已禁用${NC}"
fi
echo ""

# 8. 建议
echo -e "${YELLOW}[8] 修复建议...${NC}"
echo "  如果发现问题，请执行以下步骤："
echo ""
echo "  1. 快速修复403权限问题："
echo "     cd $(dirname ${PROJECT_SOURCE_DIR})/deploy"
echo "     ./fix-403.sh"
echo ""
echo "  2. 重新构建并部署："
echo "     ./deploy.sh"
echo ""
echo "  3. 检查文件权限："
echo "     ls -la ${FRONTEND_DIR}"
echo "     ls -la ${ADMIN_DIR}"
echo ""
echo "  4. 查看 Nginx 错误日志："
echo "     tail -50 /var/log/nginx/bjzxjj-error.log"
echo ""
echo "  5. 测试静态资源访问："
echo "     curl -I http://localhost/"
echo "     curl -I http://localhost/admin/"
echo ""

