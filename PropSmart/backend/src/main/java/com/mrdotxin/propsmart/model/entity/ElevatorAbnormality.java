package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 电梯异常事件记录表
 * @TableName elevatorAbnormality
 */
@TableName(value ="elevatorAbnormality")
@Data
public class ElevatorAbnormality implements Serializable {
    /**
     * 异常记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的电梯ID（关联elevator表）
     */
    @TableField(value = "elevatorId")
    private Long elevatorId;

    /**
     * 异常类型
     */
    @TableField(value = "abnormalityType")
    private Object abnormalityType;

    /**
     * 异常级别
     */
    @TableField(value = "abnormalityLevel")
    private Object abnormalityLevel;

    /**
     * 异常发生时间
     */
    @TableField(value = "occurrenceTime")
    private Date occurrenceTime;

    /**
     * 异常恢复时间
     */
    @TableField(value = "recoveryTime")
    private Date recoveryTime;

    /**
     * 处理状态
     */
    @TableField(value = "status")
    private Object status;

    /**
     * 处理人ID（关联user表，管理员或维修人员）
     */
    @TableField(value = "handlerId")
    private Long handlerId;

    /**
     * 异常详细描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 处理过程记录
     */
    @TableField(value = "handlingNotes")
    private String handlingNotes;

    /**
     * 记录创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}