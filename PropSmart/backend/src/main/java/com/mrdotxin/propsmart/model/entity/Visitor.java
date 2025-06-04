package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 访客管理
 */
@TableName(value = "visitor")
@Data
public class Visitor implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 被访用户ID
     */
    private Long userId;

    /**
     * 访客姓名
     */
    private String visitorName;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 访问原因
     */
    private String visitReason;

    /**
     * 预计访问时间
     */
    private Date visitTime;

    /**
     * 预计时长（小时）
     */
    private Integer duration;

    /**
     * 审批状态（待审批/已通过/已拒绝）
     */
    private String reviewStatus;

    /**
     * 审批人ID
     */
    private Long reviewerId;

    /**
     * 审批时间
     */
    private Date reviewTime;

    /**
     * 审批理由
     */
    private String reviewMessage;

    /**
     * 电子通行证
     */
    private String passCode;

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