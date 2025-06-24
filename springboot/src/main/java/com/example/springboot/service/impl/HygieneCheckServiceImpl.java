package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.HygieneCheck;
import com.example.springboot.mapper.HygieneCheckMapper;
import com.example.springboot.service.HygieneCheckService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 卫生检查Service实现类
 */
@Service
public class HygieneCheckServiceImpl implements HygieneCheckService {
    
    @Resource
    private HygieneCheckMapper hygieneCheckMapper;
    
    @Override
    public Page<HygieneCheck> findPage(Integer pageNum, Integer pageSize, String search) {
        QueryWrapper<HygieneCheck> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            queryWrapper.like("dormroom_id", search)
                    .or().like("checker", search)
                    .or().like("grade", search);
        }
        queryWrapper.orderByDesc("check_date");
        
        Page<HygieneCheck> page = new Page<>(pageNum, pageSize);
        return hygieneCheckMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public int addHygieneCheck(HygieneCheck hygieneCheck) {
        // 计算总分
        int totalScore = hygieneCheck.getDoorWindowScore() + 
                        hygieneCheck.getItemPlacementScore() + 
                        hygieneCheck.getBeddingScore() + 
                        hygieneCheck.getCleanlinessScore() + 
                        hygieneCheck.getOverallScore();
        hygieneCheck.setTotalScore(totalScore);
        
        // 根据总分确定等级
        if (totalScore >= 85) {
            hygieneCheck.setGrade("优");
        } else if (totalScore >= 75) {
            hygieneCheck.setGrade("良");
        } else if (totalScore >= 60) {
            hygieneCheck.setGrade("中");
        } else {
            hygieneCheck.setGrade("差");
        }
        
        return hygieneCheckMapper.insert(hygieneCheck);
    }
    
    @Override
    public int updateHygieneCheck(HygieneCheck hygieneCheck) {
        // 重新计算总分和等级
        int totalScore = hygieneCheck.getDoorWindowScore() + 
                        hygieneCheck.getItemPlacementScore() + 
                        hygieneCheck.getBeddingScore() + 
                        hygieneCheck.getCleanlinessScore() + 
                        hygieneCheck.getOverallScore();
        hygieneCheck.setTotalScore(totalScore);
        
        if (totalScore >= 85) {
            hygieneCheck.setGrade("优");
        } else if (totalScore >= 75) {
            hygieneCheck.setGrade("良");
        } else if (totalScore >= 60) {
            hygieneCheck.setGrade("中");
        } else {
            hygieneCheck.setGrade("差");
        }
        
        return hygieneCheckMapper.updateById(hygieneCheck);
    }
    
    @Override
    public int deleteHygieneCheck(Integer id) {
        return hygieneCheckMapper.deleteById(id);
    }
    
    @Override
    public Page<HygieneCheck> findByRoomId(Integer roomId, Integer pageNum, Integer pageSize) {
        QueryWrapper<HygieneCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dormroom_id", roomId);
        queryWrapper.orderByDesc("check_date");
        
        Page<HygieneCheck> page = new Page<>(pageNum, pageSize);
        return hygieneCheckMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public Double getAverageScoreByRoom(Integer roomId) {
        QueryWrapper<HygieneCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dormroom_id", roomId);
        queryWrapper.select("AVG(total_score) as avg_score");
        
        List<HygieneCheck> list = hygieneCheckMapper.selectList(queryWrapper);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getTotalScore().doubleValue();
        }
        return 0.0;
    }
} 