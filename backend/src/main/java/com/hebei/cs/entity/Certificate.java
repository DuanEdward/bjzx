package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 证件实体类
 *
 * @author Hebei CS
 * @since 2024-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("certificate")
public class Certificate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 证件名称
     */
    @TableField("name")
    private String name;

    /**
     * 证件类型
     */
    @TableField("type")
    private String type;

    /**
     * 证件编号
     */
    @TableField("number")
    private String number;

    /**
     * 发证机关
     */
    @TableField("issuing_authority")
    private String issuingAuthority;

    /**
     * 发证日期
     */
    @TableField("issue_date")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDateTime issueDate;

    /**
     * 有效期开始
     */
    @TableField("valid_from")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDateTime validFrom;

    /**
     * 有效期结束
     */
    @TableField("valid_until")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDateTime validUntil;

    /**
     * 证件状态 (0:过期, 1:有效, 2:即将过期)
     */
    @TableField("status")
    private Integer status;

    /**
     * 证书持有人
     */
    @TableField("holder")
    private String holder;

    /**
     * 性别
     */
    @TableField("gender")
    private String gender;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 岗位名称
     */
    @TableField("position")
    private String position;

    /**
     * 技能等级
     */
    @TableField("skill_level")
    private String skillLevel;

    /**
     * 证书持有人联系方式
     */
    @TableField("holder_contact")
    private String holderContact;

    /**
     * 证件描述
     */
    @TableField("description")
    private String description;

    /**
     * 附件路径
     */
    @TableField("attachment_path")
    private String attachmentPath;

    /**
     * 是否公开显示
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标记
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}