package com.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.DormBuild;
import com.example.springboot.entity.DormBuildDTO;
import com.example.springboot.service.DormBuildService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/building")
public class DormBuildController {

    @Resource
    private DormBuildService dormBuildService;

    /**
     * 楼宇添加
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody DormBuild dormBuild) {
        int i = dormBuildService.addNewBuilding(dormBuild);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "添加失败");
        }
    }

    /**
     * 楼宇信息更新
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody DormBuild dormBuild) {
        int i = dormBuildService.updateNewBuilding(dormBuild);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "更新失败");
        }
    }

    /**
     * 楼宇删除
     */
    @DeleteMapping("/delete/{dormBuildId}")
    public Result<?> delete(@PathVariable Integer dormBuildId) {
        int i = dormBuildService.deleteBuilding(dormBuildId);
        if (i == 1) {
            return Result.success();
        } else {
            return Result.error("-1", "删除失败");
        }
    }

    /**
     * 楼宇查找（包含围合信息）
     */
    @GetMapping("/find")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page<DormBuildDTO> page = dormBuildService.findWithCompound(pageNum, pageSize, search);
        if (page != null) {
            return Result.success(page);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 首页Echarts 获取楼宇信息
     */
    @GetMapping("/getBuildingName")
    public Result<?> getBuildingName() {
        List<DormBuild> buildingName = dormBuildService.getBuildingId();
        List<Integer> buildingId = buildingName.stream().map(dormBuildId -> dormBuildId.getDormBuildId()).collect(Collectors.toList());

        if (!buildingId.isEmpty()) {
            return Result.success(buildingId);
        } else {
            return Result.error("-1", "查询失败");
        }

    }

    /**
     * 获取所有楼栋信息（包含围合信息）
     */
    @GetMapping("/getAllWithCompound")
    public Result<?> getAllWithCompound() {
        List<DormBuildDTO> buildings = dormBuildService.getAllBuildingsWithCompound();
        if (buildings != null) {
            return Result.success(buildings);
        } else {
            return Result.error("-1", "查询失败");
        }
    }

    /**
     * 根据园区ID获取楼栋列表
     */
    @GetMapping("/getByCompound")
    public Result<?> getByCompound(@RequestParam Integer compoundId) {
        System.out.println("接收到的园区ID: " + compoundId);
        List<DormBuildDTO> buildings = dormBuildService.getBuildingsByCompound(compoundId);
        System.out.println("查询到的楼栋数量: " + (buildings != null ? buildings.size() : 0));
        if (buildings != null) {
            return Result.success(buildings);
        } else {
            return Result.error("-1", "查询失败");
        }
    }
}
