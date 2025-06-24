package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.StudentCheckin;

public interface StudentCheckinService {
    Page<StudentCheckin> findPage(Integer pageNum, Integer pageSize, String search);
    int addStudentCheckin(StudentCheckin studentCheckin);
    int deleteById(Integer id);
} 