#!/bin/bash

# 数据库初始化脚本

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

echo "=========================================="
echo "初始化数据库..."
echo "=========================================="

# 检查配置文件
if [ -z "$DB_PASSWORD" ] || [ -z "$MYSQL_ROOT_PASSWORD" ]; then
    echo "错误: 请先在 config.sh 中配置 MySQL root密码和数据库密码"
    exit 1
fi

# 检查MySQL服务状态
echo "检查MySQL服务状态..."
if ! systemctl is-active --quiet mysqld 2>/dev/null; then
    echo "MySQL服务未运行，尝试启动..."
    systemctl start mysqld
    
    # 等待MySQL启动
    echo "等待MySQL服务启动..."
    sleep 5
    
    # 再次检查服务状态
    if ! systemctl is-active --quiet mysqld 2>/dev/null; then
        echo "错误: MySQL服务启动失败，请检查服务状态："
        echo "  systemctl status mysqld"
        echo "  journalctl -u mysqld -n 50"
        exit 1
    fi
    echo "MySQL服务已启动"
else
    echo "MySQL服务正在运行"
fi

# 测试MySQL连接
echo "测试MySQL连接..."
if ! mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1;" >/dev/null 2>&1; then
    echo "错误: 无法连接到MySQL服务器"
    echo "请检查："
    echo "  1. MySQL服务是否正常运行: systemctl status mysqld"
    echo "  2. root密码是否正确: ${MYSQL_ROOT_PASSWORD}"
    echo "  3. MySQL socket文件是否存在: ls -l /var/lib/mysql/mysql.sock"
    exit 1
fi
echo "MySQL连接成功"

# 创建数据库
echo "创建数据库..."
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<EOF
CREATE DATABASE IF NOT EXISTS ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOF

# 创建用户并授权
echo "创建数据库用户..."
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<EOF
DROP USER IF EXISTS '${DB_USER}'@'localhost';
CREATE USER '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;
EOF

# 导入数据库结构
echo "导入数据库结构..."
SCHEMA_FILE="${PROJECT_SOURCE_DIR}/docs/database/schema.sql"
if [ -f "$SCHEMA_FILE" ]; then
    mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" ${DB_NAME} < ${SCHEMA_FILE}
    echo "数据库结构导入完成"
else
    echo "警告: 未找到数据库结构文件 ${SCHEMA_FILE}"
fi

# 导入初始数据（可选）
read -p "是否导入初始数据？(y/n): " IMPORT_DATA
if [ "$IMPORT_DATA" = "y" ] || [ "$IMPORT_DATA" = "Y" ]; then
    INIT_FILE="${PROJECT_SOURCE_DIR}/docs/database/init.sql"
    if [ -f "$INIT_FILE" ]; then
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" ${DB_NAME} < ${INIT_FILE}
        echo "初始数据导入完成"
    else
        echo "警告: 未找到初始数据文件 ${INIT_FILE}"
    fi
fi

echo "=========================================="
echo "数据库初始化完成！"
echo "=========================================="
echo ""
echo "数据库信息："
echo "  数据库名: ${DB_NAME}"
echo "  用户名: ${DB_USER}"
echo "  密码: ${DB_PASSWORD}"
echo ""
