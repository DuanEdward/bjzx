#!/bin/bash

# 北京中兴建机职业技能鉴定中心网站 - 部署准备脚本
# 用于在阿里云服务器上安装必要的软件和配置环境

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

echo "=========================================="
echo "开始准备部署环境..."
echo "项目部署目录: ${PROJECT_HOME}"
echo "=========================================="

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "请使用root用户运行此脚本"
    exit 1
fi

# 检查配置文件
if [ -z "$DB_PASSWORD" ] || [ -z "$MYSQL_ROOT_PASSWORD" ]; then
    echo "错误: 请先在 config.sh 中配置 MySQL root密码和数据库密码"
    exit 1
fi

# 更新系统 - 首次部署建议执行，后续可跳过
# echo "更新系统包..."
# yum update -y

# 安装基础工具 - 如果已安装可跳过
echo "检查基础工具..."
if ! command -v wget &> /dev/null || ! command -v curl &> /dev/null || ! command -v git &> /dev/null; then
    echo "安装基础工具..."
    yum install -y wget curl git vim net-tools
else
    echo "基础工具已安装，跳过"
fi

# 安装JDK 8
echo "安装JDK 8..."
if ! command -v java &> /dev/null; then
    yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel
    echo "JDK 8 安装完成"
else
    echo "JDK 已安装: $(java -version 2>&1 | head -n 1)"
fi

# 安装MySQL 8.0 - 如果已安装MySQL可跳过此步骤
echo "检查MySQL安装状态..."
if ! command -v mysql &> /dev/null; then
    echo "MySQL未安装，开始安装MySQL 8.0..."
    # 检测系统版本
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS_VERSION=$VERSION_ID
    else
        OS_VERSION=$(cat /etc/redhat-release | grep -oE '[0-9]+' | head -1)
    fi
    
    # 根据系统版本选择对应的MySQL仓库
    if [[ "$OS_VERSION" == "7" ]] || [[ "$OS_VERSION" =~ ^7\. ]]; then
        MYSQL_REPO="mysql80-community-release-el7-3.noarch.rpm"
    elif [[ "$OS_VERSION" == "8" ]] || [[ "$OS_VERSION" =~ ^8\. ]]; then
        MYSQL_REPO="mysql80-community-release-el8-3.noarch.rpm"
    else
        MYSQL_REPO="mysql80-community-release-el7-3.noarch.rpm"
    fi
    
    # 下载MySQL Yum仓库
    echo "下载MySQL仓库文件..."
    wget https://dev.mysql.com/get/${MYSQL_REPO} -O /tmp/${MYSQL_REPO}
    
    # 导入GPG密钥
    echo "导入MySQL GPG密钥..."
    rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022 || true
    
    # 安装MySQL仓库
    rpm -ivh /tmp/${MYSQL_REPO} || rpm -Uvh /tmp/${MYSQL_REPO}
    
    # 安装MySQL
    echo "安装MySQL服务器..."
    if ! yum install -y mysql-community-server 2>&1 | grep -q "GPG check FAILED"; then
        echo "MySQL安装成功"
    else
        echo "GPG检查失败，使用--nogpgcheck选项安装..."
        yum install -y mysql-community-server --nogpgcheck
    fi
    
    # 启动MySQL服务
    systemctl start mysqld
    systemctl enable mysqld
    
    # 等待MySQL启动
    sleep 5
    
    # 获取临时密码并修改root密码
    if [ -f /var/log/mysqld.log ]; then
        TEMP_PASSWORD=$(grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}' | tail -1)
        if [ -n "$TEMP_PASSWORD" ]; then
            echo "修改MySQL root密码..."
            mysql -uroot -p"${TEMP_PASSWORD}" --connect-expired-password <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
FLUSH PRIVILEGES;
EOF
            echo "MySQL root密码已设置"
        fi
    fi
    
    # 清理临时文件
    rm -f /tmp/${MYSQL_REPO}
else
    echo "MySQL 已安装: $(mysql --version)，跳过安装步骤"
fi

# 安装Node.js 18.x (用于构建前端) - 如果已安装可跳过
echo "检查Node.js安装状态..."
if ! command -v node &> /dev/null; then
    echo "Node.js未安装，开始安装Node.js 18.x..."
    curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -
    yum install -y nodejs
    echo "Node.js 安装完成: $(node -v)"
else
    echo "Node.js 已安装: $(node -v)，跳过安装步骤"
fi

# 安装Nginx
echo "安装Nginx..."
if ! command -v nginx &> /dev/null; then
    yum install -y nginx
    
    # 启动Nginx服务
    systemctl start nginx
    systemctl enable nginx
    
    echo "Nginx 安装完成"
else
    echo "Nginx 已安装"
fi

# 安装Maven (用于构建后端) - 如果已安装可跳过
echo "检查Maven安装状态..."
if ! command -v mvn &> /dev/null; then
    echo "Maven未安装，开始安装..."
    cd /opt
    wget https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz
    tar -xzf apache-maven-3.8.8-bin.tar.gz
    mv apache-maven-3.8.8 maven
    
    # 配置Maven环境变量
    if ! grep -q "MAVEN_HOME" /etc/profile; then
        cat >> /etc/profile << 'EOF'
export MAVEN_HOME=/opt/maven
export PATH=$MAVEN_HOME/bin:$PATH
EOF
    fi
    
    source /etc/profile
    echo "Maven 安装完成: $(mvn -v | head -n 1)"
    rm -f apache-maven-3.8.8-bin.tar.gz
else
    echo "Maven 已安装: $(mvn -v | head -n 1)，跳过安装步骤"
fi

# 创建项目目录
echo "创建项目目录..."
mkdir -p ${PROJECT_HOME}
mkdir -p ${UPLOAD_DIR}
mkdir -p ${LOG_DIR}
mkdir -p ${FRONTEND_DIR}
mkdir -p ${ADMIN_DIR}
mkdir -p ${BACKEND_DIR}

# 设置目录权限
chown -R nginx:nginx ${PROJECT_HOME} 2>/dev/null || chown -R root:root ${PROJECT_HOME}
chmod -R 755 ${PROJECT_HOME}

# 配置防火墙 - 根据实际情况决定是否启用
# echo "配置防火墙..."
# if command -v firewall-cmd &> /dev/null; then
#     firewall-cmd --permanent --add-service=http
#     firewall-cmd --permanent --add-service=https
#     firewall-cmd --permanent --add-port=${BACKEND_PORT}/tcp
#     firewall-cmd --reload
#     echo "防火墙配置完成"
# fi

# 配置SELinux (如果需要) - 根据实际情况决定是否启用
# if [ -f /etc/selinux/config ]; then
#     echo "检查SELinux配置..."
#     SELINUX_STATUS=$(getenforce)
#     if [ "$SELINUX_STATUS" = "Enforcing" ]; then
#         echo "SELinux处于强制模式，建议设置为Permissive模式"
#         echo "可以运行: setenforce 0 (临时) 或修改 /etc/selinux/config (永久)"
#     fi
# fi

echo "=========================================="
echo "环境准备完成！"
echo "=========================================="
echo ""
echo "下一步操作："
echo "1. 运行数据库初始化脚本: ./init-database.sh"
echo "2. 运行部署脚本: ./deploy.sh"
echo ""
