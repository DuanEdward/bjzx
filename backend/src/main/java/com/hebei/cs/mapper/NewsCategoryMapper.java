package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hebei.cs.entity.NewsCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 新闻分类Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface NewsCategoryMapper extends BaseMapper<NewsCategory> {

}