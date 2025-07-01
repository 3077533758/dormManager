package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.StudentCheckin;
import com.example.springboot.entity.DormManager;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.service.StudentCheckinService;
import com.example.springboot.service.DormRoomService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/checkin")
public class StudentCheckinController {

    @Resource
    private StudentCheckinService studentCheckinService;

    @Resource
    private DormRoomService dormRoomService;

    @GetMapping("/list")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              HttpSession session) {
        // 获取用户身份和权限控制
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        
        String identity = identityObj.toString();
        Page<StudentCheckin> page;
        
        if ("admin".equals(identity)) {
            // 管理员可以看到所有入住记录
            page = studentCheckinService.findPage(pageNum, pageSize, search);
        } else if ("dormManager".equals(identity)) {
            // 宿管只能看到自己楼栋的入住记录
            if (!(userObj instanceof DormManager)) {
                return Result.error("-1", "无权限");
            }
            DormManager manager = (DormManager) userObj;
            Integer dormbuildId = manager.getDormbuildId();
            
            if (dormbuildId == null) {
                return Result.error("-1", "无权限：您没有分配管理的楼栋");
            }
            
            page = studentCheckinService.findPageByBuild(pageNum, pageSize, search, dormbuildId);
        } else {
            return Result.error("-1", "无权限");
        }
        
        return page != null ? Result.success(page) : Result.error("-1", "查询失败");
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody StudentCheckin studentCheckin, HttpSession session) {
        // 获取用户身份和权限控制
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        
        String identity = identityObj.toString();
        
        if ("admin".equals(identity)) {
            // 管理员可以添加所有楼栋的入住记录
        } else if ("dormManager".equals(identity)) {
            // 宿管只能添加自己楼栋的入住记录
            if (!(userObj instanceof DormManager)) {
                return Result.error("-1", "无权限");
            }
            DormManager manager = (DormManager) userObj;
            Integer dormbuildId = manager.getDormbuildId();
            
            if (dormbuildId == null) {
                return Result.error("-1", "无权限：您没有分配管理的楼栋");
            }
            
            // 检查房间是否属于宿管管理的楼栋
            DormRoom dormRoom = dormRoomService.getById(studentCheckin.getDormroomId());
            if (dormRoom == null) {
                return Result.error("-1", "房间不存在");
            }
            
            if (dormRoom.getDormBuildId() != dormbuildId) {
                return Result.error("-1", "无权限：该房间不在您的管辖范围内");
            }
        } else {
            return Result.error("-1", "无权限");
        }
        
        int result = studentCheckinService.addStudentCheckin(studentCheckin);
        if (result > 0) {
            return Result.success();
        } else if (result == -1) {
            return Result.error("-1", "学号不存在");
        } else if (result == -2) {
            return Result.error("-1", "房间不存在");
        } else if (result == -3) {
            return Result.error("-1", "床位号无效");
        } else if (result == -4) {
            return Result.error("-1", "床位已被占用");
        } else {
            return Result.error("-1", "添加失败");
        }
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody StudentCheckin studentCheckin, HttpSession session) {
        // 获取用户身份和权限控制
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        
        String identity = identityObj.toString();
        
        if ("admin".equals(identity)) {
            // 管理员可以更新所有楼栋的入住记录
        } else if ("dormManager".equals(identity)) {
            // 宿管只能更新自己楼栋的入住记录
            if (!(userObj instanceof DormManager)) {
                return Result.error("-1", "无权限");
            }
            DormManager manager = (DormManager) userObj;
            Integer dormbuildId = manager.getDormbuildId();
            
            if (dormbuildId == null) {
                return Result.error("-1", "无权限：您没有分配管理的楼栋");
            }
            
            // 检查房间是否属于宿管管理的楼栋
            DormRoom dormRoom = dormRoomService.getById(studentCheckin.getDormroomId());
            if (dormRoom == null) {
                return Result.error("-1", "房间不存在");
            }
            
            if (dormRoom.getDormBuildId() != dormbuildId) {
                return Result.error("-1", "无权限：该房间不在您的管辖范围内");
            }
        } else {
            return Result.error("-1", "无权限");
        }
        
        int result = studentCheckinService.updateStudentCheckin(studentCheckin);
        if (result > 0) {
            return Result.success();
        } else if (result == -1) {
            return Result.error("-1", "学号不存在");
        } else if (result == -2) {
            return Result.error("-1", "房间不存在");
        } else if (result == -3) {
            return Result.error("-1", "床位号无效");
        } else if (result == -4) {
            return Result.error("-1", "床位已被其他学生占用");
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id, HttpSession session) {
        // 获取用户身份和权限控制
        Object userObj = session.getAttribute("User");
        Object identityObj = session.getAttribute("Identity");
        
        if (userObj == null || identityObj == null) {
            return Result.error("-1", "无权限");
        }
        
        String identity = identityObj.toString();
        int result;
        
        if ("admin".equals(identity)) {
            // 管理员可以删除所有入住记录
            result = studentCheckinService.deleteById(id);
        } else if ("dormManager".equals(identity)) {
            // 宿管只能删除自己楼栋的入住记录
            if (!(userObj instanceof DormManager)) {
                return Result.error("-1", "无权限");
            }
            DormManager manager = (DormManager) userObj;
            Integer dormbuildId = manager.getDormbuildId();
            
            if (dormbuildId == null) {
                return Result.error("-1", "无权限：您没有分配管理的楼栋");
            }
            
            result = studentCheckinService.deleteByIdWithAuth(id, dormbuildId);
            if (result == -1) {
                return Result.error("-1", "记录不存在");
            } else if (result == -2) {
                return Result.error("-1", "无权限删除该记录");
            }
        } else {
            return Result.error("-1", "无权限");
        }
        
        return result == 1 ? Result.success() : Result.error("-1", "删除失败");
    }
} 