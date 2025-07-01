package com.example.springboot.common;

import com.example.springboot.entity.BatchStudentImportDetail;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StudentImportExcelUtils {

    /**
     * 解析学生批量导入Excel文件
     */
    public static List<BatchStudentImportDetail> parseStudentImportExcel(MultipartFile file) throws IOException {
        List<BatchStudentImportDetail> details = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 取第一个工作表

            // 跳过表头，从第二行开始读
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                BatchStudentImportDetail detail = parseRow(row, i + 1);
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
    private static BatchStudentImportDetail parseRow(Row row, int rowNum) {
        BatchStudentImportDetail detail = new BatchStudentImportDetail();
        // 检查是否为空行（前3列必填）
        boolean allEmpty = true;
        for (int i = 0; i < 3; i++) {
            String v = getCellString(row, i);
            if (v != null && !v.trim().isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        if (allEmpty) return null;
        detail.setUsername(getCellString(row, 0));
        detail.setName(getCellString(row, 1));
        detail.setGender(getCellString(row, 2));
        String ageStr = getCellString(row, 3);
        detail.setAge(ageStr.isEmpty() ? null : Integer.parseInt(ageStr));
        detail.setPhoneNum(getCellString(row, 4));
        detail.setEmail(getCellString(row, 5));
        detail.setStatus("PENDING");
        // 必填项校验
        if (detail.getUsername().isEmpty()) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 学号不能为空");
        }
        // 姓名校验
        String name = detail.getName();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 姓名不能为空");
        }
        if (!name.matches("^[\u4e00-\u9fa5A-Za-z·\\s-]{2,20}$")) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 姓名格式不合法（仅限2-20位中英文、·、-、空格）");
        }
        if (name.matches("^\\d+$")) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 姓名不能全为数字");
        }
        if (detail.getGender().isEmpty() || !("男".equals(detail.getGender()) || "女".equals(detail.getGender()))) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 性别必须为男或女");
        }
        // 年龄校验（必填）
        if (ageStr.isEmpty()) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 年龄不能为空");
        }
        if (!ageStr.matches("^\\d+$")) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 年龄格式错误");
        }
        int age = Integer.parseInt(ageStr);
        if (age < 15 || age > 100) {
            throw new IllegalArgumentException("第" + rowNum + "行数据错误: 年龄必须在15~100之间");
        }
        // 手机号校验（可选）
        if (detail.getPhoneNum() != null && !detail.getPhoneNum().isEmpty()) {
            if (!detail.getPhoneNum().matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("第" + rowNum + "行数据错误: 手机号格式不正确");
            }
        }
        // 邮箱校验（可选）
        if (detail.getEmail() != null && !detail.getEmail().isEmpty()) {
            if (!detail.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException("第" + rowNum + "行数据错误: 邮箱格式不正确");
            }
        }
        return detail;
    }

    private static String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}