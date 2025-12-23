package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 新闻实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@TableName("news")
public class News extends BaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    private String author;

    /**
     * 浏览量
     */
    private Integer views;

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
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建人ID
     */
    private Long createdBy;

    // Getters and Setters
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
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
        News news = (News) obj;
        return java.util.Objects.equals(title, news.title) &&
               java.util.Objects.equals(summary, news.summary) &&
               java.util.Objects.equals(content, news.content) &&
               java.util.Objects.equals(coverImage, news.coverImage) &&
               java.util.Objects.equals(author, news.author) &&
               java.util.Objects.equals(views, news.views) &&
               java.util.Objects.equals(categoryId, news.categoryId) &&
               java.util.Objects.equals(categoryName, news.categoryName) &&
               java.util.Objects.equals(status, news.status) &&
               java.util.Objects.equals(isTop, news.isTop) &&
               java.util.Objects.equals(publishedAt, news.publishedAt) &&
               java.util.Objects.equals(createdBy, news.createdBy);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), title, summary, content, coverImage, author, views, categoryId, categoryName, status, isTop, publishedAt, createdBy);
    }
}