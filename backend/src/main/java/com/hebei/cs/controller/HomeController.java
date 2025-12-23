package com.hebei.cs.controller;

import com.hebei.cs.common.Result;
import com.hebei.cs.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 首页控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 获取Banner列表
     */
    @GetMapping("/banners")
    public Result<?> getBanners() {
        return Result.success(homeService.getBanners());
    }

    /**
     * 获取最新新闻
     *
     * @param pageSize 每页数量
     */
    @GetMapping("/latest-news")
    public Result<?> getLatestNews(Integer page, Integer pageSize) {
        if (pageSize == null) pageSize = 5;
        return Result.success(homeService.getLatestNews(pageSize));
    }

    /**
     * 获取最新活动
     *
     * @param pageSize 每页数量
     */
    @GetMapping("/latest-activities")
    public Result<?> getLatestActivities(Integer page, Integer pageSize) {
        if (pageSize == null) pageSize = 6;
        return Result.success(homeService.getLatestActivities(pageSize));
    }

    /**
     * 获取学会简介
     */
    @GetMapping("/society-info")
    public Result<?> getSocietyInfo() {
        return Result.success(homeService.getSocietyInfo());
    }

}