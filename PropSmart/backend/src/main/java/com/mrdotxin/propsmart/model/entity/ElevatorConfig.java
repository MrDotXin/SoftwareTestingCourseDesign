package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 电梯配置实体类
 */
@Data
@TableName("elevatorConfig")
public class ElevatorConfig implements Serializable {
    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 电梯ID
     */
    private Long elevatorId;

    /**
     * 最大轿厢温度（默认35℃）
     */
    private BigDecimal maxCabinTemperature;

    /**
     * 最大电机温度（默认70℃）
     */
    private BigDecimal maxMotorTemperature;

    /**
     * 最大运行速度（默认2.5m/s）
     */
    private BigDecimal maxSpeed;

    /**
     * 最大功耗（默认8kW）
     */
    private BigDecimal maxPowerConsumption;

    /**
     * 维护间隔天数（默认90天）
     */
    private Integer maintenanceIntervalDays;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}