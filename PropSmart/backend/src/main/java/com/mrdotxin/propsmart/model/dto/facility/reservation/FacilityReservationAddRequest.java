package com.mrdotxin.propsmart.model.dto.facility.reservation;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "设施ID")
    private Integer facilityId;
    
    /**
     * 预订时间
     */
    @ApiModelProperty(value = "预订时间")
    private Date reservationTime;
    
    /**
     * 时长（小时）
     */
    @ApiModelProperty(value = "时长（小时）")
    private Integer duration;
    
    private static final long serialVersionUID = 1L;
} 