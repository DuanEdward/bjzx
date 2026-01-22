package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 新闻Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {

    /**
     * 分页查询新闻（带分类名）
     *
     * @param page 分页对象
     * @param categoryId 分类ID
     * @param keyword 关键词
     * @return 新闻列表
     */
    @Select("<script>" +
            "SELECT n.*, c.name as category_name FROM news n " +
            "LEFT JOIN news_categories c ON n.category_id = c.id " +
            "WHERE n.deleted = 0 " +
            "<if test='categoryId != null'>AND n.category_id = #{categoryId}</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND (n.title LIKE CONCAT('%', #{keyword}, '%') OR n.content LIKE CONCAT('%', #{keyword}, '%'))</if> " +
            "<if test='status != null'>AND n.status = #{status}</if> " +
            "ORDER BY n.is_top DESC, n.published_at DESC, n.id DESC" +
            "</script>")
    Page<News> selectNewsPage(Page<News> page, @Param("categoryId") Long categoryId, @Param("keyword") String keyword, @Param("status") Integer status);

}