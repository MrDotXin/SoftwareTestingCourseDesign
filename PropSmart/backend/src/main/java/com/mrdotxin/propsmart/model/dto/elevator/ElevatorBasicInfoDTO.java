package com.mrdotxin.propsmart.model.dto.elevator;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 电梯基本信息DTO (用户视图)
 * 用户只可见电梯ID，电梯编号，安装日期，上次维护时间和当前运行状态
 */
@Data
public class ElevatorBasicInfoDTO {
    /**
     * 电梯ID
     */
    @Schema(description = "电梯ID")
    private Long id;
    
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
} 