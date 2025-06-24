package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.StudentCheckin;
import com.example.springboot.service.StudentCheckinService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/checkin")
public class StudentCheckinController {

    @Resource
    private StudentCheckinService studentCheckinService;

    @GetMapping("/list")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page<StudentCheckin> page = studentCheckinService.findPage(pageNum, pageSize, search);
        return Result.success(page);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody StudentCheckin studentCheckin) {
        int result = studentCheckinService.addStudentCheckin(studentCheckin);
        return result > 0 ? Result.success() : Result.error("-1", "添加失败或学号不存在");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        int result = studentCheckinService.deleteById(id);
        return result == 1 ? Result.success() : Result.error("-1", "删除失败");
    }
} 