package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 卫生检查记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "hygiene_check")
public class HygieneCheck {
    
    @TableId(value = "id")
    private Integer id;
    
    @TableField("dormroom_id")
    private Integer dormroomId;
    
    @TableField("dormbuild_id")
    private Integer dormbuildId;
    
    @TableField("check_date")
    private LocalDate checkDate;
    
    @TableField("door_window_score")
    private Integer doorWindowScore;
    
    @TableField("item_placement_score")
    private Integer itemPlacementScore;
    
    @TableField("bedding_score")
    private Integer beddingScore;
    
    @TableField("cleanliness_score")
    private Integer cleanlinessScore;
    
    @TableField("overall_score")
    private Integer overallScore;
    
    @TableField("total_score")
    private Integer totalScore;
    
    @TableField("grade")
    private String grade;
    
    @TableField("checker")
    private String checker;
    
    @TableField("remarks")
    private String remarks;
    
    @TableField("create_time")
    private LocalDateTime createTime;
} 