package com.mrdotxin.propsmart.controller;

import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
    public List<ElevatorAbnormalityDTO> listAbnormalities() {
        return elevatorAbnormalityService.listAllDTO();
    }
    
    @GetMapping("/list/{elevatorId}")
    @ApiOperation("获取指定电梯的异常记录（管理员视图）")
    public List<ElevatorAbnormalityDTO> listElevatorAbnormalities(@PathVariable("elevatorId") Long elevatorId) {
        return elevatorAbnormalityService.listByElevatorIdDTO(elevatorId);
    }
    
    @GetMapping("/detail/{id}")
    @ApiOperation("获取异常详情")
    public ElevatorAbnormalityDTO getAbnormalityDetail(@PathVariable("id") Long id) {
        return elevatorAbnormalityService.getDTOById(id);
    }
    
    @PostMapping("/handle/{id}")
    @ApiOperation("处理电梯异常（管理员操作）")
    public boolean handleAbnormality(@PathVariable("id") Long abnormalityId, 
                                     @RequestParam("handlerId") Long handlerId,
                                     @RequestParam("handlerName") String handlerName,
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
    public List<ElevatorAbnormalityDTO> listUnresolvedAbnormalities() {
        List<ElevatorAbnormality> unresolvedList = elevatorAbnormalityService.listUnresolved();
        return elevatorAbnormalityService.convertToDTOList(unresolvedList);
    }
    
    @GetMapping("/stats")
    @ApiOperation("获取电梯异常统计数据（管理员视图）")
    public List<Object> getAbnormalityStats() {
        return elevatorAbnormalityService.getAbnormalityStats();
    }
} 