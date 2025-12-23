package com.hebei.cs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.Banner;

import java.util.List;

/**
 * Banner服务接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取启用的Banner列表
     *
     * @return Banner列表
     */
    List<Banner> getEnabledBanners();

}