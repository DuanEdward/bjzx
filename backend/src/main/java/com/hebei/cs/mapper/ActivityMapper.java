package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 活动Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    /**
     * 分页查询活动
     *
     * @param page 分页对象
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 活动列表
     */
    @Select("SELECT * FROM activities WHERE deleted = 0 ORDER BY start_time DESC, id DESC")
    Page<Activity> selectActivityPage(Page<Activity> page,
                                        @Param("status") Integer status,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

}