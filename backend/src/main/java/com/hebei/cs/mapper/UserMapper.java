package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hebei.cs.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT u.*, GROUP_CONCAT(r.code) as roles FROM users u " +
            "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.username = #{username} AND u.deleted = 0 " +
            "GROUP BY u.id")
    User findByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Select("SELECT r.code FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> findRolesByUserId(@Param("userId") Long userId);

}