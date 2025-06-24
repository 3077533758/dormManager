package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.DormCompound;

import java.util.List;

public interface DormCompoundService extends IService<DormCompound> {

    //新增围合
    int addNewCompound(DormCompound dormCompound);

    //查询围合
    Page find(Integer pageNum, Integer pageSize, String search);

    //更新围合信息
    int updateNewCompound(DormCompound dormCompound);

    //删除围合信息
    int deleteCompound(Integer compoundId);

    //根据园区查询围合
    List<DormCompound> getCompoundsByCampus(String campus);

    //获取所有围合
    List<DormCompound> getAllCompounds();
} 