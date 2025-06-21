package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.QuitRoom;
import com.example.springboot.mapper.QuitRoomMapper;
import com.example.springboot.service.QuitRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class QuitRoomServiceImpl extends ServiceImpl<QuitRoomMapper, QuitRoom> implements QuitRoomService {

    @Resource
    private QuitRoomMapper quitRoomMapper;

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
}
