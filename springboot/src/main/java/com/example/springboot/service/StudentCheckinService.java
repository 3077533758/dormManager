package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.StudentCheckin;

public interface StudentCheckinService {
    Page<StudentCheckin> findPage(Integer pageNum, Integer pageSize, String search);
    Page<StudentCheckin> findPageByBuild(Integer pageNum, Integer pageSize, String search, Integer dormbuildId);
    int addStudentCheckin(StudentCheckin studentCheckin);
    int updateStudentCheckin(StudentCheckin studentCheckin);
    int deleteById(Integer id);
    int deleteByIdWithAuth(Integer id, Integer dormbuildId);
} 