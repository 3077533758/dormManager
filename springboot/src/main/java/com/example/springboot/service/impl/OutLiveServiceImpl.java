package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.OutLive;
import com.example.springboot.mapper.OutLiveMapper;
import com.example.springboot.service.OutLiveService;
import com.example.springboot.service.DormRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OutLiveServiceImpl extends ServiceImpl<OutLiveMapper, OutLive> implements OutLiveService {

    @Resource
    private OutLiveMapper outLiveMapper;
    @Resource
    private DormRoomService dormRoomService;

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
        Page<OutLive> resultPage = outLiveMapper.selectPage(page, qw);
        // 补充每条的dormRoomId和dormBuildId
        for (OutLive outLive : resultPage.getRecords()) {
            if (outLive.getUsername() != null) {
                com.example.springboot.entity.DormRoom dormRoom = dormRoomService.judgeHadBed(outLive.getUsername());
                if (dormRoom != null) {
                    outLive.setDormRoomId(dormRoom.getDormRoomId());
                    outLive.setDormBuildId(dormRoom.getDormBuildId());
                }
            }
        }
        return resultPage;
    }

    @Override
    public Page findByUsername(String username, Integer pageNum, Integer pageSize) {
        Page<OutLive> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OutLive> qw = new QueryWrapper<>();
        qw.eq("username", username);
        qw.orderByDesc("apply_time");
        return outLiveMapper.selectPage(page, qw);
    }

    @Override
    @Transactional
    public void approveOutLive(OutLive outLive) {
        // 1. 更新 out_live
        outLive.setState("通过");
        outLive.setFinishTime(java.time.LocalDateTime.now().toString());
        outLiveMapper.updateById(outLive);

        // 2. 如需腾空床位，可调用 dormRoomService.deleteBedInfo
        // dormRoomService.deleteBedInfo(...);
    }

    @Override
    public Page findByBuild(Integer pageNum, Integer pageSize, String search, int dormbuildId) {
        Page<OutLive> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OutLive> qw = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            qw.like("username", search).or().like("name", search);
        }
        
        // 先获取所有符合搜索条件的外宿申请
        Page<OutLive> allPage = outLiveMapper.selectPage(page, qw);
        
        // 过滤出属于指定楼栋的申请
        java.util.List<OutLive> filteredRecords = new java.util.ArrayList<>();
        for (OutLive outLive : allPage.getRecords()) {
            if (outLive.getUsername() != null) {
                com.example.springboot.entity.DormRoom dormRoom = dormRoomService.judgeHadBed(outLive.getUsername());
                if (dormRoom != null && dormRoom.getDormBuildId() == dormbuildId) {
                    outLive.setDormRoomId(dormRoom.getDormRoomId());
                    outLive.setDormBuildId(dormRoom.getDormBuildId());
                    filteredRecords.add(outLive);
                }
            }
        }
        
        // 重新计算分页
        int total = filteredRecords.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        java.util.List<OutLive> pagedRecords = new java.util.ArrayList<>();
        if (start < total) {
            pagedRecords = filteredRecords.subList(start, end);
        }
        
        Page<OutLive> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(pagedRecords);
        resultPage.setTotal(total);
        
        return resultPage;
    }
}
