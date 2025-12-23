package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 资源Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 分页查询资源（带分类名）
     *
     * @param page 分页对象
     * @param categoryId 分类ID
     * @return 资源列表
     */
    @Select("<script>" +
            "SELECT r.*, c.name as category_name FROM resources r " +
            "LEFT JOIN resource_categories c ON r.category_id = c.id " +
            "WHERE r.deleted = 0 AND r.status = 1 " +
            "<if test='categoryId != null'>AND r.category_id = #{categoryId}</if> " +
            "ORDER BY r.created_at DESC, r.id DESC" +
            "</script>")
    Page<Resource> selectResourcePage(Page<Resource> page, @Param("categoryId") Long categoryId);

}