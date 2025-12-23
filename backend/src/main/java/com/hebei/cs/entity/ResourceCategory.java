package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源分类实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resource_categories")
public class ResourceCategory extends BaseEntity {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类编码
     */
    private String code;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

}