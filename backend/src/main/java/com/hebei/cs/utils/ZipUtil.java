package com.hebei.cs.utils;

import com.hebei.cs.entity.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ZIP文件工具类
 * 用于解析包含Excel和图片的ZIP文件
 *
 * @author Beijing ZXJJ
 * @since 2026-02-03
 */
public class ZipUtil {

    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * ZIP文件解析结果
     */
    public static class ZipParseResult {
        private List<Certificate> certificates;
        private Map<String, byte[]> photoMap; // key: 证件编号或持有人姓名, value: 图片字节数组

        public ZipParseResult(List<Certificate> certificates, Map<String, byte[]> photoMap) {
            this.certificates = certificates;
            this.photoMap = photoMap;
        }

        public List<Certificate> getCertificates() {
            return certificates;
        }

        public Map<String, byte[]> getPhotoMap() {
            return photoMap;
        }
    }

    /**
     * 解析ZIP文件，提取Excel和图片
     *
     * @param zipFile ZIP文件
     * @param tempDir 临时目录
     * @return 解析结果（证件列表和图片映射）
     */
    public static ZipParseResult parseZipFile(MultipartFile zipFile, String tempDir) throws Exception {
        List<Certificate> certificates = new ArrayList<>();
        Map<String, byte[]> photoMap = new HashMap<>();

        // 创建临时目录
        Path tempPath = Paths.get(tempDir);
        if (!Files.exists(tempPath)) {
            Files.createDirectories(tempPath);
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            MultipartFile excelFile = null;
            List<Map.Entry<String, byte[]>> photos = new ArrayList<>();

            // 遍历ZIP文件中的所有条目
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                
                // 跳过目录
                if (entry.isDirectory()) {
                    continue;
                }

                // 读取文件内容到字节数组
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                byte[] fileData = baos.toByteArray();
                zipInputStream.closeEntry();

                // 判断文件类型
                String lowerName = entryName.toLowerCase();
                if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
                    // Excel文件
                    excelFile = new ByteArrayMultipartFile(fileData, entryName);
                } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || 
                           lowerName.endsWith(".png") || lowerName.endsWith(".gif")) {
                    // 图片文件
                    String fileName = new File(entryName).getName();
                    // 移除扩展名作为key
                    String key = fileName.substring(0, fileName.lastIndexOf('.'));
                    photos.add(new AbstractMap.SimpleEntry<>(key, fileData));
                }
            }

            // 解析Excel文件
            if (excelFile != null) {
                certificates = ExcelUtil.parseCertificateExcel(excelFile);
            } else {
                throw new RuntimeException("ZIP文件中未找到Excel文件");
            }

            // 构建图片映射表
            for (Map.Entry<String, byte[]> photo : photos) {
                photoMap.put(photo.getKey(), photo.getValue());
            }

            log.info("ZIP文件解析完成：证件数量={}, 图片数量={}", certificates.size(), photoMap.size());
        }

        return new ZipParseResult(certificates, photoMap);
    }

    /**
     * 匹配图片到证件
     * 优先使用证件编号匹配，如果找不到则使用持有人姓名匹配
     *
     * @param certificate 证件
     * @param photoMap 图片映射表
     * @return 匹配的图片字节数组，如果未匹配到返回null
     */
    public static byte[] matchPhotoToCertificate(Certificate certificate, Map<String, byte[]> photoMap) {
        if (photoMap == null || photoMap.isEmpty()) {
            return null;
        }

        // 优先使用证件编号匹配
        if (certificate.getNumber() != null && !certificate.getNumber().trim().isEmpty()) {
            String number = certificate.getNumber().trim();
            // 尝试精确匹配
            if (photoMap.containsKey(number)) {
                return photoMap.get(number);
            }
            // 尝试忽略大小写匹配
            for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(number)) {
                    return entry.getValue();
                }
            }
        }

        // 使用持有人姓名匹配
        if (certificate.getHolder() != null && !certificate.getHolder().trim().isEmpty()) {
            String holder = certificate.getHolder().trim();
            // 尝试精确匹配
            if (photoMap.containsKey(holder)) {
                return photoMap.get(holder);
            }
            // 尝试忽略大小写匹配
            for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(holder)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 内部类：用于将字节数组转换为MultipartFile
     */
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;

        public ByteArrayMultipartFile(byte[] content, String name) {
            this.content = content;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return new File(name).getName();
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.write(dest.toPath(), content);
        }
    }
}
