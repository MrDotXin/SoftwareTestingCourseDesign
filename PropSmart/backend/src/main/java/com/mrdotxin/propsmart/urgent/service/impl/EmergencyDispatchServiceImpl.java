package com.mrdotxin.propsmart.urgent.service.impl;

import com.mrdotxin.propsmart.urgent.model.entity.EmergencyContext;
import com.mrdotxin.propsmart.urgent.model.entity.EvacuationPoint;
import com.mrdotxin.propsmart.urgent.service.EmergencyDispatchService;
import com.mrdotxin.propsmart.urgent.strategy.EmergencyStrategy;
import com.mrdotxin.propsmart.urgent.strategy.EmergencyStrategyFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

// EmergencyDispatchService.java
@Service
public class EmergencyDispatchServiceImpl implements EmergencyDispatchService {
    @Resource
    private EmergencyStrategyFactory strategyFactory;

    private EmergencyContext emergencyContext;

    @Override
    public void dispatchEmergency(EmergencyContext context) {
        emergencyContext = context;
        // 1. 获取对应策略
        EmergencyStrategy strategy = strategyFactory.getStrategy(context.getEmergencyType());
        strategy.startEmergencyPlan(context);
        strategy.planEvacuationRoutes(context);
        strategy.allocateResources(context);
    }

    @Override
    public List<EvacuationPoint> getSafeLocation() {
        return emergencyContext.getEvacuationPoints();
    }
}
