package com.example.springboot.common;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StudentImportTemplateGenerator {
    /**
     * 生成美观的学生批量导入Excel模板
     */
    public static byte[] generateBlankTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("学生导入模板");

            // 标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 13);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 说明样式
            CellStyle noteStyle = workbook.createCellStyle();
            Font noteFont = workbook.createFont();
            noteFont.setFontHeightInPoints((short) 10);
            noteStyle.setFont(noteFont);
            noteStyle.setWrapText(true);

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 0;

            // 添加标题
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("学生批量导入模板");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            // 添加说明
            String[] instructions = {
                    "说明：",
                    "1. 请严格按照模板格式填写学生信息",
                    "2. 学号、姓名、性别、年龄为必填项，学号唯一",
                    "3. 年龄为15~100的整数，性别为男或女",
                    "4. 手机号、邮箱为可选项，填写时请保证格式正确",
                    "5. 密码和头像不在模板中，系统自动设置默认值"
            };
            for (String instruction : instructions) {
                Row instructionRow = sheet.createRow(rowNum++);
                Cell instructionCell = instructionRow.createCell(0);
                instructionCell.setCellValue(instruction);
                instructionCell.setCellStyle(noteStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
            }

            // 空行
            rowNum++;

            // 添加表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"学号", "姓名", "性别", "年龄", "手机号", "邮箱"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 示例数据行
            Row exampleRow = sheet.createRow(rowNum++);
            exampleRow.createCell(0).setCellValue("20230001");
            exampleRow.createCell(1).setCellValue("张三");
            exampleRow.createCell(2).setCellValue("男");
            exampleRow.createCell(3).setCellValue("18");
            exampleRow.createCell(4).setCellValue("13800000000");
            exampleRow.createCell(5).setCellValue("zhangsan@example.com");

            // 设置列宽
            sheet.setColumnWidth(0, 15 * 256); // 学号
            sheet.setColumnWidth(1, 12 * 256); // 姓名
            sheet.setColumnWidth(2, 8 * 256);  // 性别
            sheet.setColumnWidth(3, 8 * 256);  // 年龄
            sheet.setColumnWidth(4, 15 * 256); // 手机号
            sheet.setColumnWidth(5, 25 * 256); // 邮箱

            workbook.write(out);
            return out.toByteArray();
        }
    }
} 