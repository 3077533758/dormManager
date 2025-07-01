package com.example.springboot.service;

import com.example.springboot.entity.BatchCheckinRecord;
import com.example.springboot.entity.BatchCheckinDetail;
import java.util.List;

public interface BatchCheckinService {
    void createBatch(BatchCheckinRecord record, List<BatchCheckinDetail> details);
    BatchCheckinRecord getBatchByNo(String batchNo);
    List<BatchCheckinDetail> getDetailsByBatchNo(String batchNo);
    List<BatchCheckinRecord> getBatchList(Integer pageNum, Integer pageSize, String search, Integer userBuildId);
    void processBatch(String batchNo, String strategy, Integer userBuildId);
    void deleteBatch(String batchNo);
    // 其他批量相关方法
} 