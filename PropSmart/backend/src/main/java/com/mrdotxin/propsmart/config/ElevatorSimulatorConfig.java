package com.mrdotxin.propsmart.config;

import com.mrdotxin.propsmart.service.ElevatorService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 电梯数据模拟器配置
 * 应用启动时自动启动电梯数据模拟器
 */
@Configuration
public class ElevatorSimulatorConfig {
    
    @Resource
    private ElevatorService elevatorService;
    
    @PostConstruct
    public void init() {
        // 应用启动时自动启动电梯数据模拟器
        elevatorService.startElevatorDataSimulator();
    }
}