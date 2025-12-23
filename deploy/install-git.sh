#!/bin/bash

# 安装Git脚本

set -e

echo "=========================================="
echo "安装Git..."
echo "=========================================="

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "请使用root用户运行此脚本"
    exit 1
fi

# 检查Git是否已安装
if command -v git &> /dev/null; then
    echo "Git 已安装: $(git --version)"
    exit 0
fi

# 检测系统类型
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
else
    OS=$(cat /etc/redhat-release | awk '{print $1}' | tr '[:upper:]' '[:lower:]')
fi

# 根据系统类型安装Git
case $OS in
    centos|rhel|almalinux|rocky)
        echo "检测到 CentOS/RHEL 系统，使用yum安装..."
        yum install -y git
        ;;
    ubuntu|debian)
        echo "检测到 Ubuntu/Debian 系统，使用apt安装..."
        apt-get update
        apt-get install -y git
        ;;
    *)
        echo "未知系统类型，尝试使用yum安装..."
        yum install -y git || apt-get update && apt-get install -y git
        ;;
esac

# 验证安装
if command -v git &> /dev/null; then
    echo "=========================================="
    echo "Git 安装成功！"
    echo "版本: $(git --version)"
    echo "=========================================="
else
    echo "Git 安装失败，请手动安装"
    exit 1
fi

