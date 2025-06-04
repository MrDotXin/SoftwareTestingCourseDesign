package com.mrdotxin.propsmart.model.dto.fireequipment;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 添加消防设备请求
 */
@Data
public class FireEquipmentAddRequest implements Serializable {

    /**
     * 所属楼栋ID
     */
    private Long buildingId;

    /**
     * 当前状态
     */
    private String currentStatus;

    /**
     * 上次巡检人ID
     */
    private Long lastInspectorId;

    /**
     * 上次巡检时间
     */
    private Date lastInspectionTime;

    /**
     * 下次巡检截止时间
     */
    private Date nextInspectionDue;

    /**
     * 巡检备注
     */
    private String inspectionRemarks;

    private static final long serialVersionUID = 1L;
} 