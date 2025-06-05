package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 报修申请
 * @TableName repairorder
 */
@TableName(value ="repairorder")
@Data
public class RepairOrder implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报修用户ID
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 报修房产ID
     */
    @TableField(value = "propertyId")
    private Long propertyId;

    /**
     * 问题描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 处理人ID（管理员）
     */
    @TableField(value = "reviewerId")
    private Long reviewerId;

    /**
     * 完成时间
     */
    @TableField(value = "reviewTime")
    private Date reviewTime;

    /**
     * 审批原因
     */
    @TableField(value = "reviewMessage")
    private String reviewMessage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}