# 快速部署指南

## ⚠️ 重要提示

**`deploy.sh` 脚本不能单独完成首次部署！**

`deploy.sh` 只负责：
- ✅ 构建项目（后端、前端、管理后台）
- ✅ 生成配置文件
- ✅ 配置 Nginx
- ✅ 重启服务

**缺少的步骤：**
- ❌ 环境准备（安装 Node.js、Nginx、MySQL 等）
- ❌ 数据库初始化
- ❌ 系统服务安装（bjzxjj-backend.service）

## 📋 完整部署流程

### 首次部署（必须按顺序执行）

```bash
cd /path/to/bjzx/deploy

# 1. 环境准备（安装 Node.js、Nginx、MySQL 等）
chmod +x prepare.sh
./prepare.sh

# 2. 初始化数据库
chmod +x init-database.sh
./init-database.sh

# 3. 安装系统服务
chmod +x setup-service.sh
./setup-service.sh

# 4. 部署项目
chmod +x deploy.sh
./deploy.sh
```

### 后续更新（只需运行 deploy.sh）

```bash
cd /path/to/bjzx/deploy
./deploy.sh
```

## ✅ 部署后检查清单

运行 `deploy.sh` 后，检查以下项目：

### 1. 检查服务状态
```bash
# 后端服务
systemctl status bjzxjj-backend

# Nginx
systemctl status nginx

# MySQL
systemctl status mysqld
```

### 2. 检查端口
```bash
# 后端端口 8080
netstat -tlnp | grep 8080

# Nginx 端口 80
netstat -tlnp | grep 80
```

### 3. 检查域名解析
```bash
# 确保域名解析到服务器 IP
nslookup bjzxjj.org.cn
```

### 4. 检查防火墙
```bash
# 确保开放 80 和 443 端口
firewall-cmd --list-ports
# 如果没有，执行：
# firewall-cmd --permanent --add-service=http
# firewall-cmd --permanent --add-service=https
# firewall-cmd --reload
```

### 5. 访问测试
- 前端：http://bjzxjj.org.cn/
- 管理后台：http://bjzxjj.org.cn/admin/
- 后端 API：http://bjzxjj.org.cn/api/

## 🔧 如果无法访问

### 问题 1: 后端服务未启动
```bash
# 检查服务是否安装
systemctl list-unit-files | grep bjzxjj-backend

# 如果未安装，运行：
./setup-service.sh

# 然后启动服务
systemctl start bjzxjj-backend
systemctl enable bjzxjj-backend
```

### 问题 2: Nginx 配置错误
```bash
# 检查 Nginx 配置
nginx -t

# 查看错误日志
tail -f /var/log/nginx/bjzxjj-error.log
```

### 问题 3: 数据库未初始化
```bash
# 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES LIKE 'bjzxjj';"

# 如果不存在，运行：
./init-database.sh
```

## 📝 总结

- **首次部署**：需要运行 4 个脚本（prepare.sh → init-database.sh → setup-service.sh → deploy.sh）
- **后续更新**：只需运行 deploy.sh
- **部署后**：检查服务状态、端口、域名解析、防火墙

**建议**：首次部署时，按顺序执行所有脚本，确保所有依赖都已安装和配置。

