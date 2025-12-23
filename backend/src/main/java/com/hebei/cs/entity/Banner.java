package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Banner实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("banners")
public class Banner extends BaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 跳转链接
     */
    private String link;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

}