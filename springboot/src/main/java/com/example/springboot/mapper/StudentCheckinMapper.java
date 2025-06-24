package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.StudentCheckin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生入住记录Mapper接口
 */
@Mapper
public interface StudentCheckinMapper extends BaseMapper<StudentCheckin> {
} 