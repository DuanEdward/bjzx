package com.hebei.cs.service;

import com.hebei.cs.entity.Activity;
import com.hebei.cs.entity.Banner;
import com.hebei.cs.entity.News;

import java.util.List;
import java.util.Map;

/**
 * 首页服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface HomeService {

    /**
     * 获取首页数据
     *
     * @return 首页数据
     */
    Map<String, Object> getHomeData();

    /**
     * 获取Banner列表
     *
     * @return Banner列表
     */
    List<Banner> getBanners();

    /**
     * 获取最新新闻
     *
     * @param pageSize 每页数量
     * @return 新闻列表
     */
    List<News> getLatestNews(Integer pageSize);

    /**
     * 获取最新活动
     *
     * @param pageSize 每页数量
     * @return 活动列表
     */
    List<Activity> getLatestActivities(Integer pageSize);

    /**
     * 获取学会简介
     *
     * @return 学会简介信息
     */
    Map<String, Object> getSocietyInfo();

}