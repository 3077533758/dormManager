package com.example.springboot.entity;

import lombok.Data;

/**
 * 宿舍楼数据传输对象，包含围合信息
 */
@Data
public class DormBuildDTO {
    private Integer id;
    private int dormBuildId;
    private String dormBuildName;
    private String dormBuildDetail;
    private Integer compoundId;
    private String campus;
    private String compoundName; // 围合名称
} 