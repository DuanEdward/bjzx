package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hebei.cs.entity.Contact;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联系我们 Mapper接口
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
}

