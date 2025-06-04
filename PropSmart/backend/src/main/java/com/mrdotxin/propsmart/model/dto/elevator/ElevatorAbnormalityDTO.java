package com.mrdotxin.propsmart.model.dto.elevator;

import lombok.Data;

import java.util.Date;

/**
 * 电梯异常信息DTO
 */
@Data
public class ElevatorAbnormalityDTO {
    /**
     * 异常记录ID
     */
    private Long id;
    
    /**
     * 关联的电梯ID
     */
    private Long elevatorId;
    
    /**
     * 电梯编号
     */
    private String elevatorNumber;
    
    /**
     * 异常类型
     */
    private String abnormalityType;
    
    /**
     * 异常级别
     */
    private String abnormalityLevel;
    
    /**
     * 异常发生时间
     */
    private Date occurrenceTime;
    
    /**
     * 异常恢复时间
     */
    private Date recoveryTime;
    
    /**
     * 处理状态
     */
    private String status;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理人姓名
     */
    private String handlerName;
    
    /**
     * 异常详细描述
     */
    private String description;
    
    /**
     * 处理过程记录
     */
    private String handlingNotes;
} 