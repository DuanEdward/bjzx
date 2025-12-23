# 快速部署指南

## 部署文件说明

### 脚本文件

1. **prepare.sh** - 环境准备脚本
   - 安装JDK 8、MySQL、Redis、Node.js、Nginx、Maven等必要软件
   - 创建项目目录
   - 配置防火墙

2. **init-database.sh** - 数据库初始化脚本
   - 创建数据库和用户
   - 导入数据库结构
   - 可选导入初始数据

3. **deploy.sh** - 项目部署脚本
   - 构建后端、前端、管理后台
   - 复制文件到部署目录
   - 重启服务

4. **start-backend.sh** - 后端启动脚本
   - 启动后端服务（不使用systemd时）

### 配置文件

1. **application-prod.yml** - 生产环境配置文件
   - 数据库配置
   - Redis配置
   - JWT配置
   - 日志配置

2. **nginx.conf** - Nginx配置文件
   - 前端站点配置
   - 管理后台配置
   - API代理配置
   - 文件上传路径配置

3. **bjzxjj-backend.service** - Systemd服务文件
   - 后端服务自动启动配置
   - 服务管理配置

## 快速开始

### 1. 上传项目到服务器

```bash
# 在服务器上
cd /opt
git clone your-repository-url
cd hebei-cs-website
```

### 2. 运行环境准备

```bash
cd deploy
chmod +x *.sh
./prepare.sh
```

### 3. 初始化数据库

```bash
./init-database.sh
# 按照提示输入MySQL root密码
```

### 4. 配置生产环境

编辑 `application-prod.yml`，修改：
- 数据库用户名和密码
- JWT密钥
- Redis密码（如果设置了）

编辑 `nginx.conf`，修改：
- 域名（your-domain.com）

### 5. 配置系统服务

```bash
# 复制服务文件
cp bjzxjj-backend.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable bjzxjj-backend

# 复制Nginx配置
cp nginx.conf /etc/nginx/conf.d/bjzxjj.conf
nginx -t  # 测试配置
systemctl reload nginx
```

### 6. 部署项目

```bash
./deploy.sh
```

### 7. 启动服务

```bash
systemctl start bjzxjj-backend
systemctl status bjzxjj-backend
```

## 验证部署

1. 访问前端：`http://your-domain.com`
2. 访问管理后台：`http://your-domain.com/admin`
3. 检查API：`http://your-domain.com/api/health`

## 常用命令

```bash
# 查看后端日志
journalctl -u bjzxjj-backend -f

# 重启后端
systemctl restart bjzxjj-backend

# 重启Nginx
systemctl restart nginx

# 查看服务状态
systemctl status bjzxjj-backend
systemctl status nginx
systemctl status mysqld
systemctl status redis
```

## 详细文档

更多详细信息请参考 [DEPLOYMENT.md](./DEPLOYMENT.md)

