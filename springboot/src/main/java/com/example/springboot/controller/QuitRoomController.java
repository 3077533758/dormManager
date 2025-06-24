package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.QuitRoom;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.QuitRoomService;
import com.example.springboot.common.JudgeBedName;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
        int result = quitRoomService.addQuit(quitRoom);
        return result == 1 ? Result.success() : Result.error("-1", "添加失败");
    }

    /**
     * 更新退宿申请（state为true代表“通过”）
     */
    @PutMapping("/update/{state}")
    public Result<?> update(@RequestBody QuitRoom quitRoom, @PathVariable Boolean state) {
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
    public Result<?> delete(@PathVariable Integer id) {
        int result = quitRoomService.deleteQuit(id);
        return result == 1 ? Result.success() : Result.error("-1", "删除失败");
    }

    /**
     * 分页查询退宿申请
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page page = quitRoomService.find(pageNum, pageSize, search);
        return page != null ? Result.success(page) : Result.error("-1", "查询失败");
    }
}
