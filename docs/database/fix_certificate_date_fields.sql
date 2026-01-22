-- 修复证件表日期字段，允许NULL值
-- 执行时间: 2024-01-22

USE bjzxjj;

-- 修改 issue_date, valid_from, valid_until 字段允许 NULL
ALTER TABLE `certificate`
  MODIFY COLUMN `issue_date` date DEFAULT NULL COMMENT '发证日期',
  MODIFY COLUMN `valid_from` date DEFAULT NULL COMMENT '有效期起始日期',
  MODIFY COLUMN `valid_until` date DEFAULT NULL COMMENT '有效期截止日期';

