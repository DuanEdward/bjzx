package com.hebei.cs.controller;

import com.hebei.cs.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * 用户登录页面（GET请求）
     */
    @GetMapping("/login")
    public Result<?> loginPage() {
        return Result.error("登录接口只支持POST请求，请使用POST方法提交登录数据");
    }

    /**
     * 用户登录
     *
     * @param loginData 登录数据
     * @return JWT token
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            // 简单的模拟验证（实际项目中应该调用UserService）
            if ("admin".equals(username) && "admin123".equals(password)) {
                // 生成mock token（实际项目中应该使用JWT工具生成）
                String token = "mock-jwt-token-" + System.currentTimeMillis();

                Map<String, Object> result = new HashMap<>();
                result.put("token", token);

                Map<String, Object> user = new HashMap<>();
                user.put("id", 1);
                user.put("username", "admin");
                user.put("realname", "系统管理员");
                user.put("roles", new String[]{"admin"});
                result.put("user", user);

                return Result.success("登录成功", result);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success("登出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public Result<Map<String, Object>> getCurrentUser() {
        // Mock用户信息（实际项目中应该从token中解析）
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "admin");
        user.put("realname", "系统管理员");
        user.put("roles", new String[]{"admin"});

        return Result.success(user);
    }

}