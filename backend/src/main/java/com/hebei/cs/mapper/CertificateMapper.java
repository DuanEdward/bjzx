package com.hebei.cs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hebei.cs.entity.Certificate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 证件Mapper接口
 *
 * @author Hebei CS
 * @since 2024-12-13
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {

    /**
     * 查询证件列表（支持条件查询）
     */
    List<Certificate> selectCertificateList(@Param("name") String name,
                                             @Param("type") String type,
                                             @Param("number") String number,
                                             @Param("holder") String holder,
                                             @Param("status") Integer status);

    /**
     * 查询公开的证件列表
     */
    List<Certificate> selectPublicCertificateList(@Param("name") String name,
                                                   @Param("type") String type,
                                                   @Param("number") String number,
                                                   @Param("holder") String holder);
}