# 部署指南

本文档介绍河北省计算机学会网站的部署方案。

## 部署架构

```
                              ┌─────────────┐
                              │  用户访问    │
                              └──────┬──────┘
                                     │
                              ┌──────▼──────┐
                              │    Nginx    │  (反向代理)
                              └──────┬──────┘
                                     │
                ┌────────────────────┼────────────────────┐
                │                    │                    │
        ┌───────▼────────┐   ┌───────▼────────┐   ┌──────▼──────┐
        │   前台网站     │   │  后台管理系统   │   │   后端API    │
        │   (Vue3)       │   │   (Vue3)       │   │ (Node.js)   │
        │   Port: 3000   │   │   Port: 3001   │   │  Port: 8080 │
        └────────────────┘   └────────────────┘   └─────────────┘
                                           │
                                    ┌──────▼──────┐
                                    │  MySQL      │
                                    │  数据库      │
                                    └─────────────┘
```

## 服务器要求

### 最低配置

- CPU: 2核
- 内存: 4GB
- 硬盘: 50GB SSD
- 带宽: 5Mbps

### 推荐配置

- CPU: 4核
- 内存: 8GB
- 硬盘: 100GB SSD
- 带宽: 10Mbps

### 软件环境

- CentOS 7.6+ / Ubuntu 18.04+
- Node.js 18+
- MySQL 8.0+
- Nginx 1.18+
- Redis 6.0+ (可选)

## 部署步骤

### 1. 服务器初始化

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y  # Ubuntu
# 或
sudo yum update -y  # CentOS

# 安装必要软件
sudo apt install -y git curl wget

# 创建部署用户
sudo useradd -m -s /bin/bash deploy
sudo usermod -aG sudo deploy
su - deploy
```

### 2. 安装 Node.js

```bash
# 使用 nvm 安装 Node.js
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18
```

### 3. 安装 MySQL

```bash
# Ubuntu
sudo apt install -y mysql-server mysql-client
sudo mysql_secure_installation

# CentOS
sudo yum install -y mysql-server
sudo systemctl start mysqld
sudo mysql_secure_installation
```

### 4. 安装 Nginx

```bash
# Ubuntu
sudo apt install -y nginx

# CentOS
sudo yum install -y epel-release
sudo yum install -y nginx
sudo systemctl start nginx
```

### 5. 部署应用

```bash
# 创建项目目录
sudo mkdir -p /var/www/hebei-cs
sudo chown deploy:deploy /var/www/hebei-cs
cd /var/www/hebei-cs

# 克隆项目代码
git clone <项目地址> .

# 部署前台
cd frontend
npm install
npm run build
sudo cp -r dist/* /var/www/hebei-cs/frontend-dist/

# 部署后台
cd ../admin
npm install
npm run build
sudo cp -r dist/* /var/www/hebei-cs/admin-dist/
```

### 6. 配置 Nginx

创建前台配置 `/etc/nginx/sites-available/hebei-cs-frontend`：

```nginx
server {
    listen 80;
    server_name www.hsjs.org.cn hsjs.org.cn;
    root /var/www/hebei-cs/frontend-dist;
    index index.html;

    # HTTPS重定向
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name www.hsjs.org.cn hsjs.org.cn;
    root /var/www/hebei-cs/frontend-dist;
    index index.html;

    # SSL证书配置
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript image/svg+xml;

    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache";
    }

    # API代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时设置
        proxy_connect_timeout 10s;
        proxy_send_timeout 10s;
        proxy_read_timeout 10s;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        add_header Vary Accept-Encoding;
    }

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
}
```

创建后台配置 `/etc/nginx/sites-available/hebei-cs-admin`：

```nginx
server {
    listen 80;
    server_name admin.hsjs.org.cn;
    root /var/www/hebei-cs/admin-dist;
    index index.html;

    # HTTPS重定向
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name admin.hsjs.org.cn;
    root /var/www/hebei-cs/admin-dist;
    index index.html;

    # SSL证书配置
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;

    # 限制访问IP（可选）
    # allow 192.168.1.0/24;
    # deny all;

    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache";
    }

    # API代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 安全头
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
}
```

启用站点：

```bash
sudo ln -s /etc/nginx/sites-available/hebei-cs-frontend /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/hebei-cs-admin /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 7. 配置数据库

```bash
# 创建数据库
sudo mysql -u root -p
CREATE DATABASE hebei_cs CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'hebei_cs'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON hebei_cs.* TO 'hebei_cs'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# 导入数据库结构
mysql -u hebei_cs -p hebei_cs < database/schema.sql
mysql -u hebei_cs -p hebei_cs < database/init.sql
```

### 8. 配置 PM2（Node.js 进程管理）

```bash
# 全局安装 PM2
npm install -g pm2

# 创建 PM2 配置文件
cat > ecosystem.config.js << EOF
module.exports = {
  apps: [{
    name: 'hebei-cs-api',
    script: './backend/server.js',
    instances: 'max',
    exec_mode: 'cluster',
    env: {
      NODE_ENV: 'production',
      PORT: 8080,
      DB_HOST: 'localhost',
      DB_PORT: 3306,
      DB_NAME: 'hebei_cs',
      DB_USER: 'hebei_cs',
      DB_PASS: 'your_password'
    },
    error_file: './logs/err.log',
    out_file: './logs/out.log',
    log_file: './logs/combined.log',
    time: true
  }]
}
EOF

# 启动应用
pm2 start ecosystem.config.js
pm2 save
pm2 startup
```

### 9. 配置 SSL 证书

使用 Let's Encrypt 免费证书：

```bash
# 安装 Certbot
sudo apt install -y certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d www.hsjs.org.cn -d hsjs.org.cn -d admin.hsjs.org.cn

# 自动续期
sudo crontab -e
# 添加：0 12 * * * /usr/bin/certbot renew --quiet
```

### 10. 配置日志轮转

创建 `/etc/logrotate.d/hebei-cs`：

```
/var/www/hebei-cs/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 deploy deploy
    postrotate
        pm2 reload all
    endscript
}
```

## 监控和维护

### 1. 系统监控

使用 htop、iotop 等工具监控系统资源：

```bash
sudo apt install -y htop iotop
```

### 2. 应用监控

配置 PM2 监控：

```bash
pm2 monit
```

### 3. 日志查看

```bash
# Nginx日志
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# PM2日志
pm2 logs

# 系统日志
sudo journalctl -f -u nginx
```

### 4. 数据库备份

创建备份脚本 `/home/deploy/backup.sh`：

```bash
#!/bin/bash

BACKUP_DIR="/var/backups/hebei-cs"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="hebei_cs"
DB_USER="hebei_cs"
DB_PASS="your_password"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u$DB_USER -p$DB_PASS $DB_NAME | gzip > $BACKUP_DIR/db_$DATE.sql.gz

# 备份文件
tar -czf $BACKUP_DIR/files_$DATE.tar.gz /var/www/hebei-cs/uploads

# 删除30天前的备份
find $BACKUP_DIR -name "*.gz" -mtime +30 -delete
```

设置定时备份：

```bash
chmod +x /home/deploy/backup.sh
crontab -e
# 添加：0 2 * * * /home/deploy/backup.sh
```

## 性能优化

### 1. 开启 Gzip 压缩

已在 Nginx 配置中启用。

### 2. 使用 CDN

对于静态资源（JS、CSS、图片），建议使用 CDN 加速。

### 3. 数据库优化

- 添加适当的索引
- 优化查询语句
- 使用 Redis 缓存热点数据

### 4. 启用 HTTP/2

已在 Nginx 配置中启用。

## 安全建议

1. **定期更新系统**
   ```bash
   sudo apt update && sudo apt upgrade
   ```

2. **配置防火墙**
   ```bash
   sudo ufw enable
   sudo ufw allow ssh
   sudo ufw allow 'Nginx Full'
   ```

3. **禁用root登录**
   ```bash
   sudo vim /etc/ssh/sshd_config
   # PermitRootLogin no
   sudo systemctl restart ssh
   ```

4. **定期备份数据**

5. **监控异常访问**

## 故障排查

### 1. 502 Bad Gateway

检查后端服务是否正常运行：
```bash
pm2 status
pm2 logs
```

### 2. 404 Not Found

检查 Nginx 配置和文件路径：
```bash
sudo nginx -t
sudo systemctl status nginx
```

### 3. 数据库连接失败

检查数据库服务和连接配置：
```bash
sudo systemctl status mysql
mysql -u hebei_cs -p -e "SHOW DATABASES;"
```

## 回滚方案

### 1. 代码回滚

```bash
cd /var/www/hebei-cs
git checkout <commit_hash>

# 重新构建
cd frontend && npm run build
sudo cp -r dist/* /var/www/hebei-cs/frontend-dist/
```

### 2. 数据库回滚

```bash
mysql -u hebei_cs -p hebei_cs < /var/backups/hebei-cs/db_20240101_020000.sql.gz
```