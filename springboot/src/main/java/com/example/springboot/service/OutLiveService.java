package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.OutLive;

public interface OutLiveService extends IService<OutLive> {

    // 查询外宿申请
    Page find(Integer pageNum, Integer pageSize, String search);

    // 根据用户名查询外宿申请
    Page findByUsername(String username, Integer pageNum, Integer pageSize);

    // 删除外宿申请
    int deleteOutLive(Integer id);

    // 更新外宿申请
    int updateOutLive(OutLive outLive);

    // 添加外宿申请
    int addOutLive(OutLive outLive);
}
