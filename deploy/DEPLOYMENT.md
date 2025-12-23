# 阿里云服务器部署指南

本文档介绍如何将北京中兴建机职业技能鉴定中心网站项目部署到阿里云服务器上。

## 一、服务器要求

### 最低配置
- CPU: 2核
- 内存: 4GB
- 硬盘: 40GB SSD
- 操作系统: CentOS 7.x / Alibaba Cloud Linux 2.x

### 推荐配置
- CPU: 4核
- 内存: 8GB
- 硬盘: 100GB SSD
- 操作系统: CentOS 7.x / Alibaba Cloud Linux 2.x

## 二、部署步骤

### 1. 连接服务器

使用SSH连接到你的阿里云服务器：

```bash
ssh root@your-server-ip
```

### 2. 上传项目文件

将项目文件上传到服务器，可以使用以下方法：

**方法一：使用Git（推荐）**

```bash
# 在服务器上克隆项目
cd /opt
git clone your-git-repository-url
cd hebei-cs-website
```

**方法二：使用SCP**

```bash
# 在本地执行
scp -r /path/to/hebei-cs-website root@your-server-ip:/opt/
```

**方法三：使用FTP工具**

使用FileZilla等FTP工具上传项目文件。

### 3. 运行环境准备脚本

```bash
cd /opt/hebei-cs-website/deploy
chmod +x prepare.sh
./prepare.sh
```

这个脚本会自动安装：
- JDK 8
- MySQL 8.0
- Redis
- Node.js 18.x
- Nginx
- Maven

### 4. 配置数据库

#### 4.1 初始化数据库

```bash
cd /opt/hebei-cs-website/deploy
chmod +x init-database.sh
./init-database.sh
```

按照提示输入MySQL root密码，脚本会自动：
- 创建数据库
- 创建数据库用户
- 导入数据库结构
- 可选导入初始数据

#### 4.2 手动配置数据库（可选）

如果需要手动配置：

```bash
# 登录MySQL
mysql -uroot -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS bjzxjj DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户
CREATE USER 'bjzxjj_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON bjzxjj.* TO 'bjzxjj_user'@'localhost';
FLUSH PRIVILEGES;

# 导入数据库结构
USE bjzxjj;
SOURCE /opt/hebei-cs-website/docs/database/schema.sql;

# 导入初始数据（可选）
SOURCE /opt/hebei-cs-website/docs/database/init.sql;
```

### 5. 配置生产环境

#### 5.1 修改数据库配置

编辑 `deploy/application-prod.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bjzxjj?...
    username: bjzxjj_user  # 你的数据库用户名
    password: your_password  # 你的数据库密码
```

#### 5.2 修改JWT密钥

编辑 `deploy/application-prod.yml`：

```yaml
jwt:
  secret: your-very-long-and-random-secret-key  # 修改为复杂的密钥
```

#### 5.3 配置Redis密码（可选）

如果Redis设置了密码，编辑 `deploy/application-prod.yml`：

```yaml
spring:
  data:
    redis:
      password: your_redis_password
```

### 6. 配置Nginx

#### 6.1 复制Nginx配置

```bash
cp /opt/hebei-cs-website/deploy/nginx.conf /etc/nginx/conf.d/bjzxjj.conf
```

#### 6.2 修改域名

编辑 `/etc/nginx/conf.d/bjzxjj.conf`，将 `your-domain.com` 替换为你的实际域名：

```nginx
server_name your-domain.com www.your-domain.com;
```

#### 6.3 测试Nginx配置

```bash
nginx -t
```

#### 6.4 重载Nginx

```bash
systemctl reload nginx
```

### 7. 配置系统服务

#### 7.1 安装后端服务

```bash
cp /opt/hebei-cs-website/deploy/bjzxjj-backend.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable bjzxjj-backend
```

#### 7.2 修改服务配置（如果需要）

编辑 `/etc/systemd/system/bjzxjj-backend.service`，根据需要调整JVM参数。

### 8. 运行部署脚本

```bash
cd /opt/hebei-cs-website/deploy
chmod +x deploy.sh
./deploy.sh
```

这个脚本会：
1. 构建后端项目（Maven）
2. 构建前端项目（npm）
3. 构建管理后台（npm）
4. 复制文件到部署目录
5. 重启服务

### 9. 启动服务

```bash
# 启动后端服务
systemctl start bjzxjj-backend

# 检查服务状态
systemctl status bjzxjj-backend

# 查看日志
journalctl -u bjzxjj-backend -f
```

## 三、服务管理

### 后端服务

```bash
# 启动
systemctl start bjzxjj-backend

# 停止
systemctl stop bjzxjj-backend

# 重启
systemctl restart bjzxjj-backend

# 查看状态
systemctl status bjzxjj-backend

# 查看日志
journalctl -u bjzxjj-backend -f
```

### Nginx服务

```bash
# 启动
systemctl start nginx

# 停止
systemctl stop nginx

# 重启
systemctl restart nginx

# 重载配置
systemctl reload nginx

# 查看状态
systemctl status nginx
```

### MySQL服务

```bash
# 启动
systemctl start mysqld

# 停止
systemctl stop mysqld

# 重启
systemctl restart mysqld

# 查看状态
systemctl status mysqld
```

### Redis服务

```bash
# 启动
systemctl start redis

# 停止
systemctl stop redis

# 重启
systemctl restart redis

# 查看状态
systemctl status redis
```

## 四、目录结构

部署后的目录结构：

```
/var/www/bjzxjj/
├── backend/              # 后端部署目录
│   ├── bjzxjj-website.jar
│   └── application-prod.yml
├── frontend/             # 前端部署目录
│   └── (构建后的静态文件)
├── admin/                # 管理后台部署目录
│   └── (构建后的静态文件)
├── uploads/              # 文件上传目录
└── logs/                 # 日志目录
    └── bjzxjj-website.log
```

## 五、防火墙配置

### 开放端口

```bash
# HTTP
firewall-cmd --permanent --add-service=http
firewall-cmd --permanent --add-service=https

# 后端API（如果需要外部访问）
firewall-cmd --permanent --add-port=8080/tcp

# 重载防火墙
firewall-cmd --reload
```

### 阿里云安全组配置

在阿里云控制台配置安全组规则：
- 开放 80 端口（HTTP）
- 开放 443 端口（HTTPS，如果使用SSL）
- 开放 8080 端口（后端API，如果需要外部访问）

## 六、SSL证书配置（可选）

### 使用Let's Encrypt免费证书

```bash
# 安装Certbot
yum install -y certbot python3-certbot-nginx

# 申请证书
certbot --nginx -d your-domain.com -d www.your-domain.com

# 自动续期
certbot renew --dry-run
```

### 使用阿里云SSL证书

1. 在阿里云控制台申请SSL证书
2. 下载证书文件
3. 修改Nginx配置，添加SSL配置（参考 `nginx.conf` 中的HTTPS配置示例）

## 七、常见问题

### 1. MySQL安装时GPG密钥验证失败

**问题现象：**
```
Error: GPG check FAILED
Public key for mysql-community-*.rpm is not installed
```

**解决方法：**

**方法一：使用修复脚本（推荐）**
```bash
cd /opt/hebei-cs-website/deploy
chmod +x fix-mysql-gpg.sh
./fix-mysql-gpg.sh
```

**方法二：手动修复**
```bash
# 导入MySQL GPG密钥
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022

# 如果还是失败，使用--nogpgcheck选项安装
yum install -y mysql-community-server --nogpgcheck
```

**方法三：临时禁用GPG检查**
```bash
# 编辑MySQL仓库配置
vi /etc/yum.repos.d/mysql-community.repo

# 将所有 gpgcheck=1 改为 gpgcheck=0
# 然后重新安装
yum install -y mysql-community-server
```

### 2. 后端服务无法启动

**检查日志：**
```bash
journalctl -u bjzxjj-backend -n 50
```

**常见原因：**
- 数据库连接失败：检查数据库配置和数据库服务状态
- 端口被占用：检查8080端口是否被占用
- JVM内存不足：调整JVM参数

### 3. 前端页面无法访问

**检查Nginx配置：**
```bash
nginx -t
```

**检查Nginx日志：**
```bash
tail -f /var/log/nginx/bjzxjj-error.log
```

### 4. 文件上传失败

**检查目录权限：**
```bash
chmod -R 777 /var/www/bjzxjj/uploads
chown -R nginx:nginx /var/www/bjzxjj/uploads
```

### 5. 数据库连接失败

**检查MySQL服务：**
```bash
systemctl status mysqld
```

**检查数据库用户权限：**
```bash
mysql -uroot -p
SHOW GRANTS FOR 'bjzxjj_user'@'localhost';
```

### 6. Redis连接失败

**检查Redis服务：**
```bash
systemctl status redis
redis-cli ping
```

## 八、性能优化

### 1. JVM参数优化

编辑 `/etc/systemd/system/bjzxjj-backend.service`：

```ini
ExecStart=/usr/bin/java -Xms1024m -Xmx2048m -XX:+UseG1GC ...
```

### 2. MySQL优化

编辑 `/etc/my.cnf`：

```ini
[mysqld]
innodb_buffer_pool_size = 1G
max_connections = 200
```

### 3. Nginx优化

编辑 `/etc/nginx/nginx.conf`：

```nginx
worker_processes auto;
worker_connections 1024;
```

## 九、备份策略

### 数据库备份

```bash
# 创建备份脚本
cat > /opt/backup-db.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u bjzxjj_user -p'your_password' bjzxjj > $BACKUP_DIR/bjzxjj_$DATE.sql
# 保留最近7天的备份
find $BACKUP_DIR -name "bjzxjj_*.sql" -mtime +7 -delete
EOF

chmod +x /opt/backup-db.sh

# 添加到定时任务（每天凌晨2点备份）
crontab -e
# 添加：0 2 * * * /opt/backup-db.sh
```

### 文件备份

```bash
# 备份上传文件
tar -czf /opt/backups/uploads_$(date +%Y%m%d).tar.gz /var/www/bjzxjj/uploads
```

## 十、监控和维护

### 查看系统资源

```bash
# CPU和内存
top

# 磁盘使用
df -h

# 网络连接
netstat -an | grep :8080
```

### 日志查看

```bash
# 后端日志
tail -f /var/www/bjzxjj/logs/bjzxjj-website.log

# Nginx访问日志
tail -f /var/log/nginx/bjzxjj-access.log

# Nginx错误日志
tail -f /var/log/nginx/bjzxjj-error.log
```

## 十一、更新部署

当代码更新后，重新运行部署脚本：

```bash
cd /opt/hebei-cs-website
git pull  # 如果使用Git
cd deploy
./deploy.sh
```

## 十二、联系支持

如有问题，请联系技术支持。

