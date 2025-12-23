-- 证件表
CREATE TABLE IF NOT EXISTS certificate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '证件ID',
    name VARCHAR(100) NOT NULL COMMENT '证件名称',
    type VARCHAR(50) NOT NULL COMMENT '证件类型（营业执照、组织机构代码证、税务登记证、社会保险登记证、统计登记证、开户许可证、卫生许可证、安全生产许可证、其他）',
    number VARCHAR(50) NOT NULL COMMENT '证件编号',
    holder VARCHAR(50) NOT NULL COMMENT '持有人姓名',
    holder_contact VARCHAR(100) COMMENT '持有人联系方式',
    issuing_authority VARCHAR(100) NOT NULL COMMENT '发证机关',
    issue_date DATE NOT NULL COMMENT '发证日期',
    valid_from DATE NOT NULL COMMENT '有效期起始日期',
    valid_until DATE NOT NULL COMMENT '有效期截止日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '证件状态（0：已过期，1：有效，2：即将过期）',
    attachment_path VARCHAR(255) COMMENT '附件路径',
    description TEXT COMMENT '描述',
    is_public BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否公开（true：公开，false：不公开）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_number (number),
    INDEX idx_holder (holder),
    INDEX idx_status (status),
    INDEX idx_is_public (is_public),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证件信息表';

-- 插入示例数据
INSERT INTO certificate (name, type, number, holder, holder_contact, issuing_authority, issue_date, valid_from, valid_until, status, description, is_public, create_by) VALUES
('北京中兴建机职业技能鉴定中心营业执照', '营业执照', '91110000MA07X8XX9K', '北京中兴建机职业技能鉴定中心', '010-88888888', '北京市市场监督管理局', '2020-01-15', '2020-01-15', '2030-01-14', 1, '北京中兴建机职业技能鉴定中心统一社会信用代码营业执照', TRUE, 'admin'),
('北京中兴建机职业技能鉴定中心组织机构代码证', '组织机构代码证', 'G25110000-6', '北京中兴建机职业技能鉴定中心', '010-88888888', '北京市质量技术监督局', '2020-01-20', '2020-01-20', '2025-01-19', 1, '组织机构代码证', TRUE, 'admin'),
('北京中兴建机职业技能鉴定中心税务登记证', '税务登记证', '京税证字11000012345678号', '北京中兴建机职业技能鉴定中心', '010-88888888', '北京市国家税务局', '2020-01-25', '2020-01-25', '2030-01-24', 1, '税务登记证书', TRUE, 'admin'),
('北京中兴建机职业技能鉴定中心开户许可证', '开户许可证', 'J4010001234501', '北京中兴建机职业技能鉴定中心', '010-88888888', '中国人民银行北京中心支行', '2020-02-01', '2020-02-01', '2025-02-01', 2, '基本存款账户开户许可证', TRUE, 'admin');