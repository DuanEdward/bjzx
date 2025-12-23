package com.hebei.cs.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应结果
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Data
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {}

    public PageResult(List<T> list, Long total, Long page, Long pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.pages = (total + pageSize - 1) / pageSize;
    }

}