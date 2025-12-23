package com.hebei.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.User;

/**
 * 用户服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);

}