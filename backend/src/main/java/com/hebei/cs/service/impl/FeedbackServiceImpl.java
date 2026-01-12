package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Feedback;
import com.hebei.cs.mapper.FeedbackMapper;
import com.hebei.cs.service.FeedbackService;
import org.springframework.stereotype.Service;

/**
 * 投诉反馈服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
}

