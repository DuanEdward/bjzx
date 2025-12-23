#!/bin/bash

# MySQL GPG密钥修复脚本

set -e

echo "=========================================="
echo "修复MySQL GPG密钥问题..."
echo "=========================================="

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "请使用root用户运行此脚本"
    exit 1
fi

# 方法1: 导入正确的GPG密钥
echo "导入MySQL GPG密钥..."
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022

# 如果上面的密钥不行，尝试其他密钥
if [ $? -ne 0 ]; then
    echo "尝试导入备用GPG密钥..."
    rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql
fi

# 方法2: 如果MySQL仓库已安装，更新GPG密钥配置
if [ -f /etc/yum.repos.d/mysql-community.repo ]; then
    echo "更新MySQL仓库GPG密钥配置..."
    sed -i 's/gpgcheck=1/gpgcheck=0/' /etc/yum.repos.d/mysql-community.repo
    echo "已临时禁用GPG检查"
fi

# 方法3: 使用--nogpgcheck安装（如果上述方法都不行）
echo ""
echo "如果仍然遇到GPG错误，可以使用以下命令安装："
echo "yum install -y mysql-community-server --nogpgcheck"
echo ""

# 清理yum缓存
echo "清理yum缓存..."
yum clean all

echo "=========================================="
echo "修复完成！"
echo "=========================================="
echo ""
echo "现在可以尝试重新安装MySQL："
echo "yum install -y mysql-community-server --nogpgcheck"
echo ""

