package com.hebei.cs.utils;

import com.hebei.cs.entity.Certificate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 *
 * @author Beijing ZXJJ
 * @since 2024-12-16
 */
public class ExcelUtil {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 解析Excel文件为证件列表
     *
     * @param file Excel文件
     * @return 证件列表
     */
    public static List<Certificate> parseCertificateExcel(MultipartFile file) throws Exception {
        List<Certificate> certificates = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过表头，从第二行开始读取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Certificate certificate = new Certificate();

                // 读取各列数据（根据Excel列顺序）
                // 列顺序：证件名称、证件类型、证件编号、持有人、性别、身份证号、岗位名称、技能等级、持有人联系方式、发证机关、发证日期、有效期起始、有效期截止、证件状态、是否公开、附件路径、描述
                // 注意：一寸照需要在导入后单独上传文件，不在Excel中填写
                certificate.setName(getCellValue(row, 0));
                certificate.setType(getCellValue(row, 1));
                certificate.setNumber(getCellValue(row, 2));
                certificate.setHolder(getCellValue(row, 3));
                certificate.setGender(getCellValue(row, 4)); // 性别
                certificate.setIdCard(getCellValue(row, 5)); // 身份证号
                certificate.setPosition(getCellValue(row, 6)); // 岗位名称
                certificate.setSkillLevel(getCellValue(row, 7)); // 技能等级
                certificate.setHolderContact(getCellValue(row, 8));
                certificate.setIssuingAuthority(getCellValue(row, 9));
                certificate.setIssueDate(parseDateFromCell(row, 10));
                certificate.setValidFrom(parseDateFromCell(row, 11));
                certificate.setValidUntil(parseDateFromCell(row, 12));

                // 证件状态：有效=1，即将过期=2，已过期=0
                String statusStr = getCellValue(row, 13);
                if (statusStr != null && !statusStr.trim().isEmpty()) {
                    if (statusStr.contains("有效") || statusStr.equals("1")) {
                        certificate.setStatus(1);
                    } else if (statusStr.contains("即将过期") || statusStr.equals("2")) {
                        certificate.setStatus(2);
                    } else if (statusStr.contains("已过期") || statusStr.equals("0")) {
                        certificate.setStatus(0);
                    } else {
                        certificate.setStatus(1); // 默认有效
                    }
                } else {
                    certificate.setStatus(1); // 默认有效
                }

                // 是否公开：公开=true/1，不公开=false/0
                String isPublicStr = getCellValue(row, 14);
                if (isPublicStr != null && !isPublicStr.trim().isEmpty()) {
                    certificate.setIsPublic(isPublicStr.contains("公开") || isPublicStr.equals("1") || isPublicStr.equalsIgnoreCase("true"));
                } else {
                    certificate.setIsPublic(true); // 默认公开
                }

                // 一寸照路径不在Excel中填写，需要在导入后单独上传文件
                // 跳过一寸照路径列，附件路径现在是第15列，描述是第16列
                certificate.setAttachmentPath(getCellValue(row, 15));
                certificate.setDescription(getCellValue(row, 16));

                // 只添加非空行
                if (certificate.getName() != null && !certificate.getName().trim().isEmpty()) {
                    certificates.add(certificate);
                }
            }

            workbook.close();
        }

        return certificates;
    }

    /**
     * 获取单元格值
     */
    private static String getCellValue(Row row, int cellIndex) {
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(date);
                } else {
                    // 处理数字，避免科学计数法
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 从单元格直接解析日期
     */
    private static java.time.LocalDateTime parseDateFromCell(Row row, int cellIndex) {
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }

        try {
            // 如果单元格是日期格式
            if (DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            // 如果是数字格式（Excel日期序列号）
            else if (cell.getCellType() == CellType.NUMERIC) {
                double dateValue = cell.getNumericCellValue();
                Date date = DateUtil.getJavaDate(dateValue);
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            // 如果是字符串格式
            else if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                if (dateStr.isEmpty()) {
                    return null;
                }
                return parseDateString(dateStr);
            }
        } catch (Exception e) {
            log.warn("解析日期失败，行: {}, 列: {}", row.getRowNum() + 1, cellIndex + 1, e);
        }

        return null;
    }

    /**
     * 解析日期字符串为LocalDateTime
     */
    private static java.time.LocalDateTime parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            // 尝试解析常见日期格式 yyyy-MM-dd
            if (dateStr.contains("-")) {
                String[] parts = dateStr.split("-");
                if (parts.length >= 3) {
                    int year = Integer.parseInt(parts[0].trim());
                    int month = Integer.parseInt(parts[1].trim());
                    String dayPart = parts[2].trim().split(" ")[0].split("T")[0];
                    int day = Integer.parseInt(dayPart);
                    return LocalDate.of(year, month, day).atStartOfDay();
                }
            }
            // 如果是数字格式（Excel日期序列号字符串）
            try {
                double dateValue = Double.parseDouble(dateStr.trim());
                Date date = DateUtil.getJavaDate(dateValue);
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } catch (NumberFormatException e) {
                // 忽略，继续尝试其他格式
            }
        } catch (Exception e) {
            log.warn("解析日期字符串失败: {}", dateStr, e);
        }

        return null;
    }

    /**
     * 生成Excel模板
     */
    public static Workbook createCertificateTemplate() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("证件导入模板");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "证件名称", "证件类型", "证件编号", "持有人", "性别", "身份证号", "岗位名称", "技能等级",
            "持有人联系方式", "发证机关", "发证日期", "有效期起始", "有效期截止", "证件状态",
            "是否公开", "附件路径", "描述"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 设置列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 4000);
        }

        // 添加示例数据行
        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue("示例：营业执照");
        exampleRow.createCell(1).setCellValue("营业执照");
        exampleRow.createCell(2).setCellValue("91110000MA07X8XX9K");
        exampleRow.createCell(3).setCellValue("张三");
        exampleRow.createCell(4).setCellValue("男");
        exampleRow.createCell(5).setCellValue("110101199001011234");
        exampleRow.createCell(6).setCellValue("操作员");
        exampleRow.createCell(7).setCellValue("高级");
        exampleRow.createCell(8).setCellValue("010-88888888");
        exampleRow.createCell(9).setCellValue("北京市市场监督管理局");
        exampleRow.createCell(10).setCellValue("2024-01-01");
        exampleRow.createCell(11).setCellValue("2024-01-01");
        exampleRow.createCell(12).setCellValue("2034-01-01");
        exampleRow.createCell(13).setCellValue("有效");
        exampleRow.createCell(14).setCellValue("公开");
        exampleRow.createCell(15).setCellValue(""); // 附件路径
        exampleRow.createCell(16).setCellValue("示例描述");

        return workbook;
    }
}

