package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("members")
public class Member extends BaseEntity {

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生年月
     */
    private LocalDate birthDate;

    /**
     * 职称
     */
    private String title;

    /**
     * 工作单位
     */
    private String company;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 研究方向
     */
    private String research;

    /**
     * 会员等级（1-普通，2-高级，3-理事）
     */
    private Integer level;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 审核状态（0-待审核，1-已通过，2-已拒绝）
     */
    private Integer status;

    /**
     * 审核人ID
     */
    private Long reviewedBy;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;

    /**
     * 审核意见
     */
    private String reviewRemark;

    /**
     * 入会时间
     */
    private LocalDateTime joinedAt;

}