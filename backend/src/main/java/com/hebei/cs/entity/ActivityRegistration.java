package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动报名实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_registrations")
public class ActivityRegistration extends BaseEntity {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 单位
     */
    private String company;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0-待审核，1-已通过，2-已拒绝）
     */
    private Integer status;

}