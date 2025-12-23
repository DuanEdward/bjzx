package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 活动实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@TableName("activities")
public class Activity extends BaseEntity {

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动摘要
     */
    private String summary;

    /**
     * 活动内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime registrationDeadline;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;

    /**
     * 当前参与人数
     */
    private Integer currentParticipants;

    /**
     * 活动状态（0-未开始，1-进行中，2-已结束）
     */
    private Integer status;

    /**
     * 是否开放报名（0-否，1-是）
     */
    private Integer isRegistrationOpen;

    /**
     * 创建人ID
     */
    private Long createdBy;

    // Getter and Setter methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsRegistrationOpen() {
        return isRegistrationOpen;
    }

    public void setIsRegistrationOpen(Integer isRegistrationOpen) {
        this.isRegistrationOpen = isRegistrationOpen;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Activity activity = (Activity) obj;
        return java.util.Objects.equals(title, activity.title) &&
                java.util.Objects.equals(startTime, activity.startTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), title, startTime);
    }
}