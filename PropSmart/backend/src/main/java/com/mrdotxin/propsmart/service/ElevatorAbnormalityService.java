package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;

import java.util.List;

/**
 * 电梯异常服务接口
 */
public interface ElevatorAbnormalityService extends IService<ElevatorAbnormality> {
    
    /**
     * 获取所有电梯异常记录
     * @return 异常记录列表
     */
    List<ElevatorAbnormalityDTO> listAbnormalities();
    
    /**
     * 根据电梯ID获取异常记录
     * @param elevatorId 电梯ID
     * @return 异常记录列表
     */
    List<ElevatorAbnormalityDTO> listAbnormalitiesByElevatorId(Long elevatorId);
    
    /**
     * 创建异常记录
     * @param abnormality 异常信息
     * @return 异常记录ID
     */
    Long createAbnormality(ElevatorAbnormality abnormality);
    
    /**
     * 处理异常
     * @param abnormalityId 异常ID
     * @param handlerId 处理人ID
     * @param status 处理状态
     * @param handlingNotes 处理记录
     * @return 是否处理成功
     */
    boolean handleAbnormality(Long abnormalityId, Long handlerId, String status, String handlingNotes);
    
    /**
     * 关闭异常
     * @param abnormalityId 异常ID
     * @param handlerId 处理人ID
     * @return 是否关闭成功
     */
    boolean closeAbnormality(Long abnormalityId, Long handlerId);
}
