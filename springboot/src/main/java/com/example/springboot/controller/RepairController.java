package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.entity.Repair;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.RepairService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/repair")
public class RepairController {

    @Resource
    private RepairService repairService;

    @Resource
    private DormRoomService dormRoomService;

    /**
     * 添加订单
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Repair repair) {
        // 检查学生是否有宿舍
        DormRoom dormRoom = dormRoomService.judgeHadBed(repair.getRepairer());
        if (dormRoom == null) {
            return Result.error("-1", "您当前没有宿舍，无法申请报修");
        }
        // 自动生成订单创建时间
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        repair.setOrderBuildTime(now.format(formatter));
        int i = repairService.addNewOrder(repair);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "添加失败");
        }
    }

    /**
     * 更新订单
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody Repair repair) {
        int i = repairService.updateNewOrder(repair);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id, javax.servlet.http.HttpSession session) {
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        String identity = identityObj.toString();
        Repair repair = repairService.getById(id);
        if (repair == null) {
            return Result.error("-1", "报修申请不存在");
        }
        if ("stu".equals(identity)) {
            // 学生只能撤销自己未完成的报修申请
            String username = null;
            if (userObj instanceof com.example.springboot.entity.Student) {
                username = ((com.example.springboot.entity.Student) userObj).getUsername();
            } else if (userObj instanceof com.example.springboot.entity.User) {
                username = ((com.example.springboot.entity.User) userObj).getUsername();
            }
            if (!repair.getRepairer().equals(username)) {
                return Result.error("-1", "只能撤销自己的报修申请");
            }
            if (!"未完成".equals(repair.getState())) {
                return Result.error("-1", "已完成的报修申请无法撤销");
            }
        }
        int i = repairService.deleteOrder(id);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "删除失败");
        }
    }

    /**
     * 查找订单
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page page = repairService.find(pageNum, pageSize, search);
        if (page != null) {
            return Result.success(page);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 个人申报报修 分页查询
     */
    @GetMapping("/find/{name}")
    public Result<?> individualFind(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(defaultValue = "") String search,
                                    @PathVariable String name) {
        System.out.println(name);
        Page page = repairService.individualFind(pageNum, pageSize, search, name);
        if (page != null) {
            return Result.success(page);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 首页顶部：报修统计
     */
    @GetMapping("/orderNum")
    public Result<?> orderNum() {
        int num = repairService.showOrderNum();
        if (num >= 0) {
            return Result.success(num);
        } else {
            return Result.error("-1", "报修统计查询失败");
        }
    }
}
