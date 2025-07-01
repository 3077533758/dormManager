package com.example.springboot.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BatchCheckinDetail {
    private Integer id;
    private String batchNo;
    private String studentUsername;
    private String studentName;
    private Integer dormbuildId;
    private Integer dormroomId;
    private Integer bedNumber;
    private String status;        // PENDING/SUCCESS/FAILED/SKIPPED
    private String errorMessage;
    private Date processTime;
    private String remark;
} 