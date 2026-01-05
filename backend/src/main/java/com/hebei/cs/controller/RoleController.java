package com.hebei.cs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Role;
import com.hebei.cs.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    public Result<List<Role>> getRoleList(
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        
        wrapper.eq(Role::getDeleted, 0)
                .orderByDesc(Role::getCreatedAt);
        
        List<Role> list = roleService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<Role> getRoleDetail(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.notFound();
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<Role> createRole(@RequestBody Role role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode())
                .eq(Role::getDeleted, 0);
        Role existing = roleService.getOne(wrapper);
        if (existing != null) {
            return Result.error("角色编码已存在");
        }
        
        // 设置默认值
        if (role.getStatus() == null) {
            role.setStatus(1); // 默认启用
        }
        
        boolean saved = roleService.save(role);
        if (!saved) {
            return Result.error("创建角色失败");
        }
        return Result.success("创建成功", role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role existing = roleService.getById(id);
        if (existing == null) {
            return Result.notFound();
        }
        
        // 如果角色编码改变，检查新编码是否已存在
        if (!existing.getCode().equals(role.getCode())) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getCode, role.getCode())
                    .eq(Role::getDeleted, 0);
            Role checkRole = roleService.getOne(wrapper);
            if (checkRole != null && !checkRole.getId().equals(id)) {
                return Result.error("角色编码已存在");
            }
        }
        
        role.setId(id);
        boolean updated = roleService.updateById(role);
        if (!updated) {
            return Result.error("更新角色失败");
        }
        return Result.success("更新成功", role);
    }

    /**
     * 删除角色（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        boolean removed = roleService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }
}

