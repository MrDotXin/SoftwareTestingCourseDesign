package com.mrdotxin.propsmart.model.dto.facility.reservation;

import com.mrdotxin.propsmart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询设施预订请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FacilityReservationQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 设施ID
     */
    private Integer facilityId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 预订时间
     */
    private Date reservationTime;
    
    /**
     * 状态（pending/success/rejected）
     */
    private String status;
    
    private static final long serialVersionUID = 1L;
} 