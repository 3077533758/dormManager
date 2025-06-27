package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.common.JudgeBedName;
import com.example.springboot.entity.AdjustRoom;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.mapper.DormRoomMapper;
import com.example.springboot.service.DormRoomService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.example.springboot.common.CalPeopleNum.calNum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DormRoomImpl extends ServiceImpl<DormRoomMapper, DormRoom> implements DormRoomService {

    @Resource
    private DormRoomMapper dormRoomMapper;

    /**
     * 首页顶部：空宿舍统计
     */
    @Override
    public int notFullRoom() {
        QueryWrapper<DormRoom> qw = new QueryWrapper<>();
        qw.lt("current_capacity", 4);
        int notFullRoomNum = Math.toIntExact(dormRoomMapper.selectCount(qw));
        return notFullRoomNum;
    }

    /**
     * 添加房间
     */
    @Override
    public int addNewRoom(DormRoom dormRoom) {
        if (!isValidRoomId(dormRoom.getDormRoomId())) {
            return -2;
        }
        int insert = dormRoomMapper.insert(dormRoom);
        return insert;
    }

    /**
     * 查找房间
     */
    @Override
    public Page find(Integer pageNum, Integer pageSize, String search) {
        Page page = new Page<>(pageNum, pageSize);
        QueryWrapper<DormRoom> qw = new QueryWrapper<>();
        qw.like("Dormroom_id", search);
        Page roomPage = dormRoomMapper.selectPage(page, qw);
        return roomPage;
    }

    /**
     * 更新房间
     */
    @Override
    public int updateNewRoom(DormRoom dormRoom) {
        if (!isValidRoomId(dormRoom.getDormRoomId())) {
            return -2;
        }
        int i = dormRoomMapper.updateById(dormRoom);
        return i;
    }

    /**
     * 删除房间
     */
    @Override
    public int deleteRoom(Integer dormRoomId) {
        int i = dormRoomMapper.deleteById(dormRoomId);
        return i;
    }

    /**
     * 删除床位上的学生信息
     */
    @Override
    public int deleteBedInfo(String bedName, Integer dormRoomId, int calCurrentNum) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("dormroom_id", dormRoomId);
        updateWrapper.set(bedName, null);
        updateWrapper.set("current_capacity", calCurrentNum - 1);
        int update = dormRoomMapper.update(null, updateWrapper);
        return update;

    }

    /**
     * 床位信息，查询该学生是否已由床位
     */
    @Override
    public DormRoom judgeHadBed(String username) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("first_bed", username);
        qw.or();
        qw.eq("second_bed", username);
        qw.or();
        qw.eq("third_bed", username);
        qw.or();
        qw.eq("fourth_bed", username);
        DormRoom dormRoom = dormRoomMapper.selectOne(qw);
        return dormRoom;
    }

    /**
     * 主页 住宿人数
     */
    @Override
    public Long selectHaveRoomStuNum() {
        QueryWrapper<DormRoom> qw1 = new QueryWrapper<>();
        qw1.isNotNull("first_bed");
        Long first_bed = dormRoomMapper.selectCount(qw1);

        QueryWrapper<DormRoom> qw2 = new QueryWrapper<>();
        qw2.isNotNull("second_bed");
        Long second_bed = dormRoomMapper.selectCount(qw2);

        QueryWrapper<DormRoom> qw3 = new QueryWrapper<>();
        qw3.isNotNull("third_bed");
        Long third_bed = dormRoomMapper.selectCount(qw3);

        QueryWrapper<DormRoom> qw4 = new QueryWrapper<>();
        qw4.isNotNull("fourth_bed");
        Long fourth_bed = dormRoomMapper.selectCount(qw4);

        Long count = first_bed + second_bed + third_bed + fourth_bed;
        return count;
    }

    /**
     * 获取每栋宿舍学生总人数
     */
    @Override
    public Long getEachBuildingStuNum(int dormBuildId) {

        QueryWrapper<DormRoom> qw1 = new QueryWrapper<>();
        qw1.eq("dormbuild_id", dormBuildId);
        qw1.isNotNull("first_bed");
        Long first_bed = dormRoomMapper.selectCount(qw1);

        QueryWrapper<DormRoom> qw2 = new QueryWrapper<>();
        qw2.eq("dormbuild_id", dormBuildId);
        qw2.isNotNull("second_bed");
        Long second_bed = dormRoomMapper.selectCount(qw2);

        QueryWrapper<DormRoom> qw3 = new QueryWrapper<>();
        qw3.eq("dormbuild_id", dormBuildId);
        qw3.isNotNull("third_bed");
        Long third_bed = dormRoomMapper.selectCount(qw3);

        QueryWrapper<DormRoom> qw4 = new QueryWrapper<>();
        qw4.eq("dormbuild_id", dormBuildId);
        qw4.isNotNull("fourth_bed");
        Long fourth_bed = dormRoomMapper.selectCount(qw4);

        Long count = first_bed + second_bed + third_bed + fourth_bed;

        return count;
    }

    /**
     * 根据调宿申请表对房间表内的学生床位进行调整
     */
    @Override
    public int adjustRoomUpdate(AdjustRoom adjustRoom) {
        //调宿人
        String username = adjustRoom.getUsername();
        //当前房间号
        int currentRoomId = adjustRoom.getCurrentRoomId();
        //当前床位名称
        String currentBedName = JudgeBedName.getBedName(adjustRoom.getCurrentBedId());
        //目标房间号
        int towardsRoomId = adjustRoom.getTowardsRoomId();
        //目标床位名称
        String towardsBedName = JudgeBedName.getBedName(adjustRoom.getTowardsBedId());

        // 1. 检查当前房间和床位是否存在
        QueryWrapper qw = new QueryWrapper();
        qw.eq("dormroom_id", currentRoomId);
        qw.isNotNull(currentBedName);
        DormRoom dormRoom1 = dormRoomMapper.selectOne(qw);

        if (dormRoom1 == null) {
            return -2; // 当前床位不存在
        }
        
        // 2. 检查目标房间是否存在
        DormRoom dormRoom2 = dormRoomMapper.selectById(towardsRoomId);
        if (dormRoom2 == null) {
            return -3; // 目标房间不存在
        }
        
        // 3. 检查目标床位是否为空
        String targetBedStudent = null;
        switch (adjustRoom.getTowardsBedId()) {
            case 1: targetBedStudent = dormRoom2.getFirstBed(); break;
            case 2: targetBedStudent = dormRoom2.getSecondBed(); break;
            case 3: targetBedStudent = dormRoom2.getThirdBed(); break;
            case 4: targetBedStudent = dormRoom2.getFourthBed(); break;
            default: return -4; // 目标床位号无效
        }
        
        if (targetBedStudent != null && !targetBedStudent.trim().isEmpty()) {
            return -5; // 目标床位已被占用
        }

        // 4. 执行调宿操作
        if (currentRoomId == towardsRoomId) {
            // 同寝室调宿：只需要交换床位，不改变总人数
            UpdateWrapper uw = new UpdateWrapper();
            uw.eq("dormroom_id", currentRoomId);
            uw.set(currentBedName, null);
            uw.set(towardsBedName, username);
            int result = dormRoomMapper.update(null, uw);
            return result;
        } else {
            // 跨寝室调宿：需要调整两个房间的人数
            // 4.1 清空当前床位并减少人数
            UpdateWrapper uw1 = new UpdateWrapper();
            uw1.eq("dormroom_id", currentRoomId);
            uw1.set(currentBedName, null);
            uw1.setSql("current_capacity = current_capacity - 1");
            int result1 = dormRoomMapper.update(null, uw1);

            if (result1 == 1) {
                // 4.2 设置目标床位并增加人数
                UpdateWrapper uw2 = new UpdateWrapper();
                uw2.eq("dormroom_id", towardsRoomId);
                uw2.set(towardsBedName, username);
                uw2.setSql("current_capacity = current_capacity + 1");
                int result2 = dormRoomMapper.update(null, uw2);
                return result2;
            }
            return -1;
        }
    }


    /**
     * 检查该房间是否满了
     */
    @Override
    public DormRoom checkRoomState(Integer dormRoomId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("dormroom_id", dormRoomId);
        qw.lt("current_capacity", 4);
        DormRoom dormRoom = dormRoomMapper.selectOne(qw);
        return dormRoom;
    }

    /**
     * 检查该房间是否存在
     */
    @Override
    public DormRoom checkRoomExist(Integer dormRoomId) {
        DormRoom dormRoom = dormRoomMapper.selectById(dormRoomId);
        return dormRoom;
    }


    /**
     * 检查床位是否有人
     */
    @Override
    public DormRoom checkBedState(Integer dormRoomId, int bedNum) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("dormroom_id", dormRoomId);
        qw.isNull(JudgeBedName.getBedName(bedNum));
        DormRoom dormRoom = dormRoomMapper.selectOne(qw);
        return dormRoom;
    }

    private boolean isValidRoomId(Integer roomId) {
        if (roomId == null) return false;
        String idStr = roomId.toString();
        if (idStr.length() < 4) return false;
        String last3 = idStr.substring(idStr.length() - 3);
        int floor = Integer.parseInt(last3.substring(0, 1));
        int room = Integer.parseInt(last3.substring(1));
        if (floor < 1 || floor > 9 || room < 1 || room > 99) return false;
        return true;
    }

    /**
     * 根据楼栋ID获取房间列表
     */
    @Override
    public List<DormRoom> getRoomsByBuild(Integer buildId) {
        QueryWrapper<DormRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dormbuild_id", buildId);
        queryWrapper.orderByAsc("dormroom_id");
        return dormRoomMapper.selectList(queryWrapper);
    }

    /**
     * 根据房间ID获取床位列表
     */
    @Override
    public List<Map<String, Object>> getBedsByRoom(Integer roomId) {
        DormRoom room = dormRoomMapper.selectById(roomId);
        if (room == null) {
            return null;
        }

        List<Map<String, Object>> beds = new ArrayList<>();
        int maxCapacity = room.getMaxCapacity();
        String[] bedFields = {room.getFirstBed(), room.getSecondBed(), room.getThirdBed(), room.getFourthBed()};

        for (int i = 1; i <= maxCapacity; i++) {
            Map<String, Object> bed = new HashMap<>();
            bed.put("bedNumber", i);
            String studentUsername = bedFields[i - 1];
            bed.put("occupied", studentUsername != null && !studentUsername.trim().isEmpty());
            beds.add(bed);
        }

        return beds;
    }

}
