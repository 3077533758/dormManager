package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 宿舍围合
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "dorm_compound")
public class DormCompound {

    @TableId(value = "compound_id", type = IdType.AUTO)
    private Integer compoundId;
    
    @TableField("compound_name")
    private String compoundName;
    
    @TableField("campus")
    private String campus;
    
    @TableField("compound_detail")
    private String compoundDetail;
} 