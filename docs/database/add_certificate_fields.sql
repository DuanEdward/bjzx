-- 为证件表添加新字段
-- 执行时间: 2024-01-10

USE bjzxjj;

-- 添加性别字段
ALTER TABLE `certificate` 
ADD COLUMN `gender` varchar(10) DEFAULT NULL COMMENT '性别（男、女）' AFTER `holder`;

-- 添加身份证号字段
ALTER TABLE `certificate` 
ADD COLUMN `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号' AFTER `gender`,
ADD INDEX `idx_id_card` (`id_card`);

-- 添加岗位名称字段
ALTER TABLE `certificate` 
ADD COLUMN `position` varchar(100) DEFAULT NULL COMMENT '岗位名称' AFTER `id_card`;

-- 添加技能等级字段
ALTER TABLE `certificate` 
ADD COLUMN `skill_level` varchar(50) DEFAULT NULL COMMENT '技能等级' AFTER `position`;

