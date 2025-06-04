package com.mrdotxin.propsmart.controller;

import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorBasicInfoDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorConfigDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorDetailDTO;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorConfigService;
import com.mrdotxin.propsmart.service.ElevatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电梯控制器
 */
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
    public BaseResponse<List<ElevatorBasicInfoDTO>> listElevatorBasicInfo() {
        List<ElevatorBasicInfoDTO> elevatorList = elevatorService.listElevatorBasicInfo();
        return ResultUtils.success(elevatorList);
    }
    
    @GetMapping("/basic/{id}")
    @ApiOperation("获取电梯基本信息（用户视图）")
    public BaseResponse<ElevatorBasicInfoDTO> getElevatorBasicInfo(
            @ApiParam("电梯ID") @PathVariable("id") Long elevatorId) {
        ElevatorBasicInfoDTO elevator = elevatorService.getElevatorBasicInfo(elevatorId);
        if (elevator == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "电梯不存在");
        }
        return ResultUtils.success(elevator);
    }
    
    @GetMapping("/list/detail")
    @ApiOperation("获取所有电梯详细信息（管理员视图）")
    public BaseResponse<List<ElevatorDetailDTO>> listElevatorDetails() {
        List<ElevatorDetailDTO> elevatorList = elevatorService.listElevatorDetails();
        return ResultUtils.success(elevatorList);
    }
    
    @GetMapping("/detail/{id}")
    @ApiOperation("获取电梯详细信息（管理员视图）")
    public BaseResponse<ElevatorDetailDTO> getElevatorDetail(
            @ApiParam("电梯ID") @PathVariable("id") Long elevatorId) {
        ElevatorDetailDTO elevator = elevatorService.getElevatorDetail(elevatorId);
        if (elevator == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "电梯不存在");
        }
        return ResultUtils.success(elevator);
    }
    
    @PostMapping("/maintenance/{id}")
    @ApiOperation("更新电梯维护日期（管理员操作）")
    public BaseResponse<Boolean> updateMaintenanceDate(
            @ApiParam("电梯ID") @PathVariable("id") Long elevatorId) {
        boolean result = elevatorService.updateMaintenanceDate(elevatorId);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "更新维护日期失败");
        }
        return ResultUtils.success(true);
    }
    
    @GetMapping("/config/{id}")
    @ApiOperation("获取电梯配置信息（管理员视图）")
    public BaseResponse<ElevatorConfigDTO> getElevatorConfig(
            @ApiParam("电梯ID") @PathVariable("id") Long elevatorId) {
        ElevatorConfigDTO config = elevatorConfigService.getElevatorConfig(elevatorId);
        if (config == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "电梯配置不存在");
        }
        return ResultUtils.success(config);
    }
    
    @PostMapping("/config/update")
    @ApiOperation("更新电梯配置（管理员操作）")
    public BaseResponse<Boolean> updateElevatorConfig(@RequestBody ElevatorConfigDTO configDTO) {
        if (configDTO == null || configDTO.getElevatorId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean result = elevatorConfigService.updateElevatorConfig(configDTO);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "更新配置失败");
        }
        return ResultUtils.success(true);
    }
    
    @PostMapping("/simulator/start")
    @ApiOperation("启动电梯数据模拟器（管理员操作）")
    public BaseResponse<Boolean> startElevatorSimulator() {
        elevatorService.startElevatorDataSimulator();
        return ResultUtils.success(true);
    }
    
    @PostMapping("/simulator/stop")
    @ApiOperation("停止电梯数据模拟器（管理员操作）")
    public BaseResponse<Boolean> stopElevatorSimulator() {
        elevatorService.stopElevatorDataSimulator();
        return ResultUtils.success(true);
    }
    
    @GetMapping("/maintenance/needed")
    @ApiOperation("获取所有需要维护的电梯（管理员视图）")
    public BaseResponse<List<ElevatorDetailDTO>> getElevatorsNeedingMaintenance() {
        List<ElevatorDetailDTO> elevatorList = elevatorService.listElevatorDetails()
                .stream()
                .filter(e -> e.getCurrentStatus().equals(ElevatorStatusEnum.FAULT.getStatus()) ||
                             e.getCurrentStatus().equals(ElevatorStatusEnum.WARNING.getStatus()))
                .collect(Collectors.toList());
        return ResultUtils.success(elevatorList);
    }
} 