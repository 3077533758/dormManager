package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.DormBuild;
import com.example.springboot.entity.DormBuildDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DormBuildMapper extends BaseMapper<DormBuild> {

    /**
     * 分页查询楼栋信息（包含围合名称）
     */
    Page<DormBuildDTO> selectPageWithCompound(Page<DormBuildDTO> page, @Param("search") String search);

    /**
     * 查询所有楼栋信息（包含围合名称）
     */
    List<DormBuildDTO> selectListWithCompound();
} 