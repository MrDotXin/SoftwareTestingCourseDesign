package com.mrdotxin.propsmart.service.elevator;

import com.mrdotxin.propsmart.model.entity.ElevatorConfig;

/**
 * 电梯配置服务接口
 */
public interface ElevatorConfigService {
    
    /**
     * 为电梯创建默认配置
     * @param elevatorId 电梯ID
     * @return 是否创建成功
     */
    boolean createDefaultConfig(Long elevatorId);
    
    /**
     * 获取电梯配置信息
     * @param elevatorId 电梯ID
     * @return 电梯配置
     */
    ElevatorConfig getElevatorConfig(Long elevatorId);
    
    /**
     * 更新电梯配置
     * @param elevatorConfig 电梯配置
     * @return 是否更新成功
     */
    boolean updateConfig(ElevatorConfig elevatorConfig);
} 