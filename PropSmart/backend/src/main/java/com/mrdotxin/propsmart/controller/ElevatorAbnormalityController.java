package com.mrdotxin.propsmart.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电梯异常管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/elevator/abnormality")
@Api(tags = "电梯异常管理接口")
public class ElevatorAbnormalityController {
    
    @Resource
    private ElevatorAbnormalityService elevatorAbnormalityService;
    
    @GetMapping("/list")
    @ApiOperation("获取所有电梯异常记录（管理员视图）")
    public List<ElevatorAbnormality> listAbnormalities() {
        return elevatorAbnormalityService.list();
    }
    
    @GetMapping("/list/{elevatorId}")
    @ApiOperation("获取指定电梯的异常记录（管理员视图）")
    public List<ElevatorAbnormality> listElevatorAbnormalities(@PathVariable("elevatorId") Long elevatorId) {
        LambdaQueryWrapper<ElevatorAbnormality> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorAbnormality::getElevatorId, elevatorId);
        return elevatorAbnormalityService.list(queryWrapper);
    }
    
    @PostMapping("/handle/{id}")
    @ApiOperation("处理电梯异常（管理员操作）")
    public boolean handleAbnormality(@PathVariable("id") Long abnormalityId, 
                                     @RequestParam("handlerId") Long handlerId,
                                     @RequestParam("notes") String handlingNotes) {
        ElevatorAbnormality abnormality = elevatorAbnormalityService.getById(abnormalityId);
        if (abnormality == null) {
            return false;
        }
        
        abnormality.setStatus(AbnormalityStatusEnum.PROCESSING.getStatus());
        abnormality.setHandlerId(handlerId);
        abnormality.setHandlingNotes(handlingNotes);
        abnormality.setUpdateTime(new Date());
        
        return elevatorAbnormalityService.updateById(abnormality);
    }
    
    @PostMapping("/close/{id}")
    @ApiOperation("关闭电梯异常（管理员操作）")
    public boolean closeAbnormality(@PathVariable("id") Long abnormalityId, 
                                   @RequestParam("handlerId") Long handlerId,
                                   @RequestParam("notes") String handlingNotes) {
        ElevatorAbnormality abnormality = elevatorAbnormalityService.getById(abnormalityId);
        if (abnormality == null) {
            return false;
        }
        
        abnormality.setStatus(AbnormalityStatusEnum.RESOLVED.getStatus());
        abnormality.setHandlerId(handlerId);
        abnormality.setHandlingNotes(handlingNotes);
        abnormality.setRecoveryTime(new Date());
        abnormality.setUpdateTime(new Date());
        
        return elevatorAbnormalityService.updateById(abnormality);
    }
    
    @GetMapping("/unresolved")
    @ApiOperation("获取所有未解决的电梯异常（管理员视图）")
    public List<ElevatorAbnormality> listUnresolvedAbnormalities() {
        LambdaQueryWrapper<ElevatorAbnormality> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PENDING.getStatus())
                .or()
                .eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PROCESSING.getStatus());
        return elevatorAbnormalityService.list(queryWrapper);
    }
    
    @GetMapping("/stats")
    @ApiOperation("获取电梯异常统计数据（管理员视图）")
    public Map<String, Object> getAbnormalityStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计各状态的异常数量
        LambdaQueryWrapper<ElevatorAbnormality> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PENDING.getStatus());
        long pendingCount = elevatorAbnormalityService.count(pendingQuery);
        
        LambdaQueryWrapper<ElevatorAbnormality> processingQuery = new LambdaQueryWrapper<>();
        processingQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PROCESSING.getStatus());
        long processingCount = elevatorAbnormalityService.count(processingQuery);
        
        LambdaQueryWrapper<ElevatorAbnormality> resolvedQuery = new LambdaQueryWrapper<>();
        resolvedQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.RESOLVED.getStatus());
        long resolvedCount = elevatorAbnormalityService.count(resolvedQuery);
        
        LambdaQueryWrapper<ElevatorAbnormality> closedQuery = new LambdaQueryWrapper<>();
        closedQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.CLOSED.getStatus());
        long closedCount = elevatorAbnormalityService.count(closedQuery);
        
        stats.put("pending", pendingCount);
        stats.put("processing", processingCount);
        stats.put("resolved", resolvedCount);
        stats.put("closed", closedCount);
        stats.put("total", pendingCount + processingCount + resolvedCount + closedCount);
        
        return stats;
    }
} 