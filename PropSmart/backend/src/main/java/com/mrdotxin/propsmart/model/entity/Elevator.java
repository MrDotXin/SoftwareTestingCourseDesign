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
 * 电梯基本信息及实时运行参数表
 * @TableName elevator
 */
@Data
@TableName("elevator")
public class Elevator implements Serializable {
    /**
     * 电梯ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属楼栋ID（关联building表）
     */
    private Long buildingId;

    /**
     * 电梯编号（如A栋1号电梯）
     */
    private String elevatorNumber;

    /**
     * 安装日期
     */
    private Date installationDate;

    /**
     * 上次维护日期
     */
    private Date lastMaintenanceDate;

    /**
     * 当前运行状态
     */
    private String currentStatus;

    /**
     * 当前所在楼层
     */
    private Integer currentFloor;

    /**
     * 运行方向
     */
    private String runningDirection;

    /**
     * 负载百分比（0-100）
     */
    private Integer loadPercentage;

    /**
     * 电梯门状态
     */
    private String doorStatus;

    /**
     * 轿厢温度（单位：℃）
     */
    private BigDecimal cabinTemperature;

    /**
     * 电机温度（单位：℃）
     */
    private BigDecimal motorTemperature;

    /**
     * 运行速度（单位：m/s）
     */
    private BigDecimal runningSpeed;

    /**
     * 额定速度（单位：m/s，出厂设定值）
     */
    private BigDecimal ratedSpeed;

    /**
     * 加速度/减速度（单位：m/s²，保留3位小数）
     */
    private BigDecimal acceleration;

    /**
     * 实时功耗（单位：kW）
     */
    private BigDecimal powerConsumption;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}