package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mrdotxin.propsmart.model.enums.BillStatusEnum;
import com.mrdotxin.propsmart.model.enums.BillTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 费用账单
 * @TableName bill
 */
@TableName(value ="bill")
@Data
public class Bill implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房产ID
     */
    @TableField(value = "propertyId")
    private Long propertyId;

    /**
     * 费用类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 截止日期
     */
    @TableField(value = "deadline")
    private Date deadline;

    /**
     * 缴费状态
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
     * 缴费时间
     */
    @TableField(value = "paidTime")
    private Date paidTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}