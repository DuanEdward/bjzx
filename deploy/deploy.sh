#!/bin/bash

# 北京中兴建机职业技能鉴定中心网站 - 部署脚本
# 用于构建和部署项目到服务器

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}请使用root用户运行此脚本${NC}"
    exit 1
fi

# 检查配置文件
if [ -z "$DB_PASSWORD" ] || [ -z "$MYSQL_ROOT_PASSWORD" ]; then
    echo -e "${RED}错误: 请先在 config.sh 中配置 MySQL root密码和数据库密码${NC}"
    exit 1
fi

echo -e "${GREEN}=========================================="
echo "开始部署项目..."
echo "项目源码目录: ${PROJECT_SOURCE_DIR}"
echo "部署目录: ${PROJECT_HOME}"
echo "==========================================${NC}"

# 检查项目源码目录
if [ ! -d "${PROJECT_SOURCE_DIR}/backend" ] || [ ! -d "${PROJECT_SOURCE_DIR}/frontend" ] || [ ! -d "${PROJECT_SOURCE_DIR}/admin" ]; then
    echo -e "${RED}错误: 项目目录结构不正确，请在项目根目录运行此脚本${NC}"
    exit 1
fi

# 1. 构建后端
echo -e "${YELLOW}[1/5] 构建后端项目...${NC}"
cd ${PROJECT_SOURCE_DIR}/backend

# 清理旧的构建
if [ -d "target" ]; then
    rm -rf target
fi

# 使用Maven构建
mvn clean package -DskipTests

if [ ! -f "target/bjzxjj-website-1.0.0.jar" ]; then
    echo -e "${RED}后端构建失败！${NC}"
    exit 1
fi

# 复制JAR文件到部署目录
cp target/bjzxjj-website-1.0.0.jar ${BACKEND_DIR}/bjzxjj-website.jar
echo -e "${GREEN}后端构建完成${NC}"

# 2. 构建前端
echo -e "${YELLOW}[2/5] 构建前端项目...${NC}"
cd ${PROJECT_SOURCE_DIR}/frontend

# 检查Node.js和npm是否安装
if ! command -v npm &> /dev/null; then
    echo -e "${RED}错误: npm未安装，请先安装Node.js${NC}"
    echo "执行以下命令安装Node.js 18.x:"
    echo "  curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -"
    echo "  yum install -y nodejs"
    echo "或者运行环境准备脚本: ./prepare.sh"
    exit 1
fi

echo "Node.js版本: $(node -v)"
echo "npm版本: $(npm -v)"

# 检查并更新 vue-tsc 版本（修复兼容性问题）
if [ -f "package.json" ]; then
    echo "检查 vue-tsc 版本..."
    VUE_TSC_VERSION=$(grep -o '"vue-tsc": "[^"]*"' package.json | cut -d'"' -f4)
    if [[ "$VUE_TSC_VERSION" == "^1.8.25" ]] || [[ "$VUE_TSC_VERSION" == "1.8.25" ]]; then
        echo "更新 vue-tsc 到 2.0.0 以修复兼容性问题..."
        sed -i 's/"vue-tsc": "\^1.8.25"/"vue-tsc": "^2.0.0"/' package.json
        sed -i 's/"vue-tsc": "1.8.25"/"vue-tsc": "^2.0.0"/' package.json
    fi
fi

# 安装或更新依赖
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
else
    echo "更新前端依赖（特别是 vue-tsc）..."
    npm install vue-tsc@^2.0.0 --save-dev
fi

# 构建前端
npm run build

if [ ! -d "dist" ]; then
    echo -e "${RED}前端构建失败！${NC}"
    exit 1
fi

# 复制构建文件到部署目录
rm -rf ${FRONTEND_DIR}/*
cp -r dist/* ${FRONTEND_DIR}/
echo -e "${GREEN}前端构建完成${NC}"

# 3. 构建管理后台
echo -e "${YELLOW}[3/5] 构建管理后台...${NC}"
cd ${PROJECT_SOURCE_DIR}/admin

# 检查并更新 vue-tsc 版本（修复兼容性问题）
if [ -f "package.json" ]; then
    echo "检查 vue-tsc 版本..."
    VUE_TSC_VERSION=$(grep -o '"vue-tsc": "[^"]*"' package.json | cut -d'"' -f4)
    if [[ "$VUE_TSC_VERSION" == "^1.8.25" ]] || [[ "$VUE_TSC_VERSION" == "1.8.25" ]]; then
        echo "更新 vue-tsc 到 2.0.0 以修复兼容性问题..."
        sed -i 's/"vue-tsc": "\^1.8.25"/"vue-tsc": "^2.0.0"/' package.json
        sed -i 's/"vue-tsc": "1.8.25"/"vue-tsc": "^2.0.0"/' package.json
    fi
fi

# 安装或更新依赖
if [ ! -d "node_modules" ]; then
    echo "安装管理后台依赖..."
    npm install
else
    echo "更新管理后台依赖（特别是 vue-tsc）..."
    npm install vue-tsc@^2.0.0 --save-dev
fi

# 构建管理后台
npm run build

if [ ! -d "dist" ]; then
    echo -e "${RED}管理后台构建失败！${NC}"
    exit 1
fi

# 复制构建文件到部署目录
rm -rf ${ADMIN_DIR}/*
cp -r dist/* ${ADMIN_DIR}/
echo -e "${GREEN}管理后台构建完成${NC}"

# 4. 生成并复制配置文件
echo -e "${YELLOW}[4/5] 生成配置文件...${NC}"

# 生成生产环境配置文件
cat > ${BACKEND_DIR}/application-prod.yml <<EOF
server:
  port: ${BACKEND_PORT}
  servlet:
    context-path: /api
    encoding:
      charset: UTF-8
      enabled: true

spring:
  application:
    name: bjzxjj-website

  # 数据源配置（生产环境）
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/${DB_NAME}?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  # Redis配置（生产环境）- 暂未使用，已注释
  # data:
  #   redis:
  #     host: localhost
  #     port: 6379
  #     password: ${REDIS_PASSWORD}
  #     database: 0
  #     timeout: 3000
  #     lettuce:
  #       pool:
  #         max-active: 20
  #         max-idle: 10
  #         min-idle: 0

  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

  # Jackson配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

# MyBatis Plus配置
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.hebei.cs.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24小时

# 文件存储配置
file:
  upload-path: ${UPLOAD_DIR}
  access-path: /uploads

# 日志配置
logging:
  level:
    root: info
    com.hebei.cs: info
    org.springframework.security: warn
  file:
    name: ${LOG_DIR}/bjzxjj-website.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
      total-size-cap: 1GB
EOF

echo "已生成生产环境配置文件"

# 生成Nginx配置
cat > /etc/nginx/conf.d/bjzxjj.conf <<EOF
# 北京中兴建机职业技能鉴定中心网站 - Nginx配置

# 前端配置
server {
    listen 80;
    server_name ${DOMAIN} ${DOMAIN_WWW};
    
    # 前端站点
    location / {
        root ${FRONTEND_DIR};
        index index.html;
        try_files \$uri \$uri/ /index.html;
        
        # 缓存静态资源
        location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # 管理后台
    location /admin {
        alias ${ADMIN_DIR};
        index index.html;
        try_files \$uri \$uri/ /admin/index.html;
        
        # 缓存静态资源
        location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # 文件上传访问路径
    location /uploads {
        alias ${UPLOAD_DIR};
        expires 7d;
        add_header Cache-Control "public";
    }
    
    # 后端API代理
    location /api {
        proxy_pass http://localhost:${BACKEND_PORT}/api;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # 文件上传大小限制
        client_max_body_size 10M;
    }
    
    # 健康检查
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
    
    # 日志配置
    access_log /var/log/nginx/bjzxjj-access.log;
    error_log /var/log/nginx/bjzxjj-error.log;
}
EOF

echo "已生成Nginx配置文件"

# 测试Nginx配置
nginx -t
if [ $? -eq 0 ]; then
    echo "Nginx配置验证通过"
else
    echo -e "${RED}Nginx配置验证失败，请检查配置${NC}"
    exit 1
fi

# 5. 重启服务
echo -e "${YELLOW}[5/5] 重启服务...${NC}"

# 重启后端服务
if systemctl is-active --quiet bjzxjj-backend 2>/dev/null; then
    systemctl restart bjzxjj-backend
    echo -e "${GREEN}后端服务已重启${NC}"
else
    echo -e "${YELLOW}后端服务未运行，请稍后手动启动${NC}"
fi

# 重启Nginx
systemctl restart nginx
echo -e "${GREEN}Nginx已重启${NC}"

# 设置目录权限
chown -R nginx:nginx ${PROJECT_HOME} 2>/dev/null || chown -R root:root ${PROJECT_HOME}
chmod -R 755 ${PROJECT_HOME}
chmod -R 777 ${UPLOAD_DIR}
chmod -R 777 ${LOG_DIR}

echo -e "${GREEN}=========================================="
echo "部署完成！"
echo "==========================================${NC}"
echo ""
echo "部署信息："
echo "  前端地址: http://${DOMAIN}/"
echo "  管理后台: http://${DOMAIN}/admin/"
echo "  后端API: http://${DOMAIN}/api/"
echo ""
echo "服务管理："
echo "  查看后端日志: journalctl -u bjzxjj-backend -f"
echo "  重启后端服务: systemctl restart bjzxjj-backend"
echo "  重启Nginx: systemctl restart nginx"
echo ""
