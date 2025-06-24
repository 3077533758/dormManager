package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormEvaluation;
import com.example.springboot.service.DormEvaluationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class DormEvaluationController {

    @Resource
    private DormEvaluationService dormEvaluationService;

    @GetMapping("/list")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page<DormEvaluation> page = dormEvaluationService.findPage(pageNum, pageSize, search);
        return Result.success(page);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody DormEvaluation dormEvaluation) {
        int result = dormEvaluationService.addDormEvaluation(dormEvaluation);
        return result > 0 ? Result.success() : Result.error("-1", "添加失败");
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody DormEvaluation dormEvaluation) {
        int result = dormEvaluationService.updateDormEvaluation(dormEvaluation);
        return result > 0 ? Result.success() : Result.error("-1", "更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        int result = dormEvaluationService.deleteDormEvaluation(id);
        return result > 0 ? Result.success() : Result.error("-1", "删除失败");
    }

    @PutMapping("/publish")
    public Result<?> publish(@RequestBody Map<String, Object> payload) {
        Integer id = (Integer) payload.get("id");
        String evaluator = (String) payload.get("evaluator");
        int result = dormEvaluationService.publishEvaluation(id, evaluator);
        return result > 0 ? Result.success() : Result.error("-1", "发布失败");
    }
} 