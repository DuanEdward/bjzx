#!/bin/bash

# MySQL启动失败诊断和修复脚本

set -e

echo "=========================================="
echo "MySQL启动失败诊断和修复工具"
echo "=========================================="
echo ""

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "错误: 请使用root用户运行此脚本"
    exit 1
fi

# 1. 查看MySQL服务状态
echo "[1/8] 查看MySQL服务状态..."
systemctl status mysqld --no-pager -l || true
echo ""

# 2. 查看MySQL错误日志
echo "[2/8] 查看MySQL错误日志（最近50行）..."
if [ -f /var/log/mysqld.log ]; then
    echo "--- /var/log/mysqld.log ---"
    tail -50 /var/log/mysqld.log
elif [ -f /var/log/mysql/error.log ]; then
    echo "--- /var/log/mysql/error.log ---"
    tail -50 /var/log/mysql/error.log
else
    echo "未找到MySQL错误日志文件"
    echo "尝试查找日志文件..."
    find /var/log -name "*mysql*" -o -name "*mysqld*" 2>/dev/null | head -5
fi
echo ""

# 3. 检查MySQL数据目录
echo "[3/8] 检查MySQL数据目录..."
MYSQL_DATA_DIR="/var/lib/mysql"
if [ -d "$MYSQL_DATA_DIR" ]; then
    echo "数据目录: $MYSQL_DATA_DIR"
    echo "目录权限:"
    ls -ld "$MYSQL_DATA_DIR"
    echo "目录内容:"
    ls -la "$MYSQL_DATA_DIR" | head -10
    echo ""
    
    # 检查权限
    if [ ! -w "$MYSQL_DATA_DIR" ]; then
        echo "警告: 数据目录权限可能有问题"
    fi
else
    echo "错误: MySQL数据目录不存在: $MYSQL_DATA_DIR"
fi
echo ""

# 4. 检查MySQL进程
echo "[4/8] 检查MySQL进程..."
ps aux | grep -E "mysql|mysqld" | grep -v grep || echo "没有运行的MySQL进程"
echo ""

# 5. 检查端口占用
echo "[5/8] 检查3306端口占用..."
if command -v netstat &> /dev/null; then
    netstat -tlnp | grep 3306 || echo "3306端口未被占用"
elif command -v ss &> /dev/null; then
    ss -tlnp | grep 3306 || echo "3306端口未被占用"
else
    echo "无法检查端口占用（netstat和ss命令都不可用）"
fi
echo ""

# 6. 检查MySQL配置文件
echo "[6/8] 检查MySQL配置文件..."
if [ -f /etc/my.cnf ]; then
    echo "主配置文件: /etc/my.cnf"
    cat /etc/my.cnf
elif [ -f /etc/mysql/my.cnf ]; then
    echo "主配置文件: /etc/mysql/my.cnf"
    cat /etc/mysql/my.cnf
else
    echo "未找到MySQL主配置文件"
    echo "查找配置文件..."
    find /etc -name "my.cnf" -o -name "*.cnf" | grep -i mysql | head -5
fi
echo ""

# 7. 验证MySQL配置
echo "[7/8] 验证MySQL配置..."
if command -v mysqld &> /dev/null; then
    MYSQLD=$(which mysqld)
    echo "使用 $MYSQLD 验证配置..."
    $MYSQLD --validate-config 2>&1 || echo "配置验证失败"
else
    echo "找不到mysqld命令，无法验证配置"
fi
echo ""

# 8. 提供修复建议
echo "[8/8] 修复建议..."
echo "=========================================="
echo "根据以上信息，尝试以下修复步骤："
echo ""
echo "1. 修复数据目录权限："
echo "   chown -R mysql:mysql /var/lib/mysql"
echo "   chmod 750 /var/lib/mysql"
echo ""
echo "2. 检查并修复SELinux（如果启用）："
echo "   getenforce"
echo "   # 如果返回Enforcing，临时设置为Permissive："
echo "   setenforce 0"
echo ""
echo "3. 清理可能的锁文件："
echo "   rm -f /var/lib/mysql/*.pid"
echo "   rm -f /var/run/mysqld/mysqld.pid"
echo ""
echo "4. 检查磁盘空间："
echo "   df -h /var/lib/mysql"
echo ""
echo "5. 尝试手动启动MySQL（查看详细错误）："
echo "   mysqld --user=mysql --datadir=/var/lib/mysql --console"
echo ""
echo "6. 如果数据目录损坏，可能需要初始化："
echo "   mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql"
echo "   # 注意：这会清空现有数据，请谨慎使用！"
echo ""
echo "7. 查看systemd日志："
echo "   journalctl -u mysqld -n 100 --no-pager"
echo ""
echo "=========================================="

