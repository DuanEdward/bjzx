package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Activity;
import com.hebei.cs.entity.ActivityRegistration;
import com.hebei.cs.mapper.ActivityMapper;
import com.hebei.cs.mapper.ActivityRegistrationMapper;
import com.hebei.cs.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 活动服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivityRegistrationMapper activityRegistrationMapper;

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Activity> getActivityPage(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Activity> page,
            Integer status,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {

        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getDeleted, 0);
        
        // 状态过滤
        if (status != null) {
            queryWrapper.eq(Activity::getStatus, status);
        }
        
        // 日期范围过滤
        if (startDate != null) {
            queryWrapper.ge(Activity::getStartTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            queryWrapper.le(Activity::getEndTime, endDate.atTime(23, 59, 59));
        }
        
        queryWrapper.orderByDesc(Activity::getStartTime)
                   .orderByDesc(Activity::getId);
        
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerActivity(Long activityId, String name, String phone, String email, String company, String remark) {
        // 检查活动是否存在
        Activity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        
        // 检查是否已经报名过（根据手机号或邮箱）
        LambdaQueryWrapper<ActivityRegistration> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ActivityRegistration::getActivityId, activityId)
                   .and(wrapper -> wrapper.eq(ActivityRegistration::getPhone, phone)
                                          .or()
                                          .eq(StringUtils.hasText(email), ActivityRegistration::getEmail, email))
                   .eq(ActivityRegistration::getDeleted, 0);
        
        ActivityRegistration existing = activityRegistrationMapper.selectOne(checkWrapper);
        if (existing != null) {
            throw new RuntimeException("您已经报名过该活动");
        }
        
        // 创建报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setName(name);
        registration.setPhone(phone);
        registration.setEmail(email);
        registration.setCompany(company);
        registration.setRemark(remark);
        registration.setStatus(0); // 待审核
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());
        registration.setDeleted(0);
        
        int insertResult = activityRegistrationMapper.insert(registration);
        
        // 更新活动的当前参与人数
        if (insertResult > 0 && activity.getCurrentParticipants() != null) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
            updateById(activity);
        } else if (insertResult > 0) {
            activity.setCurrentParticipants(1);
            updateById(activity);
        }
        
        return insertResult > 0;
    }

    @Override
    public boolean updateActivityStatus(Long activityId, Integer status) {
        Activity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        activity.setStatus(status);
        return updateById(activity);
    }

    @Override
    public boolean openRegistration(Long activityId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        activity.setIsRegistrationOpen(1);
        return updateById(activity);
    }

    @Override
    public boolean closeRegistration(Long activityId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        activity.setIsRegistrationOpen(0);
        return updateById(activity);
    }
}