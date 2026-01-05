package com.hebei.cs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.Member;

/**
 * 会员服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface MemberService extends IService<Member> {

    /**
     * 分页查询会员
     *
     * @param page 分页对象
     * @param level 会员等级
     * @param status 审核状态
     * @return 会员分页列表
     */
    Page<Member> getMemberPage(Page<Member> page, Integer level, Integer status);

    /**
     * 申请会员
     *
     * @param member 会员信息
     * @return 是否成功
     */
    boolean applyMember(Member member);

    /**
     * 审核会员申请
     *
     * @param memberId 会员ID
     * @param status 审核状态（1-通过，2-拒绝）
     * @param reviewRemark 审核意见
     * @param reviewedBy 审核人ID（可选）
     * @return 是否成功
     */
    boolean reviewMember(Long memberId, Integer status, String reviewRemark, Long reviewedBy);

}

