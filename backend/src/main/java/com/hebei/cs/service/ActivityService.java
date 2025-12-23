package com.hebei.cs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.Activity;

/**
 * 活动服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 分页查询活动
     */
    Page<Activity> getActivityPage(Page<Activity> page, Integer status, java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * 活动报名
     */
    boolean registerActivity(Long activityId, String name, String phone, String email, String company, String remark);

    /**
     * 更新活动状态
     */
    boolean updateActivityStatus(Long activityId, Integer status);

    /**
     * 开放活动报名
     */
    boolean openRegistration(Long activityId);

    /**
     * 关闭活动报名
     */
    boolean closeRegistration(Long activityId);

}