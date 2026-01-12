package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 投诉反馈实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("feedbacks")
public class Feedback extends BaseEntity {

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 处理状态（0-待处理，1-处理中，2-已处理）
     */
    private Integer status;

    /**
     * 处理备注
     */
    private String remark;
}

