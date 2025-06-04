package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorConfigDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorConfig;

/**
 * 电梯配置服务接口
 */
public interface ElevatorConfigService extends IService<ElevatorConfig> {
    
    /**
     * 获取电梯配置
     * @param elevatorId 电梯ID
     * @return 电梯配置信息
     */
    ElevatorConfigDTO getElevatorConfig(Long elevatorId);
    
    /**
     * 更新电梯配置
     * @param configDTO 配置信息
     * @return 是否更新成功
     */
    boolean updateElevatorConfig(ElevatorConfigDTO configDTO);
    
    /**
     * 创建默认电梯配置
     * @param elevatorId 电梯ID
     * @return 是否创建成功
     */
    boolean createDefaultConfig(Long elevatorId);
}
