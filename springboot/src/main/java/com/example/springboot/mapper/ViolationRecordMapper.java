package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.ViolationRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 违纪记录Mapper接口
 */
@Mapper
public interface ViolationRecordMapper extends BaseMapper<ViolationRecord> {
} 