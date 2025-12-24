package com.hebei.cs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Activity;
import com.hebei.cs.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 活动控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 获取活动列表
     */
    @GetMapping("/list")
    public Result<PageResult<Activity>> getActivitiesList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Integer statusInt = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusInt = Integer.parseInt(status);
            } catch (NumberFormatException e) {
                // 忽略无效的状态值
            }
        }
        
        Page<Activity> pageParam = new Page<>(page, pageSize);
        Page<Activity> result = activityService.getActivityPage(pageParam, statusInt, startDate, endDate);
        
        PageResult<Activity> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.notFound();
        }
        return Result.success(activity);
    }

    /**
     * 活动报名
     */
    @PostMapping("/{id}/register")
    public Result<?> registerActivity(
            @PathVariable Long id,
            @RequestBody ActivityRegistrationRequest request) {
        
        // 检查活动是否存在
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.notFound();
        }
        
        // 检查活动是否开放报名
        if (activity.getIsRegistrationOpen() == null || activity.getIsRegistrationOpen() == 0) {
            return Result.error("该活动暂未开放报名");
        }
        
        // 检查报名是否已截止
        if (activity.getRegistrationDeadline() != null && 
            activity.getRegistrationDeadline().isBefore(java.time.LocalDateTime.now())) {
            return Result.error("报名已截止");
        }
        
        // 检查是否已满员
        if (activity.getMaxParticipants() != null && 
            activity.getCurrentParticipants() != null &&
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return Result.error("活动报名已满");
        }
        
        // 执行报名
        boolean success = activityService.registerActivity(
                id,
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                request.getCompany(),
                request.getRemark()
        );
        
        if (success) {
            return Result.success("报名成功");
        } else {
            return Result.error("报名失败");
        }
    }

    /**
     * 活动报名请求对象
     */
    public static class ActivityRegistrationRequest {
        private String name;
        private String phone;
        private String email;
        private String company;
        private String remark;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}

