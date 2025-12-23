package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resources")
public class Resource extends BaseEntity {

    /**
     * 资源标题
     */
    private String title;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createdBy;

}