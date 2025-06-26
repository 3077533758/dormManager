package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.AdjustRoom;
import com.example.springboot.mapper.AdjustRoomMapper;
import com.example.springboot.service.AdjustRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            adjustRoom.setFinishTime(java.time.LocalDateTime.now().format(formatter));
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

    @Override
    public Page findByBuild(Integer pageNum, Integer pageSize, String search, int dormbuildId) {
        Page page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AdjustRoom> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            qw.like("username", search);
        }
        // 通过currentRoomId查dormbuildId
        // 需要join或in子查询，这里用in实现
        // 查找所有属于该楼栋的房间号
        java.util.List<Integer> roomIds = baseMapper.selectRoomIdsByBuildId(dormbuildId);
        if (roomIds == null || roomIds.isEmpty()) {
            // 没有房间，返回空
            return page;
        }
        qw.in("currentroom_id", roomIds);
        return this.page(page, qw);
    }

}
