package com.example.springboot.service.impl;

import com.example.springboot.entity.BatchStudentImportRecord;
import com.example.springboot.entity.BatchStudentImportDetail;
import com.example.springboot.mapper.BatchStudentImportRecordMapper;
import com.example.springboot.mapper.BatchStudentImportDetailMapper;
import com.example.springboot.service.BatchStudentImportService;
import com.example.springboot.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BatchStudentImportServiceImpl implements BatchStudentImportService {

    @Resource
    private BatchStudentImportRecordMapper recordMapper;
    @Resource
    private BatchStudentImportDetailMapper detailMapper;
    @Resource
    private StudentService studentService;

    @Override
    @Transactional
    public void createBatch(BatchStudentImportRecord record, List<BatchStudentImportDetail> details) {
        recordMapper.insert(record);
        for (BatchStudentImportDetail detail : details) {
            detail.setBatchNo(record.getBatchNo());
            detailMapper.insert(detail);
        }
    }

    @Override
    public BatchStudentImportRecord getBatchByNo(String batchNo) {
        return recordMapper.selectOne(new QueryWrapper<BatchStudentImportRecord>().eq("batch_no", batchNo));
    }

    @Override
    public List<BatchStudentImportDetail> getDetailsByBatchNo(String batchNo) {
        return detailMapper.selectList(new QueryWrapper<BatchStudentImportDetail>().eq("batch_no", batchNo));
    }

    @Override
    public List<BatchStudentImportRecord> getBatchList(Integer pageNum, Integer pageSize, String search) {
        QueryWrapper<BatchStudentImportRecord> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            queryWrapper.like("batch_no", search.trim())
                        .or()
                        .like("operator", search.trim());
        }
        queryWrapper.orderByDesc("create_time");
        int offset = (pageNum - 1) * pageSize;
        queryWrapper.last("LIMIT " + offset + ", " + pageSize);
        return recordMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void processBatch(String batchNo, String strategy) {
        BatchStudentImportRecord record = getBatchByNo(batchNo);
        if (record == null) {
            throw new RuntimeException("批次不存在");
        }
        // 只允许PENDING/VALIDATED状态执行
        if (!"PENDING".equals(record.getStatus()) && !"VALIDATED".equals(record.getStatus())) {
            throw new RuntimeException("该批次已执行或已完成，不能重复执行");
        }
        record.setStatus("PROCESSING");
        recordMapper.updateById(record);

        List<BatchStudentImportDetail> details = getDetailsByBatchNo(batchNo);
        int successCount = 0, failCount = 0;

        try {
            if ("ALL_OR_NOTHING".equals(strategy)) {
                // 先校验所有数据
                for (BatchStudentImportDetail detail : details) {
                    if (!"SUCCESS".equals(detail.getStatus())) {
                        throw new RuntimeException("存在无效数据，无法全部导入");
                    }
                }
                // 全部校验通过后批量插入
                for (BatchStudentImportDetail detail : details) {
                    try {
                        insertStudent(detail);
                        detail.setStatus("SUCCESS");
                        detail.setErrorMessage(null);
                        successCount++;
                    } catch (Exception e) {
                        detail.setStatus("FAILED");
                        detail.setErrorMessage(e.getMessage());
                        failCount++;
                    }
                    detailMapper.updateById(detail);
                }
            } else {
                // CONTINUE_ON_ERROR
                for (BatchStudentImportDetail detail : details) {
                    if (!"SUCCESS".equals(detail.getStatus())) {
                        failCount++;
                        continue;
                    }
                    try {
                        insertStudent(detail);
                        detail.setStatus("SUCCESS");
                        detail.setErrorMessage(null);
                        successCount++;
                    } catch (Exception e) {
                        detail.setStatus("FAILED");
                        detail.setErrorMessage(e.getMessage());
                        failCount++;
                    }
                    detailMapper.updateById(detail);
                }
            }
            record.setSuccessCount(successCount);
            record.setFailCount(failCount);
            record.setStatus("COMPLETED");
            recordMapper.updateById(record);
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setFailCount(details.size());
            recordMapper.updateById(record);
            throw e;
        }
    }

    // 辅助方法：插入学生
    private void insertStudent(BatchStudentImportDetail detail) {
        com.example.springboot.entity.Student stu = new com.example.springboot.entity.Student();
        stu.setUsername(detail.getUsername());
        stu.setName(detail.getName());
        stu.setGender(detail.getGender());
        stu.setAge(detail.getAge());
        stu.setPhoneNum(detail.getPhoneNum());
        stu.setEmail(detail.getEmail());
        stu.setPassword("123456");
        stu.setAvatar("");
        // 假设studentService.save有唯一性校验
        studentService.save(stu);
    }

    @Override
    public void deleteBatch(String batchNo) {
        BatchStudentImportRecord record = getBatchByNo(batchNo);
        if (record == null) {
            throw new RuntimeException("批次不存在");
        }
        if (!"PENDING".equals(record.getStatus()) && !"VALIDATED".equals(record.getStatus())) {
            throw new RuntimeException("该批次已执行或已完成，不能删除");
        }
        detailMapper.delete(new QueryWrapper<BatchStudentImportDetail>().eq("batch_no", batchNo));
        recordMapper.delete(new QueryWrapper<BatchStudentImportRecord>().eq("batch_no", batchNo));
    }

    @Override
    public void updateDetailStatus(BatchStudentImportDetail detail) {
        detailMapper.updateById(detail);
    }
} 