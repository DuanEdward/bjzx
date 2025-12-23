# 后端服务 - 河北省计算机学会

## 项目简介

河北省计算机学会网站后端服务，基于 Spring Boot 3 + MyBatis Plus + MySQL 构建。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.5
- **安全**: Spring Security + JWT
- **工具**: Hutool 5.8.25
- **JSON**: FastJSON 2.0.45
- **缓存**: Redis 6.0+ (可选)

## 目录结构

```
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/hebei/cs/
│       │       ├── config/       # 配置类
│       │       ├── controller/   # 控制器
│       │       ├── dto/          # 数据传输对象
│       │       ├── entity/       # 实体类
│       │       ├── mapper/       # 数据访问层
│       │       ├── service/      # 业务层
│       │       ├── utils/        # 工具类
│       │       └── common/       # 公共类
│       └── resources/
│           ├── mapper/           # MyBatis XML文件
│           ├── static/           # 静态资源
│           └── application.yml   # 配置文件
├── pom.xml                       # Maven配置
└── README.md                     # 项目说明
```

## 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+ (可选)

## 快速开始

### 1. 数据库配置

创建数据库并导入数据：

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE hebei_cs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 导入表结构和初始数据
mysql -u root -p hebei_cs < docs/database/schema.sql
mysql -u root -p hebei_cs < docs/database/init.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yml` 文件，修改数据库连接信���：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hebei_cs?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: hebei_cs
    password: your_password
```

### 3. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/hebei-cs-website-1.0.0.jar
```

项目启动后访问：http://localhost:8080/api

## API文档

### 认证相关

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 登出
```
POST /api/auth/logout
```

### 首页数据

#### 获取Banner列表
```
GET /api/home/banners
```

#### 获取最新新闻
```
GET /api/home/latest-news?page=1&pageSize=5
```

#### 获取最新活动
```
GET /api/home/latest-activities?page=1&pageSize=6
```

#### 获取学会简介
```
GET /api/home/society-info
```

### 新闻管理

#### 获取新闻列表
```
GET /api/news/list?page=1&pageSize=10&categoryId=1&keyword=关键词
```

#### 获取新闻详情
```
GET /api/news/{id}
```

#### 增加浏览量
```
POST /api/news/{id}/views
```

## 开发规范

### 1. 代码规范

- 使用驼峰命名法
- 类名首字母大写
- 方法名和变量名首字母小写
- 常量使用大写字母和下划线

### 2. 接口规范

- 使用统一的响应格式 `Result<T>`
- 分页查询使用 `PageResult<T>`
- 异常处理使用 `GlobalExceptionHandler`

### 3. 数据库规范

- 表名使用下划线命名法
- 字段名使用下划线命名法
- 必须有主键 `id`
- 软删除字段 `deleted`

## 配置说明

### application.yml 配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 服务端口 | 8080 |
| spring.datasource.url | 数据库连接URL | - |
| spring.datasource.username | 数据库用户名 | - |
| spring.datasource.password | 数据库密码 | - |
| jwt.secret | JWT密钥 | - |
| jwt.expiration | JWT过期时间(毫秒) | 86400000 |
| file.upload-path | 文件上传路径 | ./uploads |

### 环境配置

- **开发环境**: `application-dev.yml`
- **测试环境**: `application-test.yml`
- **生产环境**: `application-prod.yml`

## 测试

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn test -Dtest=*IntegrationTest
```

## 部署

### Docker部署

创建 Dockerfile：

```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app

COPY target/hebei-cs-website-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
# 构建镜像
docker build -t hebei-cs-backend .

# 运行容器
docker run -d -p 8080:8080 --name hebei-cs-backend hebei-cs-backend
```

### 传统部署

```bash
# 打包
mvn clean package -Dmaven.test.skip=true

# 运行
java -jar -Xms512m -Xmx1024m target/hebei-cs-website-1.0.0.jar
```

## 监控

### 应用监控

项目集成了 Spring Boot Actuator，可以通过以下端点监控应用：

- `/actuator/health` - 健康检查
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 性能指标

### 日志配置

日志文件位置：`logs/hebei-cs-website.log`

日志级别配置：
- `com.hebei.cs`: DEBUG
- `root`: INFO

## 常见问题

### 1. 数据库连接失败

检查数据库服务是否启动，连接配置是否正确。

### 2. 跨域问题

前端和后端部署在不同域名时，需要配置 CORS。

### 3. 文件上传失败

检查上传目录是否存在，是否有写入权限。

## 更新日志

### v1.0.0 (2024-01-10)

- 初始版本发布
- 完成基础功能开发
- 集成 MyBatis Plus
- 实现 JWT 认证
- 添加跨域支持