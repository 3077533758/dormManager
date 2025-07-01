package com.example.springboot.common;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * Excel模板生成器
 */
@Component
public class ExcelTemplateGenerator {

    /**
     * 生成空白模板（用于下载）
     */
    public static byte[] generateBlankTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("批量入住数据");
            
            // 创建标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 12);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // 创建说明样式
            CellStyle noteStyle = workbook.createCellStyle();
            Font noteFont = workbook.createFont();
            noteFont.setFontHeightInPoints((short) 10);
            noteStyle.setFont(noteFont);
            noteStyle.setWrapText(true);
            
            // 创建表头样式
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
            titleCell.setCellValue("批量入住数据模板");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));
            
            // 添加说明
            String[] instructions = {
                "说明：",
                "1. 请严格按照模板格式填写数据",
                "2. 学号：必须为系统中存在的学生学号",
                "3. 学生姓名：系统会自动验证与学号是否匹配",
                "4. 房间号：前N位表示楼栋ID，后3位表示房间号（如：11101表示11号楼101房间）",
                "5. 床位号：1-4之间的数字，表示房间内的床位编号",
                "6. 备注：可选字段，用于记录特殊说明"
            };
            
            for (String instruction : instructions) {
                Row instructionRow = sheet.createRow(rowNum++);
                Cell instructionCell = instructionRow.createCell(0);
                instructionCell.setCellValue(instruction);
                instructionCell.setCellStyle(noteStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 4));
            }
            
            // 空行
            rowNum++;
            
            // 添加表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"学号", "学生姓名", "房间号", "床位号", "备注"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 设置列宽
            sheet.setColumnWidth(0, 15 * 256); // 学号
            sheet.setColumnWidth(1, 12 * 256); // 学生姓名
            sheet.setColumnWidth(2, 12 * 256); // 房间号
            sheet.setColumnWidth(3, 10 * 256); // 床位号
            sheet.setColumnWidth(4, 20 * 256); // 备注
            
            // 转换为字节数组
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }
} 