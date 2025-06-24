package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.QuitRoom;
import com.example.springboot.mapper.QuitRoomMapper;
import com.example.springboot.service.QuitRoomService;
import com.example.springboot.service.DormRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class QuitRoomServiceImpl extends ServiceImpl<QuitRoomMapper, QuitRoom> implements QuitRoomService {

    @Resource
    private QuitRoomMapper quitRoomMapper;
    @Resource
    private DormRoomService dormRoomService;

    @Override
    public int addQuit(QuitRoom quitRoom) {
        return quitRoomMapper.insert(quitRoom);
    }

    @Override
    public Page find(Integer pageNum, Integer pageSize, String search) {
        Page<QuitRoom> page = new Page<>(pageNum, pageSize);
        QueryWrapper<QuitRoom> qw = new QueryWrapper<>();
        qw.like("username", search);
        return quitRoomMapper.selectPage(page, qw);
    }

    @Override
    public Page findByUsername(String username, Integer pageNum, Integer pageSize) {
        Page<QuitRoom> page = new Page<>(pageNum, pageSize);
        QueryWrapper<QuitRoom> qw = new QueryWrapper<>();
        qw.eq("username", username);
        qw.orderByDesc("apply_time");
        return quitRoomMapper.selectPage(page, qw);
    }

    @Override
    public int deleteQuit(Integer id) {
        return quitRoomMapper.deleteById(id);
    }

    @Override
    public int updateQuit(QuitRoom quitRoom) {
        return quitRoomMapper.updateById(quitRoom);
    }

    @Override
    @Transactional
    public void approveQuitRoom(QuitRoom quitRoom) {
        // 1. 更新 quit_room
        quitRoom.setState("通过");
        quitRoom.setFinishTime(java.time.LocalDateTime.now().toString());
        quitRoomMapper.updateById(quitRoom);

        // 3. 清空 dorm_room 床位
        dormRoomService.deleteBedInfo("bed" + quitRoom.getBedNumber(), quitRoom.getDormRoomId(), -1);
    }

    @Override
    public boolean hasPendingQuit(String username) {
        QueryWrapper<QuitRoom> qw = new QueryWrapper<>();
        qw.eq("username", username).eq("state", "未处理");
        return quitRoomMapper.selectCount(qw) > 0;
    }
}
