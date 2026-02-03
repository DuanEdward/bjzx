-- 为证件表添加一寸照字段
-- 执行时间: 2026-02-03

USE bjzxjj;

-- 添加一寸照路径字段
ALTER TABLE `certificate` 
ADD COLUMN `photo_path` varchar(255) DEFAULT NULL COMMENT '一寸照路径' AFTER `holder_contact`;
