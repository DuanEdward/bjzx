# 故障排查指南

## MySQL安装问题

### GPG密钥验证失败

**问题：**
```
Error: GPG check FAILED
Public key for mysql-community-*.rpm is not installed
```

**解决方案：**

1. **快速修复（推荐）**
```bash
cd /opt/hebei-cs-website/deploy
./fix-mysql-gpg.sh
yum install -y mysql-community-server --nogpgcheck
```

2. **手动导入GPG密钥**
```bash
# 导入MySQL官方GPG密钥
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022

# 如果失败，尝试备用密钥
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql

# 清理缓存后重试
yum clean all
yum install -y mysql-community-server
```

3. **临时禁用GPG检查**
```bash
# 编辑仓库配置文件
vi /etc/yum.repos.d/mysql-community.repo

# 找到 [mysql80-community] 部分，修改：
# gpgcheck=1 改为 gpgcheck=0

# 保存后重新安装
yum install -y mysql-community-server
```

### MySQL服务无法启动

**检查服务状态：**
```bash
systemctl status mysqld
journalctl -u mysqld -n 50
```

**常见原因：**
- 端口被占用：`netstat -tlnp | grep 3306`
- 数据目录权限问题：`chown -R mysql:mysql /var/lib/mysql`
- 配置文件错误：`mysqld --validate-config`

**修复：**
```bash
# 检查MySQL配置
mysqld --validate-config

# 修复数据目录权限
chown -R mysql:mysql /var/lib/mysql
chmod 750 /var/lib/mysql

# 重启服务
systemctl restart mysqld
```

### 忘记MySQL root密码

**重置密码：**
```bash
# 1. 停止MySQL服务
systemctl stop mysqld

# 2. 以安全模式启动MySQL
mysqld_safe --skip-grant-tables &

# 3. 登录MySQL（无需密码）
mysql -uroot

# 4. 重置密码
USE mysql;
ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
FLUSH PRIVILEGES;
EXIT;

# 5. 重启MySQL服务
systemctl restart mysqld
```

## 后端服务问题

### 服务无法启动

**查看日志：**
```bash
journalctl -u bjzxjj-backend -n 100
tail -f /var/www/bjzxjj/logs/bjzxjj-website.log
```

**常见原因：**

1. **端口被占用**
```bash
# 检查8080端口
netstat -tlnp | grep 8080
lsof -i :8080

# 杀死占用进程
kill -9 <PID>
```

2. **数据库连接失败**
```bash
# 测试数据库连接
mysql -ubjzxjj_user -p -h localhost

# 检查配置文件
cat /var/www/bjzxjj/backend/application-prod.yml | grep datasource
```

3. **JVM内存不足**
```bash
# 检查系统内存
free -h

# 调整JVM参数（编辑服务文件）
vi /etc/systemd/system/bjzxjj-backend.service
# 修改 -Xmx 参数为更小的值，如 -Xmx512m
systemctl daemon-reload
systemctl restart bjzxjj-backend
```

4. **文件权限问题**
```bash
# 检查JAR文件权限
ls -l /var/www/bjzxjj/backend/bjzxjj-website.jar

# 修复权限
chmod 755 /var/www/bjzxjj/backend/bjzxjj-website.jar
chown root:root /var/www/bjzxjj/backend/bjzxjj-website.jar
```

### 服务频繁重启

**检查系统资源：**
```bash
# CPU和内存使用
top
htop

# 磁盘空间
df -h

# 查看OOM Killer日志
dmesg | grep -i "out of memory"
journalctl -k | grep -i "killed process"
```

**解决方案：**
- 增加服务器内存
- 减少JVM堆内存设置
- 优化应用代码

## Nginx问题

### 502 Bad Gateway

**原因：后端服务未启动或无法连接**

**检查：**
```bash
# 检查后端服务状态
systemctl status bjzxjj-backend

# 测试后端API
curl http://localhost:8080/api/health

# 检查Nginx错误日志
tail -f /var/log/nginx/bjzxjj-error.log
```

**修复：**
```bash
# 启动后端服务
systemctl start bjzxjj-backend

# 检查Nginx配置
nginx -t

# 重启Nginx
systemctl restart nginx
```

### 403 Forbidden

**原因：文件权限或目录配置错误**

**检查：**
```bash
# 检查目录权限
ls -la /var/www/bjzxjj/frontend
ls -la /var/www/bjzxjj/admin

# 检查Nginx用户
ps aux | grep nginx
```

**修复：**
```bash
# 修复目录权限
chown -R nginx:nginx /var/www/bjzxjj
chmod -R 755 /var/www/bjzxjj
```

### 404 Not Found

**检查Nginx配置：**
```bash
# 验证配置
nginx -t

# 检查站点配置
cat /etc/nginx/conf.d/bjzxjj.conf

# 检查文件是否存在
ls -la /var/www/bjzxjj/frontend/index.html
```

## 数据库问题

### 连接超时

**检查：**
```bash
# 测试连接
mysql -ubjzxjj_user -p -h localhost

# 检查MySQL服务
systemctl status mysqld

# 检查防火墙
firewall-cmd --list-all | grep mysql
```

**修复：**
```bash
# 确保MySQL服务运行
systemctl start mysqld

# 检查用户权限
mysql -uroot -p
SHOW GRANTS FOR 'bjzxjj_user'@'localhost';
```

### 数据库不存在

**创建数据库：**
```bash
mysql -uroot -p <<EOF
CREATE DATABASE IF NOT EXISTS bjzxjj DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOF
```

### 表不存在

**导入数据库结构：**
```bash
mysql -ubjzxjj_user -p bjzxjj < /opt/hebei-cs-website/docs/database/schema.sql
```

## Redis问题

### 连接失败

**检查：**
```bash
# 检查Redis服务
systemctl status redis

# 测试连接
redis-cli ping

# 检查配置
cat /etc/redis.conf | grep -E "bind|port|password"
```

**修复：**
```bash
# 启动Redis
systemctl start redis

# 如果设置了密码，检查应用配置
cat /var/www/bjzxjj/backend/application-prod.yml | grep redis
```

## 文件上传问题

### 上传失败

**检查：**
```bash
# 检查目录权限
ls -la /var/www/bjzxjj/uploads

# 检查磁盘空间
df -h /var/www/bjzxjj/uploads

# 检查Nginx配置
grep client_max_body_size /etc/nginx/conf.d/bjzxjj.conf
```

**修复：**
```bash
# 修复权限
chmod -R 777 /var/www/bjzxjj/uploads
chown -R nginx:nginx /var/www/bjzxjj/uploads

# 增加Nginx上传大小限制（如果需要）
# 编辑 /etc/nginx/conf.d/bjzxjj.conf
# 添加：client_max_body_size 50M;
# 重启Nginx
systemctl restart nginx
```

## 性能问题

### 响应慢

**检查：**
```bash
# 检查系统负载
uptime
top

# 检查数据库慢查询
mysql -uroot -p
SHOW PROCESSLIST;
SHOW FULL PROCESSLIST;

# 检查Nginx访问日志
tail -f /var/log/nginx/bjzxjj-access.log
```

**优化建议：**
- 优化数据库查询
- 增加服务器资源
- 启用Redis缓存
- 优化Nginx配置

### 内存不足

**检查内存使用：**
```bash
free -h
ps aux --sort=-%mem | head -10
```

**优化：**
- 减少JVM堆内存
- 优化应用代码
- 增加服务器内存
- 清理不必要的服务

## 日志查看

### 查看所有相关日志

```bash
# 后端应用日志
tail -f /var/www/bjzxjj/logs/bjzxjj-website.log

# 后端服务日志
journalctl -u bjzxjj-backend -f

# Nginx访问日志
tail -f /var/log/nginx/bjzxjj-access.log

# Nginx错误日志
tail -f /var/log/nginx/bjzxjj-error.log

# MySQL错误日志
tail -f /var/log/mysqld.log

# 系统日志
journalctl -f
```

## 联系支持

如果以上方法都无法解决问题，请：
1. 收集相关日志文件
2. 记录错误信息
3. 联系技术支持

