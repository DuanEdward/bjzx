package com.hebei.cs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.Resource;

/**
 * 资源服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface ResourceService extends IService<Resource> {

    /**
     * 分页查询资源
     *
     * @param page 分页对象
     * @param categoryId 分类ID
     * @return 资源分页列表
     */
    Page<Resource> getResourcePage(Page<Resource> page, Long categoryId);

    /**
     * 增加下载次数
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean incrementDownloadCount(Long id);

}

