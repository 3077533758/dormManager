package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.AdjustRoom;

public interface AdjustRoomMapper extends BaseMapper<AdjustRoom> {
    // 查询某楼栋下所有房间号
    java.util.List<Integer> selectRoomIdsByBuildId(int dormbuildId);
}
