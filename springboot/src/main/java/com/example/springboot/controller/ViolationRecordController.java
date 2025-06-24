package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.ViolationRecord;
import com.example.springboot.service.ViolationRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/violation")
public class ViolationRecordController {

    @Resource
    private ViolationRecordService violationRecordService;

    @GetMapping("/list")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page<ViolationRecord> page = violationRecordService.findPage(pageNum, pageSize, search);
        return Result.success(page);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody ViolationRecord violationRecord) {
        int result = violationRecordService.addViolationRecord(violationRecord);
        return result > 0 ? Result.success() : Result.error("-1", "添加失败");
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ViolationRecord violationRecord) {
        int result = violationRecordService.updateViolationRecord(violationRecord);
        return result > 0 ? Result.success() : Result.error("-1", "更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        int result = violationRecordService.deleteViolationRecord(id);
        return result > 0 ? Result.success() : Result.error("-1", "删除失败");
    }

    @PutMapping("/process")
    public Result<?> process(@RequestBody Map<String, Object> payload) {
        Integer id = (Integer) payload.get("id");
        String handleResult = (String) payload.get("handleResult");
        String handler = (String) payload.get("handler"); // 前端应传递处理人信息
        int result = violationRecordService.processViolationRecord(id, handleResult, handler);
        return result > 0 ? Result.success() : Result.error("-1", "处理失败");
    }
} 