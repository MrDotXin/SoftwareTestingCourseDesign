package com.mrdotxin.propsmart.model.dto.fireequipment;

import com.mrdotxin.propsmart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询消防设备请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FireEquipmentQueryRequest extends PageRequest implements Serializable {

    /**
     * 设备ID
     */
    private Long id;

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
     * 上次巡检时间开始
     */
    private Date lastInspectionTimeStart;

    /**
     * 上次巡检时间结束
     */
    private Date lastInspectionTimeEnd;

    /**
     * 下次巡检截止时间开始
     */
    private Date nextInspectionDueStart;

    /**
     * 下次巡检截止时间结束
     */
    private Date nextInspectionDueEnd;

    private static final long serialVersionUID = 1L;
} 