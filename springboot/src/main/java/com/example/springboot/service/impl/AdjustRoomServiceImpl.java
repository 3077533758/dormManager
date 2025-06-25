package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.AdjustRoom;
import com.example.springboot.mapper.AdjustRoomMapper;
import com.example.springboot.service.AdjustRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdjustRoomServiceImpl extends ServiceImpl<AdjustRoomMapper, AdjustRoom> implements AdjustRoomService {


    @Resource
    private AdjustRoomMapper adjustRoomMapper;

    /**
     * 添加调宿申请
     */
    @Override
    public int addApply(AdjustRoom adjustRoom) {
        int insert = adjustRoomMapper.insert(adjustRoom);
        return insert;
    }

    /**
     * 查找调宿申请
     */
    @Override
    public Page find(Integer pageNum, Integer pageSize, String search) {
        Page page = new Page<>(pageNum, pageSize);
        QueryWrapper<AdjustRoom> qw = new QueryWrapper<>();
        qw.like("username", search);
        Page orderPage = adjustRoomMapper.selectPage(page, qw);
        return orderPage;
    }

    /**
     * 删除调宿申请
     */
    @Override
    public int deleteAdjustment(Integer id) {
        return adjustRoomMapper.deleteById(id);
    }


    /**
     * 更新调宿申请
     */
    @Override
    public int updateApply(AdjustRoom adjustRoom) {
        // 自动设置处理时间
        if ("通过".equals(adjustRoom.getState()) || "驳回".equals(adjustRoom.getState())) {
            adjustRoom.setFinishTime(java.time.LocalDateTime.now().toString());
        } else {
            adjustRoom.setFinishTime(null);
        }
        int i = adjustRoomMapper.updateById(adjustRoom);
        return i;
    }

    @Override
    public Page findByUsername(String username, Integer pageNum, Integer pageSize) {
        Page<AdjustRoom> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AdjustRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return adjustRoomMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean hasPendingAdjust(String username) {
        QueryWrapper<AdjustRoom> qw = new QueryWrapper<>();
        qw.eq("username", username).eq("state", "未处理");
        return adjustRoomMapper.selectCount(qw) > 0;
    }

}
