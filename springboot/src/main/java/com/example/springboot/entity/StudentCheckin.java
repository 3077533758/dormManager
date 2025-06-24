package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学生入住记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "student_checkin")
public class StudentCheckin {
    
    @TableId(value = "id")
    private Integer id;
    
    @TableField("student_username")
    private String studentUsername;
    
    @TableField("student_name")
    private String studentName;
    
    @TableField("action_type")
    private String actionType;   // 入住/退宿/调宿/请假
    
    @TableField("action_time")
    private LocalDateTime actionTime;
    
    @TableField("operator")
    private String operator;
    
    @TableField("remarks")
    private String remarks;

    @TableField("dormbuild_id")
    private Integer dormbuildId;

    @TableField("dormroom_id")
    private Integer dormroomId;

    @TableField("bed_number")
    private Integer bedNumber;
} 