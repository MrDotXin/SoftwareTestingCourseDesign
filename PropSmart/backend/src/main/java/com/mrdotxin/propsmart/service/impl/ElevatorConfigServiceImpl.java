package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.ElevatorConfigMapper;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorConfigDTO;
import com.mrdotxin.propsmart.model.entity.ElevatorConfig;
import com.mrdotxin.propsmart.service.ElevatorConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 电梯配置服务实现类
 */
@Service
public class ElevatorConfigServiceImpl extends ServiceImpl<ElevatorConfigMapper, ElevatorConfig> implements ElevatorConfigService {

    @Override
    public ElevatorConfigDTO getElevatorConfig(Long elevatorId) {
        ElevatorConfig config = getById(elevatorId);
        if (config == null) {
            // 如果配置不存在，创建默认配置
            createDefaultConfig(elevatorId);
            config = getById(elevatorId);
        }
        
        return convertToDTO(config);
    }

    @Override
    public boolean updateElevatorConfig(ElevatorConfigDTO configDTO) {
        ElevatorConfig config = new ElevatorConfig();
        BeanUtils.copyProperties(configDTO, config);
        config.setUpdateTime(new Date());
        return updateById(config);
    }

    @Override
    public boolean createDefaultConfig(Long elevatorId) {
        // 检查配置是否已存在
        ElevatorConfig existingConfig = getById(elevatorId);
        if (existingConfig != null) {
            return true;
        }
        
        // 创建默认配置
        ElevatorConfig config = new ElevatorConfig();
        config.setElevatorId(elevatorId);
        config.setCabinTempAlertThr(new BigDecimal("35.0")); // 轿厢温度预警阈值 35℃
        config.setMotorTempAlertThr(new BigDecimal("60.0")); // 电机温度预警阈值 60℃
        config.setSpeedAlertPercent(new BigDecimal("10.0")); // 速度异常百分比阈值 ±10%
        config.setAccelAlertThr(new BigDecimal("1.500")); // 加速度异常阈值 1.5m/s²
        config.setEffectiveTime(new Date());
        config.setUpdateTime(new Date());
        
        return save(config);
    }
    
    /**
     * 将配置实体转换为DTO
     */
    private ElevatorConfigDTO convertToDTO(ElevatorConfig config) {
        if (config == null) {
            return null;
        }
        
        ElevatorConfigDTO dto = new ElevatorConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }
}




