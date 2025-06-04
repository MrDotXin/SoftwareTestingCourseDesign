package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 电梯异常实体类
 */
@Data
@TableName("elevator_abnormality")
public class ElevatorAbnormality implements Serializable {
    /**
     * 异常记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的电梯ID
     */
    private Long elevatorId;
    
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
     * 异常详细描述
     */
    private String description;
    
    /**
     * 处理过程记录
     */
    private String handlingNotes;
    
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