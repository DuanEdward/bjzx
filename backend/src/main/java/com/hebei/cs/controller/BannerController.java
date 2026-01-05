package com.hebei.cs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Banner;
import com.hebei.cs.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Banner控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 获取所有Banner列表（后台管理）
     */
    @GetMapping("/list")
    public Result<List<Banner>> getBannerList() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getDeleted, 0)
                .orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getCreatedAt);
        List<Banner> list = bannerService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取Banner详情
     */
    @GetMapping("/{id}")
    public Result<Banner> getBannerDetail(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.notFound();
        }
        return Result.success(banner);
    }

    /**
     * 创建Banner
     */
    @PostMapping
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        boolean saved = bannerService.save(banner);
        if (!saved) {
            return Result.error("创建Banner失败");
        }
        return Result.success("创建成功", banner);
    }

    /**
     * 更新Banner
     */
    @PutMapping("/{id}")
    public Result<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        Banner existing = bannerService.getById(id);
        if (existing == null) {
            return Result.notFound();
        }
        banner.setId(id);
        boolean updated = bannerService.updateById(banner);
        if (!updated) {
            return Result.error("更新Banner失败");
        }
        return Result.success("更新成功", banner);
    }

    /**
     * 删除Banner（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteBanner(@PathVariable Long id) {
        boolean removed = bannerService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

}

