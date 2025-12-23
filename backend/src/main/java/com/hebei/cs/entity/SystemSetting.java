package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_settings")
public class SystemSetting extends BaseEntity {

    /**
     * 设置键
     */
    private String key;

    /**
     * 设置值
     */
    private String value;

    /**
     * 设置描述
     */
    private String description;

    /**
     * 数据类型
     */
    private String type;

}