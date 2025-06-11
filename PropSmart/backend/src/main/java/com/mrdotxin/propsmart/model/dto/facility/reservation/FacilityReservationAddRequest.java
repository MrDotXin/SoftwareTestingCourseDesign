package com.mrdotxin.propsmart.model.dto.facility.reservation;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建设施预订请求
 */
@Data
public class FacilityReservationAddRequest implements Serializable {
    
    /**
     * 设施ID
     */
    @Schema(description = "设施ID")
    private Integer facilityId;
    
    /**
     * 预订时间
     */
    @Schema(description = "预订时间")
    private Date reservationTime;
    
    /**
     * 时长（小时）
     */
    @Schema(description = "结束时间")
    private Date reservationEndTime;
    
    private static final long serialVersionUID = 1L;
} 