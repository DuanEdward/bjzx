package com.hebei.cs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.PageResult;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Contact;
import com.hebei.cs.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 联系我们控制器
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * 提交联系我们（前端）
     */
    @PostMapping
    public Result<?> createContact(@RequestBody Contact contact) {
        contact.setStatus(0); // 默认待处理
        boolean saved = contactService.save(contact);
        return saved ? Result.success("提交成功") : Result.error("提交失败");
    }

    /**
     * 获取联系我们列表（后台管理）
     */
    @GetMapping("/list")
    public Result<PageResult<Contact>> getContactList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<Contact> page = new Page<>(current, size);
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Contact::getStatus, status);
        }
        wrapper.orderByDesc(Contact::getCreatedAt);
        Page<Contact> result = contactService.page(page, wrapper);
        PageResult<Contact> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        return Result.success(pageResult);
    }

    /**
     * 获取联系我们详情
     */
    @GetMapping("/{id}")
    public Result<Contact> getContactDetail(@PathVariable Long id) {
        Contact contact = contactService.getById(id);
        if (contact == null) {
            return Result.notFound();
        }
        return Result.success(contact);
    }

    /**
     * 更新联系我们状态（后台管理）
     */
    @PutMapping("/{id}")
    public Result<?> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        Contact existing = contactService.getById(id);
        if (existing == null) {
            return Result.notFound();
        }
        contact.setId(id);
        boolean updated = contactService.updateById(contact);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除联系我们（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteContact(@PathVariable Long id) {
        boolean removed = contactService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }
}

