package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Resource;
import com.hebei.cs.mapper.ResourceMapper;
import com.hebei.cs.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Resource> getResourcePage(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Resource> page,
            Long categoryId) {
        return baseMapper.selectResourcePage(page, categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementDownloadCount(Long id) {
        Resource resource = getById(id);
        if (resource == null) {
            return false;
        }
        if (resource.getDownloadCount() == null) {
            resource.setDownloadCount(0);
        }
        resource.setDownloadCount(resource.getDownloadCount() + 1);
        return updateById(resource);
    }
}

