package com.mrdotxin.propsmart.controller;

import com.mrdotxin.propsmart.model.dto.elevator.ElevatorBasicInfoDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorDetailDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorConfig;
import com.mrdotxin.propsmart.service.ElevatorConfigService;
import com.mrdotxin.propsmart.service.ElevatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 电梯管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/elevator")
@Api(tags = "电梯管理接口")
public class ElevatorController {
    
    @Resource
    private ElevatorService elevatorService;
    
    @Resource
    private ElevatorConfigService elevatorConfigService;
    
    @GetMapping("/list/basic")
    @ApiOperation("获取所有电梯基本信息（用户视图）")
    public List<ElevatorBasicInfoDTO> listElevatorBasicInfo() {
        return elevatorService.listElevatorBasicInfo();
    }
    
    @GetMapping("/basic/{id}")
    @ApiOperation("获取指定电梯基本信息（用户视图）")
    public ElevatorBasicInfoDTO getElevatorBasicInfo(@PathVariable("id") Long elevatorId) {
        List<ElevatorBasicInfoDTO> elevators = elevatorService.listElevatorBasicInfo();
        for (ElevatorBasicInfoDTO elevator : elevators) {
            if (elevator.getId().equals(elevatorId)) {
                return elevator;
            }
        }
        return null;
    }
    
    @GetMapping("/list/detail")
    @ApiOperation("获取所有电梯详细信息（管理员视图）")
    public List<ElevatorDetailDTO> listElevatorDetails() {
        return elevatorService.listElevatorDetails();
    }
    
    @GetMapping("/detail/{id}")
    @ApiOperation("获取指定电梯详细信息（管理员视图）")
    public ElevatorDetailDTO getElevatorDetail(@PathVariable("id") Long elevatorId) {
        return elevatorService.getElevatorDetail(elevatorId);
    }
    
    @PostMapping("/maintenance/{id}")
    @ApiOperation("更新电梯维护日期（管理员操作）")
    public boolean updateMaintenanceDate(@PathVariable("id") Long elevatorId) {
        return elevatorService.updateMaintenanceDate(elevatorId);
    }
    
    @GetMapping("/config/{id}")
    @ApiOperation("获取电梯配置信息（管理员视图）")
    public ElevatorConfig getElevatorConfig(@PathVariable("id") Long elevatorId) {
        return elevatorConfigService.getElevatorConfig(elevatorId);
    }
    
    @PostMapping("/config/update")
    @ApiOperation("更新电梯配置（管理员操作）")
    public boolean updateElevatorConfig(@RequestBody ElevatorConfig elevatorConfig) {
        return elevatorConfigService.updateConfig(elevatorConfig);
    }
    
    @PostMapping("/simulator/start")
    @ApiOperation("启动电梯数据模拟器（管理员操作）")
    public String startElevatorDataSimulator() {
        elevatorService.startElevatorDataSimulator();
        return "电梯数据模拟器已启动";
    }
    
    @PostMapping("/simulator/stop")
    @ApiOperation("停止电梯数据模拟器（管理员操作）")
    public String stopElevatorDataSimulator() {
        elevatorService.stopElevatorDataSimulator();
        return "电梯数据模拟器已停止";
    }
} 