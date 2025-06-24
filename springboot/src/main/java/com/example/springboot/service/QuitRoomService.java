package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.QuitRoom;

public interface QuitRoomService extends IService<QuitRoom> {

    // 查询退宿申请
    Page find(Integer pageNum, Integer pageSize, String search);

    // 根据用户名查询退宿申请
    Page findByUsername(String username, Integer pageNum, Integer pageSize);

    // 删除退宿申请
    int deleteQuit(Integer id);

    // 更新退宿申请
    int updateQuit(QuitRoom quitRoom);

    // 添加退宿申请
    int addQuit(QuitRoom quitRoom);

    // 退宿审批通过（同步三表）
    void approveQuitRoom(QuitRoom quitRoom);

    // 查询是否有未处理的退宿申请
    boolean hasPendingQuit(String username);
}
