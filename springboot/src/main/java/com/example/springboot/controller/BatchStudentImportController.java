package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.BatchStudentImportRecord;
import com.example.springboot.entity.BatchStudentImportDetail;
import com.example.springboot.service.BatchStudentImportService;
import com.example.springboot.common.StudentImportTemplateGenerator;
import com.example.springboot.common.StudentImportExcelUtils;
import com.example.springboot.service.StudentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.net.URLEncoder;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import com.example.springboot.entity.Student;

@RestController
@RequestMapping("/studentImport")
public class BatchStudentImportController {

    @Resource
    private BatchStudentImportService batchStudentImportService;

    @Resource
    private StudentService studentService;

    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            String fileName = URLEncoder.encode("学生批量导入模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            byte[] templateBytes = StudentImportTemplateGenerator.generateBlankTemplate();
            response.getOutputStream().write(templateBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    @PostMapping("/upload")
    public Result<?> uploadExcel(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            // 检查文件格式
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".xlsx")) {
                return Result.error("-1", "请上传.xlsx格式的文件");
            }
            // 检查文件大小 (限制10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("-1", "文件大小不能超过10MB");
            }
            // 解析Excel文件
            java.util.List<com.example.springboot.entity.BatchStudentImportDetail> details = StudentImportExcelUtils.parseStudentImportExcel(file);
            if (details.isEmpty()) {
                return Result.error("-1", "Excel文件中没有有效数据");
            }
            // 生成批次号
            String batchNo = generateBatchNo();
            // 创建批次记录
            com.example.springboot.entity.BatchStudentImportRecord record = new com.example.springboot.entity.BatchStudentImportRecord();
            record.setBatchNo(batchNo);
            record.setTotalCount(details.size());
            record.setStatus("PENDING");
            record.setStrategy("CONTINUE_ON_ERROR");
            // 获取操作人
            Object userObj = session.getAttribute("User");
            if (userObj != null) {
                try {
                    java.lang.reflect.Method getName = userObj.getClass().getMethod("getName");
                    String operator = (String) getName.invoke(userObj);
                    record.setOperator(operator);
                } catch (Exception e) {
                    record.setOperator("系统");
                }
            } else {
                record.setOperator("系统");
            }
            // 保存批次和详情数据
            batchStudentImportService.createBatch(record, details);
            // 返回预览数据
            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("totalCount", details.size());
            result.put("details", details);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error("-1", e.getMessage());
        } catch (IOException e) {
            return Result.error("-1", "文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("-1", "文件处理失败: " + e.getMessage());
        }
    }

    /**
     * 生成批次号
     */
    private String generateBatchNo() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String sequence = String.format("%03d", (int) (System.currentTimeMillis() % 1000));
        return "STU_IMPORT_" + dateTime + "_" + sequence;
    }

    @PostMapping("/validate")
    public Result<?> validateBatch(@RequestParam String batchNo, HttpSession session) {
        try {
            // 获取批次详情
            List<BatchStudentImportDetail> details = batchStudentImportService.getDetailsByBatchNo(batchNo);
            if (details.isEmpty()) {
                return Result.error("-1", "批次数据不存在");
            }
            int validCount = 0;
            int invalidCount = 0;
            List<Map<String, Object>> errors = new ArrayList<>();
            Set<String> seenUsernames = new HashSet<>();
            for (BatchStudentImportDetail detail : details) {
                boolean isValid = true;
                List<String> errorMessages = new ArrayList<>();
                // 1. 学号必填且唯一
                if (detail.getUsername() == null || detail.getUsername().trim().isEmpty()) {
                    isValid = false;
                    errorMessages.add("学号不能为空");
                } else {
                    if (studentService.getById(detail.getUsername()) != null) {
                        isValid = false;
                        errorMessages.add("学号已存在");
                    }
                    if (!seenUsernames.add(detail.getUsername())) {
                        isValid = false;
                        errorMessages.add("本批次中学号重复");
                    }
                }
                // 2. 姓名必填
                if (detail.getName() == null || detail.getName().trim().isEmpty()) {
                    isValid = false;
                    errorMessages.add("姓名不能为空");
                }
                // 3. 性别必填且为男或女
                if (detail.getGender() == null || (!"男".equals(detail.getGender()) && !"女".equals(detail.getGender()))) {
                    isValid = false;
                    errorMessages.add("性别必须为男或女");
                }
                // 4. 年龄必填且为正整数
                if (detail.getAge() == null || detail.getAge() <= 0) {
                    isValid = false;
                    errorMessages.add("年龄必须为正整数");
                }
                // 5. 手机号格式（可选）
                if (detail.getPhoneNum() != null && !detail.getPhoneNum().trim().isEmpty()) {
                    if (!detail.getPhoneNum().matches("^1[3-9]\\d{9}$")) {
                        isValid = false;
                        errorMessages.add("手机号格式不正确");
                    }
                }
                // 6. 邮箱格式（可选）
                if (detail.getEmail() != null && !detail.getEmail().trim().isEmpty()) {
                    if (!detail.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        isValid = false;
                        errorMessages.add("邮箱格式不正确");
                    }
                }
                // 更新详情状态
                if (isValid) {
                    detail.setStatus("SUCCESS");
                    validCount++;
                } else {
                    detail.setStatus("FAILED");
                    detail.setErrorMessage(String.join("; ", errorMessages));
                    invalidCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("username", detail.getUsername());
                    error.put("name", detail.getName());
                    error.put("errors", errorMessages);
                    errors.add(error);
                }
                // 写回数据库
                batchStudentImportService.updateDetailStatus(detail);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("validCount", validCount);
            result.put("invalidCount", invalidCount);
            result.put("errors", errors);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("-1", "校验失败: " + e.getMessage());
        }
    }

    @PostMapping("/execute")
    public Result<?> executeBatch(@RequestParam String batchNo,
                                  @RequestParam(defaultValue = "CONTINUE_ON_ERROR") String strategy,
                                  HttpSession session) {
        try {
            BatchStudentImportRecord record = batchStudentImportService.getBatchByNo(batchNo);
            if (record == null) {
                return Result.error("-1", "批次不存在");
            }
            if (!"PENDING".equals(record.getStatus()) && !"VALIDATED".equals(record.getStatus())) {
                return Result.error("-1", "该批次已执行或已完成，不能重复执行");
            }
            batchStudentImportService.processBatch(batchNo, strategy);
            return Result.success("批量导入处理完成");
        } catch (Exception e) {
            return Result.error("-1", "执行失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<?> getBatchList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(defaultValue = "") String search) {
        try {
            List<BatchStudentImportRecord> batchList = batchStudentImportService.getBatchList(pageNum, pageSize, search);
            return Result.success(batchList);
        } catch (Exception e) {
            return Result.error("-1", "查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/details/{batchNo}")
    public Result<?> getBatchDetails(@PathVariable String batchNo) {
        try {
            List<BatchStudentImportDetail> details = batchStudentImportService.getDetailsByBatchNo(batchNo);
            return Result.success(details);
        } catch (Exception e) {
            return Result.error("-1", "查询失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{batchNo}")
    public Result<?> deleteBatch(@PathVariable String batchNo) {
        try {
            batchStudentImportService.deleteBatch(batchNo);
            return Result.success("批次已删除");
        } catch (Exception e) {
            return Result.error("-1", "删除失败: " + e.getMessage());
        }
    }
} 