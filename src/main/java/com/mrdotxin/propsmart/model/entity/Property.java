package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房产信息
 *
 * @TableName properties
 */
@TableName(value = "property")
@Data
public class Property implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 实际拥有者的身份证号
     */
    @TableField(value = "ownerIdentity")
    private String ownerIdentity;

    /**
     * 楼栋ID
     */
    @TableField(value = "buildingId")
    private Long buildingId;

    /**
     * 单元号
     */
    @TableField(value = "unitNumber")
    private String unitNumber;

    /**
     * 房号
     */
    @TableField(value = "roomNumber")
    private String roomNumber;

    /**
     * 建筑面积
     */
    @TableField(value = "area")
    private Double area;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}