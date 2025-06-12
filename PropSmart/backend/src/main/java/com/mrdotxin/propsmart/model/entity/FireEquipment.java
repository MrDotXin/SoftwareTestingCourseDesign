package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消防设备综合管理表
 */
@TableName(value ="fireEquipment")
@Data
public class FireEquipment implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属楼栋ID（关联building表，根据楼栋确定位置）
     */
    @TableField(value = "buildingId")
    private Long buildingId;

    /**
     * 设备所在具体楼层
     */
    @TableField(value = "specificLevel")
    private Integer specificLevel;

    /**
     * 当前状态
     */
    @TableField(value = "currentStatus")
    private String currentStatus;

    /**
     * 上次巡检人ID
     */
    @TableField(value = "lastInspectorId")
    private Long lastInspectorId;

    /**
     * 上次巡检时间
     */
    @TableField(value = "lastInspectionTime")
    private Date lastInspectionTime;

    /**
     * 下次巡检截止时间
     */
    @TableField(value = "nextInspectionDue")
    private Date nextInspectionDue;

    /**
     * 巡检备注
     */
    @TableField(value = "inspectionRemarks")
    private String inspectionRemarks;

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