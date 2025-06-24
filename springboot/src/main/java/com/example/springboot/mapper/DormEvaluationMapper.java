package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.DormEvaluation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 寝室评比Mapper接口
 */
@Mapper
public interface DormEvaluationMapper extends BaseMapper<DormEvaluation> {
} 