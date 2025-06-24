package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.HygieneCheck;

/**
 * 卫生检查Service接口
 */
public interface HygieneCheckService {
    
    /**
     * 分页查询卫生检查记录
     */
    Page<HygieneCheck> findPage(Integer pageNum, Integer pageSize, String search);
    
    /**
     * 添加卫生检查记录
     */
    int addHygieneCheck(HygieneCheck hygieneCheck);
    
    /**
     * 更新卫生检查记录
     */
    int updateHygieneCheck(HygieneCheck hygieneCheck);
    
    /**
     * 删除卫生检查记录
     */
    int deleteHygieneCheck(Integer id);
    
    /**
     * 根据房间号查询卫生检查记录
     */
    Page<HygieneCheck> findByRoomId(Integer roomId, Integer pageNum, Integer pageSize);
    
    /**
     * 统计房间卫生得分
     */
    Double getAverageScoreByRoom(Integer roomId);
} 