#!/bin/bash

# MySQL root密码重置脚本
# 使用方法: ./reset-mysql-password.sh

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

NEW_PASSWORD="Root@123456"

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
sleep 2

# 检查MySQL是否完全停止
if systemctl is-active --quiet mysqld 2>/dev/null; then
    echo "警告: MySQL服务仍在运行，强制停止..."
    systemctl kill mysqld 2>/dev/null || true
    sleep 2
    # 确保所有MySQL进程都停止
    pkill -9 mysqld 2>/dev/null || true
    sleep 2
fi

# 查找mysqld_safe或mysqld命令
MYSQLD_SAFE=$(which mysqld_safe 2>/dev/null || find /usr -name mysqld_safe 2>/dev/null | head -1)
MYSQLD=$(which mysqld 2>/dev/null || find /usr -name mysqld 2>/dev/null | head -1)

if [ -z "$MYSQLD_SAFE" ] && [ -z "$MYSQLD" ]; then
    echo "错误: 找不到mysqld_safe或mysqld命令"
    echo "请检查MySQL是否正确安装"
    systemctl start mysqld
    exit 1
fi

# 获取MySQL数据目录
MYSQL_DATA_DIR="/var/lib/mysql"
if [ ! -d "$MYSQL_DATA_DIR" ]; then
    # 尝试从配置文件中获取
    if [ -f /etc/my.cnf ]; then
        MYSQL_DATA_DIR=$(grep "^datadir" /etc/my.cnf | awk -F'=' '{print $2}' | tr -d ' ' | head -1)
    fi
    if [ -z "$MYSQL_DATA_DIR" ] || [ ! -d "$MYSQL_DATA_DIR" ]; then
        MYSQL_DATA_DIR="/var/lib/mysql"
    fi
fi

echo "MySQL数据目录: $MYSQL_DATA_DIR"

# 检查数据目录权限
if [ ! -w "$MYSQL_DATA_DIR" ]; then
    echo "警告: MySQL数据目录权限可能有问题"
    echo "尝试修复权限..."
    chown -R mysql:mysql "$MYSQL_DATA_DIR" 2>/dev/null || true
fi

echo "以跳过权限表模式启动MySQL..."
# 使用mysqld_safe或mysqld启动
if [ -n "$MYSQLD_SAFE" ]; then
    echo "使用 mysqld_safe 启动..."
    $MYSQLD_SAFE --skip-grant-tables --skip-networking --user=mysql --datadir="$MYSQL_DATA_DIR" > /tmp/mysqld_safe.log 2>&1 &
    MYSQL_PID=$!
elif [ -n "$MYSQLD" ]; then
    echo "使用 mysqld 启动..."
    $MYSQLD --skip-grant-tables --skip-networking --user=mysql --datadir="$MYSQL_DATA_DIR" > /tmp/mysqld.log 2>&1 &
    MYSQL_PID=$!
fi

echo "等待MySQL启动（PID: $MYSQL_PID）..."
# 等待更长时间，并检查进程
for i in {1..10}; do
    sleep 1
    if ps -p $MYSQL_PID > /dev/null 2>&1; then
        # 检查MySQL socket文件
        if [ -S "$MYSQL_DATA_DIR/mysql.sock" ] || [ -S "/tmp/mysql.sock" ]; then
            echo "MySQL已启动"
            break
        fi
    fi
    if [ $i -eq 10 ]; then
        echo "错误: MySQL启动超时"
        echo "检查日志:"
        [ -f /tmp/mysqld_safe.log ] && tail -20 /tmp/mysqld_safe.log
        [ -f /tmp/mysqld.log ] && tail -20 /tmp/mysqld.log
        # 清理进程
        kill $MYSQL_PID 2>/dev/null || true
        pkill -9 mysqld 2>/dev/null || true
        # 正常启动MySQL
        systemctl start mysqld
        exit 1
    fi
done

# 再次检查MySQL进程是否在运行
if ! ps -p $MYSQL_PID > /dev/null 2>&1; then
    echo "错误: MySQL进程已退出"
    echo "检查日志:"
    [ -f /tmp/mysqld_safe.log ] && tail -20 /tmp/mysqld_safe.log
    [ -f /tmp/mysqld.log ] && tail -20 /tmp/mysqld.log
    # 正常启动MySQL
    systemctl start mysqld
    exit 1
fi

# 等待MySQL完全就绪
sleep 3

echo "连接MySQL并重置密码..."
# 尝试不同的socket路径
MYSQL_SOCKET=""
for sock in "$MYSQL_DATA_DIR/mysql.sock" "/tmp/mysql.sock" "/var/run/mysqld/mysqld.sock"; do
    if [ -S "$sock" ]; then
        MYSQL_SOCKET="$sock"
        break
    fi
done

if [ -n "$MYSQL_SOCKET" ]; then
    mysql -S "$MYSQL_SOCKET" <<EOF
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY '${NEW_PASSWORD}';
FLUSH PRIVILEGES;
EOF
else
    # 尝试不使用socket，直接连接
    mysql <<EOF
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY '${NEW_PASSWORD}';
FLUSH PRIVILEGES;
EOF
fi

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

