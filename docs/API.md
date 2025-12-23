# API 接口设计文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- `code`: 状态码（200-成功，其他-失败）
- `message`: 响应消息
- `data`: 响应数据

## 1. 首页相关接口

### 1.1 获取Banner列表

**GET** `/home/banners`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "Banner标题",
      "image": "https://example.com/image.jpg",
      "link": "https://example.com",
      "sort": 1,
      "status": 1,
      "createdAt": "2024-01-01 12:00:00",
      "updatedAt": "2024-01-01 12:00:00"
    }
  ]
}
```

### 1.2 获取最新新闻

**GET** `/home/latest-news`

**参数**:
- `page`: 页码（默认1）
- `pageSize`: 每页数量（默认5）

### 1.3 获取最新活动

**GET** `/home/latest-activities`

**参数**:
- `page`: 页码（默认1）
- `pageSize`: 每页数量（默认6）

### 1.4 获取学会简介

**GET** `/home/society-info`

## 2. 新闻管理接口

### 2.1 获取新闻列表

**GET** `/news/list`

**参数**:
- `page`: 页码
- `pageSize`: 每页数量
- `category`: 分类（society-学会动态，industry-行业资讯，notice-通知公告）
- `keyword`: 关键词搜索

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取新闻详情

**GET** `/news/{id}`

### 2.3 创建新闻

**POST** `/news`

**请求体**:
```json
{
  "title": "新闻标题",
  "summary": "新闻摘要",
  "content": "新闻内容",
  "coverImage": "封面图片URL",
  "author": "作者",
  "categoryId": 1,
  "status": 1,
  "isTop": 0
}
```

### 2.4 更新新闻

**PUT** `/news/{id}`

### 2.5 删除新闻

**DELETE** `/news/{id}`

### 2.6 增加新闻浏览量

**POST** `/news/{id}/views`

## 3. 活动管理接口

### 3.1 获取活动列表

**GET** `/activities/list`

**参数**:
- `page`: 页码
- `pageSize`: 每页数量
- `status`: 活动状态（0-未开始，1-进行中，2-已结束）
- `startDate`: 开始日期
- `endDate`: 结束日期

### 3.2 获取活动详情

**GET** `/activities/{id}`

### 3.3 创建活动

**POST** `/activities`

**请求体**:
```json
{
  "title": "活动标题",
  "summary": "活动摘要",
  "content": "活动内容",
  "coverImage": "封面图片URL",
  "location": "活动地点",
  "startTime": "2024-01-01 12:00:00",
  "endTime": "2024-01-01 14:00:00",
  "registrationDeadline": "2024-01-01 10:00:00",
  "maxParticipants": 100,
  "status": 0,
  "isRegistrationOpen": 1
}
```

### 3.4 活动报名

**POST** `/activities/{id}/register`

**请求体**:
```json
{
  "name": "姓名",
  "phone": "手机号",
  "email": "邮箱",
  "company": "单位",
  "remark": "备注"
}
```

## 4. 会员管理接口

### 4.1 申请入会

**POST** `/member/apply`

**请求体**:
```json
{
  "name": "姓名",
  "gender": "性别",
  "birthDate": "1990-01-01",
  "title": "职称",
  "company": "工作单位",
  "phone": "手机号",
  "email": "邮箱",
  "research": "研究方向",
  "level": 1,
  "introduction": "个人简介"
}
```

### 4.2 获取会员列表

**GET** `/member/list`

**参数**:
- `page`: 页码
- `pageSize`: 每页数量
- `level`: 会员等级
- `status`: 审核状态

### 4.3 审核会员申请

**PUT** `/member/{id}/review`

**请求体**:
```json
{
  "status": 1,
  "remark": "审核意见"
}
```

## 5. 资源管理接口

### 5.1 获取资源列表

**GET** `/resources/list`

**参数**:
- `page`: 页码
- `pageSize`: 每页数量
- `category`: 分类（document-技术文档，report-研究报告，conference-会议资料，standard-标准规范，material-学习资料）

### 5.2 获取资源详情

**GET** `/resources/{id}`

### 5.3 上传资源

**POST** `/resources`

**请求体** (multipart/form-data):
- `title`: 资源标题
- `description`: 资源描述
- `file`: 资源文件
- `categoryId`: 分类ID

### 5.4 记录下载

**POST** `/resources/{id}/download`

## 6. Banner管理接口

### 6.1 获取Banner列表

**GET** `/banners`

### 6.2 创建Banner

**POST** `/banners`

**请求体**:
```json
{
  "title": "Banner标题",
  "image": "图片URL",
  "link": "跳转链接",
  "sort": 1,
  "status": 1
}
```

### 6.3 更新Banner

**PUT** `/banners/{id}`

### 6.4 删除Banner

**DELETE** `/banners/{id}`

### 6.5 更新Banner排序

**PUT** `/banners/sort`

**请求体**:
```json
{
  "banners": [
    {"id": 1, "sort": 1},
    {"id": 2, "sort": 2}
  ]
}
```

## 7. 用户认证接口

### 7.1 用户登录

**POST** `/auth/login`

**请求体**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "jwt-token",
    "user": {
      "id": 1,
      "username": "admin",
      "role": "admin"
    }
  }
}
```

### 7.2 用户登出

**POST** `/auth/logout`

### 7.3 获取当前用户信息

**GET** `/auth/user`

## 8. 系统管理接口

### 8.1 获取系统设置

**GET** `/system/settings`

### 8.2 更新系统设置

**PUT** `/system/settings`

**请求体**:
```json
{
  "siteName": "网站名称",
  "siteUrl": "网站地址",
  "icp": "备案号",
  "copyright": "版权信息"
}
```

### 8.3 获取用户列表

**GET** `/system/users`

### 8.4 创建用户

**POST** `/system/users`

### 8.5 更新用户

**PUT** `/system/users/{id}`

### 8.6 删除用户

**DELETE** `/system/users/{id}`

## 9. 文件上传接口

### 9.1 上传图片

**POST** `/upload/image`

**请求体**: multipart/form-data
- `file`: 图片文件

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.com/image.jpg"
  }
}
```

### 9.2 上传文件

**POST** `/upload/file`

## 错误码说明

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |