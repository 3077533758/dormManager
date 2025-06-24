package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.ViolationRecord;

public interface ViolationRecordService {
    Page<ViolationRecord> findPage(Integer pageNum, Integer pageSize, String search);
    int addViolationRecord(ViolationRecord violationRecord);
    int updateViolationRecord(ViolationRecord violationRecord);
    int deleteViolationRecord(Integer id);
    int processViolationRecord(Integer id, String handleResult, String handler);
} 