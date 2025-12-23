
w# 数据库设计文档

## 数据库概述

- **数据库类型**: MySQL 8.0+
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_general_ci

## 表结构设计

### 1. 用户表 (users)

```sql
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（加密）',
  `realname` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. 角色表 (roles)

```sql
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

### 3. 用户角色关联表 (user_roles)

```sql
CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `fk_user_roles_role_id` (`role_id`),
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

### 4. Banner表 (banners)

```sql
CREATE TABLE `banners` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Banner ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `image` varchar(200) NOT NULL COMMENT '图片URL',
  `link` varchar(200) DEFAULT NULL COMMENT '跳转链接',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort` (`sort`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner表';
```

### 5. 新闻分类表 (news_categories)

```sql
CREATE TABLE `news_categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `code` varchar(50) NOT NULL COMMENT '分类编码',
  `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻分类表';
```

### 6. 新闻表 (news)

```sql
CREATE TABLE `news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `content` longtext COMMENT '内容',
  `cover_image` varchar(200) DEFAULT NULL COMMENT '封面图片URL',
  `author` varchar(50) DEFAULT NULL COMMENT '作者',
  `views` int(11) DEFAULT 0 COMMENT '浏览量',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `status` tinyint(1) DEFAULT 0 COMMENT '状态（0-草稿，1-已发布）',
  `is_top` tinyint(1) DEFAULT 0 COMMENT '是否置顶（0-否，1-是）',
  `published_at` datetime DEFAULT NULL COMMENT '发布时间',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_published_at` (`published_at`),
  KEY `fk_news_created_by` (`created_by`),
  CONSTRAINT `fk_news_category` FOREIGN KEY (`category_id`) REFERENCES `news_categories` (`id`),
  CONSTRAINT `fk_news_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻表';
```

### 7. 活动表 (activities)

```sql
CREATE TABLE `activities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(200) NOT NULL COMMENT '活动标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '活动摘要',
  `content` longtext COMMENT '活动内容',
  `cover_image` varchar(200) DEFAULT NULL COMMENT '封面图片URL',
  `location` varchar(200) DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `registration_deadline` datetime DEFAULT NULL COMMENT '报名截止时间',
  `max_participants` int(11) DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int(11) DEFAULT 0 COMMENT '当前参与人数',
  `status` tinyint(1) DEFAULT 0 COMMENT '活动状态（0-未开始，1-进行中，2-已结束）',
  `is_registration_open` tinyint(1) DEFAULT 1 COMMENT '是否开放报名（0-否，1-是）',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_status` (`status`),
  KEY `idx_is_registration_open` (`is_registration_open`),
  KEY `fk_activities_created_by` (`created_by`),
  CONSTRAINT `fk_activities_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';
```

### 8. 活动报名表 (activity_registrations)

```sql
CREATE TABLE `activity_registrations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `company` varchar(100) DEFAULT NULL COMMENT '单位',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) DEFAULT 0 COMMENT '状态（0-待审核，1-已通过，2-已拒绝）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_registrations_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';
```

### 9. 会员表 (members)

```sql
CREATE TABLE `members` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `birth_date` date DEFAULT NULL COMMENT '出生年月',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `company` varchar(100) DEFAULT NULL COMMENT '工作单位',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像URL',
  `research` varchar(200) DEFAULT NULL COMMENT '研究方向',
  `level` tinyint(1) DEFAULT 1 COMMENT '会员等级（1-普通，2-高级，3-理事）',
  `introduction` text COMMENT '个人简介',
  `status` tinyint(1) DEFAULT 0 COMMENT '审核状态（0-待审核，1-已通过，2-已拒绝）',
  `reviewed_by` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `review_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `joined_at` datetime DEFAULT NULL COMMENT '入会时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`),
  KEY `idx_joined_at` (`joined_at`),
  KEY `fk_members_reviewed_by` (`reviewed_by`),
  CONSTRAINT `fk_members_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';
```

### 10. 资源分类表 (resource_categories)

```sql
CREATE TABLE `resource_categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `code` varchar(50) NOT NULL COMMENT '分类编码',
  `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源分类表';
```

### 11. 资源表 (resources)

```sql
CREATE TABLE `resources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `title` varchar(200) NOT NULL COMMENT '资源标题',
  `description` varchar(500) DEFAULT NULL COMMENT '资源描述',
  `file_url` varchar(200) NOT NULL COMMENT '文件URL',
  `file_name` varchar(200) NOT NULL COMMENT '文件名',
  `file_size` bigint(20) DEFAULT 0 COMMENT '文件大小（字节）',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型',
  `download_count` int(11) DEFAULT 0 COMMENT '下载次数',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_download_count` (`download_count`),
  KEY `fk_resources_created_by` (`created_by`),
  CONSTRAINT `fk_resources_category` FOREIGN KEY (`category_id`) REFERENCES `resource_categories` (`id`),
  CONSTRAINT `fk_resources_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';
```

### 12. 证件表 (certificate)

```sql
CREATE TABLE `certificate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '证件ID',
  `name` varchar(100) NOT NULL COMMENT '证件名称',
  `type` varchar(50) NOT NULL COMMENT '证件类型（营业执照、组织机构代码证、税务登记证、社会保险登记证、统计登记证、开户许可证、卫生许可证、安全生产许可证、其他）',
  `number` varchar(50) NOT NULL COMMENT '证件编号',
  `holder` varchar(50) NOT NULL COMMENT '持有人姓名',
  `holder_contact` varchar(100) DEFAULT NULL COMMENT '持有人联系方式',
  `issuing_authority` varchar(100) NOT NULL COMMENT '发证机关',
  `issue_date` date NOT NULL COMMENT '发证日期',
  `valid_from` date NOT NULL COMMENT '有效期起始日期',
  `valid_until` date NOT NULL COMMENT '有效期截止日期',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '证件状态（0：已过期，1：有效，2：即将过期）',
  `attachment_path` varchar(255) DEFAULT NULL COMMENT '附件路径',
  `description` text COMMENT '描述',
  `is_public` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否公开（1：公开，0：不公开）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_number` (`number`),
  KEY `idx_holder` (`holder`),
  KEY `idx_status` (`status`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证件信息表';
```

### 13. 系统设置表 (system_settings)

```sql
CREATE TABLE `system_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `key` varchar(100) NOT NULL COMMENT '设置键',
  `value` text COMMENT '设置值',
  `description` varchar(200) DEFAULT NULL COMMENT '设置描述',
  `type` varchar(20) DEFAULT 'string' COMMENT '数据类型',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';
```

## 初始化数据

### 1. 角色初始化

```sql
INSERT INTO `roles` (`name`, `code`, `description`) VALUES
('超级管理员', 'super_admin', '拥有所有权限'),
('管理员', 'admin', '拥有大部分权限'),
('编辑', 'editor', '拥有内容编辑权限');
```

### 2. 用户初始化

```sql
INSERT INTO `users` (`username`, `password`, `realname`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi0pE2BgFzLNdcFcbnATUxfK', '系统管理员', 'admin@example.com');
-- 密码: admin123
```

### 3. 用户角色关联

```sql
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (1, 1);
```

### 4. 新闻分类初始化

```sql
INSERT INTO `news_categories` (`name`, `code`, `description`) VALUES
('学会动态', 'society', '学会新闻动态'),
('行业资讯', 'industry', '计算机行业资讯'),
('通知公告', 'notice', '重要通知公告');
```

### 5. 资源分类初始化

```sql
INSERT INTO `resource_categories` (`name`, `code`, `description`) VALUES
('技术文档', 'document', '技术相关文档'),
('研究报告', 'report', '调研报告文档'),
('会议资料', 'conference', '会议相关资料'),
('标准规范', 'standard', '技术标准规范'),
('学习资料', 'material', '学习相关资料');
```

### 6. 系统设置初始化

```sql
INSERT INTO `system_settings` (`key`, `value`, `description`, `type`) VALUES
('site_name', '河北省计算机学会', '网站名称', 'string'),
('site_url', 'https://www.hsjs.org.cn', '网站地址', 'string'),
('site_description', '河北省计算机学会官方网站', '网站描述', 'string'),
('site_keywords', '河北省计算机学会,计算机,学术', '网站关键词', 'string'),
('icp', '冀ICP备xxxxxxxx号', '备案号', 'string'),
('copyright', '© 2024 河北省计算机学会 版权所有', '版权信息', 'string');
```

## 索引优化建议

1. **经常查询的字段添加索引**
   - news表的published_at、category_id、status
   - activities表的start_time、status
   - members表的status、level

2. **复合索引**
   - news表: (status, is_top, published_at)
   - activities表: (status, is_registration_open, start_time)

3. **避免索引失效**
   - 避免在索引列上使用函数
   - 避免使用前导模糊查询（LIKE '%xx'）

## 数据备份策略

1. **每日备份**
   - 全量备份所有表
   - 保留30天

2. **增量备份**
   - 每小时备份事务日志
   - 保留7天

3. **异地备份**
   - 定期将备份文件同步到异地存储