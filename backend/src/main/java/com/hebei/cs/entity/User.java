package com.hebei.cs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hebei.cs.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@TableName("users")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realname;

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
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 角色列表（非数据库字段）
     */
    @TableField(exist = false)
    private String[] roles;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        User user = (User) obj;
        return java.util.Objects.equals(username, user.username) &&
               java.util.Objects.equals(password, user.password) &&
               java.util.Objects.equals(realname, user.realname) &&
               java.util.Objects.equals(email, user.email) &&
               java.util.Objects.equals(phone, user.phone) &&
               java.util.Objects.equals(avatar, user.avatar) &&
               java.util.Objects.equals(status, user.status) &&
               java.util.Objects.equals(lastLoginAt, user.lastLoginAt) &&
               java.util.Arrays.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), username, password, realname, email, phone, avatar, status, lastLoginAt, java.util.Arrays.hashCode(roles));
    }
}