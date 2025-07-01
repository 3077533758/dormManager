package com.example.springboot.common;

import com.example.springboot.entity.BatchCheckinDetail;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    
    /**
     * 解析批量入住Excel文件
     */
    public static List<BatchCheckinDetail> parseBatchCheckinExcel(MultipartFile file) throws IOException {
        List<BatchCheckinDetail> details = new ArrayList<>();
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            
            // 跳过表头，从第二行开始读取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                BatchCheckinDetail detail = parseRow(row, i + 1);
                if (detail != null) {
                    details.add(detail);
                }
            }
        }
        
        return details;
    }
    
    /**
     * 解析单行数据
     */
    private static BatchCheckinDetail parseRow(Row row, int rowNum) {
        try {
            // 检查是否为空行
            if (isEmptyRow(row)) {
                return null;
            }
            
            BatchCheckinDetail detail = new BatchCheckinDetail();
            
            // 学号 (第1列)
            String studentUsername = getCellValueAsString(row.getCell(0));
            if (studentUsername == null || studentUsername.trim().isEmpty()) {
                throw new IllegalArgumentException("学号不能为空");
            }
            detail.setStudentUsername(studentUsername.trim());
            
            // 学生姓名 (第2列)
            String studentName = getCellValueAsString(row.getCell(1));
            if (studentName == null || studentName.trim().isEmpty()) {
                throw new IllegalArgumentException("学生姓名不能为空");
            }
            detail.setStudentName(studentName.trim());
            
            // 房间号 (第3列)
            String roomIdStr = getCellValueAsString(row.getCell(2));
            if (roomIdStr == null || roomIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("房间号不能为空");
            }
            try {
                int roomId = Integer.parseInt(roomIdStr.trim());
                detail.setDormroomId(roomId);
                
                // 从房间号提取楼栋ID
                // 格式：前N位表示楼栋ID，后3位表示房间号
                // 例如：11101 -> 楼栋11, 房间101; 1001 -> 楼栋1, 房间001
                String roomIdStrFull = roomIdStr.trim();
                if (roomIdStrFull.length() < 4) {
                    throw new IllegalArgumentException("房间号格式错误，至少需要4位数字");
                }
                
                // 后3位是房间号，前面的都是楼栋ID
                String buildIdStr = roomIdStrFull.substring(0, roomIdStrFull.length() - 3);
                int buildId = Integer.parseInt(buildIdStr);
                detail.setDormbuildId(buildId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("房间号格式错误，应为数字格式");
            }
            
            // 床位号 (第4列)
            String bedNumberStr = getCellValueAsString(row.getCell(3));
            if (bedNumberStr == null || bedNumberStr.trim().isEmpty()) {
                throw new IllegalArgumentException("床位号不能为空");
            }
            try {
                int bedNumber = Integer.parseInt(bedNumberStr.trim());
                if (bedNumber < 1 || bedNumber > 4) {
                    throw new IllegalArgumentException("床位号必须在1-4之间");
                }
                detail.setBedNumber(bedNumber);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("床位号格式错误，应为1-4之间的数字");
            }
            
            // 备注 (第5列，可选)
            String remark = getCellValueAsString(row.getCell(4));
            detail.setRemark(remark != null ? remark.trim() : "");
            
            // 设置初始状态
            detail.setStatus("PENDING");
            
            return detail;
            
        } catch (Exception e) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取单元格值作为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 避免科学计数法，转为整数
                    double value = cell.getNumericCellValue();
                    if (value == (long) value) {
                        return String.valueOf((long) value);
                    } else {
                        return String.valueOf(value);
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
     * 检查是否为空行
     */
    private static boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        for (int i = 0; i < 4; i++) { // 检查前4列（必填字段）
            Cell cell = row.getCell(i);
            if (cell != null) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
} 