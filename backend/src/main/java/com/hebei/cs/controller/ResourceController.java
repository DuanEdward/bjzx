package com.hebei.cs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Resource;
import com.hebei.cs.entity.ResourceCategory;
import com.hebei.cs.mapper.ResourceCategoryMapper;
import com.hebei.cs.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资源控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceCategoryMapper resourceCategoryMapper;

    /**
     * 获取资源列表
     */
    @GetMapping("/list")
    public Result<PageResult<Resource>> getResourcesList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category) {
        
        // 如果传入分类编码，则转换为分类ID
        if (categoryId == null && category != null && !category.isEmpty()) {
            LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ResourceCategory::getCode, category)
                   .eq(ResourceCategory::getDeleted, 0);
            ResourceCategory resourceCategory = resourceCategoryMapper.selectOne(wrapper);
            if (resourceCategory != null) {
                categoryId = resourceCategory.getId();
            }
        }
        
        Page<Resource> pageParam = new Page<>(page, pageSize);
        Page<Resource> result = resourceService.getResourcePage(pageParam, categoryId);
        
        PageResult<Resource> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/{id}")
    public Result<Resource> getResourceDetail(@PathVariable Long id) {
        Resource resource = resourceService.getById(id);
        if (resource == null) {
            return Result.notFound();
        }
        return Result.success(resource);
    }

    /**
     * 增加下载次数
     */
    @PostMapping("/{id}/download")
    public Result<?> incrementDownload(@PathVariable Long id) {
        boolean success = resourceService.incrementDownloadCount(id);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 创建资源（后台管理）
     */
    @PostMapping
    public Result<Resource> createResource(@RequestBody Resource resource) {
        // 新建资源默认下载次数为0
        if (resource.getDownloadCount() == null) {
            resource.setDownloadCount(0);
        }
        boolean saved = resourceService.save(resource);
        if (!saved) {
            return Result.error("创建资源失败");
        }
        return Result.success("创建成功", resource);
    }

    /**
     * 更新资源（后台管理）
     */
    @PutMapping("/{id}")
    public Result<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        Resource existing = resourceService.getById(id);
        if (existing == null) {
            return Result.notFound();
        }
        resource.setId(id);
        boolean updated = resourceService.updateById(resource);
        if (!updated) {
            return Result.error("更新资源失败");
        }
        return Result.success("更新成功", resource);
    }

    /**
     * 删除资源（后台管理，逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteResource(@PathVariable Long id) {
        boolean removed = resourceService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }
}

