package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.DormManager;

import java.util.List;

public interface DormManagerService extends IService<DormManager> {

    //学生登陆
    DormManager dormManagerLogin(String username, String password);

    //新增学生
    int addNewDormManager(DormManager dormManager);

    //查询学生
    Page find(Integer pageNum, Integer pageSize, String search);

    //更新学生信息
    int updateNewDormManager(DormManager dormManager);

    //删除学生信息
    int deleteDormManager(String username);

    // 分页查询宿管信息（包含管辖区域信息）
    Page<DormManager> selectPageWithArea(Page<DormManager> page, String search);
    
    // 查询所有宿管信息（包含管辖区域信息）
    List<DormManager> selectListWithArea();
    
    // 根据用户名查询宿管信息（包含管辖区域信息）
    DormManager selectByUsernameWithArea(String username);
    
    // 添加宿管（支持楼栋宿管和围合宿管）
    boolean addManager(DormManager dormManager);
    
    // 更新宿管信息（支持楼栋宿管和围合宿管）
    boolean updateManager(DormManager dormManager);
}
