package com.hebei.cs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.News;
import com.hebei.cs.entity.NewsCategory;
import com.hebei.cs.mapper.NewsCategoryMapper;
import com.hebei.cs.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsCategoryMapper newsCategoryMapper;

    /**
     * 获取新闻列表（前台/后台通用）
     *
     * 支持通过分类ID或分类编码过滤：
     * - categoryId：分类ID
     * - category：分类编码（society/industry/notice/policy/study 等）
     */
    @GetMapping("/list")
    public Result<PageResult<News>> getNewsList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        // 如果传入分类编码，则转换为分类ID
        if (categoryId == null && category != null && !category.isEmpty()) {
            LambdaQueryWrapper<NewsCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NewsCategory::getCode, category)
                    .eq(NewsCategory::getDeleted, 0);
            NewsCategory newsCategory = newsCategoryMapper.selectOne(wrapper);
            if (newsCategory != null) {
                categoryId = newsCategory.getId();
            }
        }

        Page<News> pageParam = new Page<>(page, pageSize);
        Page<News> result = newsService.getNewsPage(pageParam, categoryId, keyword, status);

        PageResult<News> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );

        return Result.success(pageResult);
    }

    /**
     * 获取新闻详情
     */
    @GetMapping("/{id}")
    public Result<News> getNewsDetail(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news == null) {
            return Result.notFound();
        }
        return Result.success(news);
    }

    /**
     * 增加新闻浏览量
     */
    @PostMapping("/{id}/views")
    public Result<?> incrementViews(@PathVariable Long id) {
        boolean success = newsService.incrementViews(id);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 创建新闻（后台管理）
     */
    @PostMapping
    public Result<News> createNews(@RequestBody News news) {
        // 新建新闻默认浏览量为0
        if (news.getViews() == null) {
            news.setViews(0);
        }
        boolean saved = newsService.save(news);
        if (!saved) {
            return Result.error("创建新闻失败");
        }
        return Result.success("创建成功", news);
    }

    /**
     * 更新新闻（后台管理）
     */
    @PutMapping("/{id}")
    public Result<News> updateNews(@PathVariable Long id, @RequestBody News news) {
        News existing = newsService.getById(id);
        if (existing == null) {
            return Result.notFound();
        }
        news.setId(id);
        boolean updated = newsService.updateById(news);
        if (!updated) {
            return Result.error("更新新闻失败");
        }
        return Result.success("更新成功", news);
    }

    /**
     * 删除新闻（后台管理，逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteNews(@PathVariable Long id) {
        boolean removed = newsService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

}