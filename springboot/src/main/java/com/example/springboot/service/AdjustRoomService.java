package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.AdjustRoom;

public interface AdjustRoomService extends IService<AdjustRoom> {

    //查询调宿申请
    Page find(Integer pageNum, Integer pageSize, String search);

    //删除调宿申请
    int deleteAdjustment(Integer id);

    //更新
    int updateApply(AdjustRoom adjustRoom);

    // 添加
    int addApply(AdjustRoom adjustRoom);

    Page findByUsername(String username, Integer pageNum, Integer pageSize);

    // 查询是否有未处理的调宿申请
    boolean hasPendingAdjust(String username);

    // 按楼栋分页查询调宿申请
    Page findByBuild(Integer pageNum, Integer pageSize, String search, int dormbuildId);

}
