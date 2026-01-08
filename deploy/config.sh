#!/bin/bash

# ============================================
# 部署配置文件
# 请根据实际情况修改以下配置
# ============================================

# 项目部署目录（项目将放在此目录下）
PROJECT_HOME="/home/bjzxjj"

# 域名配置
DOMAIN="bjzxjj.org.cn"
DOMAIN_WWW="www.bjzxjj.org.cn"

# MySQL配置
MYSQL_ROOT_PASSWORD="Root@123456"  # MySQL root密码（必填）
DB_NAME="bjzxjj"        # 数据库名
DB_USER="bjzxjj_user"   # 数据库用户名
DB_PASSWORD="Bjzx@123456"          # 数据库密码（必填）

# JWT密钥（生产环境建议使用复杂的随机字符串）
JWT_SECRET="bjzxjj-website-jwt-secret-key-2024-prod-$(date +%s)"

# Redis配置（如果使用Redis）- 暂未使用，已注释
# REDIS_PASSWORD=""       # Redis密码（可选，如果Redis没有设置密码则留空）

# 后端服务配置
BACKEND_PORT="8080"     # 后端服务端口
JVM_XMS="512m"          # JVM初始堆内存
JVM_XMX="1024m"         # JVM最大堆内存

# SSL证书配置（可选，如果配置了证书路径则启用HTTPS）
# 证书文件路径（支持 .crt/.pem 格式）
# 如果留空，部署脚本会自动在 ${PROJECT_HOME}/cert 目录下查找证书文件
# 支持的证书文件名：*.crt, *.pem, fullchain.pem, certificate.crt, domain.crt, server.crt 等
# 支持的私钥文件名：*.key, privkey.pem, private.key, domain.key, server.key 等
SSL_CERT_PATH=""  # 证书文件路径，留空则自动检测
SSL_KEY_PATH=""   # 私钥文件路径，留空则自动检测
# 如果使用 Let's Encrypt，可以手动指定路径：
# SSL_CERT_PATH="/etc/letsencrypt/live/${DOMAIN}/fullchain.pem"
# SSL_KEY_PATH="/etc/letsencrypt/live/${DOMAIN}/privkey.pem"
# 或者将证书复制到 ${PROJECT_HOME}/cert 目录，脚本会自动检测

# ============================================
# 以下为自动计算的路径，无需修改
# ============================================

# 项目源码目录（脚本运行时的当前目录）
PROJECT_SOURCE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# 部署目录
DEPLOY_DIR="${PROJECT_HOME}/deploy"
BACKEND_DIR="${PROJECT_HOME}/backend"
FRONTEND_DIR="${PROJECT_HOME}/frontend"
ADMIN_DIR="${PROJECT_HOME}/admin"
UPLOAD_DIR="${PROJECT_HOME}/uploads"
LOG_DIR="${PROJECT_HOME}/logs"

# 导出变量供其他脚本使用
export PROJECT_HOME
export DOMAIN
export DOMAIN_WWW
export MYSQL_ROOT_PASSWORD
export DB_NAME
export DB_USER
export DB_PASSWORD
export JWT_SECRET
# export REDIS_PASSWORD  # Redis暂未使用
export BACKEND_PORT
export JVM_XMS
export JVM_XMX
export SSL_CERT_PATH
export SSL_KEY_PATH
export PROJECT_SOURCE_DIR
export DEPLOY_DIR
export BACKEND_DIR
export FRONTEND_DIR
export ADMIN_DIR
export UPLOAD_DIR
export LOG_DIR

