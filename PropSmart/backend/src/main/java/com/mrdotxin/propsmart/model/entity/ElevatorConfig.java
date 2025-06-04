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
@TableName("elevator_config")
public class ElevatorConfig implements Serializable {
    /**
     * 配置ID，与电梯ID相同
     */
    @TableId(type = IdType.INPUT)
    private Long elevatorId;

    /**
     * 轿厢温度预警阈值（默认35℃）
     */
    @TableField(value = "cabinTempAlertThr")
    private BigDecimal cabinTempAlertThr;

    /**
     * 电机温度预警阈值（默认60℃）
     */
    @TableField(value = "motorTempAlertThr")
    private BigDecimal motorTempAlertThr;

    /**
     * 速度异常百分比阈值（默认±10%）
     */
    @TableField(value = "speedAlertPercent")
    private BigDecimal speedAlertPercent;

    /**
     * 加速度异常阈值（默认1.5m/s²）
     */
    @TableField(value = "accelAlertThr")
    private BigDecimal accelAlertThr;

    /**
     * 配置生效时间
     */
    @TableField(value = "effectiveTime")
    private Date effectiveTime;

    /**
     * 配置更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}