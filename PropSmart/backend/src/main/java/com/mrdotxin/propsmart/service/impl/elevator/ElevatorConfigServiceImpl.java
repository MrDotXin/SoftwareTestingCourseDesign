package com.mrdotxin.propsmart.service.impl.elevator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.mysql.ElevatorConfigMapper;
import com.mrdotxin.propsmart.model.entity.ElevatorConfig;
import com.mrdotxin.propsmart.service.elevator.ElevatorConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 电梯配置服务实现类
 */
@Slf4j
@Service
public class ElevatorConfigServiceImpl extends ServiceImpl<ElevatorConfigMapper, ElevatorConfig> implements ElevatorConfigService {

    @Override
    public boolean createDefaultConfig(Long elevatorId) {
        // 检查是否已存在配置
        LambdaQueryWrapper<ElevatorConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorConfig::getElevatorId, elevatorId);
        
        if (this.count(queryWrapper) > 0) {
            log.warn("电梯 {} 已存在配置，不再创建默认配置", elevatorId);
            return false;
        }
        
        // 创建默认配置
        ElevatorConfig config = new ElevatorConfig();
        config.setElevatorId(elevatorId);
        
        // 设置默认阈值
        config.setMaxSpeed(new BigDecimal("2.5"));  // 最大速度 2.5 m/s
        config.setMaxMotorTemperature(new BigDecimal("70.0"));  // 最大电机温度 70℃
        config.setMaxCabinTemperature(new BigDecimal("35.0"));  // 最大轿厢温度 35℃
        config.setMaxPowerConsumption(new BigDecimal("8.0"));  // 最大功耗 8 kW
        config.setMaintenanceIntervalDays(90);  // 维护间隔 90 天
        
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        
        return this.save(config);
    }

    @Override
    public ElevatorConfig getElevatorConfig(Long elevatorId) {
        LambdaQueryWrapper<ElevatorConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorConfig::getElevatorId, elevatorId);
        
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean updateConfig(ElevatorConfig elevatorConfig) {
        if (elevatorConfig.getId() == null) {
            log.error("更新电梯配置失败：配置ID不能为空");
            return false;
        }
        
        elevatorConfig.setUpdateTime(new Date());
        return this.updateById(elevatorConfig);
    }
} 