package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.DormManager;
import com.example.springboot.mapper.DormManagerMapper;
import com.example.springboot.service.DormManagerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DormManagerServiceImpl extends ServiceImpl<DormManagerMapper, DormManager> implements DormManagerService {

    /**
     * 注入DAO层对象
     */
    @Resource
    private DormManagerMapper dormManagerMapper;

    /**
     * 宿管登录
     */
    @Override
    public DormManager dormManagerLogin(String username, String password) {
        QueryWrapper<DormManager> qw = new QueryWrapper<>();
        qw.eq("username", username);
        qw.eq("password", password);
        DormManager dormManager = dormManagerMapper.selectOne(qw);
        if (dormManager != null) {
            return dormManager;
        } else {
            return null;
        }
    }

    /**
     * 宿管新增
     */
    @Override
    public int addNewDormManager(DormManager dormManager) {
        int insert = dormManagerMapper.insert(dormManager);
        return insert;
    }

    /**
     * 宿管查找
     */
    @Override
    public Page find(Integer pageNum, Integer pageSize, String search) {
        Page page = new Page<>(pageNum, pageSize);
        QueryWrapper<DormManager> qw = new QueryWrapper<>();
        qw.like("name", search);
        Page dormManagerPage = dormManagerMapper.selectPage(page, qw);
        return dormManagerPage;
    }

    /**
     * 宿管信息更新
     */
    @Override
    public int updateNewDormManager(DormManager dormManager) {
        int i = dormManagerMapper.updateById(dormManager);
        return i;
    }

    /**
     * 宿管删除
     */
    @Override
    public int deleteDormManager(String username) {
        return baseMapper.deleteById(username);
    }

    @Override
    public Page<DormManager> selectPageWithArea(Page<DormManager> page, String search) {
        return baseMapper.selectPageWithArea(page, search);
    }

    @Override
    public List<DormManager> selectListWithArea() {
        return baseMapper.selectListWithArea();
    }

    @Override
    public DormManager selectByUsernameWithArea(String username) {
        return baseMapper.selectByUsernameWithArea(username);
    }

    @Override
    public boolean addManager(DormManager dormManager) {
        // 只校验楼栋宿管的 dormbuildId
        if (dormManager.getDormbuildId() == null) {
            throw new RuntimeException("宿管必须指定管理的楼栋");
        }
        return save(dormManager);
    }

    @Override
    public boolean updateManager(DormManager dormManager) {
        // 只校验楼栋宿管的 dormbuildId
        if (dormManager.getDormbuildId() == null) {
            throw new RuntimeException("宿管必须指定管理的楼栋");
        }
        return updateById(dormManager);
    }
}
