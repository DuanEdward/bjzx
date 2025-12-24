package com.hebei.cs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hebei.cs.common.Result;
import com.hebei.cs.entity.Certificate;
import com.hebei.cs.service.CertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.hebei.cs.utils.ExcelUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 证件管理控制器
 *
 * @author Hebei CS
 * @since 2024-12-13
 */
@RestController
@RequestMapping("/certificate")
public class CertificateController {

    private static final Logger log = LoggerFactory.getLogger(CertificateController.class);

    private Map<String, Object> createTypeMap(String value, String label) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        return map;
    }

    private Map<String, Object> createStatusMap(int value, String label, String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        map.put("color", color);
        return map;
    }

    @Autowired
    private CertificateService certificateService;

    /**
     * 分页查询证件列表（管理员）
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getCertificatePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String holder,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<Certificate> page = certificateService.getCertificatePage(current, size, name, type, number, holder, status);
            Map<String, Object> result = new HashMap<>();
            result.put("records", page.getRecords());
            result.put("total", page.getTotal());
            result.put("current", page.getCurrent());
            result.put("size", page.getSize());
            result.put("pages", page.getPages());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询证件列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 条件查询证件列表（管理员）
     */
    @GetMapping("/list")
    public Result<List<Certificate>> getCertificateList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String holder,
            @RequestParam(required = false) Integer status) {
        try {
            List<Certificate> list = certificateService.getCertificateList(name, type, number, holder, status);
            return Result.success("查询成功", list);
        } catch (Exception e) {
            log.error("查询证件列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询公开证件列表（前端用户）
     */
    @GetMapping("/public")
    public Result<List<Certificate>> getPublicCertificateList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String holder,
            @RequestParam(required = false) String idCard) {
        try {
            List<Certificate> list = certificateService.getPublicCertificateList(name, type, number, holder, idCard);
            return Result.success("查询成功", list);
        } catch (Exception e) {
            log.error("查询公开证件列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取证件详情
     */
    @GetMapping("/{id}")
    public Result<Certificate> getCertificateById(@PathVariable Long id) {
        try {
            Certificate certificate = certificateService.getCertificateById(id);
            if (certificate == null) {
                return Result.error("证件不存在");
            }
            return Result.success("查询成功", certificate);
        } catch (Exception e) {
            log.error("查询证件详情失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 创建证件
     */
    @PostMapping
    public Result<Certificate> createCertificate(@RequestBody Certificate certificate) {
        try {
            Certificate savedCertificate = certificateService.createCertificate(certificate);
            return Result.success("创建成功", savedCertificate);
        } catch (Exception e) {
            log.error("创建证件失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新证件
     */
    @PutMapping("/{id}")
    public Result<Certificate> updateCertificate(@PathVariable Long id, @RequestBody Certificate certificate) {
        try {
            Certificate existingCertificate = certificateService.getCertificateById(id);
            if (existingCertificate == null) {
                return Result.error("证件不存在");
            }

            certificate.setId(id);
            Certificate updatedCertificate = certificateService.updateCertificate(certificate);
            return Result.success("更新成功", updatedCertificate);
        } catch (Exception e) {
            log.error("更新证件失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除证件
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteCertificate(@PathVariable Long id) {
        try {
            boolean result = certificateService.deleteCertificate(id);
            if (result) {
                return Result.success("删除成功");
            } else {
                return Result.error("证件不存在");
            }
        } catch (Exception e) {
            log.error("删除证件失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除证件
     */
    @DeleteMapping("/batch")
    public Result<?> batchDeleteCertificates(@RequestBody List<Long> ids) {
        try {
            boolean result = certificateService.batchDeleteCertificates(ids);
            if (result) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除证件失败", e);
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取证件类型选项
     */
    @GetMapping("/types")
    public Result<List<Map<String, Object>>> getCertificateTypes() {
        try {
            List<Map<String, Object>> types = Arrays.asList(
                createTypeMap("营业执照", "营业执照"),
                createTypeMap("组织机构代码证", "组织机构代码证"),
                createTypeMap("税务登记证", "税务登记证"),
                createTypeMap("社会保险登记证", "社会保险登记证"),
                createTypeMap("统计登记证", "统计登记证"),
                createTypeMap("开户许可证", "开户许可证"),
                createTypeMap("卫生许可证", "卫生许可证"),
                createTypeMap("安全生产许可证", "安全生产许可证"),
                createTypeMap("其他", "其他")
            );
            return Result.success("查询成功", types);
        } catch (Exception e) {
            log.error("获取证件类型失败", e);
            return Result.error("获取证件类型失败: " + e.getMessage());
        }
    }

    /**
     * 获取证件状态选项
     */
    @GetMapping("/status-options")
    public Result<List<Map<String, Object>>> getCertificateStatusOptions() {
        try {
            List<Map<String, Object>> statusOptions = Arrays.asList(
                createStatusMap(0, "已过期", "danger"),
                createStatusMap(1, "有效", "success"),
                createStatusMap(2, "即将过期", "warning")
            );
            return Result.success("查询成功", statusOptions);
        } catch (Exception e) {
            log.error("获取证件状态失败", e);
            return Result.error("获取证件状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入证件
     */
    @PostMapping("/batch-import")
    public Result<Map<String, Object>> batchImportCertificates(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择要导入的文件");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Result.error("只支持Excel文件（.xlsx或.xls格式）");
            }

            // 解析Excel文件
            List<Certificate> certificates = ExcelUtil.parseCertificateExcel(file);

            if (certificates.isEmpty()) {
                return Result.error("Excel文件中没有有效数据");
            }

            // 批量导入
            Map<String, Object> result = certificateService.batchImportCertificates(certificates);

            return Result.success("导入完成", result);
        } catch (Exception e) {
            log.error("批量导入证件失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 下载Excel模板
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=certificate_template.xlsx");

            Workbook workbook = ExcelUtil.createCertificateTemplate();
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("下载模板失败", e);
        }
    }
}