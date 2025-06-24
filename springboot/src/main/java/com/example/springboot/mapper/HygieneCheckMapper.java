package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.HygieneCheck;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卫生检查Mapper接口
 */
@Mapper
public interface HygieneCheckMapper extends BaseMapper<HygieneCheck> {
} 