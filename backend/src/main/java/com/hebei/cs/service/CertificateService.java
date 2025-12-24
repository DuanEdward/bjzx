package com.hebei.cs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hebei.cs.entity.Certificate;

import java.util.List;
import java.util.Map;

/**
 * 证件Service接口
 *
 * @author Hebei CS
 * @since 2024-12-13
 */
public interface CertificateService extends IService<Certificate> {

    /**
     * 分页查询证件列表
     */
    IPage<Certificate> getCertificatePage(Integer current, Integer size, String name, String type, String number, String holder, Integer status);

    /**
     * 条件查询证件列表
     */
    List<Certificate> getCertificateList(String name, String type, String number, String holder, Integer status);

    /**
     * 查询公开证件列表
     */
    List<Certificate> getPublicCertificateList(String name, String type, String number, String holder, String idCard);

    /**
     * 根据ID获取证件详情
     */
    Certificate getCertificateById(Long id);

    /**
     * 创建证件
     */
    Certificate createCertificate(Certificate certificate);

    /**
     * 更新证件
     */
    Certificate updateCertificate(Certificate certificate);

    /**
     * 删除证件
     */
    boolean deleteCertificate(Long id);

    /**
     * 批量删除证件
     */
    boolean batchDeleteCertificates(List<Long> ids);

    /**
     * 批量导入证件
     */
    Map<String, Object> batchImportCertificates(List<Certificate> certificates);
}