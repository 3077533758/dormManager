package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.OutLive;
import com.example.springboot.mapper.OutLiveMapper;
import com.example.springboot.service.OutLiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OutLiveServiceImpl extends ServiceImpl<OutLiveMapper, OutLive> implements OutLiveService {

    @Resource
    private OutLiveMapper outLiveMapper;

    @Override
    public int addOutLive(OutLive outLive) {
        return outLiveMapper.insert(outLive);
    }

    @Override
    public int updateOutLive(OutLive outLive) {
        return outLiveMapper.updateById(outLive);
    }

    @Override
    public int deleteOutLive(Integer id) {
        return outLiveMapper.deleteById(id);
    }

    @Override
    public Page<OutLive> find(Integer pageNum, Integer pageSize, String search) {
        Page<OutLive> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OutLive> qw = new QueryWrapper<>();
        qw.like("username", search).or().like("name", search);
        return outLiveMapper.selectPage(page, qw);
    }

    @Override
    public Page findByUsername(String username, Integer pageNum, Integer pageSize) {
        Page<OutLive> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OutLive> qw = new QueryWrapper<>();
        qw.eq("username", username);
        qw.orderByDesc("apply_time");
        return outLiveMapper.selectPage(page, qw);
    }
}
