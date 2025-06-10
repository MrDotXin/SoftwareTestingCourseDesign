package com.mrdotxin.propsmart.model.dto.elevator;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 电梯详细信息DTO (管理员视图)
 * 管理员可查看电梯全部信息
 */
@Data
public class ElevatorDetailDTO {
    /**
     * 电梯ID
     */
    @Schema(description = "电梯ID")
    private Long id;
    
    /**
     * 所属楼栋ID
     */
    @Schema(description = "所属楼栋ID")
    private Long buildingId;
    
    /**
     * 电梯编号
     */
    @Schema(description = "电梯编号")
    private String elevatorNumber;
    
    /**
     * 安装日期
     */
    @Schema(description = "安装日期")
    private Date installationDate;
    
    /**
     * 上次维护日期
     */
    @Schema(description = "上次维护日期")
    private Date lastMaintenanceDate;
    
    /**
     * 当前运行状态
     */
    @Schema(description = "当前运行状态")
    private String currentStatus;
    
    /**
     * 当前所在楼层
     */
    @Schema(description = "当前所在楼层")
    private Integer currentFloor;
    
    /**
     * 运行方向
     */
    @Schema(description = "运行方向")
    private String runningDirection;
    
    /**
     * 负载百分比
     */
    @Schema(description = "负载百分比")
    private Integer loadPercentage;
    
    /**
     * 电梯门状态
     */
    @Schema(description = "电梯门状态")
    private String doorStatus;
    
    /**
     * 轿厢温度
     */
    @Schema(description = "轿厢温度")
    private BigDecimal cabinTemperature;
    
    /**
     * 电机温度
     */
    @Schema(description = "电机温度")
    private BigDecimal motorTemperature;
    
    /**
     * 运行速度
     */
    @Schema(description = "运行速度")
    private BigDecimal runningSpeed;
    
    /**
     * 额定速度
     */
    @Schema(description = "额定速度")
    private BigDecimal ratedSpeed;
    
    /**
     * 加速度/减速度
     */
    @Schema(description = "加速度/减速度")
    private BigDecimal acceleration;
    
    /**
     * 实时功耗
     */
    @Schema(description = "实时功耗")
    private BigDecimal powerConsumption;
    
    /**
     * 状态更新时间
     */
    @Schema(description = "状态更新时间")
    private Date updateTime;
    
    /**
     * 电梯配置信息
     */
    @Schema(description = "电梯配置信息")
    private ElevatorConfigDTO config;
} 