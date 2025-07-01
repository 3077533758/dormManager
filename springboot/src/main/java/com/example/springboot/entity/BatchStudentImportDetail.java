package com.example.springboot.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BatchStudentImportDetail {
    private Integer id;
    private String batchNo;
    private String username;
    private String name;
    private String gender;
    private Integer age;
    private String phoneNum;
    private String email;
    private String status;        // PENDING/SUCCESS/FAILED/SKIPPED
    private String errorMessage;
    private Date processTime;
    private String remark;
} 