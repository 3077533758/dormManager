package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.DormBuild;
import com.example.springboot.entity.DormBuildDTO;

import java.util.List;


public interface DormBuildService extends IService<DormBuild> {

    //新增楼宇
    int addNewBuilding(DormBuild dormBuild);

    //查询楼宇
    Page find(Integer pageNum, Integer pageSize, String search);

    //查询楼宇（包含围合信息）
    Page<DormBuildDTO> findWithCompound(Integer pageNum, Integer pageSize, String search);

    //更新楼宇信息
    int updateNewBuilding(DormBuild dormBuild);

    //删除楼宇信息
    int deleteBuilding(Integer id);

    List<DormBuild> getBuildingId();

    //获取所有楼栋信息（包含围合信息）
    List<DormBuildDTO> getAllBuildingsWithCompound();
}
