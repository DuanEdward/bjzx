package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Banner;
import com.hebei.cs.mapper.BannerMapper;
import com.hebei.cs.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Banner服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getEnabledBanners() {
        return baseMapper.selectEnabledBanners();
    }

}