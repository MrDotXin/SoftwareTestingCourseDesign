package com.mrdotxin.propsmart.model.dto.elevator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 电梯详细信息DTO (管理员视图)
 * 管理员可查看电梯全部信息
 */
@Data
@ApiModel(value = "电梯详细信息(管理员视图)")
public class ElevatorDetailDTO {
    /**
     * 电梯ID
     */
    @ApiModelProperty(value = "电梯ID")
    private Long id;
    
    /**
     * 所属楼栋ID
     */
    @ApiModelProperty(value = "所属楼栋ID")
    private Long buildingId;
    
    /**
     * 电梯编号
     */
    @ApiModelProperty(value = "电梯编号")
    private String elevatorNumber;
    
    /**
     * 安装日期
     */
    @ApiModelProperty(value = "安装日期")
    private Date installationDate;
    
    /**
     * 上次维护日期
     */
    @ApiModelProperty(value = "上次维护日期")
    private Date lastMaintenanceDate;
    
    /**
     * 当前运行状态
     */
    @ApiModelProperty(value = "当前运行状态")
    private String currentStatus;
    
    /**
     * 当前所在楼层
     */
    @ApiModelProperty(value = "当前所在楼层")
    private Integer currentFloor;
    
    /**
     * 运行方向
     */
    @ApiModelProperty(value = "运行方向")
    private String runningDirection;
    
    /**
     * 负载百分比
     */
    @ApiModelProperty(value = "负载百分比")
    private Integer loadPercentage;
    
    /**
     * 电梯门状态
     */
    @ApiModelProperty(value = "电梯门状态")
    private String doorStatus;
    
    /**
     * 轿厢温度
     */
    @ApiModelProperty(value = "轿厢温度")
    private BigDecimal cabinTemperature;
    
    /**
     * 电机温度
     */
    @ApiModelProperty(value = "电机温度")
    private BigDecimal motorTemperature;
    
    /**
     * 运行速度
     */
    @ApiModelProperty(value = "运行速度")
    private BigDecimal runningSpeed;
    
    /**
     * 额定速度
     */
    @ApiModelProperty(value = "额定速度")
    private BigDecimal ratedSpeed;
    
    /**
     * 加速度/减速度
     */
    @ApiModelProperty(value = "加速度/减速度")
    private BigDecimal acceleration;
    
    /**
     * 实时功耗
     */
    @ApiModelProperty(value = "实时功耗")
    private BigDecimal powerConsumption;
    
    /**
     * 状态更新时间
     */
    @ApiModelProperty(value = "状态更新时间")
    private Date updateTime;
    
    /**
     * 电梯配置信息
     */
    @ApiModelProperty(value = "电梯配置信息")
    private ElevatorConfigDTO config;
} 