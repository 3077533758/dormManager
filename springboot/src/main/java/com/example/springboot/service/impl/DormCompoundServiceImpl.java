package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.DormCompound;
import com.example.springboot.mapper.DormCompoundMapper;
import com.example.springboot.service.DormCompoundService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DormCompoundServiceImpl extends ServiceImpl<DormCompoundMapper, DormCompound> implements DormCompoundService {

    /**
     * 注入DAO层对象
     */
    @Resource
    private DormCompoundMapper dormCompoundMapper;

    /**
     * 围合添加
     */
    @Override
    public int addNewCompound(DormCompound dormCompound) {
        int insert = dormCompoundMapper.insert(dormCompound);
        return insert;
    }

    /**
     * 围合查找
     */
    @Override
    public Page find(Integer pageNum, Integer pageSize, String search) {
        Page page = new Page<>(pageNum, pageSize);
        QueryWrapper<DormCompound> qw = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            qw.like("compound_name", search).or().like("campus", search);
        }
        Page compoundPage = dormCompoundMapper.selectPage(page, qw);
        return compoundPage;
    }

    /**
     * 围合信息更新
     */
    @Override
    public int updateNewCompound(DormCompound dormCompound) {
        int i = dormCompoundMapper.updateById(dormCompound);
        return i;
    }

    /**
     * 围合删除
     */
    @Override
    public int deleteCompound(Integer compoundId) {
        int i = dormCompoundMapper.deleteById(compoundId);
        return i;
    }

    /**
     * 根据园区查询围合
     */
    @Override
    public List<DormCompound> getCompoundsByCampus(String campus) {
        QueryWrapper<DormCompound> qw = new QueryWrapper<>();
        qw.eq("campus", campus);
        List<DormCompound> compounds = dormCompoundMapper.selectList(qw);
        return compounds;
    }

    /**
     * 获取所有围合
     */
    @Override
    public List<DormCompound> getAllCompounds() {
        List<DormCompound> compounds = dormCompoundMapper.selectList(null);
        return compounds;
    }
} 