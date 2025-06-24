package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.entity.OutLive;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.OutLiveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/outLive")
public class OutLiveController {

    @Resource
    private OutLiveService outLiveService;

    @Resource
    private DormRoomService dormRoomService;

    /**
     * 添加外宿申请
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody OutLive outLive) {
        // 检查学生是否有宿舍
        DormRoom dormRoom = dormRoomService.judgeHadBed(outLive.getUsername());
        if (dormRoom == null) {
            return Result.error("-1", "您当前没有宿舍，无法申请外宿");
        }
        
        // 自动生成申请时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        outLive.setApplyTime(now.format(formatter));
        outLive.setState("未处理");
        
        int result = outLiveService.addOutLive(outLive);
        return result == 1 ? Result.success() : Result.error("-1", "添加失败");
    }

    /**
     * 更新外宿申请（state为true表示"通过"）
     */
    @PutMapping("/update/{state}")
    public Result<?> update(@RequestBody OutLive outLive, @PathVariable Boolean state) {
        // 自动生成处理时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        outLive.setFinishTime(now.format(formatter));
        
        // 这里只是更新状态，不涉及宿舍操作
        if (state) {
            outLive.setState("通过");
        } else {
            outLive.setState("驳回");
        }
        int result = outLiveService.updateOutLive(outLive);
        return result == 1 ? Result.success() : Result.error("-1", "更新失败");
    }

    /**
     * 删除外宿申请
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        int result = outLiveService.deleteOutLive(id);
        return result == 1 ? Result.success() : Result.error("-1", "删除失败");
    }

    /**
     * 分页查询外宿申请
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page page = outLiveService.find(pageNum, pageSize, search);
        return page != null ? Result.success(page) : Result.error("-1", "查询失败");
    }

    /**
     * 根据用户名查询外宿申请（学生端使用）
     */
    @GetMapping("/findByUsername/{username}")
    public Result<?> findByUsername(@PathVariable String username,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Page page = outLiveService.findByUsername(username, pageNum, pageSize);
        return page != null ? Result.success(page) : Result.error("-1", "查询失败");
    }

    /**
     * 外宿审批通过（同步三表）
     */
    @PutMapping("/approve")
    public Result<?> approve(@RequestBody OutLive outLive) {
        outLiveService.approveOutLive(outLive);
        return Result.success();
    }
}
