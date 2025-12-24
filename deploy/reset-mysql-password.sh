#!/bin/bash

# MySQL root密码重置脚本
# 使用方法: ./reset-mysql-password.sh

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

NEW_PASSWORD="root123456"

echo "=========================================="
echo "重置MySQL root密码..."
echo "=========================================="

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "错误: 请使用root用户运行此脚本"
    exit 1
fi

# 检查MySQL服务状态
echo "检查MySQL服务状态..."
if ! systemctl is-active --quiet mysqld 2>/dev/null; then
    echo "错误: MySQL服务未运行，请先启动MySQL服务"
    echo "执行: systemctl start mysqld"
    exit 1
fi

echo "MySQL服务正在运行"

# 方法1: 尝试使用当前配置的密码连接并修改
echo "尝试方法1: 使用当前配置的密码修改..."
if mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '${NEW_PASSWORD}';" 2>/dev/null; then
    echo "密码重置成功！"
    echo "新密码: ${NEW_PASSWORD}"
    exit 0
fi

echo "方法1失败，尝试方法2: 使用跳过权限表方式..."

# 方法2: 使用跳过权限表的方式
echo "停止MySQL服务..."
systemctl stop mysqld

echo "以跳过权限表模式启动MySQL..."
mysqld_safe --skip-grant-tables --skip-networking > /dev/null 2>&1 &
MYSQL_PID=$!

echo "等待MySQL启动..."
sleep 5

# 检查MySQL进程是否在运行
if ! ps -p $MYSQL_PID > /dev/null 2>&1; then
    echo "错误: MySQL启动失败"
    exit 1
fi

echo "连接MySQL并重置密码..."
mysql <<EOF
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY '${NEW_PASSWORD}';
FLUSH PRIVILEGES;
EOF

if [ $? -eq 0 ]; then
    echo "密码重置成功！"
    
    # 停止跳过权限表的MySQL进程
    echo "停止临时MySQL进程..."
    kill $MYSQL_PID 2>/dev/null || true
    sleep 2
    
    # 正常启动MySQL服务
    echo "正常启动MySQL服务..."
    systemctl start mysqld
    
    # 等待服务启动
    sleep 3
    
    # 验证新密码
    echo "验证新密码..."
    if mysql -uroot -p"${NEW_PASSWORD}" -e "SELECT 1;" >/dev/null 2>&1; then
        echo "=========================================="
        echo "密码重置成功！"
        echo "新root密码: ${NEW_PASSWORD}"
        echo "=========================================="
        echo ""
        echo "请更新 config.sh 中的 MYSQL_ROOT_PASSWORD 为: ${NEW_PASSWORD}"
        exit 0
    else
        echo "警告: 密码可能未正确设置，请手动验证"
    fi
else
    echo "错误: 密码重置失败"
    # 停止临时进程
    kill $MYSQL_PID 2>/dev/null || true
    # 正常启动MySQL
    systemctl start mysqld
    exit 1
fi

