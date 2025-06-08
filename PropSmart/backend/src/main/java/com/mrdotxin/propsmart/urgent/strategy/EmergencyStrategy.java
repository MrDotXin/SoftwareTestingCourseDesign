package com.mrdotxin.propsmart.urgent.strategy;

import com.mrdotxin.propsmart.urgent.model.entity.EmergencyContext;

public interface EmergencyStrategy {
    // 启动应急预案
    void startEmergencyPlan(EmergencyContext context);

    // 生成疏散路线
    void planEvacuationRoutes(EmergencyContext context);

    // 统筹救援资源
    void allocateResources(EmergencyContext context);
}