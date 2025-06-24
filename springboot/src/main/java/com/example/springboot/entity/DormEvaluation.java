package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 寝室评比
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "dorm_evaluation")
public class DormEvaluation {
    
    @TableId(value = "id")
    private Integer id;
    
    @TableField("dormroom_id")
    private Integer dormroomId;
    
    @TableField("dormbuild_id")
    private Integer dormbuildId;
    
    @TableField("evaluation_period")
    private String evaluationPeriod;
    
    @TableField("hygiene_score")
    private BigDecimal hygieneScore;
    
    @TableField("gpa_score")
    private BigDecimal gpaScore;
    
    @TableField("total_score")
    private BigDecimal totalScore;
    
    @TableField("star_level")
    private String starLevel;
    
    @TableField("evaluation_type")
    private String evaluationType;
    
    @TableField("status")
    private String status;
    
    @TableField("evaluator")
    private String evaluator;
    
    @TableField("evaluation_time")
    private LocalDateTime evaluationTime;
    
    @TableField("remarks")
    private String remarks;
    
    @TableField("create_time")
    private LocalDateTime createTime;
} 