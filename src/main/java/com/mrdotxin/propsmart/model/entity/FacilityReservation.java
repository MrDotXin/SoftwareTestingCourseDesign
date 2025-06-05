package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 设施预订
 */
@TableName(value ="facilityReservation")
@Data
public class FacilityReservation implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预订用户ID
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 设施ID
     */
    @TableField(value = "facilityId")
    private Integer facilityId;

    /**
     * 预订时间
     */
    @TableField(value = "ReservationTime")
    private Date reservationTime;

    /**
     * 时长（小时）
     */
    @TableField(value = "duration")
    private Integer duration;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Object status;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 审批人ID（管理员）
     */
    @TableField(value = "reviewerId")
    private Long reviewerId;

    /**
     * 同意/拒绝原因
     */
    @TableField(value = "reviewMessage")
    private String reviewMessage;

    /**
     * 审批时间
     */
    @TableField(value = "reviewTime")
    private Date reviewTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}