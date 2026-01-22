package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.entity.Activity;
import com.hebei.cs.entity.Banner;
import com.hebei.cs.entity.News;
import com.hebei.cs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页服务实现类
 *
 * @author Beijing ZXJJ
 * @since 2024-01-10
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ActivityService activityService;

    @Override
    public Map<String, Object> getHomeData() {
        Map<String, Object> result = new HashMap<>();
        result.put("banners", getBanners());
        result.put("latestNews", getLatestNews(5));
        result.put("latestActivities", getLatestActivities(6));
        result.put("societyInfo", getSocietyInfo());
        return result;
    }

    @Override
    public List<Banner> getBanners() {
        return bannerService.getEnabledBanners();
    }

    @Override
    public List<News> getLatestNews(Integer pageSize) {
        Page<News> page = new Page<>(1, pageSize);
        Page<News> result = newsService.getNewsPage(page, null, null, null);
        return result.getRecords();
    }

    @Override
    public List<Activity> getLatestActivities(Integer pageSize) {
        Page<Activity> page = new Page<>(1, pageSize);
        Page<Activity> result = activityService.getActivityPage(page, null, null, null);
        return result.getRecords();
    }

    @Override
    public Map<String, Object> getSocietyInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "北京中兴建机职业技能鉴定中心");
        info.put("englishName", "Beijing Zhongxing Construction Machinery Vocational Skills Appraisal Center");
        info.put("founded", "2020");
        info.put("description", "北京中兴建机职业技能鉴定中心是经相关部门批准设立的职业技能鉴定机构，致力于为建筑机械行业提供专业的职业技能鉴定服务。");
        info.put("memberCount", 856);
        info.put("activityCount", 28);
        return info;
    }

}