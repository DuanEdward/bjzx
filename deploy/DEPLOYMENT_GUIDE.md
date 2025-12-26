# 完整部署指南

## 部署前准备

### 1. 检查配置文件
编辑 `deploy/config.sh`，确保以下配置正确：
- `DOMAIN`: 域名（如：bjzxjj.org.cn）
- `MYSQL_ROOT_PASSWORD`: MySQL root 密码
- `DB_PASSWORD`: 数据库密码
- `PROJECT_HOME`: 项目部署目录

### 2. 确保域名解析
确保域名已正确解析到服务器 IP 地址：
```bash
# 检查域名解析
nslookup bjzxjj.org.cn
```

## 首次部署流程

如果是**首次部署**，需要按以下顺序执行：

### 步骤 1: 环境准备（仅首次需要）
```bash
cd /path/to/bjzx/deploy
chmod +x prepare.sh
./prepare.sh
```
这会安装：
- Node.js 和 npm
- Nginx
- MySQL（如果未安装）
- Java 和 Maven（如果未安装）

### 步骤 2: 初始化数据库（仅首次需要）
```bash
chmod +x init-database.sh
./init-database.sh
```
这会：
- 创建数据库
- 创建数据库用户
- 导入初始数据

### 步骤 3: 安装系统服务（仅首次需要）
```bash
chmod +x setup-service.sh
./setup-service.sh
```
这会：
- 创建 systemd 服务文件
- 配置后端服务开机自启

### 步骤 4: 部署项目（每次更新都需要）
```bash
chmod +x deploy.sh
./deploy.sh
```
这会：
- 构建后端、前端、管理后台
- 生成配置文件
- 配置 Nginx
- 重启服务

## 后续更新部署

如果已经完成首次部署，后续只需要运行：
```bash
cd /path/to/bjzx/deploy
./deploy.sh
```

## 一键部署（推荐）

使用 `deploy-all.sh` 可以自动执行所有步骤：
```bash
cd /path/to/bjzx/deploy
chmod +x deploy-all.sh
./deploy-all.sh
```

## 部署后验证

### 1. 检查服务状态
```bash
# 检查后端服务
systemctl status bjzxjj-backend

# 检查 Nginx
systemctl status nginx

# 检查 MySQL
systemctl status mysqld
```

### 2. 检查端口监听
```bash
# 检查后端端口
netstat -tlnp | grep 8080

# 检查 Nginx 端口
netstat -tlnp | grep 80
```

### 3. 访问网站
- 前端：http://bjzxjj.org.cn/
- 管理后台：http://bjzxjj.org.cn/admin/
- 后端 API：http://bjzxjj.org.cn/api/

### 4. 查看日志
```bash
# 后端日志
journalctl -u bjzxjj-backend -f

# Nginx 访问日志
tail -f /var/log/nginx/bjzxjj-access.log

# Nginx 错误日志
tail -f /var/log/nginx/bjzxjj-error.log
```

## 常见问题

### 1. 无法访问域名
- 检查域名解析是否正确
- 检查防火墙是否开放 80 端口
- 检查 Nginx 是否正常运行

### 2. 后端服务无法启动
- 检查 Java 是否安装：`java -version`
- 检查 JAR 文件是否存在：`ls -lh /home/bjzxjj/backend/bjzxjj-website.jar`
- 查看服务日志：`journalctl -u bjzxjj-backend -n 50`

### 3. 数据库连接失败
- 检查 MySQL 是否运行：`systemctl status mysqld`
- 检查数据库配置：`cat /home/bjzxjj/backend/application-prod.yml`
- 测试数据库连接：`mysql -u bjzxjj_user -p bjzxjj`

### 4. 前端构建失败（内存不足）
- 使用本地构建：`./build-local.sh`（在本地机器）
- 或增加服务器内存
- 或使用更高的内存限制：`NODE_OPTIONS="--max_old_space_size=2048" npm run build`

## 注意事项

1. **首次部署必须按顺序执行**：prepare.sh → init-database.sh → setup-service.sh → deploy.sh
2. **后续更新只需运行**：deploy.sh
3. **确保使用 root 用户**运行所有脚本
4. **备份重要数据**：部署前建议备份数据库和配置文件

