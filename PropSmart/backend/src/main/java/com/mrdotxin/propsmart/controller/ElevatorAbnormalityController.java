package com.mrdotxin.propsmart.controller;

import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 电梯异常控制器
 */
@RestController
@RequestMapping("/elevator/abnormality")
@Api(tags = "电梯异常管理接口")
public class ElevatorAbnormalityController {
    
    @Resource
    private ElevatorAbnormalityService elevatorAbnormalityService;
    
    @GetMapping("/list")
    @ApiOperation("获取所有电梯异常记录（管理员视图）")
    public BaseResponse<List<ElevatorAbnormalityDTO>> listAbnormalities() {
        List<ElevatorAbnormalityDTO> abnormalities = elevatorAbnormalityService.listAbnormalities();
        return ResultUtils.success(abnormalities);
    }
    
    @GetMapping("/list/{elevatorId}")
    @ApiOperation("获取指定电梯的异常记录（管理员视图）")
    public BaseResponse<List<ElevatorAbnormalityDTO>> listAbnormalitiesByElevatorId(
            @ApiParam("电梯ID") @PathVariable("elevatorId") Long elevatorId) {
        List<ElevatorAbnormalityDTO> abnormalities = elevatorAbnormalityService.listAbnormalitiesByElevatorId(elevatorId);
        return ResultUtils.success(abnormalities);
    }
    
    @PostMapping("/handle/{id}")
    @ApiOperation("处理电梯异常（管理员操作）")
    public BaseResponse<Boolean> handleAbnormality(
            @ApiParam("异常ID") @PathVariable("id") Long abnormalityId,
            @ApiParam("处理人ID") @RequestParam("handlerId") Long handlerId,
            @ApiParam("处理状态") @RequestParam("status") String status,
            @ApiParam("处理记录") @RequestParam("handlingNotes") String handlingNotes) {
        
        // 验证状态是否合法
        boolean validStatus = status.equals(AbnormalityStatusEnum.PROCESSING.getStatus()) || 
                status.equals(AbnormalityStatusEnum.RESOLVED.getStatus()) || 
                status.equals(AbnormalityStatusEnum.CLOSED.getStatus());
        
        if (!validStatus) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "状态参数错误");
        }
        
        boolean result = elevatorAbnormalityService.handleAbnormality(abnormalityId, handlerId, status, handlingNotes);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "处理异常失败");
        }
        return ResultUtils.success(true);
    }
    
    @PostMapping("/close/{id}")
    @ApiOperation("关闭电梯异常（管理员操作）")
    public BaseResponse<Boolean> closeAbnormality(
            @ApiParam("异常ID") @PathVariable("id") Long abnormalityId,
            @ApiParam("处理人ID") @RequestParam("handlerId") Long handlerId) {
        
        boolean result = elevatorAbnormalityService.closeAbnormality(abnormalityId, handlerId);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "关闭异常失败");
        }
        return ResultUtils.success(true);
    }
    
    @GetMapping("/unresolved")
    @ApiOperation("获取所有未解决的电梯异常（管理员视图）")
    public BaseResponse<List<ElevatorAbnormalityDTO>> listUnresolvedAbnormalities() {
        List<ElevatorAbnormalityDTO> abnormalities = elevatorAbnormalityService.listAbnormalities()
                .stream()
                .filter(a -> !AbnormalityStatusEnum.RESOLVED.getStatus().equals(a.getStatus()) &&
                             !AbnormalityStatusEnum.CLOSED.getStatus().equals(a.getStatus()))
                .collect(Collectors.toList());
        return ResultUtils.success(abnormalities);
    }
    
    @GetMapping("/stats")
    @ApiOperation("获取电梯异常统计数据（管理员视图）")
    public BaseResponse<Map<String, Object>> getAbnormalityStatistics() {
        List<ElevatorAbnormalityDTO> abnormalities = elevatorAbnormalityService.listAbnormalities();
        
        // 获取各种类型异常的数量
        Map<String, Long> typeStats = abnormalities.stream()
                .collect(Collectors.groupingBy(
                    ElevatorAbnormalityDTO::getAbnormalityType, 
                    Collectors.counting()
                ));
        
        // 获取各种级别异常的数量
        Map<String, Long> levelStats = abnormalities.stream()
                .collect(Collectors.groupingBy(
                    ElevatorAbnormalityDTO::getAbnormalityLevel, 
                    Collectors.counting()
                ));
        
        // 获取各种状态的数量
        Map<String, Long> statusStats = abnormalities.stream()
                .collect(Collectors.groupingBy(
                    ElevatorAbnormalityDTO::getStatus, 
                    Collectors.counting()
                ));
        
        // 未解决的异常数量
        long unresolvedCount = abnormalities.stream()
                .filter(a -> !AbnormalityStatusEnum.RESOLVED.getStatus().equals(a.getStatus()) &&
                             !AbnormalityStatusEnum.CLOSED.getStatus().equals(a.getStatus()))
                .count();
        
        // 返回数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("typeStats", typeStats);
        stats.put("levelStats", levelStats);
        stats.put("statusStats", statusStats);
        stats.put("totalCount", abnormalities.size());
        stats.put("unresolvedCount", unresolvedCount);
        
        return ResultUtils.success(stats);
    }
} 