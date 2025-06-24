package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.StudentCheckin;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.mapper.StudentCheckinMapper;
import com.example.springboot.mapper.StudentMapper;
import com.example.springboot.service.StudentCheckinService;
import com.example.springboot.service.DormRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import javax.servlet.http.HttpSession;

@Service
public class StudentCheckinServiceImpl implements StudentCheckinService {

    @Resource
    private StudentCheckinMapper studentCheckinMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private DormRoomService dormRoomService;

    @Resource
    private HttpSession session;

    @Override
    public Page<StudentCheckin> findPage(Integer pageNum, Integer pageSize, String search) {
        QueryWrapper<StudentCheckin> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            queryWrapper.like("student_name", search).or().like("student_username", search);
        }
        queryWrapper.orderByDesc("action_time");
        return studentCheckinMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public int addStudentCheckin(StudentCheckin studentCheckin) {
        // 添加前先查 student 表
        Student student = studentMapper.selectById(studentCheckin.getStudentUsername());
        if (student == null) {
            return -1; // 学号不存在
        }
        studentCheckin.setStudentName(student.getName());
        studentCheckin.setActionTime(LocalDateTime.now());
        // 自动设置操作人
        Object userObj = session.getAttribute("User");
        if (userObj != null) {
            try {
                java.lang.reflect.Method getName = userObj.getClass().getMethod("getName");
                String operator = (String) getName.invoke(userObj);
                studentCheckin.setOperator(operator);
            } catch (Exception e) {
                studentCheckin.setOperator("系统");
            }
        } else {
            studentCheckin.setOperator("系统");
        }
        // 只有 actionType 为"入住"时才写入寝室信息，否则置空
        if (!"入住".equals(studentCheckin.getActionType())) {
            studentCheckin.setDormbuildId(null);
            studentCheckin.setDormroomId(null);
            studentCheckin.setBedNumber(null);
        }
        int result = studentCheckinMapper.insert(studentCheckin);
        // 新增：同步更新dormroom表
        if (result > 0 && "入住".equals(studentCheckin.getActionType())) {
            String bedField = com.example.springboot.common.JudgeBedName.getBedName(studentCheckin.getBedNumber());
            DormRoom dormRoom = dormRoomService.getById(studentCheckin.getDormroomId());
            if (dormRoom != null) {
                // 设置床位为学号，current_capacity+1
                switch (studentCheckin.getBedNumber()) {
                    case 1: dormRoom.setFirstBed(studentCheckin.getStudentUsername()); break;
                    case 2: dormRoom.setSecondBed(studentCheckin.getStudentUsername()); break;
                    case 3: dormRoom.setThirdBed(studentCheckin.getStudentUsername()); break;
                    case 4: dormRoom.setFourthBed(studentCheckin.getStudentUsername()); break;
                }
                dormRoom.setCurrentCapacity(dormRoom.getCurrentCapacity() + 1);
                dormRoomService.updateById(dormRoom);
            }
        }
        return result;
    }

    @Override
    public int deleteById(Integer id) {
        return studentCheckinMapper.deleteById(id);
    }
} 