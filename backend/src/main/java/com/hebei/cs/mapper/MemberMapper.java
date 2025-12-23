package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 会员Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 分页查询会员
     *
     * @param page 分页对象
     * @param level 会员等级
     * @param status 审核状态
     * @return 会员列表
     */
    @Select("<script>" +
            "SELECT * FROM members " +
            "WHERE deleted = 0 " +
            "<if test='level != null'>AND level = #{level}</if> " +
            "<if test='status != null'>AND status = #{status}</if> " +
            "ORDER BY joined_at DESC, id DESC" +
            "</script>")
    Page<Member> selectMemberPage(Page<Member> page,
                                   @Param("level") Integer level,
                                   @Param("status") Integer status);

}