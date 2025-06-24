package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormCompound;
import com.example.springboot.service.DormCompoundService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/compound")
public class DormCompoundController {

    @Resource
    private DormCompoundService dormCompoundService;

    /**
     * 围合添加
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody DormCompound dormCompound) {
        int i = dormCompoundService.addNewCompound(dormCompound);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "添加失败");
        }
    }

    /**
     * 围合信息更新
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody DormCompound dormCompound) {
        int i = dormCompoundService.updateNewCompound(dormCompound);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    /**
     * 围合删除
     */
    @DeleteMapping("/delete/{compoundId}")
    public Result<?> delete(@PathVariable Integer compoundId) {
        int i = dormCompoundService.deleteCompound(compoundId);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "删除失败");
        }
    }

    /**
     * 围合查找
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page page = dormCompoundService.find(pageNum, pageSize, search);
        if (page != null) {
            return Result.success(page);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 根据园区查询围合
     */
    @GetMapping("/getByCampus/{campus}")
    public Result<?> getByCampus(@PathVariable String campus) {
        List<DormCompound> compounds = dormCompoundService.getCompoundsByCampus(campus);
        if (compounds != null) {
            return Result.success(compounds);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 获取所有围合
     */
    @GetMapping("/getAll")
    public Result<?> getAll() {
        List<DormCompound> compounds = dormCompoundService.getAllCompounds();
        if (compounds != null) {
            return Result.success(compounds);
        } else {
            return Result.error("-1", "查询失败");
        }
    }
} 