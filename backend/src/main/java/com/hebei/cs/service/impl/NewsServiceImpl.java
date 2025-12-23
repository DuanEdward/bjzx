package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.News;
import com.hebei.cs.mapper.NewsMapper;
import com.hebei.cs.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 新闻服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Override
    public Page<News> getNewsPage(Page<News> page, Long categoryId, String keyword) {
        return baseMapper.selectNewsPage(page, categoryId, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishNews(Long id) {
        News news = getById(id);
        if (news == null) {
            return false;
        }
        news.setStatus(1);
        news.setPublishedAt(LocalDateTime.now());
        return updateById(news);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean topNews(Long id) {
        News news = getById(id);
        if (news == null) {
            return false;
        }
        news.setIsTop(news.getIsTop() == 1 ? 0 : 1);
        return updateById(news);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViews(Long id) {
        News news = getById(id);
        if (news == null) {
            return false;
        }
        news.setViews(news.getViews() + 1);
        return updateById(news);
    }

}