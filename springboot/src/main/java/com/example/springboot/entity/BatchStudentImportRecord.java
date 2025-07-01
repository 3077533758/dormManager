package com.example.springboot.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BatchStudentImportRecord {
    private Integer id;
    private String batchNo;
    private String operator;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private String status;      // PENDING/PROCESSING/COMPLETED/FAILED
    private String strategy;    // CONTINUE_ON_ERROR/ALL_OR_NOTHING
    private Date createTime;
    private Date completeTime;
    private String remark;
} 