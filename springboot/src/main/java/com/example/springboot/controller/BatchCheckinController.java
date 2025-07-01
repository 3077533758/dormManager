package com.example.springboot.controller;

import com.example.springboot.common.ExcelUtils;
import com.example.springboot.common.ExcelTemplateGenerator;
import com.example.springboot.common.Result;
import com.example.springboot.entity.BatchCheckinRecord;
import com.example.springboot.entity.BatchCheckinDetail;
import com.example.springboot.entity.DormManager;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.entity.Student;
import com.example.springboot.service.BatchCheckinService;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.StudentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/batchCheckin")
public class BatchCheckinController {

    @Resource
    private BatchCheckinService batchCheckinService;

    @Resource
    private DormRoomService dormRoomService;

    @Resource
    private StudentService studentService;

    /**
     * 下载Excel模板（支持中文文件名，兼容主流浏览器）
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            String fileName = URLEncoder.encode("批量入住模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            byte[] templateBytes = com.example.springboot.common.ExcelTemplateGenerator.generateBlankTemplate();
            response.getOutputStream().write(templateBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    /**
     * 上传Excel文件并解析
     */
    @PostMapping("/upload")
    public Result<?> uploadExcel(@RequestParam("file") MultipartFile file, HttpSession session) {
        System.out.println("[UPLOAD] identity=" + session.getAttribute("Identity") + ", user=" + session.getAttribute("User"));
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
            List<BatchCheckinDetail> details = ExcelUtils.parseBatchCheckinExcel(file);

            if (details.isEmpty()) {
                return Result.error("-1", "Excel文件中没有有效数据");
            }

            // 生成批次号
            String batchNo = generateBatchNo();

            // 创建批次记录
            BatchCheckinRecord record = new BatchCheckinRecord();
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
            batchCheckinService.createBatch(record, details);

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
            System.out.println("[UPLOAD][ERROR] " + e.getMessage());
            return Result.error("-1", "文件处理失败: " + e.getMessage());
        }
    }

    /**
     * 验证批量数据
     */
    @PostMapping("/validate")
    public Result<?> validateBatch(@RequestParam String batchNo, HttpSession session) {
        System.out.println("[VALIDATE] batchNo=" + batchNo + ", identity=" + session.getAttribute("Identity") + ", user=" + session.getAttribute("User"));
        try {
            Object userObj = session.getAttribute("User");
            Object identityObj = session.getAttribute("Identity");
            if (userObj == null || identityObj == null) {
                System.out.println("[VALIDATE][NO SESSION] userObj or identityObj is null");
                return Result.error("-1", "无权限");
            }
            String identity = identityObj.toString();
            Integer userBuildId = null;
            if ("dormManager".equals(identity)) {
                if (!(userObj instanceof DormManager)) {
                    System.out.println("[VALIDATE][NO PERMISSION] userObj not DormManager, actual=" + userObj.getClass().getName());
                    return Result.error("-1", "无权限");
                }
                DormManager manager = (DormManager) userObj;
                userBuildId = manager.getDormbuildId();
                if (userBuildId == null) {
                    System.out.println("[VALIDATE][NO BUILDING] DormManager has no dormbuildId");
                    return Result.error("-1", "无权限：您没有分配管理的楼栋");
                }
            }
            System.out.println("[VALIDATE] userBuildId=" + userBuildId);
            List<BatchCheckinDetail> details = batchCheckinService.getDetailsByBatchNo(batchNo);
            if (details.isEmpty()) {
                System.out.println("[VALIDATE][NO DETAILS] batchNo=" + batchNo);
                return Result.error("-1", "批次数据不存在");
            }

            // 新增：统计学生和床位分配次数，并记录首次出现位置
            Map<String, Integer> studentFirstIndex = new HashMap<>();
            Map<String, Integer> bedFirstIndex = new HashMap<>();
            for (int i = 0; i < details.size(); i++) {
                BatchCheckinDetail detail = details.get(i);
                studentFirstIndex.putIfAbsent(detail.getStudentUsername(), i);
                String bedKey = detail.getDormroomId() + "-" + detail.getBedNumber();
                bedFirstIndex.putIfAbsent(bedKey, i);
            }

            int validCount = 0;
            int invalidCount = 0;
            List<Map<String, Object>> errors = new ArrayList<>();

            for (int i = 0; i < details.size(); i++) {
                BatchCheckinDetail detail = details.get(i);
                Map<String, Object> error = new HashMap<>();
                boolean isValid = true;
                List<String> errorMessages = new ArrayList<>();

                // 1. 检查学号是否存在
                Student student = studentService.getById(detail.getStudentUsername());
                if (student == null) {
                    isValid = false;
                    errorMessages.add("学号不存在");
                } else {
                    // 新增：学号和姓名是否匹配
                    if (!student.getName().equals(detail.getStudentName())) {
                        isValid = false;
                        errorMessages.add("学号与姓名不匹配");
                    }
                }

                // 2. 检查学生是否已有宿舍
                if (dormRoomService.judgeHadBed(detail.getStudentUsername()) != null) {
                    isValid = false;
                    errorMessages.add("学生已有宿舍");
                }

                // 3. 检查房间是否存在
                DormRoom room = dormRoomService.getById(detail.getDormroomId());
                if (room == null) {
                    isValid = false;
                    errorMessages.add("房间不存在");
                } else {
                    // 4. 检查权限（宿管只能操作自己楼栋）
                    if (userBuildId != null && room.getDormBuildId() != userBuildId) {
                        isValid = false;
                        errorMessages.add("房间不在您的管辖范围内");
                    } else {
                        // 5. 检查床位是否可用
                        String occupiedStudent = null;
                        switch (detail.getBedNumber()) {
                            case 1: occupiedStudent = room.getFirstBed(); break;
                            case 2: occupiedStudent = room.getSecondBed(); break;
                            case 3: occupiedStudent = room.getThirdBed(); break;
                            case 4: occupiedStudent = room.getFourthBed(); break;
                            default:
                                isValid = false;
                                errorMessages.add("床位号无效");
                                break;
                        }
                        
                        if (occupiedStudent != null && !occupiedStudent.trim().isEmpty()) {
                            isValid = false;
                            errorMessages.add("床位已被占用");
                        }
                    }
                }

                // 新增：校验同一学生和同一床位是否被分配多次（只对后出现的报错）
                if (studentFirstIndex.get(detail.getStudentUsername()) != i) {
                    isValid = false;
                    errorMessages.add("同一学生在批处理中被分配多次");
                }
                String bedKey = detail.getDormroomId() + "-" + detail.getBedNumber();
                if (bedFirstIndex.get(bedKey) != i) {
                    isValid = false;
                    errorMessages.add("同一房间床位在批处理中被分配多次");
                }

                // 更新详情状态
                if (isValid) {
                    detail.setStatus("SUCCESS");
                    validCount++;
                } else {
                    detail.setStatus("FAILED");
                    invalidCount++;
                    
                    error.put("studentUsername", detail.getStudentUsername());
                    error.put("studentName", detail.getStudentName());
                    error.put("roomId", detail.getDormroomId());
                    error.put("bedNumber", detail.getBedNumber());
                    error.put("errors", errorMessages);
                    errors.add(error);
                }
            }

            // 更新批次状态
            BatchCheckinRecord record = batchCheckinService.getBatchByNo(batchNo);
            if (record != null) {
                record.setSuccessCount(validCount);
                record.setFailCount(invalidCount);
                // TODO: 更新记录到数据库
            }

            Map<String, Object> result = new HashMap<>();
            result.put("batchNo", batchNo);
            result.put("validCount", validCount);
            result.put("invalidCount", invalidCount);
            result.put("errors", errors);

            return Result.success(result);

        } catch (Exception e) {
            System.out.println("[VALIDATE][ERROR] " + e.getMessage());
            return Result.error("-1", "验证失败: " + e.getMessage());
        }
    }

    /**
     * 执行批量入住
     */
    @PostMapping("/execute")
    public Result<?> executeBatch(@RequestParam String batchNo, 
                                 @RequestParam(defaultValue = "CONTINUE_ON_ERROR") String strategy,
                                 HttpSession session) {
        try {
            // 权限验证
            Object userObj = session.getAttribute("User");
            Object identityObj = session.getAttribute("Identity");
            
            if (userObj == null || identityObj == null) {
                return Result.error("-1", "无权限");
            }

            String identity = identityObj.toString();
            Integer userBuildId = null;
            if ("dormManager".equals(identity)) {
                if (!(userObj instanceof DormManager)) {
                    return Result.error("-1", "无权限");
                }
                DormManager manager = (DormManager) userObj;
                userBuildId = manager.getDormbuildId();
                if (userBuildId == null) {
                    return Result.error("-1", "无权限：您没有分配管理的楼栋");
                }
            }

            // 执行批量处理，管理员userBuildId为null
            batchCheckinService.processBatch(batchNo, strategy, userBuildId);

            return Result.success("批量入住处理完成");

        } catch (Exception e) {
            return Result.error("-1", "执行失败: " + e.getMessage());
        }
    }

    /**
     * 查询批次状态
     */
    @GetMapping("/status/{batchNo}")
    public Result<?> getBatchStatus(@PathVariable String batchNo) {
        try {
            BatchCheckinRecord record = batchCheckinService.getBatchByNo(batchNo);
            if (record == null) {
                return Result.error("-1", "批次不存在");
            }

            return Result.success(record);

        } catch (Exception e) {
            return Result.error("-1", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询批次列表
     */
    @GetMapping("/list")
    public Result<?> getBatchList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(defaultValue = "") String search,
                                  HttpSession session) {
        try {
            // 获取用户身份和权限控制
            Object userObj = session.getAttribute("User");
            Object identityObj = session.getAttribute("Identity");
            
            if (userObj == null || identityObj == null) {
                return Result.error("-1", "无权限");
            }
            
            String identity = identityObj.toString();
            Integer userBuildId = null;
            
            if ("dormManager".equals(identity)) {
                if (!(userObj instanceof DormManager)) {
                    return Result.error("-1", "无权限");
                }
                DormManager manager = (DormManager) userObj;
                userBuildId = manager.getDormbuildId();
                
                if (userBuildId == null) {
                    return Result.error("-1", "无权限：您没有分配管理的楼栋");
                }
            }

            // 查询批次列表
            List<BatchCheckinRecord> batchList = batchCheckinService.getBatchList(pageNum, pageSize, search, userBuildId);
            
            return Result.success(batchList);

        } catch (Exception e) {
            return Result.error("-1", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询批次详情
     */
    @GetMapping("/details/{batchNo}")
    public Result<?> getBatchDetails(@PathVariable String batchNo, HttpSession session) {
        try {
            // 权限验证
            Object userObj = session.getAttribute("User");
            Object identityObj = session.getAttribute("Identity");
            
            if (userObj == null || identityObj == null) {
                return Result.error("-1", "无权限");
            }

            // 获取批次详情
            List<BatchCheckinDetail> details = batchCheckinService.getDetailsByBatchNo(batchNo);
            
            return Result.success(details);

        } catch (Exception e) {
            return Result.error("-1", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 生成批次号
     */
    private String generateBatchNo() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String sequence = String.format("%03d", (int) (System.currentTimeMillis() % 1000));
        return "BATCH_" + dateTime + "_" + sequence;
    }

    @DeleteMapping("/delete/{batchNo}")
    public Result<?> deleteBatch(@PathVariable String batchNo) {
        batchCheckinService.deleteBatch(batchNo);
        return Result.success("批次已删除");
    }
} 