package com.mrdotxin.propsmart.model.dto.elevator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 电梯配置信息DTO
 */
@Data
public class ElevatorConfigDTO {
    /**
     * 电梯ID
     */
    @ApiModelProperty(value = "电梯ID")
    private Long elevatorId;
    
    /**
     * 轿厢温度预警阈值
     */
    @ApiModelProperty(value = "轿厢温度预警阈值")
    private BigDecimal cabinTempAlertThr;
    
    /**
     * 电机温度预警阈值
     */
    @ApiModelProperty(value = "电机温度预警阈值")
    private BigDecimal motorTempAlertThr;
    
    /**
     * 速度异常百分比阈值
     */
    @ApiModelProperty(value = "速度异常百分比阈值")
    private BigDecimal speedAlertPercent;
    
    /**
     * 加速度异常阈值
     */
    @ApiModelProperty(value = "加速度异常阈值")
    private BigDecimal accelAlertThr;
    
    /**
     * 配置生效时间
     */
    @ApiModelProperty(value = "配置生效时间")
    private Date effectiveTime;
    
    /**
     * 配置更新时间
     */
    @ApiModelProperty(value = "配置更新时间")
    private Date updateTime;
} 