package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Certificate;
import com.hebei.cs.mapper.CertificateMapper;
import com.hebei.cs.service.CertificateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 证件Service实现类
 *
 * @author Hebei CS
 * @since 2024-12-13
 */
@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {

    @Override
    public IPage<Certificate> getCertificatePage(Integer current, Integer size, String name, String type, String number, String holder, Integer status) {
        Page<Certificate> page = new Page<>(current, size);
        LambdaQueryWrapper<Certificate> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.hasText(name), Certificate::getName, name)
                  .eq(StringUtils.hasText(type), Certificate::getType, type)
                  .like(StringUtils.hasText(number), Certificate::getNumber, number)
                  .like(StringUtils.hasText(holder), Certificate::getHolder, holder)
                  .eq(status != null, Certificate::getStatus, status)
                  .orderByDesc(Certificate::getCreateTime);

        return page(page, queryWrapper);
    }

    @Override
    public List<Certificate> getCertificateList(String name, String type, String number, String holder, Integer status) {
        return baseMapper.selectCertificateList(name, type, number, holder, status);
    }

    @Override
    public List<Certificate> getPublicCertificateList(String name, String type, String number, String holder, String idCard) {
        return baseMapper.selectPublicCertificateList(name, type, number, holder, idCard);
    }

    @Override
    public Certificate getCertificateById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Certificate createCertificate(Certificate certificate) {
        certificate.setCreateTime(LocalDateTime.now());
        certificate.setUpdateTime(LocalDateTime.now());
        certificate.setDeleted(0);

        // 设置默认状态为有效
        if (certificate.getStatus() == null) {
            certificate.setStatus(1);
        }

        // 设置默认为公开
        if (certificate.getIsPublic() == null) {
            certificate.setIsPublic(true);
        }

        baseMapper.insert(certificate);
        return certificate;
    }

    @Override
    public Certificate updateCertificate(Certificate certificate) {
        Certificate existingCertificate = baseMapper.selectById(certificate.getId());
        if (existingCertificate == null) {
            throw new RuntimeException("证件不存在");
        }

        // 复制属性，但不覆盖创建时间和创建人
        BeanUtils.copyProperties(certificate, existingCertificate, "createTime", "createBy", "deleted");
        existingCertificate.setUpdateTime(LocalDateTime.now());

        baseMapper.updateById(existingCertificate);
        return existingCertificate;
    }

    @Override
    public boolean deleteCertificate(Long id) {
        Certificate certificate = baseMapper.selectById(id);
        if (certificate == null) {
            return false;
        }

        certificate.setDeleted(1);
        certificate.setUpdateTime(LocalDateTime.now());

        return baseMapper.updateById(certificate) > 0;
    }

    @Override
    public boolean batchDeleteCertificates(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 创建实体用于更新
        Certificate updateEntity = new Certificate();
        updateEntity.setDeleted(1);
        updateEntity.setUpdateTime(LocalDateTime.now());

        return baseMapper.update(updateEntity,
            new LambdaQueryWrapper<Certificate>()
                .in(Certificate::getId, ids)
        ) > 0;
    }

    @Override
    public Map<String, Object> batchImportCertificates(List<Certificate> certificates) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < certificates.size(); i++) {
            Certificate cert = certificates.get(i);
            try {
                // 设置默认值
                if (cert.getStatus() == null) {
                    cert.setStatus(1);
                }
                if (cert.getIsPublic() == null) {
                    cert.setIsPublic(true);
                }
                cert.setCreateTime(LocalDateTime.now());
                cert.setUpdateTime(LocalDateTime.now());
                cert.setDeleted(0);

                // 验证必填字段
                if (cert.getName() == null || cert.getName().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：证件名称不能为空");
                    failCount++;
                    continue;
                }
                if (cert.getType() == null || cert.getType().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：证件类型不能为空");
                    failCount++;
                    continue;
                }
                if (cert.getNumber() == null || cert.getNumber().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：证件编号不能为空");
                    failCount++;
                    continue;
                }
                if (cert.getHolder() == null || cert.getHolder().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：持有人不能为空");
                    failCount++;
                    continue;
                }
                if (cert.getIssuingAuthority() == null || cert.getIssuingAuthority().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：发证机关不能为空");
                    failCount++;
                    continue;
                }

                baseMapper.insert(cert);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errorMessages.add("第" + (i + 1) + "行：导入失败 - " + e.getMessage());
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("totalCount", certificates.size());
        result.put("errorMessages", errorMessages);

        return result;
    }
}