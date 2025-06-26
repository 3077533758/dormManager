package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.DormBuild;
import com.example.springboot.entity.DormBuildDTO;
import com.example.springboot.mapper.DormBuildMapper;
import com.example.springboot.service.DormBuildService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DormBuildImpl extends ServiceImpl<DormBuildMapper, DormBuild> implements DormBuildService {


    /**
     * 注入DAO层对象
     */
    @Resource
    private DormBuildMapper dormBuildMapper;

    /**
     * 楼宇添加
     */
    @Override
    public int addNewBuilding(DormBuild dormBuild) {
        int insert = dormBuildMapper.insert(dormBuild);
        return insert;
    }

    /**
     * 楼宇查找
     */
    @Override
    public Page find(Integer pageNum, Integer pageSize, String search) {
        Page page = new Page<>(pageNum, pageSize);
        QueryWrapper<DormBuild> qw = new QueryWrapper<>();
        qw.like("DormBuild_id", search);
        Page buildingPage = dormBuildMapper.selectPage(page, qw);
        return buildingPage;
    }

    /**
     * 楼宇查找（包含围合信息）
     */
    @Override
    public Page<DormBuildDTO> findWithCompound(Integer pageNum, Integer pageSize, String search) {
        Page<DormBuildDTO> page = new Page<>(pageNum, pageSize);
        Page<DormBuildDTO> buildingPage = dormBuildMapper.selectPageWithCompound(page, search);
        return buildingPage;
    }

    /**
     * 楼宇信息更新
     */
    @Override
    public int updateNewBuilding(DormBuild dormBuild) {
        int i = dormBuildMapper.updateById(dormBuild);
        return i;
    }

    /**
     * 楼宇删除
     */
    @Override
    public int deleteBuilding(Integer id) {
        int i = dormBuildMapper.deleteById(id);
        return i;
    }

    /**
     * 首页 获取建筑名称
     */
    @Override
    public List<DormBuild> getBuildingId() {
        QueryWrapper<DormBuild> qw = new QueryWrapper<>();
        qw.select("dormbuild_id");
        List<DormBuild> dormBuilds = dormBuildMapper.selectList(qw);
        return dormBuilds;
    }

    /**
     * 获取所有楼栋信息（包含围合信息）
     */
    @Override
    public List<DormBuildDTO> getAllBuildingsWithCompound() {
        List<DormBuildDTO> buildings = dormBuildMapper.selectListWithCompound();
        return buildings;
    }

    /**
     * 根据园区ID获取楼栋列表
     */
    @Override
    public List<DormBuildDTO> getBuildingsByCompound(Integer compoundId) {
        System.out.println("Service层接收到的园区ID: " + compoundId);
        System.out.println("园区ID类型: " + (compoundId != null ? compoundId.getClass().getName() : "null"));
        
        QueryWrapper<DormBuild> qw = new QueryWrapper<>();
        qw.eq("compound_id", compoundId);
        System.out.println("查询条件: compound_id = " + compoundId);
        
        List<DormBuild> buildings = dormBuildMapper.selectList(qw);
        System.out.println("数据库查询到的楼栋数量: " + buildings.size());
        
        // 打印查询到的楼栋信息
        for (DormBuild building : buildings) {
            System.out.println("楼栋ID: " + building.getDormBuildId() + 
                             ", 楼栋名: " + building.getDormBuildName() + 
                             ", 园区ID: " + building.getCompoundId());
        }
        
        // 转换为 DormBuildDTO
        List<DormBuildDTO> result = buildings.stream().map(building -> {
            DormBuildDTO dto = new DormBuildDTO();
            dto.setId(building.getId());
            dto.setDormBuildId(building.getDormBuildId());
            dto.setDormBuildName(building.getDormBuildName());
            dto.setDormBuildDetail(building.getDormBuildDetail());
            dto.setCompoundId(building.getCompoundId());
            return dto;
        }).collect(Collectors.toList());
        
        System.out.println("转换后的DTO数量: " + result.size());
        return result;
    }

}
