package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 违纪记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "violation_record")
public class ViolationRecord {
    
    @TableId(value = "id")
    private Integer id;
    
    @TableField("dormroom_id")
    private Integer dormroomId;
    
    @TableField("dormbuild_id")
    private Integer dormbuildId;
    
    @TableField("student_username")
    private String studentUsername;
    
    @TableField("student_name")
    private String studentName;
    
    @TableField("violation_type")
    private String violationType;
    
    @TableField("violation_description")
    private String violationDescription;
    
    @TableField("violation_date")
    private LocalDateTime violationDate;
    
    @TableField("penalty_score")
    private Integer penaltyScore;
    
    @TableField("status")
    private String status;
    
    @TableField("reporter")
    private String reporter;
    
    @TableField("handler")
    private String handler;
    
    @TableField("handle_time")
    private LocalDateTime handleTime;
    
    @TableField("handle_result")
    private String handleResult;
    
    @TableField("create_time")
    private LocalDateTime createTime;
} 