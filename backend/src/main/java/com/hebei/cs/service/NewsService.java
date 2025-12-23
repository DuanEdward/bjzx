package com.hebei.cs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.News;

/**
 * 新闻服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface NewsService extends IService<News> {

    /**
     * 分页查询新闻
     *
     * @param page 分页对象
     * @param categoryId 分类ID
     * @param keyword 关键词
     * @return 新闻分页列表
     */
    Page<News> getNewsPage(Page<News> page, Long categoryId, String keyword);

    /**
     * 发布新闻
     *
     * @param id 新闻ID
     * @return 是否成功
     */
    boolean publishNews(Long id);

    /**
     * 置顶新闻
     *
     * @param id 新闻ID
     * @return 是否成功
     */
    boolean topNews(Long id);

    /**
     * 增加浏览量
     *
     * @param id 新闻ID
     * @return 是否成功
     */
    boolean incrementViews(Long id);

}