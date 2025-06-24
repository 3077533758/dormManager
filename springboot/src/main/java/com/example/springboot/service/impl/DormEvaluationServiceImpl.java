package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.DormEvaluation;
import com.example.springboot.mapper.DormEvaluationMapper;
import com.example.springboot.service.DormEvaluationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DormEvaluationServiceImpl implements DormEvaluationService {

    @Resource
    private DormEvaluationMapper dormEvaluationMapper;

    @Override
    public Page<DormEvaluation> findPage(Integer pageNum, Integer pageSize, String search) {
        QueryWrapper<DormEvaluation> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            queryWrapper.like("dormroom_id", search)
                    .or().like("evaluation_period", search)
                    .or().like("star_level", search);
        }
        queryWrapper.orderByDesc("evaluation_period");
        return dormEvaluationMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    private void calculateScores(DormEvaluation dormEvaluation) {
        // 模拟计算总分和星级
        // 实际场景中，卫生分和绩点分可能需要从其他服务查询得到
        if (dormEvaluation.getHygieneScore() == null) {
            dormEvaluation.setHygieneScore(BigDecimal.ZERO);
        }
        if (dormEvaluation.getGpaScore() == null) {
            dormEvaluation.setGpaScore(BigDecimal.ZERO);
        }

        // 示例权重：卫生分占60%，绩点分占40%
        BigDecimal totalScore = dormEvaluation.getHygieneScore().multiply(new BigDecimal("0.6"))
                .add(dormEvaluation.getGpaScore().multiply(new BigDecimal("0.4")));
        dormEvaluation.setTotalScore(totalScore.setScale(2, BigDecimal.ROUND_HALF_UP));

        // 根据分数评定星级
        // 注意：按比例评选的逻辑会更复杂，这里简化为按分数阈值
        if (totalScore.compareTo(new BigDecimal("90")) >= 0) {
            dormEvaluation.setStarLevel("五星");
        } else if (totalScore.compareTo(new BigDecimal("85")) >= 0) {
            dormEvaluation.setStarLevel("四星");
        } else if (totalScore.compareTo(new BigDecimal("80")) >= 0) {
            dormEvaluation.setStarLevel("三星");
        } else if (totalScore.compareTo(new BigDecimal("75")) >= 0) {
            dormEvaluation.setStarLevel("二星");
        } else {
            dormEvaluation.setStarLevel("无星");
        }
    }

    @Override
    public int addDormEvaluation(DormEvaluation dormEvaluation) {
        calculateScores(dormEvaluation);
        dormEvaluation.setStatus("待评选");
        return dormEvaluationMapper.insert(dormEvaluation);
    }

    @Override
    public int updateDormEvaluation(DormEvaluation dormEvaluation) {
        calculateScores(dormEvaluation);
        return dormEvaluationMapper.updateById(dormEvaluation);
    }

    @Override
    public int deleteDormEvaluation(Integer id) {
        return dormEvaluationMapper.deleteById(id);
    }

    @Override
    public int publishEvaluation(Integer id, String evaluator) {
        DormEvaluation evaluation = dormEvaluationMapper.selectById(id);
        if (evaluation != null) {
            evaluation.setStatus("已发布");
            evaluation.setEvaluator(evaluator);
            evaluation.setEvaluationTime(LocalDateTime.now());
            return dormEvaluationMapper.updateById(evaluation);
        }
        return 0;
    }
} 