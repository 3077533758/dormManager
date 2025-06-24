package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 宿舍管理员
 */
@Data
@TableName("dorm_manager")
public class DormManager {

    @TableId(type = IdType.AUTO)
    private String username;
    private String password;
    private Integer dormbuildId;
    private String name;
    private String gender;
    private Integer age;
    private String phoneNum;
    private String email;
    private String avatar;
}
