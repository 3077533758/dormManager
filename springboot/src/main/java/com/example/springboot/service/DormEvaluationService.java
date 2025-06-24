package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.DormEvaluation;

public interface DormEvaluationService {
    Page<DormEvaluation> findPage(Integer pageNum, Integer pageSize, String search);
    int addDormEvaluation(DormEvaluation dormEvaluation);
    int updateDormEvaluation(DormEvaluation dormEvaluation);
    int deleteDormEvaluation(Integer id);
    int publishEvaluation(Integer id, String evaluator);
} 