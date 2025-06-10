package com.mrdotxin.propsmart.controller;

import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.service.elevator.ElevatorAbnormalityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "电梯异常管理接口")
public class ElevatorAbnormalityController {
    
    @Resource
    private ElevatorAbnormalityService elevatorAbnormalityService;
    
    @GetMapping("/list")
    @Operation(method = "获取所有电梯异常记录（管理员视图）")
    public List<ElevatorAbnormalityDTO> listAbnormalities() {
        return elevatorAbnormalityService.listAllDTO();
    }

    @GetMapping("/list/{elevatorId}")
    @Operation(method = "获取指定电梯的异常记录（管理员视图）")
    public List<ElevatorAbnormalityDTO> listElevatorAbnormalities(@PathVariable("elevatorId") Long elevatorId) {
        return elevatorAbnormalityService.listByElevatorIdDTO(elevatorId);
    }

    @GetMapping("/detail/{id}")
    @Operation(method = "获取异常详情")
    public ElevatorAbnormalityDTO getAbnormalityDetail(@PathVariable("id") Long id) {
        return elevatorAbnormalityService.getDTOById(id);
    }

    @PostMapping("/handle/{id}")
    @Operation(method = "处理电梯异常（管理员操作）")
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
    @Operation(method = "关闭电梯异常（管理员操作）")
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
    @Operation(method = "获取所有未解决的电梯异常（管理员视图）")
    public List<ElevatorAbnormalityDTO> listUnresolvedAbnormalities() {
        List<ElevatorAbnormality> unresolvedList = elevatorAbnormalityService.listUnresolved();
        return elevatorAbnormalityService.convertToDTOList(unresolvedList);
    }

    @GetMapping("/stats")
    @Operation(method = "获取电梯异常统计数据（管理员视图）")
    public List<Object> getAbnormalityStats() {
        return elevatorAbnormalityService.getAbnormalityStats();
    }
} 