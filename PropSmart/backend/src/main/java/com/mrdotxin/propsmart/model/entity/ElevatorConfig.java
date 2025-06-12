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
     * 电梯ID
     */
    @TableId(value = "elevatorId")
    private Long elevatorId;

    /**
     * 最大轿厢温度（默认35℃）
     */
    private BigDecimal cabinTempAlertThr;

    /**
     * 最大电机温度（默认70℃）
     */
    private BigDecimal motorTempAlertThr;

    /**
     * 最大运行速度（默认2.5m/s）
     */
    private BigDecimal speedAlertPercent;

    /**
     * 最大功耗（默认8kW）
     */
    private BigDecimal powerConsumptionThr;

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