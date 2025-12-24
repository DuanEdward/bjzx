package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Member;
import com.hebei.cs.mapper.MemberMapper;
import com.hebei.cs.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 会员服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Member> getMemberPage(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Member> page,
            Integer level,
            Integer status) {
        return baseMapper.selectMemberPage(page, level, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyMember(Member member) {
        // 检查是否已经申请过（根据手机号或邮箱）
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Member> checkWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        checkWrapper.and(wrapper -> wrapper.eq(Member::getPhone, member.getPhone())
                                          .or()
                                          .eq(member.getEmail() != null && !member.getEmail().isEmpty(), 
                                              Member::getEmail, member.getEmail()))
                   .eq(Member::getDeleted, 0);
        
        Member existing = getOne(checkWrapper);
        if (existing != null) {
            // 如果已存在且状态为待审核，则更新申请信息
            if (existing.getStatus() != null && existing.getStatus() == 0) {
                member.setId(existing.getId());
                member.setStatus(0); // 待审核
                member.setCreatedAt(existing.getCreatedAt());
                member.setUpdatedAt(LocalDateTime.now());
                return updateById(member);
            } else {
                throw new RuntimeException("您已经申请过会员，请勿重复申请");
            }
        }
        
        // 设置默认值
        member.setStatus(0); // 待审核
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        member.setDeleted(0);
        
        return save(member);
    }
}

