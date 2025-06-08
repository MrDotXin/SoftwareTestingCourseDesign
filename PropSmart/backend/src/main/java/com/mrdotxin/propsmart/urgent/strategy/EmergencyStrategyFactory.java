package com.mrdotxin.propsmart.urgent.strategy;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// EmergencyStrategyFactory.java
@Component
public class EmergencyStrategyFactory {
    @Resource
    private ApplicationContext applicationContext;

    public EmergencyStrategy getStrategy(String emergencyType) {
        // 根据灾害类型获取对应策略 Bean
        return applicationContext.getBean(emergencyType + "Strategy", EmergencyStrategy.class);
    }
}

