package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.DormManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DormManagerMapper extends BaseMapper<DormManager> {
    
    // 分页查询宿管信息（包含管辖区域信息）
    Page<DormManager> selectPageWithArea(Page<DormManager> page, @Param("search") String search);
    
    // 查询所有宿管信息（包含管辖区域信息）
    List<DormManager> selectListWithArea();
    
    // 根据用户名查询宿管信息（包含管辖区域信息）
    DormManager selectByUsernameWithArea(@Param("username") String username);
}
