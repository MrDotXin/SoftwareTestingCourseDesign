package com.mrdotxin.propsmart.service.elevator;

import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;

import java.util.List;

/**
 * 电梯异常解决服务接口
 * 用于解决电梯异常，并提供处理异常的相关操作
 * 抽取出来解决循环依赖问题
 */
public interface ElevatorAbnormalityResolverService {
    
    /**
     * 解决指定电梯的异常
     * @param elevatorId 电梯ID
     * @return 已解决的异常列表
     */
    List<ElevatorAbnormality> resolveElevatorAbnormalities(Long elevatorId);
    
    /**
     * 批量更新异常状态
     * @param abnormalities 异常列表
     */
    void updateBatchAbnormalities(List<ElevatorAbnormality> abnormalities);
} 