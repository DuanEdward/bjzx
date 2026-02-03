package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Certificate;
import com.hebei.cs.mapper.CertificateMapper;
import com.hebei.cs.service.CertificateService;
import com.hebei.cs.utils.ExcelUtil;
import com.hebei.cs.utils.ZipUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

/**
 * 证件Service实现类
 *
 * @author Hebei CS
 * @since 2024-12-13
 */
@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${file.access-path:/uploads}")
    private String accessPath;

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

    @Override
    public Map<String, Object> batchImportCertificatesFromZip(MultipartFile zipFile) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        int photoMatchedCount = 0;
        List<String> errorMessages = new ArrayList<>();
        List<Certificate> certificates = new ArrayList<>();
        String tempDir = System.getProperty("java.io.tmpdir") + "/certificate_import_" + System.currentTimeMillis();

        try {
            // 解析ZIP文件
            ZipUtil.ZipParseResult parseResult = ZipUtil.parseZipFile(zipFile, tempDir);
            certificates = parseResult.getCertificates();
            Map<String, byte[]> photoMap = parseResult.getPhotoMap();

            if (certificates.isEmpty()) {
                result.put("successCount", 0);
                result.put("failCount", 0);
                result.put("totalCount", 0);
                result.put("photoMatchedCount", 0);
                result.put("errorMessages", Arrays.asList("ZIP文件中未找到有效的证件数据"));
                return result;
            }

            // 导入证件数据并匹配图片
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

                    // 匹配并上传一寸照
                    byte[] photoData = ZipUtil.matchPhotoToCertificate(cert, photoMap);
                    if (photoData != null) {
                        String photoPath = uploadPhotoFile(photoData, cert.getNumber() + "_" + cert.getHolder());
                        if (photoPath != null) {
                            cert.setPhotoPath(photoPath);
                            photoMatchedCount++;
                        }
                    }

                    // 插入数据库
                    baseMapper.insert(cert);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("第" + (i + 1) + "行：导入失败 - " + e.getMessage());
                }
            }

        } catch (Exception e) {
            errorMessages.add("ZIP文件解析失败: " + e.getMessage());
            failCount = certificates.size();
        } finally {
            // 清理临时目录
            try {
                Path tempPath = Paths.get(tempDir);
                if (Files.exists(tempPath)) {
                    Files.walk(tempPath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            } catch (Exception e) {
                // 忽略清理失败
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("totalCount", certificates.size());
        result.put("photoMatchedCount", photoMatchedCount);
        result.put("errorMessages", errorMessages);

        return result;
    }

    /**
     * 上传图片文件
     *
     * @param photoData 图片字节数组
     * @param baseName 基础文件名（不含扩展名）
     * @return 图片访问路径，失败返回null
     */
    private String uploadPhotoFile(byte[] photoData, String baseName) {
        try {
            // 生成新的文件名（UUID + 扩展名，假设是JPG）
            String newFilename = UUID.randomUUID().toString().replace("-", "") + ".jpg";

            // 创建日期目录（按年月日）
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String dateDir = dateFormat.format(new Date());
            String fullUploadPath = uploadPath + "/" + dateDir;

            // 确保目录存在
            Path uploadDir = Paths.get(fullUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            String filePath = fullUploadPath + "/" + newFilename;
            File destFile = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                fos.write(photoData);
            }

            // 构建访问URL
            return accessPath + "/" + dateDir + "/" + newFilename;
        } catch (Exception e) {
            return null;
        }
    }
}