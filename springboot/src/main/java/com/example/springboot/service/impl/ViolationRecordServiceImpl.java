package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.ViolationRecord;
import com.example.springboot.mapper.ViolationRecordMapper;
import com.example.springboot.service.ViolationRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ViolationRecordServiceImpl implements ViolationRecordService {

    @Resource
    private ViolationRecordMapper violationRecordMapper;

    @Override
    public Page<ViolationRecord> findPage(Integer pageNum, Integer pageSize, String search) {
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            // 支持按学生姓名、房间号、违纪类型、状态搜索
            queryWrapper.and(wrapper -> wrapper
                .like("student_name", search)
                .or().like("dormroom_id", search)
                .or().like("violation_type", search)
                .or().like("status", search)
            );
        }
        queryWrapper.orderByDesc("violation_date");
        return violationRecordMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public int addViolationRecord(ViolationRecord violationRecord) {
        violationRecord.setStatus("待处理");
        return violationRecordMapper.insert(violationRecord);
    }

    @Override
    public int updateViolationRecord(ViolationRecord violationRecord) {
        return violationRecordMapper.updateById(violationRecord);
    }

    @Override
    public int deleteViolationRecord(Integer id) {
        return violationRecordMapper.deleteById(id);
    }

    @Override
    public int processViolationRecord(Integer id, String handleResult, String handler) {
        ViolationRecord record = violationRecordMapper.selectById(id);
        if (record != null) {
            record.setStatus("已处理");
            record.setHandleResult(handleResult);
            record.setHandleTime(LocalDateTime.now());
            record.setHandler(handler);
            return violationRecordMapper.updateById(record);
        }
        return 0;
    }
} 