package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.HygieneCheck;
import com.example.springboot.service.HygieneCheckService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 卫生检查Controller
 */
@RestController
@RequestMapping("/hygiene")
public class HygieneCheckController {
    
    @Resource
    private HygieneCheckService hygieneCheckService;
    
    /**
     * 分页查询卫生检查记录
     */
    @GetMapping("/list")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page<HygieneCheck> page = hygieneCheckService.findPage(pageNum, pageSize, search);
        return Result.success(page);
    }
    
    /**
     * 添加卫生检查记录
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody HygieneCheck hygieneCheck) {
        int result = hygieneCheckService.addHygieneCheck(hygieneCheck);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error("-1", "添加失败");
        }
    }
    
    /**
     * 更新卫生检查记录
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody HygieneCheck hygieneCheck) {
        int result = hygieneCheckService.updateHygieneCheck(hygieneCheck);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error("-1", "更新失败");
        }
    }
    
    /**
     * 删除卫生检查记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        int result = hygieneCheckService.deleteHygieneCheck(id);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error("-1", "删除失败");
        }
    }
    
    /**
     * 根据房间号查询卫生检查记录
     */
    @GetMapping("/room/{roomId}")
    public Result<?> findByRoomId(@PathVariable Integer roomId,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<HygieneCheck> page = hygieneCheckService.findByRoomId(roomId, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取房间平均卫生得分
     */
    @GetMapping("/score/{roomId}")
    public Result<?> getAverageScore(@PathVariable Integer roomId) {
        Double score = hygieneCheckService.getAverageScoreByRoom(roomId);
        return Result.success(score);
    }
} 