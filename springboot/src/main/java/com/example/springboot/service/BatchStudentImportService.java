package com.example.springboot.service;

import com.example.springboot.entity.BatchStudentImportRecord;
import com.example.springboot.entity.BatchStudentImportDetail;
import java.util.List;

public interface BatchStudentImportService {
    void createBatch(BatchStudentImportRecord record, List<BatchStudentImportDetail> details);
    BatchStudentImportRecord getBatchByNo(String batchNo);
    List<BatchStudentImportDetail> getDetailsByBatchNo(String batchNo);
    List<BatchStudentImportRecord> getBatchList(Integer pageNum, Integer pageSize, String search);
    void processBatch(String batchNo, String strategy);
    void deleteBatch(String batchNo);
    void updateDetailStatus(BatchStudentImportDetail detail);
    // 其他批量相关方法
} 