package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormRoom;
import com.example.springboot.entity.StudentCheckin;
import com.example.springboot.entity.DormBuildDTO;
import com.example.springboot.entity.DormCompound;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.DormBuild;
import com.example.springboot.service.DormRoomService;
import com.example.springboot.service.StudentCheckinService;
import com.example.springboot.service.DormBuildService;
import com.example.springboot.service.DormCompoundService;
import com.example.springboot.service.StudentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/room")
public class DormRoomController {

    @Resource
    private DormRoomService dormRoomService;
    @Resource
    private StudentCheckinService studentCheckinService;
    @Resource
    private DormBuildService dormBuildService;
    @Resource
    private DormCompoundService dormCompoundService;
    @Resource
    private StudentService studentService;

    /**
     * 添加房间
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody DormRoom dormRoom) {
        int i = dormRoomService.addNewRoom(dormRoom);
        if (i == 1) {
            return Result.success();
        } else if (i == -2) {
            return Result.error("-1", "房间号不合法，格式为楼栋+楼层(1-9)+房号(01-99)");
        } else {
            return Result.error("-1", "添加失败");
        }
    }

    /**
     * 更新房间
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody DormRoom dormRoom) {
        int i = dormRoomService.updateNewRoom(dormRoom);
        if (i == 1) {
            return Result.success();
        } else if (i == -2) {
            return Result.error("-1", "房间号不合法，格式为楼栋+楼层(1-9)+房号(01-99)");
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    /**
     * 删除房间
     */
    @DeleteMapping("/delete/{dormRoomId}")
    public Result<?> delete(@PathVariable Integer dormRoomId) {
        int i = dormRoomService.deleteRoom(dormRoomId);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "删除失败");
        }
    }

    /**
     * 查找房间
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page page = dormRoomService.find(pageNum, pageSize, search);
        if (page != null) {
            return Result.success(page);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 首页顶部：空宿舍统计
     */
    @GetMapping("/noFullRoom")
    public Result<?> noFullRoom() {
        int num = dormRoomService.notFullRoom();
        if (num >= 0) {
            return Result.success(num);
        } else {
            return Result.error("-1", "空宿舍查询失败");
        }
    }

    /**
     * 删除床位学生信息
     */
    @DeleteMapping("/delete/{bedName}/{dormRoomId}/{calCurrentNum}")
    public Result<?> deleteBedInfo(@PathVariable String bedName, @PathVariable Integer dormRoomId, @PathVariable int calCurrentNum) {
        int i = dormRoomService.deleteBedInfo(bedName, dormRoomId, calCurrentNum);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "删除失败");
        }
    }

    /**
     * 床位信息，查询该学生是否已由床位
     */
    @GetMapping("/judgeHadBed/{value}")
    public Result<?> judgeHadBed(@PathVariable String value) {
        DormRoom dormRoom = dormRoomService.judgeHadBed(value);
        if (dormRoom == null) {
            return Result.success();
        } else {
            return Result.error("-1", "该学生已有宿舍");
        }
    }

    /**
     * 主页 住宿人数
     */
    @GetMapping("/selectHaveRoomStuNum")
    public Result<?> selectHaveRoomStuNum() {
        Long count = dormRoomService.selectHaveRoomStuNum();
        if (count >= 0) {
            return Result.success(count);
        } else {
            return Result.error("-1", "查询首页住宿人数失败");
        }
    }

    /**
     * 住宿分布人数
     */
    @GetMapping("/getEachBuildingStuNum/{num}")
    public Result<?> getEachBuildingStuNum(@PathVariable int num) {
        ArrayList<Long> arrayList = new ArrayList();
        for (int i = 1; i <= num; i++) {
            Long eachBuildingStuNum = dormRoomService.getEachBuildingStuNum(i);
            arrayList.add(eachBuildingStuNum);
        }

        if (!arrayList.isEmpty()) {
            return Result.success(arrayList);
        } else {
            return Result.error("-1", "获取人数失败");
        }
    }

    /**
     * 学生功能： 我的宿舍
     */
    @GetMapping("/getMyRoom/{name}")
    public Result<?> getMyRoom(@PathVariable String name) {
        DormRoom dormRoom = dormRoomService.judgeHadBed(name);
        if (dormRoom != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("dormRoomId", dormRoom.getDormRoomId());
            result.put("floorNum", dormRoom.getFloorNum());
            result.put("maxCapacity", dormRoom.getMaxCapacity());
            result.put("currentCapacity", dormRoom.getCurrentCapacity());
            result.put("firstBed", dormRoom.getFirstBed());
            result.put("secondBed", dormRoom.getSecondBed());
            result.put("thirdBed", dormRoom.getThirdBed());
            result.put("fourthBed", dormRoom.getFourthBed());
            result.put("dormBuildId", dormRoom.getDormBuildId());
            // 查楼栋
            DormBuild build = dormBuildService.getById(dormRoom.getDormBuildId());
            if (build != null) {
                result.put("dormBuildName", build.getDormBuildName());
                // 查园区
                DormCompound compound = dormCompoundService.getById(build.getCompoundId());
                if (compound != null) {
                    result.put("compoundName", compound.getCompoundName());
                    result.put("campusName", compound.getCampus());
                }
            }
            return Result.success(result);
        } else {
            return Result.error("-1", "信息不存在，请联系管理员。");
        }
    }

    /**
     * 检查房间是否满员
     */
    @GetMapping("/checkRoomState/{dormRoomId}")
    public Result<?> checkRoomState(@PathVariable Integer dormRoomId) {
        DormRoom dormRoom = dormRoomService.checkRoomState(dormRoomId);
        if (dormRoom != null) {
            return Result.success(dormRoom);
        } else {
            return Result.error("-1", "该房间人满了");
        }
    }

    /**
     * 检查床位是否已经有人
     */
    @GetMapping("/checkBedState/{dormRoomId}/{bedNum}")
    public Result<?> getMyRoom(@PathVariable Integer dormRoomId, @PathVariable int bedNum) {
        DormRoom dormRoom = dormRoomService.checkBedState(dormRoomId, bedNum);
        if (dormRoom != null) {
            return Result.success(dormRoom);
        } else {
            return Result.error("-1", "该床位已有人");
        }
    }

    /**
     * 检查房间是否满员
     */
    @GetMapping("/checkRoomExist/{dormRoomId}")
    public Result<?> checkRoomExist(@PathVariable Integer dormRoomId) {
        DormRoom dormRoom = dormRoomService.checkRoomExist(dormRoomId);
        if (dormRoom != null) {
            return Result.success(dormRoom);
        } else {
            return Result.error("-1", "未查询到该房间");
        }
    }

    /**
     * 楼栋-楼层-房间分布及入住情况
     */
    @GetMapping("/floor-distribution")
    public Result<?> getFloorDistribution() {
        List<DormRoom> allRooms = dormRoomService.list();
        // buildId -> floorNum -> List<room>
        Map<Integer, Map<Integer, List<Map<String, Object>>>> buildMap = new HashMap<>();
        for (DormRoom room : allRooms) {
            int buildId = room.getDormBuildId();
            int floorNum = room.getFloorNum();
            buildMap.putIfAbsent(buildId, new HashMap<>());
            Map<Integer, List<Map<String, Object>>> floorMap = buildMap.get(buildId);
            floorMap.putIfAbsent(floorNum, new ArrayList<>());
            // 统计入住情况（只查 dorm_room 表的床位分配字段）
            List<Map<String, Object>> beds = new ArrayList<>();
            int maxCapacity = room.getMaxCapacity();
            int currentCapacity = 0;
            String[] bedFields = {room.getFirstBed(), room.getSecondBed(), room.getThirdBed(), room.getFourthBed()};
            for (int i = 1; i <= maxCapacity; i++) {
                Map<String, Object> bed = new HashMap<>();
                bed.put("bedNumber", i);
                String studentUsername = bedFields[i - 1];
                if (studentUsername != null) {
                    Map<String, Object> stu = new HashMap<>();
                    stu.put("username", studentUsername);
                    // 可选：查 student 表获取姓名
                    bed.put("student", stu);
                    currentCapacity++;
                } else {
                    bed.put("student", null);
                }
                beds.add(bed);
            }
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomId", room.getDormRoomId());
            roomInfo.put("roomName", room.getDormRoomId());
            roomInfo.put("currentCapacity", currentCapacity);
            roomInfo.put("maxCapacity", maxCapacity);
            roomInfo.put("beds", beds);
            floorMap.get(floorNum).add(roomInfo);
        }
        return Result.success(buildMap);
    }

    /**
     * 围合-楼栋-楼层-房间-床位分布及入住情况
     */
    @GetMapping("/compound-distribution")
    public Result<?> getCompoundDistribution() {
        List<DormCompound> compounds = dormCompoundService.getAllCompounds();
        List<DormBuildDTO> builds = dormBuildService.getAllBuildingsWithCompound();
        List<DormRoom> rooms = dormRoomService.list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (DormCompound compound : compounds) {
            Map<String, Object> compoundMap = new HashMap<>();
            compoundMap.put("compoundId", compound.getCompoundId());
            compoundMap.put("compoundName", compound.getCompoundName());
            List<Map<String, Object>> buildList = new ArrayList<>();
            for (DormBuildDTO build : builds) {
                if (!Objects.equals(build.getCompoundId(), compound.getCompoundId())) continue;
                Map<String, Object> buildMap = new HashMap<>();
                buildMap.put("buildId", build.getDormBuildId());
                buildMap.put("buildName", build.getDormBuildName());
                // 按楼层分组
                Map<Integer, List<Map<String, Object>>> floorMap = new HashMap<>();
                for (DormRoom room : rooms) {
                    if (room.getDormBuildId() != build.getDormBuildId()) continue;
                    int floorNum = room.getFloorNum();
                    floorMap.putIfAbsent(floorNum, new ArrayList<>());
                    // 组装房间和床位
                    List<Map<String, Object>> beds = new ArrayList<>();
                    int maxCapacity = room.getMaxCapacity();
                    int currentCapacity = 0;
                    String[] bedFields = {room.getFirstBed(), room.getSecondBed(), room.getThirdBed(), room.getFourthBed()};
                    for (int i = 1; i <= maxCapacity; i++) {
                        Map<String, Object> bed = new HashMap<>();
                        bed.put("bedNumber", i);
                        String studentUsername = bedFields[i - 1];
                        if (studentUsername != null) {
                            Map<String, Object> stu = new HashMap<>();
                            stu.put("username", studentUsername);
                            bed.put("student", stu);
                            currentCapacity++;
                        } else {
                            bed.put("student", null);
                        }
                        beds.add(bed);
                    }
                    Map<String, Object> roomInfo = new HashMap<>();
                    roomInfo.put("roomId", room.getDormRoomId());
                    roomInfo.put("roomName", room.getDormRoomId());
                    roomInfo.put("currentCapacity", currentCapacity);
                    roomInfo.put("maxCapacity", maxCapacity);
                    roomInfo.put("beds", beds);
                    floorMap.get(floorNum).add(roomInfo);
                }
                // 转为 floors 列表
                List<Map<String, Object>> floors = new ArrayList<>();
                for (Map.Entry<Integer, List<Map<String, Object>>> entry : floorMap.entrySet()) {
                    Map<String, Object> floor = new HashMap<>();
                    floor.put("floorNum", entry.getKey());
                    floor.put("rooms", entry.getValue());
                    floors.add(floor);
                }
                buildMap.put("floors", floors);
                buildList.add(buildMap);
            }
            compoundMap.put("builds", buildList);
            result.add(compoundMap);
        }
        return Result.success(result);
    }

    /**
     * 园区楼栋寝室监控统计
     * 返回每个园区的楼栋、房间、床位统计信息
     */
    @GetMapping("/campus-monitor")
    public Result<?> getCampusMonitor() {
        List<DormCompound> compounds = dormCompoundService.getAllCompounds();
        List<DormBuildDTO> builds = dormBuildService.getAllBuildingsWithCompound();
        List<DormRoom> rooms = dormRoomService.list();
        
        // 按园区分组统计
        Map<String, Map<String, Object>> campusMap = new HashMap<>();
        
        for (DormCompound compound : compounds) {
            String campus = compound.getCampus();
            campusMap.putIfAbsent(campus, new HashMap<>());
            Map<String, Object> campusData = campusMap.get(campus);
            
            // 初始化园区数据
            if (!campusData.containsKey("campusName")) {
                campusData.put("campusName", campus);
                campusData.put("totalBuildings", 0);
                campusData.put("totalRooms", 0);
                campusData.put("totalBeds", 0);
                campusData.put("occupiedBeds", 0);
                campusData.put("availableBeds", 0);
                campusData.put("occupancyRate", 0.0);
                campusData.put("compounds", new ArrayList<>());
            }
            
            // 统计该围合下的楼栋和房间
            List<Map<String, Object>> compoundBuilds = new ArrayList<>();
            int compoundBuildings = 0;
            int compoundRooms = 0;
            int compoundBeds = 0;
            int compoundOccupiedBeds = 0;
            
            for (DormBuildDTO build : builds) {
                if (!Objects.equals(build.getCompoundId(), compound.getCompoundId())) continue;
                
                compoundBuildings++;
                Map<String, Object> buildData = new HashMap<>();
                buildData.put("buildId", build.getDormBuildId());
                buildData.put("buildName", build.getDormBuildName());
                buildData.put("compoundName", compound.getCompoundName());
                
                // 统计该楼栋的房间和床位
                int buildRooms = 0;
                int buildBeds = 0;
                int buildOccupiedBeds = 0;
                
                for (DormRoom room : rooms) {
                    if (room.getDormBuildId() != build.getDormBuildId()) continue;
                    
                    buildRooms++;
                    int maxCapacity = room.getMaxCapacity();
                    buildBeds += maxCapacity;
                    
                    // 统计已入住床位
                    String[] bedFields = {room.getFirstBed(), room.getSecondBed(), room.getThirdBed(), room.getFourthBed()};
                    for (String bedField : bedFields) {
                        if (bedField != null) {
                            buildOccupiedBeds++;
                        }
                    }
                }
                
                buildData.put("rooms", buildRooms);
                buildData.put("beds", buildBeds);
                buildData.put("occupiedBeds", buildOccupiedBeds);
                buildData.put("availableBeds", buildBeds - buildOccupiedBeds);
                buildData.put("occupancyRate", buildBeds > 0 ? Math.round((double) buildOccupiedBeds / buildBeds * 10000) / 100.0 : 0.0);
                
                compoundBuilds.add(buildData);
                compoundRooms += buildRooms;
                compoundBeds += buildBeds;
                compoundOccupiedBeds += buildOccupiedBeds;
            }
            
            // 更新园区统计
            Map<String, Object> compoundData = new HashMap<>();
            compoundData.put("compoundId", compound.getCompoundId());
            compoundData.put("compoundName", compound.getCompoundName());
            compoundData.put("buildings", compoundBuildings);
            compoundData.put("rooms", compoundRooms);
            compoundData.put("beds", compoundBeds);
            compoundData.put("occupiedBeds", compoundOccupiedBeds);
            compoundData.put("availableBeds", compoundBeds - compoundOccupiedBeds);
            compoundData.put("occupancyRate", compoundBeds > 0 ? Math.round((double) compoundOccupiedBeds / compoundBeds * 10000) / 100.0 : 0.0);
            compoundData.put("buildList", compoundBuilds);
            
            ((List<Map<String, Object>>) campusData.get("compounds")).add(compoundData);
            
            // 更新园区总计
            campusData.put("totalBuildings", (Integer) campusData.get("totalBuildings") + compoundBuildings);
            campusData.put("totalRooms", (Integer) campusData.get("totalRooms") + compoundRooms);
            campusData.put("totalBeds", (Integer) campusData.get("totalBeds") + compoundBeds);
            campusData.put("occupiedBeds", (Integer) campusData.get("occupiedBeds") + compoundOccupiedBeds);
            campusData.put("availableBeds", (Integer) campusData.get("availableBeds") + (compoundBeds - compoundOccupiedBeds));
        }
        
        // 计算园区入住率
        for (Map<String, Object> campusData : campusMap.values()) {
            int totalBeds = (Integer) campusData.get("totalBeds");
            int occupiedBeds = (Integer) campusData.get("occupiedBeds");
            campusData.put("occupancyRate", totalBeds > 0 ? Math.round((double) occupiedBeds / totalBeds * 10000) / 100.0 : 0.0);
        }
        
        return Result.success(new ArrayList<>(campusMap.values()));
    }

    /**
     * 获取楼栋房间详情
     * 返回指定楼栋的所有房间和床位信息
     */
    @GetMapping("/building-rooms/{buildId}")
    public Result<?> getBuildingRooms(@PathVariable Integer buildId) {
        List<DormRoom> rooms = dormRoomService.list();
        List<Map<String, Object>> roomList = new ArrayList<>();
        
        for (DormRoom room : rooms) {
            if (room.getDormBuildId() != buildId) continue;
            
            // 组装床位信息
            List<Map<String, Object>> beds = new ArrayList<>();
            int maxCapacity = room.getMaxCapacity();
            int currentCapacity = 0;
            String[] bedFields = {room.getFirstBed(), room.getSecondBed(), room.getThirdBed(), room.getFourthBed()};
            for (int i = 1; i <= maxCapacity; i++) {
                Map<String, Object> bed = new HashMap<>();
                bed.put("bedNumber", i);
                String studentUsername = bedFields[i - 1];
                if (studentUsername != null) {
                    Map<String, Object> stu = new HashMap<>();
                    stu.put("username", studentUsername);
                    bed.put("student", stu);
                    currentCapacity++;
                } else {
                    bed.put("student", null);
                }
                beds.add(bed);
            }
            
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomId", room.getDormRoomId());
            roomInfo.put("roomName", room.getDormRoomId());
            roomInfo.put("maxCapacity", maxCapacity);
            roomInfo.put("currentCapacity", currentCapacity);
            roomInfo.put("beds", beds);
            roomList.add(roomInfo);
        }
        
        return Result.success(roomList);
    }

    /**
     * 根据楼栋ID获取房间列表
     */
    @GetMapping("/getByBuild")
    public Result<?> getByBuild(@RequestParam Integer buildId) {
        List<DormRoom> rooms = dormRoomService.getRoomsByBuild(buildId);
        if (rooms != null) {
            return Result.success(rooms);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 根据房间ID获取床位列表
     */
    @GetMapping("/getByRoom")
    public Result<?> getByRoom(@RequestParam Integer roomId) {
        List<Map<String, Object>> beds = dormRoomService.getBedsByRoom(roomId);
        if (beds != null) {
            return Result.success(beds);
        } else {
            return Result.error("-1", "查询失败");
        }
    }
}
