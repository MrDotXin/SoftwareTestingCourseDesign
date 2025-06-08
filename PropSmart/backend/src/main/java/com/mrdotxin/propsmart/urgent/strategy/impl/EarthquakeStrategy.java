package com.mrdotxin.propsmart.urgent.strategy.impl;

import com.mrdotxin.propsmart.urgent.model.entity.EmergencyContext;
import com.mrdotxin.propsmart.urgent.strategy.EmergencyStrategy;
import org.springframework.stereotype.Component;

@Component("earthquakeStrategy")
public class EarthquakeStrategy implements EmergencyStrategy {
    @Override
    public void startEmergencyPlan(EmergencyContext context) {
        // 1. 启动火灾专项预案
    }

    @Override
    public void planEvacuationRoutes(EmergencyContext context) {
        // 2. 规划逆风疏散路线

    }

    @Override
    public void allocateResources(EmergencyContext context) {

    }
}