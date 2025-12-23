-- 开发环境数据库初始化脚本
-- 创建时间: 2024-01-10

-- 创建开发环境数据库
CREATE DATABASE IF NOT EXISTS bjzxjj_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 导入表结构
SOURCE schema.sql;

USE bjzxjj_dev;

-- 导入初始化数据
SOURCE init.sql;