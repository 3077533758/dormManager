package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.entity.QuitRoom;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.QuitRoomService;
import com.example.springboot.common.JudgeBedName;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/quitRoom")
public class QuitRoomController {

    @Resource
    private QuitRoomService quitRoomService;

    @Resource
    private DormRoomService dormRoomService;

    /**
     * 添加退宿申请
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody QuitRoom quitRoom) {
        // 检查学生是否有未处理的退宿申请
        if (quitRoomService.hasPendingQuit(quitRoom.getUsername())) {
            return Result.error("-1", "您有未处理的退宿申请，不能重复申请");
        }
        // 检查学生是否有宿舍
        DormRoom dormRoom = dormRoomService.judgeHadBed(quitRoom.getUsername());
        if (dormRoom == null) {
            return Result.error("-1", "您当前没有宿舍，无法申请退宿");
        }
        
        // 自动生成申请时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        quitRoom.setApplyTime(now.format(formatter));
        quitRoom.setState("未处理");
        
        int result = quitRoomService.addQuit(quitRoom);
        return result == 1 ? Result.success() : Result.error("-1", "添加失败");
    }

    /**
     * 更新退宿申请（state为true代表"通过"）
     */
    @PutMapping("/update/{state}")
    public Result<?> update(@RequestBody QuitRoom quitRoom, @PathVariable Boolean state) {
        // 自动生成处理时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        quitRoom.setFinishTime(now.format(formatter));
        
        if (state) {
            String bedField = JudgeBedName.getBedName(quitRoom.getBedNumber());
            int updateResult = dormRoomService.deleteBedInfo(
                    bedField,
                    quitRoom.getDormRoomId(),
                    dormRoomService.judgeHadBed(quitRoom.getUsername()).getCurrentCapacity()
            );

            if (updateResult == -2) {
                return Result.error("-1", "重复操作或房间信息异常");
            }
        }

        int result = quitRoomService.updateQuit(quitRoom);
        return result == 1 ? Result.success() : Result.error("-1", "更新失败");
    }

    /**
     * 删除退宿申请
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id, javax.servlet.http.HttpSession session) {
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        String identity = identityObj.toString();
        QuitRoom quitRoom = quitRoomService.getById(id);
        if (quitRoom == null) {
            return Result.error("-1", "退宿申请不存在");
        }
        if ("stu".equals(identity)) {
            // 学生只能撤销自己未处理的退宿申请
            String username = null;
            if (userObj instanceof com.example.springboot.entity.Student) {
                username = ((com.example.springboot.entity.Student) userObj).getUsername();
            } else if (userObj instanceof com.example.springboot.entity.User) {
                username = ((com.example.springboot.entity.User) userObj).getUsername();
            }
            if (!quitRoom.getUsername().equals(username)) {
                return Result.error("-1", "只能撤销自己的退宿申请");
            }
            if (!"未处理".equals(quitRoom.getState())) {
                return Result.error("-1", "已处理的退宿申请无法撤销");
            }
        }
        int result = quitRoomService.deleteQuit(id);
        return result == 1 ? Result.success() : Result.error("-1", "删除失败");
    }

    /**
     * 分页查询退宿申请
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              javax.servlet.http.HttpSession session) {
        // 获取用户身份和权限控制
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        
        String identity = identityObj.toString();
        Page page;
        
        if ("admin".equals(identity)) {
            // 管理员可以看到所有申请
            page = quitRoomService.find(pageNum, pageSize, search);
        } else if ("dormManager".equals(identity)) {
            // 宿管只能看到自己楼栋的申请
            if (!(userObj instanceof com.example.springboot.entity.DormManager)) {
                return Result.error("-1", "无权限");
            }
            com.example.springboot.entity.DormManager manager = (com.example.springboot.entity.DormManager) userObj;
            int dormbuildId = manager.getDormbuildId();
            page = quitRoomService.findByBuild(pageNum, pageSize, search, dormbuildId);
        } else {
            return Result.error("-1", "无权限");
        }
        
        return page != null ? Result.success(page) : Result.error("-1", "查询失败");
    }

    /**
     * 根据用户名查询退宿申请（学生端使用）
     */
    @GetMapping("/findByUsername/{username}")
    public Result<?> findByUsername(@PathVariable String username,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Page page = quitRoomService.findByUsername(username, pageNum, pageSize);
        return page != null ? Result.success(page) : Result.error("-1", "查询失败");
    }

    /**
     * 退宿审批通过（同步三表）
     */
    @PutMapping("/approve")
    public Result<?> approve(@RequestBody QuitRoom quitRoom) {
        quitRoomService.approveQuitRoom(quitRoom);
        return Result.success();
    }
}
