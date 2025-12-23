package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hebei.cs.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Banner Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 查询启用的Banner列表
     *
     * @return Banner列表
     */
    @Select("SELECT * FROM banners WHERE status = 1 AND deleted = 0 ORDER BY sort ASC, id DESC")
    java.util.List<Banner> selectEnabledBanners();

}