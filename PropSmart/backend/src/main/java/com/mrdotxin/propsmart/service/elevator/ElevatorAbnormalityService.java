package com.mrdotxin.propsmart.service.elevator;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;

import java.util.List;

/**
 * 电梯异常服务接口
 */
public interface ElevatorAbnormalityService extends IService<ElevatorAbnormality> {
    
    /**
     * 获取所有电梯异常记录并转换为DTO
     * @return 电梯异常DTO列表
     */
    List<ElevatorAbnormalityDTO> listAllDTO();
    
    /**
     * 获取指定电梯的异常记录并转换为DTO
     * @param elevatorId 电梯ID
     * @return 电梯异常DTO列表
     */
    List<ElevatorAbnormalityDTO> listByElevatorIdDTO(Long elevatorId);
    
    /**
     * 获取指定ID的异常记录并转换为DTO
     * @param id 异常记录ID
     * @return 电梯异常DTO
     */
    ElevatorAbnormalityDTO getDTOById(Long id);
    
    /**
     * 获取所有未解决的电梯异常（待处理和处理中状态）
     * @return 未解决的电梯异常列表
     */
    List<ElevatorAbnormality> listUnresolved();
    
    /**
     * 获取电梯异常统计数据
     * @return 异常统计信息
     */
    List<Object> getAbnormalityStats();
    
    /**
     * 将实体列表转换为DTO列表
     * @param entityList 实体列表
     * @return DTO列表
     */
    List<ElevatorAbnormalityDTO> convertToDTOList(List<ElevatorAbnormality> entityList);
} 