package com.example.springboot.service.impl;

import com.example.springboot.entity.BatchCheckinRecord;
import com.example.springboot.entity.BatchCheckinDetail;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.mapper.BatchCheckinRecordMapper;
import com.example.springboot.mapper.BatchCheckinDetailMapper;
import com.example.springboot.service.BatchCheckinService;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;

@Service
public class BatchCheckinServiceImpl implements BatchCheckinService {

    @Resource
    private BatchCheckinRecordMapper recordMapper;
    @Resource
    private BatchCheckinDetailMapper detailMapper;
    @Resource
    private DormRoomService dormRoomService;
    @Resource
    private StudentService studentService;

    @Override
    @Transactional
    public void createBatch(BatchCheckinRecord record, List<BatchCheckinDetail> details) {
        recordMapper.insert(record);
        for (BatchCheckinDetail detail : details) {
            detail.setBatchNo(record.getBatchNo());
            detailMapper.insert(detail);
        }
    }

    @Override
    public BatchCheckinRecord getBatchByNo(String batchNo) {
        return recordMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BatchCheckinRecord>().eq("batch_no", batchNo));
    }

    @Override
    public List<BatchCheckinDetail> getDetailsByBatchNo(String batchNo) {
        return detailMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BatchCheckinDetail>().eq("batch_no", batchNo));
    }

    @Override
    public List<BatchCheckinRecord> getBatchList(Integer pageNum, Integer pageSize, String search, Integer userBuildId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BatchCheckinRecord> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 如果有搜索条件
        if (search != null && !search.trim().isEmpty()) {
            queryWrapper.like("batch_no", search.trim())
                       .or()
                       .like("operator", search.trim());
        }
        
        // 如果是宿管，只能看到自己楼栋的批次
        if (userBuildId != null) {
            // 通过详情表关联查询，只返回包含该楼栋房间的批次
            queryWrapper.exists("SELECT 1 FROM batch_checkin_detail d WHERE d.batch_no = batch_checkin_record.batch_no AND d.dormbuild_id = " + userBuildId);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        // 分页查询
        int offset = (pageNum - 1) * pageSize;
        queryWrapper.last("LIMIT " + offset + ", " + pageSize);
        
        return recordMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void processBatch(String batchNo, String strategy, Integer userBuildId) {
        // 获取批次记录
        BatchCheckinRecord record = getBatchByNo(batchNo);
        if (record == null) {
            throw new RuntimeException("批次不存在");
        }

        // 更新状态为处理中
        record.setStatus("PROCESSING");
        recordMapper.updateById(record);

        // 获取详情数据
        List<BatchCheckinDetail> details = getDetailsByBatchNo(batchNo);
        
        int successCount = 0;
        int failCount = 0;

        try {
            if ("ALL_OR_NOTHING".equals(strategy)) {
                // 全部成功或全部失败策略
                processAllOrNothing(details, userBuildId);
                successCount = details.size();
            } else {
                // 跳过错误继续处理策略
                for (BatchCheckinDetail detail : details) {
                    try {
                        processSingleCheckin(detail, userBuildId);
                        detail.setStatus("SUCCESS");
                        detail.setProcessTime(new Date());
                        detailMapper.updateById(detail);
                        successCount++;
                    } catch (Exception e) {
                        detail.setStatus("FAILED");
                        detail.setErrorMessage(e.getMessage());
                        detail.setProcessTime(new Date());
                        detailMapper.updateById(detail);
                        failCount++;
                    }
                }
            }

            // 更新批次记录
            record.setSuccessCount(successCount);
            record.setFailCount(failCount);
            record.setStatus("COMPLETED");
            record.setCompleteTime(new Date());
            recordMapper.updateById(record);

        } catch (Exception e) {
            // 处理失败
            record.setStatus("FAILED");
            record.setCompleteTime(new Date());
            recordMapper.updateById(record);
            throw e;
        }
    }

    /**
     * 全部成功或全部失败策略
     */
    private void processAllOrNothing(List<BatchCheckinDetail> details, Integer userBuildId) {
        // 先验证所有数据
        for (BatchCheckinDetail detail : details) {
            validateCheckinData(detail, userBuildId);
        }

        // 全部验证通过后，批量处理
        for (BatchCheckinDetail detail : details) {
            processSingleCheckin(detail, userBuildId);
            detail.setStatus("SUCCESS");
            detail.setProcessTime(new Date());
            detailMapper.updateById(detail);
        }
    }

    /**
     * 验证入住数据
     */
    private void validateCheckinData(BatchCheckinDetail detail, Integer userBuildId) {
        // 0. 检查学号和姓名是否匹配存在
        Student student = studentService.getByUsernameAndName(detail.getStudentUsername(), detail.getStudentName());
        if (student == null) {
            throw new RuntimeException("学号与姓名不匹配或不存在: " + detail.getStudentUsername() + ", " + detail.getStudentName());
        }

        // 2. 检查学生是否已有宿舍
        if (dormRoomService.judgeHadBed(detail.getStudentUsername()) != null) {
            throw new RuntimeException("学生已有宿舍: " + detail.getStudentUsername());
        }

        // 3. 检查房间是否存在
        DormRoom room = dormRoomService.getById(detail.getDormroomId());
        if (room == null) {
            throw new RuntimeException("房间不存在: " + detail.getDormroomId());
        }

        // 4.1 只有宿管需要校验房间是否属于自己楼栋
        if (userBuildId != null && room.getDormBuildId() != userBuildId) {
            throw new RuntimeException("房间不在您的管辖范围内: " + detail.getDormroomId());
        }

        // 4. 检查床位是否可用
        String occupiedStudent = null;
        switch (detail.getBedNumber()) {
            case 1: occupiedStudent = room.getFirstBed(); break;
            case 2: occupiedStudent = room.getSecondBed(); break;
            case 3: occupiedStudent = room.getThirdBed(); break;
            case 4: occupiedStudent = room.getFourthBed(); break;
            default: throw new RuntimeException("床位号无效: " + detail.getBedNumber());
        }

        if (occupiedStudent != null && !occupiedStudent.trim().isEmpty()) {
            throw new RuntimeException("床位已被占用: " + detail.getDormroomId() + "-" + detail.getBedNumber());
        }
    }

    /**
     * 处理单条入住记录
     */
    private void processSingleCheckin(BatchCheckinDetail detail, Integer userBuildId) {
        // 验证数据
        validateCheckinData(detail, userBuildId);

        // 获取房间信息
        DormRoom room = dormRoomService.getById(detail.getDormroomId());
        
        // 更新房间床位信息
        switch (detail.getBedNumber()) {
            case 1: room.setFirstBed(detail.getStudentUsername()); break;
            case 2: room.setSecondBed(detail.getStudentUsername()); break;
            case 3: room.setThirdBed(detail.getStudentUsername()); break;
            case 4: room.setFourthBed(detail.getStudentUsername()); break;
        }
        
        // 更新当前容量
        room.setCurrentCapacity(room.getCurrentCapacity() + 1);
        dormRoomService.updateById(room);
    }

    @Override
    public void deleteBatch(String batchNo) {
        // 先删详情，再删批次
        detailMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BatchCheckinDetail>().eq("batch_no", batchNo));
        recordMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BatchCheckinRecord>().eq("batch_no", batchNo));
    }
} 