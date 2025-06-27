package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.QuitRoom;

public interface QuitRoomMapper extends BaseMapper<QuitRoom> {
    // 查询某楼栋下所有房间号
    java.util.List<Integer> selectRoomIdsByBuildId(int dormbuildId);
}
