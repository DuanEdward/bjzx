package com.hebei.cs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Member;
import com.hebei.cs.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会员控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 申请会员
     */
    @PostMapping("/apply")
    public Result<?> applyMember(@RequestBody MemberApplicationRequest request) {
        try {
            Member member = new Member();
            member.setName(request.getName());
            member.setGender(request.getGender());
            member.setBirthDate(request.getBirthDate());
            member.setTitle(request.getTitle());
            member.setCompany(request.getCompany());
            member.setPhone(request.getPhone());
            member.setEmail(request.getEmail());
            member.setResearch(request.getResearch());
            member.setLevel(request.getLevel());
            member.setIntroduction(request.getIntroduction());
            
            boolean success = memberService.applyMember(member);
            if (success) {
                return Result.success("申请提交成功，请等待审核");
            } else {
                return Result.error("申请提交失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("申请提交失败：" + e.getMessage());
        }
    }

    /**
     * 获取会员列表
     */
    @GetMapping("/list")
    public Result<PageResult<Member>> getMemberList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer status) {
        
        Page<Member> pageParam = new Page<>(page, pageSize);
        Page<Member> result = memberService.getMemberPage(pageParam, level, status);
        
        PageResult<Member> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 会员申请请求对象
     */
    public static class MemberApplicationRequest {
        private String name;
        private String gender;
        private java.time.LocalDate birthDate;
        private String title;
        private String company;
        private String phone;
        private String email;
        private String research;
        private Integer level;
        private String introduction;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public java.time.LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(java.time.LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
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

        public String getResearch() {
            return research;
        }

        public void setResearch(String research) {
            this.research = research;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }
    }
}

