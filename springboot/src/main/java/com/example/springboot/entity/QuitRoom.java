package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitRoom {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "name")
    private String name;

    @TableField(value = "dormroom_id")
    private int dormRoomId;

    @TableField(value = "bed_number")
    private int bedNumber;

    @TableField(value = "reason")
    private String reason;

    @TableField(value = "state")
    private String state;

    @TableField(value = "apply_time")
    private String applyTime;

    @TableField(value = "finish_time")
    private String finishTime;
}
