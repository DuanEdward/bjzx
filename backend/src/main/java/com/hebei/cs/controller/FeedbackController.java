package com.hebei.cs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Feedback;
import com.hebei.cs.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 投诉反馈控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 提交投诉反馈（前端）
     */
    @PostMapping
    public Result<?> createFeedback(@RequestBody Feedback feedback) {
        feedback.setStatus(0); // 默认待处理
        boolean saved = feedbackService.save(feedback);
        return saved ? Result.success("提交成功") : Result.error("提交失败");
    }

    /**
     * 获取投诉反馈列表（后台管理）
     */
    @GetMapping("/list")
    public Result<PageResult<Feedback>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<Feedback> page = new Page<>(current, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreatedAt);
        Page<Feedback> result = feedbackService.page(page, wrapper);
        PageResult<Feedback> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        return Result.success(pageResult);
    }

    /**
     * 获取投诉反馈详情
     */
    @GetMapping("/{id}")
    public Result<Feedback> getFeedbackDetail(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null) {
            return Result.notFound();
        }
        return Result.success(feedback);
    }

    /**
     * 更新投诉反馈状态（后台管理）
     */
    @PutMapping("/{id}")
    public Result<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        Feedback existing = feedbackService.getById(id);
        if (existing == null) {
            return Result.notFound();
        }
        feedback.setId(id);
        boolean updated = feedbackService.updateById(feedback);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除投诉反馈（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFeedback(@PathVariable Long id) {
        boolean removed = feedbackService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }
}

